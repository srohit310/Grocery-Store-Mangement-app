package com.stancorp.grocerystorev1.AdapterClasses;

import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.stancorp.grocerystorev1.Classes.ItemAdjustmentClass;
import com.stancorp.grocerystorev1.R;

import java.util.LinkedHashMap;

public class AdjustmentAdapter extends BaseRecyclerAdapter {

    TextView AdjustmentMode;
    TextView LocationCode;
    TextView Reason;
    TextView Date;
    TextView QtyAdjusted;
    TextView UserName;
    LinearLayout LocationLayout;


    public AdjustmentAdapter(LinkedHashMap<String, ItemAdjustmentClass> adjustments, Context context, OnNoteListner onNoteListner) {
        super(context, onNoteListner);
        this.BASE_CONTEXT = context;
        dataList = adjustments;
        layout_id = R.layout.adjustments_made;
    }

    @Override
    public void onBindViewHold(int position, MyViewHolder holder) {
        ItemAdjustmentClass itemAdjustmentClass = (ItemAdjustmentClass) dataList.values().toArray()[position];

        AdjustmentMode = holder.itemView.findViewById(R.id.mode);
        LocationCode = holder.itemView.findViewById(R.id.LocationCode);
        Reason = holder.itemView.findViewById(R.id.Reason);
        Date = holder.itemView.findViewById(R.id.Date);
        QtyAdjusted = holder.itemView.findViewById(R.id.Qtyadjusted);
        UserName = holder.itemView.findViewById(R.id.Username);
        LocationLayout = holder.itemView.findViewById(R.id.LocationLayout);

        if(itemAdjustmentClass !=null){
            AdjustmentMode.setText(itemAdjustmentClass.mode);
            if(itemAdjustmentClass.mode.compareTo("Quantity")==0) {
                LocationCode.setText(itemAdjustmentClass.LocationCode);
            }else{
                LocationLayout.setVisibility(View.GONE);
            }
            Reason.setText(itemAdjustmentClass.Reason);
            QtyAdjusted.setText(itemAdjustmentClass.AmountAdjusted);
            UserName.setText(itemAdjustmentClass.Updatedby);
            Date.setText(itemAdjustmentClass.Date);
        }
    }
}
