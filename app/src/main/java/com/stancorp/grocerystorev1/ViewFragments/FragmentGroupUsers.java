package com.stancorp.grocerystorev1.ViewFragments;

import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QuerySnapshot;
import com.stancorp.grocerystorev1.AdapterClasses.ManageUserAdapter;
import com.stancorp.grocerystorev1.AdapterClasses.TransactionAdapter;
import com.stancorp.grocerystorev1.AddActivities.AddItemActivity;
import com.stancorp.grocerystorev1.AddActivities.AddUsersActivity;
import com.stancorp.grocerystorev1.Classes.StoreTransaction;
import com.stancorp.grocerystorev1.Classes.StoreUser;
import com.stancorp.grocerystorev1.R;

import java.util.LinkedHashMap;

public class FragmentGroupUsers extends FragmentsGroups {

    LinkedHashMap<String, StoreUser> users;
    FirebaseFirestore firebaseFirestore;
    ManageUserAdapter manageUserAdapter;
    ListenerRegistration userListener;
    String Permission;

    @Override
    protected void toolbarspinnersetup(Spinner toolbarspinner) {
        ArrayAdapter itemfilteroptions = ArrayAdapter.createFromResource(getContext(),
                R.array.array_permission_options, R.layout.spinner_item_text);

        itemfilteroptions.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        toolbarspinner.setAdapter(itemfilteroptions);
        toolbarspinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selection = parent.getItemAtPosition(position).toString();
                startcode = "!";
                endcode = "{";
                if (!TextUtils.isEmpty(selection)) {
                    if (selection.equals(getString(R.string.permission_admin))) {
                        Permission = "Admin";
                        attachListData(startcode, endcode);
                    } else if (selection.equals(getString(R.string.permission_employee))) {
                        Permission = "Employee";
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
        users = new LinkedHashMap<>();
        searchedittext.setHint("Search for purchase using referenceid");
        firebaseFirestore = FirebaseFirestore.getInstance();
        manageUserAdapter = new ManageUserAdapter(users, this, getContext());
        recyclerView.setAdapter(manageUserAdapter);
        Permission= "Admin";
    }

    @Override
    protected void AddIntent() {
        Intent intent = new Intent(getContext(), AddUsersActivity.class);
        intent.putExtra("ShopCode", user.ShopCode);
        startActivity(intent);
    }

    @Override
    public void onResume() {
        super.onResume();
        startcode = "!";
        endcode = "{";
        if(users!=null){
            attachListData(startcode,endcode);
        }
    }

    @Override
    public void onPause() {
        userListener.remove();
        super.onPause();
    }

    @Override
    protected void attachListData(String startcode, String endcode) {
        SDProgress(true);
        users.clear();
        manageUserAdapter.notifyDataSetChanged();
        userListener =
                firebaseFirestore.collection("UserDetails").whereEqualTo("ShopCode", user.ShopCode)
                        .whereGreaterThanOrEqualTo("Name", startcode).whereLessThan("Name", endcode)
                        .whereEqualTo("PermissionLevel",Permission).limit(50)
                        .addSnapshotListener(new EventListener<QuerySnapshot>() {
                            @Override
                            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                                if(error!=null){
                                    Log.i("failuserentry",error.getMessage());
                                }
                                if (value != null && !value.isEmpty()) {
                                    for (DocumentChange doc : value.getDocumentChanges()) {
                                        StoreUser temp = doc.getDocument().toObject(StoreUser.class);
                                        if(temp.Email.compareTo(user.Email)==0){
                                            continue;
                                        }
                                        switch (doc.getType()) {
                                            case ADDED:
                                                users.put(temp.Email,temp);
                                                manageUserAdapter.notifyDataSetChanged();
                                                break;
                                            case MODIFIED:
                                                users.put(temp.Email,temp);
                                                manageUserAdapter.notifyDataSetChanged();
                                                break;
                                            case REMOVED:
                                                users.remove(temp.Email);
                                                manageUserAdapter.notifyDataSetChanged();
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
    protected void displayIntent(int posiiton) {
        StoreUser tempuser = (StoreUser) users.values().toArray()[posiiton];
        Intent intent = new Intent(getContext(), AddUsersActivity.class);
        intent.putExtra("ShopCode", user.ShopCode);
        intent.putExtra("Mode","Edit");
        intent.putExtra("User", user);
        startActivity(intent);
    }
}
