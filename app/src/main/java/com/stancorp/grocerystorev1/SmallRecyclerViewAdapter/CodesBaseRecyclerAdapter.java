package com.stancorp.grocerystorev1.SmallRecyclerViewAdapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.stancorp.grocerystorev1.Classes.LocationStockItem;
import com.stancorp.grocerystorev1.R;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

public abstract class CodesBaseRecyclerAdapter extends RecyclerView.Adapter<CodesBaseRecyclerAdapter.CodesBaseViewHolder> {

    protected LinkedHashMap<String,LocationStockItem> locationStockItems;
    protected LinkedHashMap<String,Float> StockValue;
    protected Context BASE_CONTEXT;

    public CodesBaseRecyclerAdapter(Context context){
        this.BASE_CONTEXT = context;
    }

    @NonNull
    @Override
    public CodesBaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.location_stock_layout, parent, false);
        CodesBaseViewHolder codesbaseViewHolder = new CodesBaseRecyclerAdapter.CodesBaseViewHolder(view);
        return codesbaseViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull CodesBaseViewHolder holder,final int position) {
        LocationStockItem locationStockItem = new LocationStockItem();
        Float value = 0f;
        if(locationStockItems !=null && StockValue!=null) {
            locationStockItem = (LocationStockItem) locationStockItems.values().toArray()[position];
            value = (Float) StockValue.values().toArray()[position];
        }
        if(locationStockItem!=null && value!=null){
            holder.OpeningStock.setText(locationStockItem.Balance_Qty);
            holder.OpeningStockValue.setText(String.valueOf(value));
            holder.RestockQty.setText(locationStockItem.Reorder_Qty);
        }
        onBindViewHold(position,holder);
    }

    @Override
    public int getItemCount() {
        return locationStockItems.size();
    }

    protected abstract void onBindViewHold(int position, CodesBaseViewHolder holder);

    public class CodesBaseViewHolder extends RecyclerView.ViewHolder{
        TextView OpeningStock ;
        TextView OpeningStockValue ;
        TextView RestockQty ;

        public CodesBaseViewHolder(@NonNull View itemView) {
            super(itemView);
            OpeningStock = itemView.findViewById(R.id.OpenStock);
            OpeningStockValue = itemView.findViewById(R.id.OpenValue);
            RestockQty = itemView.findViewById(R.id.ReorderQtyText);
        }
    }

}
