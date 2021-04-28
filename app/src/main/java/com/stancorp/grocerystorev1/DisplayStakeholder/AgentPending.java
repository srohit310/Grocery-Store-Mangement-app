package com.stancorp.grocerystorev1.DisplayStakeholder;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.stancorp.grocerystorev1.AdapterClasses.BaseRecyclerAdapter;
import com.stancorp.grocerystorev1.AdapterClasses.ItemStockAdapter;
import com.stancorp.grocerystorev1.AdapterClasses.TransactionAdapter;
import com.stancorp.grocerystorev1.Classes.StoreTransaction;
import com.stancorp.grocerystorev1.R;

import java.util.LinkedHashMap;

public class AgentPending extends Fragment implements BaseRecyclerAdapter.OnNoteListner {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String ShopCode;
    private String AgentCode;

    FirebaseFirestore firebaseFirestore;
    ListenerRegistration pendinglistener;

    RecyclerView recyclerView;
    RecyclerView.LayoutManager mLayoutManager;
    LinkedHashMap<String, StoreTransaction> transactions;
    TransactionAdapter transactionAdapter;

    public static AgentPending newInstance(String ShopCode, String AgentCode) {

        Bundle args = new Bundle();

        AgentPending fragment = new AgentPending();
        args.putString(ARG_PARAM1, ShopCode);
        args.putString(ARG_PARAM2, AgentCode);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            ShopCode = getArguments().getString(ARG_PARAM1);
            AgentCode = getArguments().getString(ARG_PARAM2);
        }
        firebaseFirestore = FirebaseFirestore.getInstance();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recycler_view, container, false);
        recyclerView = view.findViewById(R.id.recyclerView);
        mLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(mLayoutManager);
        transactions = new LinkedHashMap<>();
        transactionAdapter = new TransactionAdapter(transactions, getContext(), this, "Small");
        recyclerView.setAdapter(transactionAdapter);
        attachDatabaseListener();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (transactions != null)
            attachDatabaseListener();
    }

    @Override
    public void onPause() {
        pendinglistener.remove();
        super.onPause();
    }

    private void attachDatabaseListener() {
        pendinglistener =
                firebaseFirestore.collection(ShopCode).document("doc").collection("TransactionDetails")
                        .whereEqualTo("pending", true).whereEqualTo("stakeholderCode", AgentCode).limit(20)
                        .addSnapshotListener(new EventListener<QuerySnapshot>() {
                            @Override
                            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException error) {
                                if (error != null) {
                                    Log.i("failfirestore", error.getMessage());
                                }
                                if (queryDocumentSnapshots != null && !queryDocumentSnapshots.isEmpty()) {
                                    for (DocumentChange doc : queryDocumentSnapshots.getDocumentChanges()) {
                                        StoreTransaction transaction;
                                        switch (doc.getType()) {
                                            case ADDED:
                                                transaction = doc.getDocument().toObject(StoreTransaction.class);
                                                transactions.put(transaction.code, transaction);
                                                transactionAdapter.notifyDataSetChanged();
                                                break;
                                            case MODIFIED:
                                                transaction = doc.getDocument().toObject(StoreTransaction.class);
                                                transactions.put(transaction.code, transaction);
                                                transactionAdapter.notifyDataSetChanged();
                                                break;
                                        }
                                    }
                                }
                            }
                        });
    }

    @Override
    public void OnNoteClick(int position) {

    }
}
