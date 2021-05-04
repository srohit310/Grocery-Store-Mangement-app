package com.stancorp.grocerystorev1.DisplayItems;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QuerySnapshot;
import com.stancorp.grocerystorev1.AdapterClasses.BaseRecyclerAdapter;
import com.stancorp.grocerystorev1.AdapterClasses.ItemStockAdapter;
import com.stancorp.grocerystorev1.Classes.Items;
import com.stancorp.grocerystorev1.Classes.LocationStockItem;
import com.stancorp.grocerystorev1.R;

import java.util.LinkedHashMap;

public class ItemStock extends Fragment implements BaseRecyclerAdapter.OnNoteListner {


    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String ARG_PARAM3 = "param3";
    ListenerRegistration stocklistener;

    private Items item;
    private String ShopCode;

    FloatingActionButton debug;

    RecyclerView recyclerView;
    RecyclerView.LayoutManager mLayoutManager;
    ItemStockAdapter itemStockAdapter;

    LinkedHashMap<String, LocationStockItem> locationStockItems;

    FirebaseFirestore firebaseFirestore;

    public ItemStock() {
        // Required empty public constructor
    }


    public static ItemStock newInstance(Items item, String param3) {
        ItemStock fragment = new ItemStock();
        Bundle args = new Bundle();
        args.putSerializable(ARG_PARAM1, item);
        args.putString(ARG_PARAM3, param3);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            item = (Items) getArguments().getSerializable(ARG_PARAM1);
            ShopCode = getArguments().getString(ARG_PARAM3);
        }
        if(locationStockItems == null) {
            locationStockItems = new LinkedHashMap<>();
        }
        firebaseFirestore = FirebaseFirestore.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_recycler_view, container, false);
        recyclerView = view.findViewById(R.id.recyclerView);
        debug = view.findViewById(R.id.debug);
        mLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(mLayoutManager);
        itemStockAdapter = new ItemStockAdapter(getContext(),this,locationStockItems,
                Float.parseFloat(item.Reorder_Lvl),Float.parseFloat(item.Excess_LvL));
        recyclerView.setAdapter(itemStockAdapter);
        attachDatabaseListener();
        debug.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                debugfun();
            }
        });
        return view;
    }

    private void debugfun() {
        Toast.makeText(getContext(),String.valueOf(locationStockItems.size()),Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onStop() {
        stocklistener.remove();
        super.onStop();
    }

    private void attachDatabaseListener() {
        stocklistener =
        firebaseFirestore.collection(ShopCode).document("doc").collection("Location")
                .whereEqualTo("ItemCode",item.ItemCode).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException error) {
                if (queryDocumentSnapshots != null && !queryDocumentSnapshots.isEmpty()) {
                    for (DocumentChange doc : queryDocumentSnapshots.getDocumentChanges()) {
                        LocationStockItem locationStockItem;
                        switch (doc.getType()) {
                            case ADDED:
                                locationStockItem = doc.getDocument().toObject(LocationStockItem.class);
                                locationStockItems.put(locationStockItem.LocationCode,locationStockItem);
                                itemStockAdapter.notifyDataSetChanged();
                                break;
                            case MODIFIED:
                                locationStockItem = doc.getDocument().toObject(LocationStockItem.class);
                                locationStockItems.remove(locationStockItem.LocationCode);
                                locationStockItems.put(locationStockItem.LocationCode,locationStockItem);
                                itemStockAdapter.notifyDataSetChanged();
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
