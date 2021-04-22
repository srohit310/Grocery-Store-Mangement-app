package com.stancorp.grocerystorev1.ViewFragments;

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import androidx.annotation.Nullable;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.stancorp.grocerystorev1.AddActivities.AddItemActivity;
import com.stancorp.grocerystorev1.Classes.Items;
import com.stancorp.grocerystorev1.AdapterClasses.ItemAdaptor;
import com.stancorp.grocerystorev1.DisplayItems.ItemViewActivity;
import com.stancorp.grocerystorev1.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;

public class FragmentGroupItems extends FragmentsGroups {

    LinkedHashMap<String,Items> items;
    FirebaseFirestore firebaseFirestore;
    ItemAdaptor itemAdaptor;
    LinkedHashMap<String,Items> filteredList;

    @Override
    protected void initialize() {
        items = new LinkedHashMap<>();
        firebaseFirestore = FirebaseFirestore.getInstance();
        filteredList = new LinkedHashMap<>();
        itemAdaptor = new ItemAdaptor(items,getContext(),this);
        recyclerView.setAdapter(itemAdaptor);
        sortby = "name";
    }

    @Override
    protected void setupSpinner(View view) {

        ArrayAdapter itemsortoptions = ArrayAdapter.createFromResource(getContext(),
                R.array.array_item_sort_options, R.layout.spinner_user_item_text);

        itemsortoptions.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        spinner.setAdapter(itemsortoptions);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selection = parent.getItemAtPosition(position).toString();
                if (!TextUtils.isEmpty(selection)) {
                    if (selection.equals(getString(R.string.item_name))) {
                        sortby = "name";
                    } else if (selection.equals(getString(R.string.S_Price))) {
                        sortby = "Selling_Price";
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                sortby = "name";
            }
        });
    }

    @Override
    protected void AddIntent() {
        Intent intent = new Intent(getContext(), AddItemActivity.class);
        intent.putExtra("ShopCode",user.ShopCode);
        intent.putExtra("Mode","Add");
        startActivity(intent);
    }

    @Override
    protected void attachListData(String sortby) {
        SDProgress(true);
        items.clear();
        filteredList.clear();

        Query query = firebaseFirestore.collection(user.ShopCode).document("doc").collection("Items")
                .orderBy(sortby, direction);
        query.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException error) {
                if (queryDocumentSnapshots != null && !queryDocumentSnapshots.isEmpty()) {
                    for (DocumentChange doc : queryDocumentSnapshots.getDocumentChanges()){
                        Items item;
                        switch (doc.getType()){
                            case ADDED:
                                item =  doc.getDocument().toObject(Items.class);
                                items.put(item.ItemCode,item);
                                filteredList.put(item.ItemCode,item);
                                itemAdaptor.notifyDataSetChanged();
                                break;
                            case MODIFIED:
                                item =  doc.getDocument().toObject(Items.class);
                                items.remove(item.ItemCode);
                                filteredList.remove(item.ItemCode);
                                items.put(item.ItemCode,item);
                                filteredList.put(item.ItemCode,item);
                                itemAdaptor.notifyDataSetChanged();
                                break;
                            case REMOVED:
                                item =  doc.getDocument().toObject(Items.class);
                                items.remove(item.ItemCode);
                                filteredList.remove(item.ItemCode);
                                itemAdaptor.notifyDataSetChanged();
                                break;
                        }
                    }
                    SDProgress(false);
                }else{
                    SDProgress(false);
                }
            }
        });
    }

    @Override
    protected void filteredlistcondition(String text) {
        filteredList = new LinkedHashMap<>();
        for(Items item : items.values() ){
            if(item.name.toLowerCase().startsWith(text.toLowerCase())){
                filteredList.put(item.ItemCode,item);
            }
        }
        itemAdaptor.filterList(filteredList);
    }

    @Override
    protected void displayIntent(int position) {
        Intent intent = new Intent(getContext(),ItemViewActivity.class);
        Items item = (Items) filteredList.values().toArray()[position];
        intent.putExtra("Item",item);
        intent.putExtra("ShopCode",user.ShopCode);
        intent.putExtra("UserName",user.Name);
        startActivity(intent);
    }
}
