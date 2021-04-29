package com.stancorp.grocerystorev1.DisplayItems;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.stancorp.grocerystorev1.AdapterClasses.AdjustmentAdapter;
import com.stancorp.grocerystorev1.AdapterClasses.BaseRecyclerAdapter;
import com.stancorp.grocerystorev1.Classes.ItemAdjustmentClass;
import com.stancorp.grocerystorev1.Classes.Items;
import com.stancorp.grocerystorev1.GlobalClass.Gfunc;
import com.stancorp.grocerystorev1.R;

import java.util.LinkedHashMap;

public class ItemAdjustment extends Fragment implements BaseRecyclerAdapter.OnNoteListner {


    private static final String ARG_1 = "param1";
    private static final String ARG_2 = "param2";
    private static final String ARG_3 = "param3";
    private static final String ARG_4 = "param4";
    private static final String ARG_5 = "param5";

    private Items item;
    private String ShopCode;
    private String UserName;
    private String UserPermission;
    private String UserLocation;

    ListenerRegistration adjustmentlistener;

    Gfunc gfunc;

    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    LinkedHashMap<String, ItemAdjustmentClass> itemadjustments;
    AdjustmentAdapter adjustmentAdapter;
    LinearLayout EmptyLayout;

    FirebaseFirestore firebaseFirestore;

    public ItemAdjustment() {
        // Required empty public constructor
    }

    public static ItemAdjustment newInstance(Items param1, String param2, String param3, String param4, String param5) {
        ItemAdjustment fragment = new ItemAdjustment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_1, param1);
        args.putString(ARG_2, param2);
        args.putString(ARG_3, param3);
        args.putString(ARG_4, param4);
        args.putString(ARG_5, param5);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            item = (Items) getArguments().getSerializable(ARG_1);
            ShopCode = getArguments().getString(ARG_2);
            UserName = getArguments().getString(ARG_3);
            UserPermission = getArguments().getString(ARG_4);
            UserLocation = getArguments().getString(ARG_5);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_item_adjustment, container, false);
        gfunc = new Gfunc();
        itemadjustments = new LinkedHashMap<>();
        adjustmentAdapter = new AdjustmentAdapter(itemadjustments, getContext(), this);
        firebaseFirestore = FirebaseFirestore.getInstance();
        recyclerView = view.findViewById(R.id.recyclerView);
        EmptyLayout = view.findViewById(R.id.EmptyLayout);

        EmptyLayout.setVisibility(View.VISIBLE);

        layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adjustmentAdapter);
        attachDatabaseListener();
        return view;
    }

    @Override
    public void onStop() {
        adjustmentlistener.remove();
        super.onStop();
    }

    private void attachDatabaseListener() {
        Query query =
                firebaseFirestore.collection(ShopCode).document("doc").collection("ItemAdjustments")
                        .whereEqualTo("ItemCode", item.ItemCode);
        if(UserPermission.compareTo("Employee")==0){
            query = query.whereEqualTo("LocationCode",UserLocation);
        }
        query = query.limit(30);
        adjustmentlistener = query.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if(error!=null){
                    Log.i("firebaseadjustments",error.getMessage());
                }
                if (value != null && !value.isEmpty()) {
                    for (DocumentChange doc : value.getDocumentChanges()) {
                        ItemAdjustmentClass adjustment;
                        switch (doc.getType()) {
                            case ADDED:
                                adjustment = doc.getDocument().toObject(ItemAdjustmentClass.class);
                                itemadjustments.put(doc.getDocument().getId(), adjustment);
                                adjustmentAdapter.notifyDataSetChanged();
                                break;
                        }
                    }
                }
                if (itemadjustments.size() > 0) {
                    EmptyLayout.setVisibility(View.GONE);
                }
            }
        });
    }

    @Override
    public void OnNoteClick(int position) {

    }
}