package com.stancorp.grocerystorev1.DisplayStakeholder;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.paging.PagedList;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.firebase.ui.firestore.paging.FirestorePagingOptions;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.stancorp.grocerystorev1.AdapterClasses.FirestoreBaseRecyclerAdapter;
import com.stancorp.grocerystorev1.AdapterClasses.TransactionFirestoreAdapter;
import com.stancorp.grocerystorev1.Classes.StoreTransaction;
import com.stancorp.grocerystorev1.DisplayTransactions.TransactionViewActivity;
import com.stancorp.grocerystorev1.R;

public class AgentPending extends Fragment implements FirestoreBaseRecyclerAdapter.OnNoteListner {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String ARG_PARAM3 = "param3";
    private static final String ARG_PARAM4 = "param4";

    private String ShopCode;
    private String AgentCode;
    private String UserPermission;
    private String UserLocation;

    FirebaseFirestore firebaseFirestore;

    RecyclerView recyclerView;
    RecyclerView.LayoutManager mLayoutManager;
    TransactionFirestoreAdapter transactionAdapter;

    public static AgentPending newInstance(String ShopCode, String AgentCode, String UserPermission, String UserLocation) {

        Bundle args = new Bundle();

        AgentPending fragment = new AgentPending();
        args.putString(ARG_PARAM1, ShopCode);
        args.putString(ARG_PARAM2, AgentCode);
        args.putString(ARG_PARAM3, UserPermission);
        args.putString(ARG_PARAM4, UserLocation);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            ShopCode = getArguments().getString(ARG_PARAM1);
            AgentCode = getArguments().getString(ARG_PARAM2);
            UserPermission = getArguments().getString(ARG_PARAM3);
            UserLocation = getArguments().getString(ARG_PARAM4);
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
        attachDatabaseListener();
        return view;
    }

    @Override
    public void onStop() {
        super.onStop();
        transactionAdapter.stopListening();
    }

    @Override
    public void onStart() {
        super.onStart();
        if (transactionAdapter != null)
            transactionAdapter.startListening();
    }

    private void attachDatabaseListener() {

        Query query =
                firebaseFirestore.collection(ShopCode).document("doc").collection("TransactionDetails")
                        .whereEqualTo("pending", true).whereEqualTo("stakeholderCode", AgentCode);

        if(UserPermission.compareTo("Employee")==0){
            query = query.whereEqualTo("locationCode",UserLocation);
        }
        query = query.limit(40);

        PagedList.Config config = new PagedList.Config.Builder()
                .setInitialLoadSizeHint(10)
                .setPageSize(3)
                .build();

        FirestorePagingOptions<StoreTransaction> options = new FirestorePagingOptions.Builder<StoreTransaction>()
                .setQuery(query, config, StoreTransaction.class)
                .build();

        transactionAdapter = new TransactionFirestoreAdapter(options, getContext(), this,null , null, null);
        transactionAdapter.startListening();
        transactionAdapter.notifyDataSetChanged();
        recyclerView.setAdapter(transactionAdapter);
        recyclerView.scheduleLayoutAnimation();
    }

    @Override
    public void OnNoteClick(DocumentSnapshot documentSnapshot, int position) {
        Intent intent = new Intent(getContext(), TransactionViewActivity.class);
        StoreTransaction transaction = documentSnapshot.toObject(StoreTransaction.class);
        intent.putExtra("Transaction", transaction);
        intent.putExtra("Mode", transaction.type);
        intent.putExtra("ShopCode", ShopCode);
        startActivity(intent);
    }
}
