package com.stancorp.grocerystorev1.SmallRecyclerViewAdapter;

import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.stancorp.grocerystorev1.Classes.ItemStockInfo;
import com.stancorp.grocerystorev1.Classes.LocationStockItem;
import com.stancorp.grocerystorev1.R;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

public class CodesItemRecyclerAdapter extends CodesBaseRecyclerAdapter {
    Button Delete ;
    TextView Code;
    LinkedHashMap<String,ItemStockInfo> Codes;

    public CodesItemRecyclerAdapter(Context context, @NonNull LinkedHashMap<String,LocationStockItem> objects
            , LinkedHashMap<String,ItemStockInfo> Codes, LinkedHashMap<String,Float> StockValue) {
        super(context);

        this.locationStockItems = objects;
        this.Codes = Codes;
        this.StockValue = StockValue;
    }

    @Override
    protected void onBindViewHold(final int position, CodesBaseViewHolder holder) {
        Delete = holder.itemView.findViewById(R.id.DeleteButton);

        Delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ItemStockInfo tempstockinfo = (ItemStockInfo) Codes.values().toArray()[position];
                String CodeRemoved = tempstockinfo.ItemCode;
                locationStockItems.remove(CodeRemoved);
                Codes.remove(CodeRemoved);
                StockValue.remove(CodeRemoved);
                notifyDataSetChanged();
            }
        });

        Code = holder.itemView.findViewById(R.id.Code);

        ItemStockInfo itemCode = (ItemStockInfo) Codes.values().toArray()[position];

        if(itemCode!=null){
            Code.setText(itemCode.ItemCode);
        }
    }
}
