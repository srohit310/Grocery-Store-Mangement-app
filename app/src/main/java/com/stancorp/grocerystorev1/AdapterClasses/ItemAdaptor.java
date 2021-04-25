package com.stancorp.grocerystorev1.AdapterClasses;

import android.content.Context;
import android.net.Uri;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.stancorp.grocerystorev1.Classes.Items;
import com.stancorp.grocerystorev1.GlobalClass.Gfunc;
import com.stancorp.grocerystorev1.R;

import java.util.LinkedHashMap;


public class ItemAdaptor extends BaseRecyclerAdapter {

    private LinkedHashMap<String,Items> itemsArrayList;
    private BaseRecyclerAdapter.OnNoteListner mOnNoteListner;

    TextView ItemName;
    TextView ItemCode;
    TextView SpText;
    TextView Category;
    Context context;
    ImageView ItemImage;
    Gfunc gfunc;

    String ShopCode;

    public ItemAdaptor(LinkedHashMap<String,Items> items, Context context, OnNoteListner onNoteListner, String ShopCode) {
        super(context, onNoteListner);
        gfunc = new Gfunc();
        dataList = items;
        this.context = context;
        itemsArrayList = items;
        this.mOnNoteListner = onNoteListner;
        layout_id = R.layout.items_layout;
        this.ShopCode = ShopCode;
    }


    @Override
    public void onBindViewHold(int position, MyViewHolder holder) {
        Items item = (Items) dataList.values().toArray()[position];

        //Initializing
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
