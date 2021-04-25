package com.stancorp.grocerystorev1.SmallRecyclerViewAdapter;

import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.stancorp.grocerystorev1.Classes.LocationStockItem;
import com.stancorp.grocerystorev1.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

public class CodesLocationRecyclerAdapter extends CodesBaseRecyclerAdapter {

    Button Delete ;
    protected List<String> Codes;
    TextView Code;

    public CodesLocationRecyclerAdapter(Context context, @NonNull LinkedHashMap<String,LocationStockItem> objects
            , List<String> Codes, LinkedHashMap<String,Float> StockValue) {
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
                String CodeRemoved = (String) Codes.get(position);
                locationStockItems.remove(CodeRemoved);
                Codes.remove(CodeRemoved);
                StockValue.remove(CodeRemoved);
                notifyDataSetChanged();
            }
        });

        Code = holder.itemView.findViewById(R.id.Code);

        String LocationCode = (String) Codes.get(position);

        if(LocationCode!=null){
            Code.setText(LocationCode);
        }
    }
}
