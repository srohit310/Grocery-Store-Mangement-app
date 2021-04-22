package com.stancorp.grocerystorev1.ViewFragments;

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.stancorp.grocerystorev1.Classes.Location;
import com.stancorp.grocerystorev1.AddActivities.AddlocationActivity;
import com.stancorp.grocerystorev1.AdapterClasses.LocationAdapter;
import com.stancorp.grocerystorev1.R;
import java.util.LinkedHashMap;


public class FragmentGroupsLocations extends FragmentsGroups {

    LinkedHashMap<String,Location> locations;
    LocationAdapter locationAdapter;
    LinkedHashMap<String,Location> filteredList;

    @Override
    protected void initialize() {
        locations = new LinkedHashMap<>();
        firebaseFirestore = FirebaseFirestore.getInstance();
        filteredList = new LinkedHashMap<>();
        locationAdapter = new LocationAdapter(locations,this,getContext());
        recyclerView.setAdapter(locationAdapter);
        sortby = "codeno";
    }

    @Override
    protected void setupSpinner(View view) {

        ArrayAdapter itemsortoptions = ArrayAdapter.createFromResource(getContext(),
                R.array.array_item_search_options, R.layout.spinner_user_item_text);

        itemsortoptions.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        spinner.setAdapter(itemsortoptions);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selection = parent.getItemAtPosition(position).toString();
                if (!TextUtils.isEmpty(selection)) {
                    if (selection.equals(getString(R.string.item_name))) {
                        sortby = "name";
                    } else if (selection.equals(getString(R.string.item_code))) {
                        sortby = "codeno";
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
        Intent intent = new Intent(getContext(), AddlocationActivity.class);
        intent.putExtra("ShopCode",user.ShopCode);
        startActivity(intent);
    }

    @Override
    protected void attachListData(String sortby) {
        locations.clear();
        filteredList.clear();
        firebaseFirestore.collection(user.ShopCode).document("doc").collection("LocationDetails")
                .orderBy(sortby, direction).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException error) {
                if (queryDocumentSnapshots != null && !queryDocumentSnapshots.isEmpty()) {
                    for (DocumentChange doc : queryDocumentSnapshots.getDocumentChanges()){
                        Location location;
                        switch (doc.getType()){
                            case ADDED:
                                location = doc.getDocument().toObject(Location.class);
                                locations.put(location.code,location);
                                filteredList.put(location.code,location);
                                locationAdapter.notifyDataSetChanged();
                                break;
                            case MODIFIED:
                                location = doc.getDocument().toObject(Location.class);
                                locations.remove(location.code);
                                locations.put(location.code,location);
                                filteredList.remove(location.code);
                                filteredList.put(location.code,location);
                                locationAdapter.notifyDataSetChanged();
                                break;
                            case REMOVED:
                                location = doc.getDocument().toObject(Location.class);
                                locations.remove(location.code);
                                filteredList.remove(location.code);
                                locationAdapter.notifyDataSetChanged();
                                break;
                        }
                    }
                }
                SDProgress(false);
            }
        });
    }

    @Override
    protected void filteredlistcondition(String text) {
        filteredList = new LinkedHashMap<>();
        for(Location location : locations.values() ){
            if(location.name.toLowerCase().startsWith(text.toLowerCase())){
                filteredList.put(location.code,location);
            }
        }
        locationAdapter.filterList(filteredList);
    }

    @Override
    protected void displayIntent(int position) {

    }
}
