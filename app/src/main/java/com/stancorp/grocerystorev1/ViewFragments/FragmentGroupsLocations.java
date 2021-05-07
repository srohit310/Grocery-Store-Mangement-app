package com.stancorp.grocerystorev1.ViewFragments;

import android.content.Intent;
import android.view.View;
import android.widget.Spinner;

import androidx.paging.PagedList;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.firebase.ui.firestore.paging.FirestorePagingOptions;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.stancorp.grocerystorev1.AdapterClasses.LocationFirestoreAdapter;
import com.stancorp.grocerystorev1.Classes.Location;
import com.stancorp.grocerystorev1.AddActivities.AddlocationActivity;


public class FragmentGroupsLocations extends FragmentsGroups {

    LocationFirestoreAdapter locationAdapter;

    @Override
    protected void toolbarspinnersetup(Spinner toolbarspinner) {
        toolbarspinner.setVisibility(View.GONE);
    }

    @Override
    protected void initialize() {
        searchedittext.setHint("Search for location using name");
        firebaseFirestore = FirebaseFirestore.getInstance();
        recyclerView.setAdapter(locationAdapter);
        startcode = "!";
        endcode = "{";
        attachListData(startcode, endcode);
    }

    @Override
    protected void AddIntent() {
        Intent intent = new Intent(getContext(), AddlocationActivity.class);
        intent.putExtra("ShopCode", user.ShopCode);
        intent.putExtra("Mode", "Add");
        startActivity(intent);
    }

    @Override
    public void onStop() {
        super.onStop();
        locationAdapter.stopListening();
    }

    @Override
    public void onStart() {
        super.onStart();
        if (locationAdapter != null)
            attachListData(startcode,endcode);
    }

    @Override
    protected void attachListData(String startcode, String endcode) {
        Query query = firebaseFirestore.collection(user.ShopCode).document("doc").collection("LocationDetails")
                .whereGreaterThanOrEqualTo("name", startcode).whereLessThan("name", endcode)
                .limit(20);

        PagedList.Config config = new PagedList.Config.Builder()
                .setInitialLoadSizeHint(10)
                .setPageSize(3)
                .build();

        FirestorePagingOptions<Location> options = new FirestorePagingOptions.Builder<Location>()
                .setQuery(query, config, Location.class)
                .build();

        locationAdapter = new LocationFirestoreAdapter(options, getContext(), this, progressLayout, emptyview);
        locationAdapter.startListening();
        locationAdapter.notifyDataSetChanged();
        recyclerView.setAdapter(locationAdapter);
        recyclerView.scheduleLayoutAnimation();
    }

    @Override
    protected void displayFirestoreIntent(DocumentSnapshot documentSnapshot, int position) {
        Location location = (Location) documentSnapshot.toObject(Location.class);
        Intent intent = new Intent(getContext(), AddlocationActivity.class);
        intent.putExtra("ShopCode", user.ShopCode);
        intent.putExtra("Mode", "Edit");
        intent.putExtra("LocationDetails", location);
        startActivity(intent);

    }
}
