package com.stancorp.grocerystorev1.ViewFragments;

import android.content.Intent;
import android.content.res.Configuration;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;

import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.stancorp.grocerystorev1.AddActivities.AddItemActivity;
import com.stancorp.grocerystorev1.Classes.Items;
import com.stancorp.grocerystorev1.AdapterClasses.ItemAdaptor;
import com.stancorp.grocerystorev1.DisplayItems.ItemViewActivity;
import com.stancorp.grocerystorev1.R;

import java.util.LinkedHashMap;

public class FragmentGroupItems extends FragmentsGroups {

    LinkedHashMap<String, Items> items;
    FirebaseFirestore firebaseFirestore;
    ItemAdaptor itemAdaptor;
    ListenerRegistration itemlistener;
    Boolean valid;
    LinkedHashMap<String, Items> filteredList;

    @Override
    protected void toolbarspinnersetup(Spinner toolbarspinner) {
        ArrayAdapter itemfilteroptions = ArrayAdapter.createFromResource(getContext(),
                R.array.array_item_filter_options, R.layout.spinner_item_text);

        itemfilteroptions.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        toolbarspinner.setAdapter(itemfilteroptions);
        toolbarspinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selection = parent.getItemAtPosition(position).toString();
                if (!TextUtils.isEmpty(selection)) {
                    if (selection.equals(getString(R.string.valid_items))) {
                        valid = true;
                        attachListData(startcode, endcode);
                    } else if (selection.equals(getString(R.string.invalid_items))) {
                        valid = false;
                        attachListData(startcode, endcode);
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                filterby = "valid";
            }
        });
    }

    @Override
    protected void initialize() {
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        items = new LinkedHashMap<>();
        searchedittext.setHint("Search for item using name");
        firebaseFirestore = FirebaseFirestore.getInstance();
        filteredList = new LinkedHashMap<>();
        itemAdaptor = new ItemAdaptor(items, getContext(), this, user.ShopCode);
        recyclerView.setAdapter(itemAdaptor);
        valid = true;
    }

    @Override
    protected void AddIntent() {
        Intent intent = new Intent(getContext(), AddItemActivity.class);
        intent.putExtra("ShopCode", user.ShopCode);
        intent.putExtra("Mode", "Add");
        startActivity(intent);
    }

    @Override
    public void onPause() {
        itemlistener.remove();
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        startcode = "!";
        endcode = "{";
        if(items!=null) {
            attachListData(startcode, endcode);
        }
    }

    @Override
    protected void attachListData(String startcode, String endcode) {
        SDProgress(true);
        items.clear();
        filteredList.clear();
        itemAdaptor.notifyDataSetChanged();
        itemlistener =
                firebaseFirestore.collection(user.ShopCode).document("doc").collection("Items")
                        .whereEqualTo("Valid", valid).whereGreaterThanOrEqualTo("name", startcode)
                        .whereLessThan("name", endcode).orderBy("name", direction).limit(50)
                        .addSnapshotListener(new EventListener<QuerySnapshot>() {
                            @Override
                            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException error) {
                                if (error != null) {
                                    Log.i("failfirestore", error.getMessage());
                                }
                                if (queryDocumentSnapshots != null && !queryDocumentSnapshots.isEmpty()) {
                                    for (DocumentChange doc : queryDocumentSnapshots.getDocumentChanges()) {
                                        Items item;
                                        switch (doc.getType()) {
                                            case ADDED:
                                                item = doc.getDocument().toObject(Items.class);
                                                items.put(item.ItemCode, item);
                                                filteredList.put(item.ItemCode, item);
                                                itemAdaptor.notifyDataSetChanged();
                                                break;
                                            case MODIFIED:
                                                item = doc.getDocument().toObject(Items.class);
                                                items.remove(item.ItemCode);
                                                filteredList.remove(item.ItemCode);
                                                items.put(item.ItemCode, item);
                                                filteredList.put(item.ItemCode, item);
                                                itemAdaptor.notifyDataSetChanged();
                                                break;
                                            case REMOVED:
                                                item = doc.getDocument().toObject(Items.class);
                                                items.remove(item.ItemCode);
                                                filteredList.remove(item.ItemCode);
                                                itemAdaptor.notifyDataSetChanged();
                                                break;
                                        }
                                    }
                                    SDProgress(false);
                                } else {
                                    SDProgress(false);
                                }
                            }
                        });
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 4));
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        }
    }

    @Override
    protected void displayIntent(int position) {
        Intent intent = new Intent(getContext(), ItemViewActivity.class);
        Items item = (Items) filteredList.values().toArray()[position];
        intent.putExtra("Item", item);
        intent.putExtra("ShopCode", user.ShopCode);
        intent.putExtra("UserName", user.Name);
        startActivity(intent);
    }
}
