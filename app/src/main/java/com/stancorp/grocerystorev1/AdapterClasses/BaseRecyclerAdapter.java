package com.stancorp.grocerystorev1.AdapterClasses;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.stancorp.grocerystorev1.R;

import java.util.LinkedHashMap;

public abstract class BaseRecyclerAdapter extends RecyclerView.Adapter<BaseRecyclerAdapter.MyViewHolder> {

    public int layout_id;
    private OnNoteListner mOnNoteListner;
    protected LinkedHashMap<String,?> dataList = new LinkedHashMap<>();
    Context BASE_CONTEXT;

    public BaseRecyclerAdapter(Context context, OnNoteListner onNoteListner) {
        this.BASE_CONTEXT = context;
        this.mOnNoteListner = onNoteListner;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(layout_id, parent, false);
        MyViewHolder myViewHolder = new MyViewHolder(view,mOnNoteListner);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        onBindViewHold(position,holder);
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public abstract void onBindViewHold(int position, MyViewHolder holder);

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        OnNoteListner onNoteListner;

        public MyViewHolder(@NonNull View itemView, OnNoteListner onNoteListner ) {
            super(itemView);
            this.onNoteListner = onNoteListner;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            onNoteListner.OnNoteClick(getAdapterPosition());
        }
    }

    public interface OnNoteListner { void OnNoteClick(int position);}
}
