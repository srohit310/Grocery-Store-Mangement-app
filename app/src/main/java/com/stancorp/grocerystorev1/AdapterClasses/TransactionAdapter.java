package com.stancorp.grocerystorev1.AdapterClasses;

import android.content.Context;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.stancorp.grocerystorev1.Classes.StoreTransaction;
import com.stancorp.grocerystorev1.R;

import java.util.LinkedHashMap;

public class TransactionAdapter extends BaseRecyclerAdapter {

    private LinkedHashMap<String, StoreTransaction> transactionsArrayList;
    private BaseRecyclerAdapter.OnNoteListner mOnNoteListner;

    TextView transactionCode;
    TextView transactionReference;
    TextView fromcode,fromtype;
    TextView tocode,totype;
    TextView totalPrice;
    ImageView approved;
    Context context;

    public TransactionAdapter(LinkedHashMap<String,StoreTransaction> transactions,Context context, OnNoteListner onNoteListner) {
        super(context, onNoteListner);
        dataList = transactions;
        this.context = context;
        transactionsArrayList = transactions;
        this.mOnNoteListner = onNoteListner;
        layout_id = R.layout.transaction_layout;
    }

    @Override
    public void onBindViewHold(int position, MyViewHolder holder) {
        StoreTransaction transaction = (StoreTransaction) dataList.values().toArray()[position];

        transactionCode = holder.itemView.findViewById(R.id.Code);
        transactionReference = holder.itemView.findViewById(R.id.Referenceid);
        totalPrice = holder.itemView.findViewById(R.id.Totprice);
        approved = holder.itemView.findViewById(R.id.approvedcircle);
        fromcode = holder.itemView.findViewById(R.id.fromcode);
        fromtype = holder.itemView.findViewById(R.id.fromtype);
        tocode = holder.itemView.findViewById(R.id.tocode);
        totype = holder.itemView.findViewById(R.id.totype);

        if(transaction!=null){
            transactionCode.setText(transaction.code);
            transactionReference.setText(transaction.reference);
            totalPrice.setText(String.valueOf(transaction.totalPrice)+" "+"INR");
            if (transaction.pending) {
                approved.setBackground(ContextCompat.getDrawable(context, R.drawable.circle_red));
            } else {
                approved.setBackground(ContextCompat.getDrawable(context, R.drawable.circle_green));
            }
            if(transaction.type.compareTo("Purchase")==0){
                fromcode.setText(transaction.stakeholderCode);
                fromtype.setText("Vendor");
                tocode.setText(transaction.locationCode);
                totype.setText("Store Location");
            }else{
                fromcode.setText(transaction.locationCode);
                fromtype.setText("Store Location");
                tocode.setText(transaction.stakeholderCode);
                totype.setText("Customer");
            }
        }
    }
}
