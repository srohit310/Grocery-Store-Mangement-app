package com.stancorp.grocerystorev1.ViewFragments;

import android.content.Intent;
import android.content.res.Configuration;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.paging.PagedList;
import androidx.recyclerview.widget.GridLayoutManager;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.firebase.ui.firestore.paging.FirestorePagingOptions;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.stancorp.grocerystorev1.AdapterClasses.FirestoreBaseRecyclerAdapter;
import com.stancorp.grocerystorev1.AdapterClasses.ItemFirestoreAdapter;
import com.stancorp.grocerystorev1.AddActivities.AddItemActivity;
import com.stancorp.grocerystorev1.Classes.Items;
import com.stancorp.grocerystorev1.DisplayItems.ItemViewActivity;
import com.stancorp.grocerystorev1.R;

public class FragmentGroupItems extends FragmentsGroups implements FirestoreBaseRecyclerAdapter.OnNoteListner {

    FirebaseFirestore firebaseFirestore;
    ItemFirestoreAdapter itemAdaptor;
    Boolean valid;

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
                    startcode = "!";
                    endcode = "{";
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
        valid = true;
        searchedittext.setHint("Search for item using name");
        firebaseFirestore = FirebaseFirestore.getInstance();
        if (user.PermissionLevel.compareTo("Employee") == 0) {
            mfloat.setVisibility(View.GONE);
        }
    }

    @Override
    protected void AddIntent() {
        Intent intent = new Intent(getContext(), AddItemActivity.class);
        intent.putExtra("ShopCode", user.ShopCode);
        intent.putExtra("Mode", "Add");
        startActivity(intent);
    }

    @Override
    public void onStop() {
        super.onStop();
        itemAdaptor.stopListening();
    }

    @Override
    public void onStart() {
        super.onStart();
        if (itemAdaptor != null)
            itemAdaptor.startListening();
    }

    @Override
    protected void attachListData(String startcode, String endcode) {
        SDProgress(true);
        Query query = firebaseFirestore.collection(user.ShopCode).document("doc").collection("Items")
                .whereEqualTo("Valid", valid).whereGreaterThanOrEqualTo("name", startcode)
                .whereLessThan("name", endcode).orderBy("name", direction).limit(50);

        PagedList.Config config = new PagedList.Config.Builder()
                .setInitialLoadSizeHint(10)
                .setPageSize(3)
                .build();

        FirestorePagingOptions<Items> options = new FirestorePagingOptions.Builder<Items>()
                .setQuery(query, config , Items.class)
                .build();

        itemAdaptor = new ItemFirestoreAdapter(options, getContext(), this, progressLayout);
        itemAdaptor.startListening();
        itemAdaptor.notifyDataSetChanged();
        recyclerView.setAdapter(itemAdaptor);
        recyclerView.scheduleLayoutAnimation();
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
    protected void displayFirestoreIntent(DocumentSnapshot documentSnapshot, int position) {
        Intent intent = new Intent(getContext(), ItemViewActivity.class);
        Items item = (Items) documentSnapshot.toObject(Items.class);
        intent.putExtra("Item", item);
        intent.putExtra("ShopCode", user.ShopCode);
        intent.putExtra("UserName", user.Name);
        intent.putExtra("UserPermission", user.PermissionLevel);
        intent.putExtra("UserLocation", user.Location);
        startActivity(intent);
    }
}
