package com.stancorp.grocerystorev1.SmallRecyclerViewAdapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.stancorp.grocerystorev1.Classes.LocationStockItem;
import com.stancorp.grocerystorev1.R;

import java.util.List;

public class CodesRecyclerAdapter extends RecyclerView.Adapter<CodesRecyclerAdapter.CodesViewHolder> {

    List<LocationStockItem> locationStockItems;
    List<String> Codes;
    List<Float> StockValue;
    List<String> BlankCodes;

    public CodesRecyclerAdapter(@NonNull Context context, @NonNull List<LocationStockItem> objects
            , List<String> Codes, List<Float> StockValue, List<String> BlankCodes){
        this.locationStockItems = objects;
        this.Codes = Codes;
        this.StockValue = StockValue;
        this.BlankCodes = BlankCodes;
    }

    @NonNull
    @Override
    public CodesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.location_stock_layout, parent, false);
        CodesViewHolder codesViewHolder = new CodesViewHolder(view);
        return codesViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull CodesViewHolder holder,final int position) {
        holder.Delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String CodeRemoved = Codes.get(position);
                locationStockItems.remove(position);
                Codes.remove(position);
                StockValue.remove(position);
                BlankCodes.add(CodeRemoved);
                notifyDataSetChanged();
            }
        });

        LocationStockItem locationStockItem = locationStockItems.get(position);
        String Code = Codes.get(position);
        Float value = StockValue.get(position);

        if(locationStockItem!=null){
            holder.Code.setText(Code);
            holder.OpeningStock.setText(locationStockItem.Balance_Qty);
            holder.OpeningStockValue.setText(String.valueOf(value));
            holder.RestockQty.setText(locationStockItem.Reorder_Qty);
        }
    }

    @Override
    public int getItemCount() {
        return locationStockItems.size();
    }

    public class CodesViewHolder extends RecyclerView.ViewHolder{

        TextView Code;
        TextView OpeningStock ;
        TextView OpeningStockValue ;
        TextView RestockQty ;
        Button Delete ;

        public CodesViewHolder(@NonNull View itemView) {
            super(itemView);
            Code = itemView.findViewById(R.id.Code);
            OpeningStock = itemView.findViewById(R.id.OpenStock);
            OpeningStockValue = itemView.findViewById(R.id.OpenValue);
            RestockQty = itemView.findViewById(R.id.ReorderQtyText);
            Delete = itemView.findViewById(R.id.DeleteButton);
        }
    }
}
