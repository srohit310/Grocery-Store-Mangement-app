package com.stancorp.grocerystorev1.ViewFragments;

import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QuerySnapshot;
import com.stancorp.grocerystorev1.Classes.Location;
import com.stancorp.grocerystorev1.AddActivities.AddlocationActivity;
import com.stancorp.grocerystorev1.AdapterClasses.LocationAdapter;
import com.stancorp.grocerystorev1.R;

import java.util.LinkedHashMap;


public class FragmentGroupsLocations extends FragmentsGroups {

    LinkedHashMap<String, Location> locations;
    LocationAdapter locationAdapter;
    ListenerRegistration locationlistener;

    @Override
    protected void toolbarspinnersetup(Spinner toolbarspinner) {
        toolbarspinner.setVisibility(View.GONE);
    }

    @Override
    protected void initialize() {
        locations = new LinkedHashMap<>();
        searchedittext.setHint("Search for location using name");
        firebaseFirestore = FirebaseFirestore.getInstance();
        locationAdapter = new LocationAdapter(locations, this, getContext());
        recyclerView.setAdapter(locationAdapter);
        attachListData(startcode, endcode);
    }

    @Override
    protected void AddIntent() {
        Intent intent = new Intent(getContext(), AddlocationActivity.class);
        intent.putExtra("ShopCode", user.ShopCode);
        intent.putExtra("Mode","Add");
        startActivity(intent);
    }

    @Override
    public void onResume() {
        super.onResume();
        startcode = "!";
        endcode = "{";
        if(locations!=null) {
            attachListData(startcode, endcode);
        }
    }

    @Override
    public void onPause() {
        locationlistener.remove();
        super.onPause();
    }

    @Override
    protected void attachListData(String startcode, String endcode) {
        locations.clear();
        locationlistener =
        firebaseFirestore.collection(user.ShopCode).document("doc").collection("LocationDetails")
                .whereGreaterThanOrEqualTo("name", startcode).whereLessThan("name", endcode)
                .limit(20).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Log.i("failfirestore", error.getMessage());
                }
                if (queryDocumentSnapshots != null && !queryDocumentSnapshots.isEmpty()) {
                    for (DocumentChange doc : queryDocumentSnapshots.getDocumentChanges()) {
                        Location location;
                        switch (doc.getType()) {
                            case ADDED:
                                location = doc.getDocument().toObject(Location.class);
                                locations.put(location.code, location);
                                locationAdapter.notifyDataSetChanged();
                                break;
                            case MODIFIED:
                                location = doc.getDocument().toObject(Location.class);
                                locations.put(location.code, location);
                                locationAdapter.notifyDataSetChanged();
                                break;
                            case REMOVED:
                                location = doc.getDocument().toObject(Location.class);
                                locations.remove(location.code);
                                locationAdapter.notifyDataSetChanged();
                                break;
                        }
                        recyclerView.scheduleLayoutAnimation();
                    }
                }
                SDProgress(false);
            }
        });
    }

    @Override
    protected void displayIntent(int position) {
        Location location = (Location) locations.values().toArray()[position];
        Intent intent = new Intent(getContext(), AddlocationActivity.class);
        intent.putExtra("ShopCode", user.ShopCode);
        intent.putExtra("Mode","Edit");
        intent.putExtra("LocationDetails",location);
        startActivity(intent);
    }
}
