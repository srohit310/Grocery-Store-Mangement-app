package com.stancorp.grocerystorev1.AdapterClasses;

import android.content.Context;
import android.net.Uri;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.firebase.ui.firestore.paging.FirestorePagingOptions;
import com.stancorp.grocerystorev1.Classes.Items;
import com.stancorp.grocerystorev1.GlobalClass.Gfunc;
import com.stancorp.grocerystorev1.R;

public class ItemFirestoreAdapter extends FirestoreBaseRecyclerAdapter<Items> {

    TextView ItemName;
    TextView ItemCode;
    TextView SpText;
    TextView Category;
    Context context;
    ImageView ItemImage;
    Gfunc gfunc;

    public ItemFirestoreAdapter(@NonNull FirestorePagingOptions<Items> options, Context context, OnNoteListner onNoteListner
            , RelativeLayout progressLayout, ConstraintLayout emptyview) {
        super(options, context, onNoteListner, progressLayout, emptyview);
        gfunc = new Gfunc();
        this.context = context;
        layout_id = R.layout.items_layout;
        this.mOnNoteListner = onNoteListner;
        this.progressLayout = progressLayout;
    }


    @Override
    protected void onBindViewHolder(@NonNull FirestoreBaseRecyclerAdapter.MyViewHolder holder, int position, @NonNull Items item) {
        ItemName = holder.itemView.findViewById(R.id.ItemName);
        ItemCode = holder.itemView.findViewById(R.id.ItemCode);
        Category = holder.itemView.findViewById(R.id.ItemCategory);
        SpText = holder.itemView.findViewById(R.id.SpText);
        ItemImage = holder.itemView.findViewById(R.id.ItemDetailsImage);

        ItemName.setText(gfunc.capitalize(item.name));
        ItemCode.setText(item.ItemCode);
        Category.setText(gfunc.capitalize(item.Category));
        SpText.setText(String.valueOf(item.Selling_Price) + "/" + item.Unit);
        if(item.Imguri){
            Uri uri = Uri.parse(item.ImgUriString);
            ItemImage.setForeground(null);
            Glide.with(ItemImage.getContext())
                    .load(uri)
                    .into(ItemImage);
        }else{
            ItemImage.setBackground(ContextCompat.getDrawable(context,R.drawable.ic_baseline_shopping_basket_accend_24));
        }
    }
}
