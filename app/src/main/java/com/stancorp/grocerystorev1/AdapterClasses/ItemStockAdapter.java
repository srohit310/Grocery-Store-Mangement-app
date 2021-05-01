package com.stancorp.grocerystorev1.AdapterClasses;

import android.content.Context;
import android.graphics.Color;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.stancorp.grocerystorev1.Classes.LocationStockItem;
import com.stancorp.grocerystorev1.R;

import java.util.ArrayList;
import java.util.LinkedHashMap;

public class ItemStockAdapter extends BaseRecyclerAdapter{

    private BaseRecyclerAdapter.OnNoteListner mOnNoteListner;
    private float Reorderlvl;
    private float Excesslvl;
    private float Balance;
    private float max;

    private String unit;

    TextView LocationCode;
    TextView Reorderquantity;
    TextView rlvl;
    TextView elvl;
    TextView blvl;
    ImageView WarningImg;
    TextView WarningText;

    public ItemStockAdapter(Context context, OnNoteListner onNoteListner, LinkedHashMap<String, LocationStockItem> locationstocks,
                            Float Reorderlvl,Float ExcessLvl, String Unit) {
        super(context, onNoteListner);
        this.dataList = locationstocks;
        this.Reorderlvl = Reorderlvl;
        this.Excesslvl = ExcessLvl;
        this.BASE_CONTEXT = context;
        this.unit = Unit;
        this.layout_id = R.layout.item_stock_layout;

    }

    @Override
    public void onBindViewHold(int position, MyViewHolder holder) {
        LocationStockItem locationStockItem = (LocationStockItem) dataList.values().toArray()[position];

        Balance = Float.valueOf(locationStockItem.Balance_Qty);

        LocationCode = holder.itemView.findViewById(R.id.LocationCode);
        Reorderquantity = holder.itemView.findViewById(R.id.ReorderQuantity);
        rlvl = holder.itemView.findViewById(R.id.Reorderlvl);
        elvl = holder.itemView.findViewById(R.id.Excesslvl);
        blvl = holder.itemView.findViewById(R.id.Balancelvl);
        WarningImg = holder.itemView.findViewById(R.id.Warningimg);
        WarningText = holder.itemView.findViewById(R.id.Warningtext);

        if(!locationStockItem.valid){
            WarningImg.setBackground(ContextCompat.getDrawable(BASE_CONTEXT, R.drawable.circle_grey));
            blvl.setTextColor(ContextCompat.getColor(BASE_CONTEXT, R.color.Edittext));
            WarningText.setText("Item Stock not available");
        } else if(Reorderlvl > Balance || Balance > Excesslvl){
            WarningImg.setBackground(ContextCompat.getDrawable(BASE_CONTEXT, R.drawable.circle_red));
            blvl.setTextColor(ContextCompat.getColor(BASE_CONTEXT, R.color.Red));
            if(Reorderlvl >= Balance)
                WarningText.setText("Item needs to be restocked");
            if(Balance > Excesslvl)
                WarningText.setText("Item is present at an excess level");
        }else {
            WarningImg.setBackground(ContextCompat.getDrawable(BASE_CONTEXT, R.drawable.circle_green));
            blvl.setTextColor(ContextCompat.getColor(BASE_CONTEXT,R.color.green));
            WarningText.setText("Balance quantity present at optimum Level");
        }

        LocationCode.setText(locationStockItem.LocationCode);
        Reorderquantity.setText(locationStockItem.Reorder_Qty);
        rlvl.setText(String.valueOf(Reorderlvl));
        elvl.setText(String.valueOf(Excesslvl));
        blvl.setText(String.valueOf(Balance));
    }

    public void updatevalues(Float Reorderlvl, Float Excesslvl){
        this.Reorderlvl = Reorderlvl;
        this.Excesslvl = Excesslvl;
        notifyDataSetChanged();
    }
}
