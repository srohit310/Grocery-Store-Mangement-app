package com.stancorp.grocerystorev1.DisplayItems;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.stancorp.grocerystorev1.Classes.ItemStockInfo;
import com.stancorp.grocerystorev1.Classes.Items;
import com.stancorp.grocerystorev1.R;

public class ItemDetails extends Fragment {

    FirebaseFirestore firebaseFirestore;

    Items item;
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    TextView ItemName;
    TextView ItemCode;
    TextView ItemCategory;
    TextView ItemBrand;
    TextView BalanceQtyText;
    TextView StockType;
    TextView Validity;
    TextView ItemSellingPrice;
    TextView ItemPurchasePrice;

    private Items mParam1;
    private String mParam2;
    private ItemStockInfo itemStockInfo;

    public ItemDetails() {
        // Required empty public constructor
    }


    // TODO: Rename and change types and number of parameters
    public static ItemDetails newInstance(Items param1, String param2) {
        ItemDetails fragment = new ItemDetails();
        Bundle args = new Bundle();
        args.putSerializable(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = (Items) getArguments().getSerializable(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        firebaseFirestore = FirebaseFirestore.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_item_details, container, false);
        ItemName = view.findViewById(R.id.DetailsItemNameData);
        ItemCode = view.findViewById(R.id.DetailsItemCodeData);
        ItemCategory = view.findViewById(R.id.DetailsItemCategoryData);
        ItemBrand = view.findViewById(R.id.DetailsItemBrandData);
        BalanceQtyText = view.findViewById(R.id.BalanceQtyText);
        StockType = view.findViewById(R.id.StockType);
        Validity = view.findViewById(R.id.ValidityInfo);
        ItemSellingPrice = view.findViewById(R.id.DetailsItemSPData);
        ItemPurchasePrice = view.findViewById(R.id.DetailsItemPPData);

        item = new Items(mParam1);

        SetTextViews();

        attachdatabaseListener();

        return view;
    }

    private void SetTextViews() {

        ItemName.setText(item.name);
        ItemCode.setText(item.ItemCode);
        ItemCategory.setText(item.Category);
        ItemBrand.setText(item.Brand);
        StockType.setText(item.Stock_Type);
        if(item.Valid) {
            Validity.setText("Valid");
            Validity.setTextColor(ContextCompat.getColor(getContext(),R.color.green));
        }else{
            Validity.setText("Invalid");
            Validity.setTextColor(ContextCompat.getColor(getContext(),R.color.Red));
        }
        ItemSellingPrice.setText(String.valueOf(item.Selling_Price) + " INR\n" + "per " + item.Unit);
    }

    private void attachdatabaseListener() {

        firebaseFirestore.collection(mParam2).document("doc").collection("ItemStockInfo")
                .whereEqualTo("ItemCode",mParam1.ItemCode).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if(value!=null && !value.isEmpty()){
                    for (DocumentChange doc : value.getDocumentChanges()){
                        switch (doc.getType()){
                            case ADDED:
                                setitemstockinfo(doc.getDocument());
                                break;
                            case MODIFIED:
                                setitemstockinfo(doc.getDocument());
                                break;
                        }
                    }
                }
            }
        });
        firebaseFirestore.collection(mParam2).document("doc").collection("Items")
                .whereEqualTo("ItemCode",mParam1.ItemCode).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if(value!=null && !value.isEmpty()){
                    for (DocumentChange doc : value.getDocumentChanges()){
                        switch (doc.getType()){
                            case MODIFIED:
                                item = doc.getDocument().toObject(Items.class);
                                SetTextViews();
                                break;
                        }
                    }
                }
            }
        });
    }

    private void setitemstockinfo(DocumentSnapshot snapshot) {
        itemStockInfo = snapshot.toObject(ItemStockInfo.class);
        if (itemStockInfo != null) {
            BalanceQtyText.setText(itemStockInfo.Total_Balance_Quantity + " " + item.Unit + " (All Locations)");
            Float T_bal = Float.parseFloat(itemStockInfo.Total_Balance_Quantity);
            Float T_Price = Float.parseFloat(itemStockInfo.Total_Price);
            Float p_price;
            if (T_bal != 0) {
                p_price = T_Price / T_bal;
            } else
                p_price = (float) 0;
            ItemPurchasePrice.setText(String.valueOf(p_price) + " INR\n" + "per " + item.Unit + "\n" + "( Weighted Average )");
        }
    }

}