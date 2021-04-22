package com.stancorp.grocerystorev1.AdapterClasses;

import android.content.Context;
import android.widget.TextView;

import com.stancorp.grocerystorev1.Classes.Items;
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

    public ItemAdaptor(LinkedHashMap<String,Items> items, Context context, OnNoteListner onNoteListner) {
        super(context, onNoteListner);
        dataList = items;
        this.context = context;
        itemsArrayList = items;
        this.mOnNoteListner = onNoteListner;
        layout_id = R.layout.items_layout;
    }


    @Override
    public void onBindViewHold(int position, MyViewHolder holder) {
        Items item = (Items) dataList.values().toArray()[position];

        //Initializing
        ItemName = holder.itemView.findViewById(R.id.ItemName);
        ItemCode = holder.itemView.findViewById(R.id.ItemCode);
        Category = holder.itemView.findViewById(R.id.ItemCategory);
        SpText = holder.itemView.findViewById(R.id.SpText);

        ItemName.setText(item.name.substring(0, 1).toUpperCase() + item.name.substring(1).toLowerCase());
        ItemCode.setText(item.ItemCode);
        Category.setText(item.Category);
        SpText.setText(String.valueOf(item.Selling_Price) + "/" + item.Unit);
    }

}
