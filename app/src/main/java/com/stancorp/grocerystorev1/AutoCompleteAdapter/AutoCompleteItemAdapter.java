package com.stancorp.grocerystorev1.AutoCompleteAdapter;

import android.content.Context;

import androidx.annotation.NonNull;

import com.stancorp.grocerystorev1.Classes.ItemStockInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

public class AutoCompleteItemAdapter extends AutoCompleteBaseAdapter<ItemStockInfo> {

    public AutoCompleteItemAdapter(@NonNull Context context, LinkedHashMap<String,ItemStockInfo> objects) {
        super(context);
        this.mContext = context;
        this.dataList = new LinkedHashMap<String, ItemStockInfo>(objects);
        this.dataListAll = new LinkedHashMap<String, ItemStockInfo>(objects);
    }

    @Override
    protected CharSequence setTextviewString(Object resultValue) {
        return ((ItemStockInfo) resultValue).ItemCode;
    }

    @Override
    protected void setView(int position) {
        ItemStockInfo item = getItem(position);

        if(item!=null){
            Name.setText(item.name);
            Code.setText(item.ItemCode);
        }
    }
}
