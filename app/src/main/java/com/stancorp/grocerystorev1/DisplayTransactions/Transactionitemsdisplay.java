package com.stancorp.grocerystorev1.DisplayTransactions;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.stancorp.grocerystorev1.AdapterClasses.BaseRecyclerAdapter;
import com.stancorp.grocerystorev1.AdapterClasses.ItemTransactionRecyclerAdapter;
import com.stancorp.grocerystorev1.Classes.Itemtransaction;
import com.stancorp.grocerystorev1.Classes.TransactionItemList;
import com.stancorp.grocerystorev1.R;

import java.util.ArrayList;
import java.util.LinkedHashMap;

public class Transactionitemsdisplay extends Fragment implements BaseRecyclerAdapter.OnNoteListner {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String ARG_PARAM3 = "param3";
    private static final String ARG_PARAM4 = "param4";
    private static final String ARG_PARAM5 = "param5";

    private String ShopCode;
    private String TransactionCode;
    private float totalprice;
    private String Mode;
    private float totalprofit;

    TextView totalpricetext;
    TextView totalprofittext;
    LinearLayout totalprofitlayout;

    FirebaseFirestore firebaseFirestore;
    ListenerRegistration pendinglistener;

    RecyclerView recyclerView;
    RecyclerView.LayoutManager mLayoutManager;
    LinkedHashMap<String, Itemtransaction> transactions;
    ItemTransactionRecyclerAdapter transactionAdapter;

    public static Transactionitemsdisplay newInstance(String ShopCode, String TransactionCode,
                                                      float totalprice, String Mode, float totalprofit) {

        Bundle args = new Bundle();

        Transactionitemsdisplay fragment = new Transactionitemsdisplay();
        args.putString(ARG_PARAM1, ShopCode);
        args.putString(ARG_PARAM2, TransactionCode);
        args.putFloat(ARG_PARAM3,totalprice);
        args.putFloat(ARG_PARAM5,totalprofit);
        args.putString(ARG_PARAM4,Mode);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        firebaseFirestore = FirebaseFirestore.getInstance();
        if (getArguments() != null) {
            ShopCode = getArguments().getString(ARG_PARAM1);
            TransactionCode = getArguments().getString(ARG_PARAM2);
            totalprice = getArguments().getFloat(ARG_PARAM3);
            Mode = getArguments().getString(ARG_PARAM4);
            totalprofit = getArguments().getFloat(ARG_PARAM5);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.transaction_item_list_layout, container, false);
        recyclerView = view.findViewById(R.id.ItemList);
        mLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(mLayoutManager);
        totalpricetext = view.findViewById(R.id.TotalCostText);
        totalpricetext.setText(String.valueOf(totalprice));
        totalprofittext = view.findViewById(R.id.TotalProfit);
        totalprofitlayout = view.findViewById(R.id.SaleProfit);
        if(Mode.compareTo("Sale")==0) {
            totalprofittext.setText(String.valueOf(totalprofit));
        }else{
            totalprofitlayout.setVisibility(View.GONE);
        }
        transactions = new LinkedHashMap<>();
        transactionAdapter = new ItemTransactionRecyclerAdapter(getContext(), this, transactions);
        recyclerView.setAdapter(transactionAdapter);
        fetchitemlist();
        return view;
    }

    private void fetchitemlist() {
        firebaseFirestore.collection(ShopCode).document("doc").collection("TransactionItems")
                .document(TransactionCode).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.getResult().exists()){
                    TransactionItemList itemList = task.getResult().toObject(TransactionItemList.class);
                    ArrayList<Itemtransaction> itemtransactions = itemList.itemtransactions;
                    for(Itemtransaction itemtransaction : itemtransactions){
                        transactions.put(itemtransaction.itemCode,itemtransaction);
                    }
                    transactionAdapter.notifyDataSetChanged();
                }
            }
        });
    }

    @Override
    public void OnNoteClick(int position) {

    }
}
