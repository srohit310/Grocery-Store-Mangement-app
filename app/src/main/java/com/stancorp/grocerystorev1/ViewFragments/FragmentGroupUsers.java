package com.stancorp.grocerystorev1.ViewFragments;

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.paging.PagedList;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.firebase.ui.firestore.paging.FirestorePagingOptions;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;
import com.stancorp.grocerystorev1.AdapterClasses.ManageUserFirestoreAdapter;
import com.stancorp.grocerystorev1.AddActivities.AddUsersActivity;
import com.stancorp.grocerystorev1.Classes.StoreUser;
import com.stancorp.grocerystorev1.R;

public class FragmentGroupUsers extends FragmentsGroups {

    ManageUserFirestoreAdapter manageUserAdapter;
    String Permission;
    Boolean Valid;

    @Override
    protected void toolbarspinnersetup(Spinner toolbarspinner) {
        ArrayAdapter itemfilteroptions = ArrayAdapter.createFromResource(getContext(),
                R.array.user_options, R.layout.spinner_item_text);

        itemfilteroptions.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        toolbarspinner.setAdapter(itemfilteroptions);
        toolbarspinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selection = parent.getItemAtPosition(position).toString();
                startcode = "!";
                endcode = "{";
                if (!TextUtils.isEmpty(selection)) {
                    if (selection.equals(getString(R.string.valid_admin))) {
                        Permission = "Admin";
                        Valid = true;
                        attachListData(startcode, endcode);
                    } else if (selection.equals(getString(R.string.valid_employee))) {
                        Permission = "Employee";
                        Valid = true;
                        attachListData(startcode, endcode);
                    } else if(selection.equals(getString(R.string.invalid_admin))){
                        Permission = "Admin";
                        Valid = false;
                        attachListData(startcode, endcode);
                    } else if(selection.equals(getString(R.string.invalid_employee))){
                        Permission = "Employee";
                        Valid = false;
                        attachListData(startcode, endcode);
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });
    }

    @Override
    protected void initialize() {
        searchedittext.setHint("Search for purchase using referenceid");
        Permission = "Admin";
        Valid = true;
    }

    @Override
    protected void AddIntent() {
        Intent intent = new Intent(getContext(), AddUsersActivity.class);
        intent.putExtra("ShopCode", user.ShopCode);
        intent.putExtra("Mode", "Add");
        startActivity(intent);
    }

    @Override
    public void onStop() {
        super.onStop();
        manageUserAdapter.stopListening();
    }

    @Override
    public void onStart() {
        super.onStart();
        if (manageUserAdapter != null)
            attachListData(startcode,endcode);
    }

    @Override
    protected void attachListData(String startcode, String endcode) {
        SDProgress(true);
        Query query =  firebaseFirestore.collection("UserDetails").whereEqualTo("ShopCode", user.ShopCode)
                        .whereGreaterThanOrEqualTo("Name", startcode).whereLessThan("Name", endcode)
                        .whereEqualTo("PermissionLevel", Permission).whereEqualTo("valid",Valid).limit(50);

        PagedList.Config config = new PagedList.Config.Builder()
                .setInitialLoadSizeHint(6)
                .setPageSize(3)
                .build();

        FirestorePagingOptions<StoreUser> options = new FirestorePagingOptions.Builder<StoreUser>()
                .setQuery(query, config, StoreUser.class)
                .build();

        manageUserAdapter = new ManageUserFirestoreAdapter(options, getContext(), this, progressLayout, emptyview);
        manageUserAdapter.startListening();
        manageUserAdapter.notifyDataSetChanged();
        recyclerView.setAdapter(manageUserAdapter);
        recyclerView.scheduleLayoutAnimation();
    }

    @Override
    protected void displayFirestoreIntent(DocumentSnapshot documentSnapshot, int posiiton) {
        StoreUser tempuser = documentSnapshot.toObject(StoreUser.class);
        if(tempuser.Email.compareToIgnoreCase(user.Email)==0){
            Toast.makeText(getContext(),"Edit details by accessing user settings",Toast.LENGTH_SHORT).show();
            return;
        }
        Intent intent = new Intent(getContext(), AddUsersActivity.class);
        intent.putExtra("ShopCode", tempuser.ShopCode);
        intent.putExtra("Mode", "Edit");
        intent.putExtra("UserEmail", tempuser.Email);
        startActivity(intent);
    }
}
