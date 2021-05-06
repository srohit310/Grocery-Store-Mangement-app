package com.stancorp.grocerystorev1.AdapterClasses;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.firebase.ui.firestore.paging.FirestorePagingOptions;
import com.stancorp.grocerystorev1.Classes.StoreTransaction;
import com.stancorp.grocerystorev1.R;

import javax.annotation.Nullable;

public class TransactionFirestoreAdapter extends FirestoreBaseRecyclerAdapter<StoreTransaction> {

    TextView transactionCode;
    TextView transactionReference;
    TextView fromcode, fromtype;
    TextView tocode, totype;
    TextView totalPrice;
    LinearLayout transactionExchange;
    ImageView approved;
    Context context;
    String contentSize;

    public TransactionFirestoreAdapter(@NonNull FirestorePagingOptions<StoreTransaction> options, Context context, OnNoteListner onNoteListner,
                                       @Nullable String contentSize, RelativeLayout progressLayout, ConstraintLayout emptyview) {
        super(options, context, onNoteListner, progressLayout, emptyview);
        this.context = context;
        this.mOnNoteListner = onNoteListner;
        layout_id = R.layout.transaction_layout;
        this.contentSize = contentSize;
        this.progressLayout = progressLayout;
    }

    @Override
    protected void onBindViewHolder(@NonNull FirestoreBaseRecyclerAdapter.MyViewHolder holder, int position, @NonNull StoreTransaction transaction) {

        transactionCode = holder.itemView.findViewById(R.id.Code);
        transactionReference = holder.itemView.findViewById(R.id.Referenceid);
        totalPrice = holder.itemView.findViewById(R.id.Totprice);
        approved = holder.itemView.findViewById(R.id.approvedcircle);
        fromcode = holder.itemView.findViewById(R.id.fromcode);
        fromtype = holder.itemView.findViewById(R.id.fromtype);
        tocode = holder.itemView.findViewById(R.id.tocode);
        totype = holder.itemView.findViewById(R.id.totype);
        transactionExchange = holder.itemView.findViewById(R.id.transactionexchange);

        if (transaction != null) {
            transactionCode.setText(transaction.code);
            transactionReference.setText(transaction.reference);
            String totalPriceText = String.valueOf(transaction.totalPrice) + " " + "INR";
            totalPrice.setText(totalPriceText);
            if (transaction.pending) {
                approved.setBackground(ContextCompat.getDrawable(context, R.drawable.circle_red));
            } else {
                approved.setBackground(ContextCompat.getDrawable(context, R.drawable.circle_green));
            }
            if (contentSize == null) {
                if (transaction.type.compareTo("Purchase") == 0) {
                    fromcode.setText(transaction.stakeholderCode);
                    fromtype.setText("Vendor");
                    tocode.setText(transaction.locationCode);
                    totype.setText("Store Location");
                } else {
                    fromcode.setText(transaction.locationCode);
                    fromtype.setText("Store Location");
                    tocode.setText(transaction.stakeholderCode);
                    totype.setText("Customer");
                }
            } else {
                if (contentSize.compareTo("Small") == 0) {
                    transactionExchange.setVisibility(View.GONE);
                }
            }
        }
    }
}
