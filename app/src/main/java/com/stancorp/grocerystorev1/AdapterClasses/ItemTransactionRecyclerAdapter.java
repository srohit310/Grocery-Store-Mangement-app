package com.stancorp.grocerystorev1.AdapterClasses;

import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.stancorp.grocerystorev1.AddActivities.AddTransactionActivity;
import com.stancorp.grocerystorev1.Classes.Itemtransaction;
import com.stancorp.grocerystorev1.R;

import java.util.HashMap;
import java.util.LinkedHashMap;

public class ItemTransactionRecyclerAdapter extends BaseRecyclerAdapter {

    Button Delete ;
    LinkedHashMap<String, Itemtransaction> transactionitemlist;
    TextView Quantity;
    TextView SellingPrice;
    TextView NetPrice;
    TextView Code;
    AddTransactionActivity addTransactionActivity;
    HashMap<String,String> itemreorderqty;

    public ItemTransactionRecyclerAdapter(Context context, OnNoteListner onNoteListner, LinkedHashMap<String, Itemtransaction> transactionitemlist){
        super(context,onNoteListner);
        this.transactionitemlist = transactionitemlist;
        this.dataList = transactionitemlist;
        this.BASE_CONTEXT = context;
        layout_id = R.layout.itembill_layout;
    }

    public ItemTransactionRecyclerAdapter(AddTransactionActivity activity , Context context, OnNoteListner onNoteListner,
                                          LinkedHashMap<String, Itemtransaction> transactionitemlist, HashMap<String,String> itemreorderqty) {
        super(context, onNoteListner);
        this.transactionitemlist = transactionitemlist;
        this.dataList = transactionitemlist;
        this.BASE_CONTEXT = context;
        layout_id = R.layout.itembill_layout;
        this.addTransactionActivity = activity;
        this.itemreorderqty = itemreorderqty;
    }


    @Override
    public void onBindViewHold(final int position, MyViewHolder holder) {
        Delete = holder.itemView.findViewById(R.id.DeleteButton);
        if(itemreorderqty!=null) {
            Delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Itemtransaction items = (Itemtransaction) transactionitemlist.values().toArray()[position];
                    float Totalcostitem = items.quantity * items.Price;
                    transactionitemlist.remove(items.itemCode);
                    itemreorderqty.remove(items.itemCode);
                    addTransactionActivity.totalcost -= Totalcostitem;
                    addTransactionActivity.TotalCostText.setText(String.valueOf(addTransactionActivity.totalcost) + "  " + "INR");
                    if (transactionitemlist.size() == 0) {
                        addTransactionActivity.BillTop.setVisibility(View.GONE);
                        addTransactionActivity.TotalPriceLayout.setVisibility(View.GONE);
                    }
                    notifyDataSetChanged();
                }
            });
        }else{
            Delete.setVisibility(View.GONE);
        }

        Code = holder.itemView.findViewById(R.id.Code);
        Quantity = holder.itemView.findViewById(R.id.Quantity);
        SellingPrice = holder.itemView.findViewById(R.id.SPrice);
        NetPrice = holder.itemView.findViewById(R.id.NetPrice);

        Itemtransaction items = (Itemtransaction) dataList.values().toArray()[position];
        if(items!=null){
            Code.setText(items.itemCode);
            Quantity.setText(String.valueOf(items.quantity));
            SellingPrice.setText(String.valueOf(items.Price) + " INR");
            NetPrice.setText(String.valueOf(items.quantity*items.Price));
        }
    }
}
