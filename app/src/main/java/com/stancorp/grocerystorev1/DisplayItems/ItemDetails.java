package com.stancorp.grocerystorev1.DisplayItems;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.stancorp.grocerystorev1.Classes.ItemStockInfo;
import com.stancorp.grocerystorev1.Classes.Items;
import com.stancorp.grocerystorev1.Classes.LocationStockItem;
import com.stancorp.grocerystorev1.GlobalClass.Gfunc;
import com.stancorp.grocerystorev1.R;

public class ItemDetails extends Fragment {

    FirebaseFirestore firebaseFirestore;

    Items item;
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String ARG_PARAM3 = "param3";
    private static final String ARG_PARAM4 = "param4";
    private static final String ARG_PARAM5 = "param5";
    TextView ItemName;
    TextView ItemCode;
    TextView ItemCategory;
    TextView ItemBrand;
    TextView BalanceQtyText;
    TextView StockType;
    TextView Validity;
    TextView ItemSellingPrice;
    TextView ItemPurchasePrice;
    TextView locationtexttag;

    //Itemstocklayout
    TextView LocationCode;
    TextView Reorderquantity;
    TextView rlvl;
    TextView elvl;
    TextView blvl;
    ImageView WarningImg;
    TextView WarningText;
    View ItemstockLayout;

    Gfunc gfunc;

    private Items items;
    private ItemStockInfo itemStockInfo;
    private String UserPermission;
    private String UserLocation;
    private String ShopCode;

    public ItemDetails() {
        // Required empty public constructor
    }


    // TODO: Rename and change types and number of parameters
    public static ItemDetails newInstance(Items item, ItemStockInfo itemStockInfo,String UserPermission,
                                          String UserLocation,String ShopCode) {
        ItemDetails fragment = new ItemDetails();
        Bundle args = new Bundle();
        args.putSerializable(ARG_PARAM1, item);
        args.putSerializable(ARG_PARAM2,itemStockInfo);
        args.putString(ARG_PARAM3,UserPermission);
        args.putString(ARG_PARAM4,UserLocation);
        args.putString(ARG_PARAM5,ShopCode);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            items = (Items) getArguments().getSerializable(ARG_PARAM1);
            itemStockInfo = (ItemStockInfo) getArguments().getSerializable(ARG_PARAM2);
            UserPermission = getArguments().getString(ARG_PARAM3);
            UserLocation = getArguments().getString(ARG_PARAM4);
            ShopCode = getArguments().getString(ARG_PARAM5);
        }
        gfunc = new Gfunc();
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

        ItemstockLayout = view.findViewById(R.id.itemstocklayout);
        LocationCode = ItemstockLayout.findViewById(R.id.LocationCode);
        Reorderquantity = ItemstockLayout.findViewById(R.id.ReorderQuantity);
        rlvl = ItemstockLayout.findViewById(R.id.Reorderlvl);
        elvl = ItemstockLayout.findViewById(R.id.Excesslvl);
        blvl = ItemstockLayout.findViewById(R.id.Balancelvl);
        WarningImg = ItemstockLayout.findViewById(R.id.Warningimg);
        WarningText = ItemstockLayout.findViewById(R.id.Warningtext);

        View locationtag = view.findViewById(R.id.locationtag);
        locationtexttag = locationtag.findViewById(R.id.tag);

        item = new Items(items);

        SetTextViews();
        setitemstockinfo(itemStockInfo);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if(UserPermission.compareTo("Employee")==0){
            fetchlocationstockbalance(view);
        }else{
            ItemstockLayout.setVisibility(View.GONE);
        }
    }

    private void fetchlocationstockbalance(final View view) {
        firebaseFirestore.collection(ShopCode).document("doc").collection("Location")
                .document(item.ItemCode+UserLocation).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(!task.getResult().exists()){
                    WarningImg.setBackground(ContextCompat.getDrawable(view.getContext(), R.drawable.circle_grey));
                    blvl.setTextColor(ContextCompat.getColor(view.getContext(), R.color.Edittext));
                    WarningText.setText("Item Stock not available");
                } else {
                    LocationStockItem locationStockItem = task.getResult().toObject(LocationStockItem.class);
                    Float Reorderlvl = Float.parseFloat(item.Reorder_Lvl);
                    Float Balance = Float.parseFloat(locationStockItem.Balance_Qty);
                    Float Excesslvl = Float.parseFloat(item.Excess_LvL);
                    if(Reorderlvl > Balance || Balance > Excesslvl){
                        WarningImg.setBackground(ContextCompat.getDrawable(view.getContext(), R.drawable.circle_red));
                        blvl.setTextColor(ContextCompat.getColor(view.getContext(), R.color.Red));
                        WarningText.setText("Balance quantity present at optimum Level");
                    }else {
                        WarningImg.setBackground(ContextCompat.getDrawable(view.getContext(), R.drawable.circle_green));
                        blvl.setTextColor(ContextCompat.getColor(view.getContext(),R.color.green));
                        if(Reorderlvl >= Balance)
                            WarningText.setText("Item needs to be restocked");
                        if(Balance > Excesslvl)
                            WarningText.setText("Item is present at an excess level");
                    }
                    LocationCode.setText(locationStockItem.LocationCode);
                    Reorderquantity.setText(locationStockItem.Reorder_Qty);
                    if(UserPermission.compareTo("Admin")==0){
                        locationtexttag.setText("All locations");
                    }else{
                        locationtexttag.setText(locationStockItem.LocationCode);
                    }
                    rlvl.setText(String.valueOf(Reorderlvl));
                    elvl.setText(String.valueOf(Excesslvl));
                    blvl.setText(String.valueOf(Balance));
                }
            }
        });
    }

    private void SetTextViews() {

        ItemName.setText(item.name);
        ItemCode.setText(item.ItemCode);
        ItemCategory.setText(item.Category);
        ItemBrand.setText(item.Brand);
        locationtexttag.setText("All locations");
        StockType.setText(item.Stock_Type);
        if(item.Valid) {
            Validity.setText("Valid");
            Validity.setTextColor(ContextCompat.getColor(getActivity(),R.color.green));
        }else{
            Validity.setText("Invalid");
            Validity.setTextColor(ContextCompat.getColor(getActivity(),R.color.Red));
        }
        ItemSellingPrice.setText(String.valueOf(item.Selling_Price) + " INR\n" + "per " + item.Unit);
    }

    private void setitemstockinfo(ItemStockInfo itemStockInfo) {
        if (itemStockInfo != null) {
            BalanceQtyText.setText(itemStockInfo.Total_Balance_Quantity + " " + item.Unit);

            Float T_bal = Float.parseFloat(itemStockInfo.Total_Balance_Quantity);
            Float T_Price = Float.parseFloat(itemStockInfo.Total_Price);
            Float p_price;
            if (T_bal != 0) {
                p_price = T_Price / T_bal;
            } else
                p_price = (float) 0;
            gfunc.roundof(p_price,2);
            ItemPurchasePrice.setText(String.valueOf(gfunc.roundof(p_price,2)) + " INR\n" + "per " + item.Unit);
        }
    }

}