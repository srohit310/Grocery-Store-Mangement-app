package com.stancorp.grocerystorev1.AdapterClasses;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.firebase.ui.firestore.paging.FirestorePagingAdapter;
import com.firebase.ui.firestore.paging.FirestorePagingOptions;
import com.firebase.ui.firestore.paging.LoadingState;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.LinkedHashMap;

public abstract class FirestoreBaseRecyclerAdapter<T> extends FirestorePagingAdapter<T, FirestoreBaseRecyclerAdapter.MyViewHolder> {

    public int layout_id;
    public OnNoteListner mOnNoteListner;
    Context BASE_CONTEXT;
    public RelativeLayout progressLayout;

    public FirestoreBaseRecyclerAdapter(@NonNull FirestorePagingOptions<T> options, Context context, OnNoteListner onNoteListner,
                                        RelativeLayout progressLayout) {
        super((FirestorePagingOptions<T>) options);
        this.BASE_CONTEXT = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(layout_id, parent, false);
        MyViewHolder myViewHolder = new MyViewHolder(view, mOnNoteListner);
        return myViewHolder;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        OnNoteListner onNoteListner;

        public MyViewHolder(@NonNull View itemView, final OnNoteListner onNoteListner) {
            super(itemView);
            this.onNoteListner = onNoteListner;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            if (position != RecyclerView.NO_POSITION && onNoteListner != null) {
                onNoteListner.OnNoteClick(getItem(getAdapterPosition()), position);
            }
        }
    }

    @Override
    protected void onLoadingStateChanged(@NonNull LoadingState state) {
        super.onLoadingStateChanged(state);
        if(progressLayout!=null) {
            switch (state) {
                case LOADING_INITIAL:
                    SDProgress(true);
                    break;
                case LOADED:
                    SDProgress(false);
                    break;
            }
        }
    }

    public interface OnNoteListner {
        void OnNoteClick(DocumentSnapshot documentSnapshot, int position);
    }

    public void SDProgress(boolean show) {
        if (show) {
            progressLayout.animate()
                    .alpha(1.0f)
                    .setDuration(300)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                            progressLayout.setVisibility(View.VISIBLE);
                        }
                    });
        } else {
            progressLayout.animate()
                    .alpha(0.0f)
                    .setDuration(300)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                            progressLayout.setVisibility(View.GONE);
                        }
                    });
        }
    }
}
