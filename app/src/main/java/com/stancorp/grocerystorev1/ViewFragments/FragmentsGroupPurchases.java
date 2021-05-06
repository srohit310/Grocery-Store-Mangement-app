package com.stancorp.grocerystorev1.ViewFragments;

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.paging.PagedList;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.firebase.ui.firestore.paging.FirestorePagingOptions;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;
import com.stancorp.grocerystorev1.AdapterClasses.TransactionFirestoreAdapter;
import com.stancorp.grocerystorev1.AddActivities.AddTransactionActivity;
import com.stancorp.grocerystorev1.Classes.StoreTransaction;
import com.stancorp.grocerystorev1.DisplayTransactions.TransactionViewActivity;
import com.stancorp.grocerystorev1.MainActivity;
import com.stancorp.grocerystorev1.R;

public class FragmentsGroupPurchases extends FragmentsGroups {

    TransactionFirestoreAdapter transactionAdapter;
    Boolean pending;

    @Override
    protected void toolbarspinnersetup(Spinner toolbarspinner) {
        ArrayAdapter transactionfilteroptions = ArrayAdapter.createFromResource(getContext(),
                R.array.array_transaction_filter_options, R.layout.spinner_item_text);

        transactionfilteroptions.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        toolbarspinner.setAdapter(transactionfilteroptions);
        toolbarspinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selection = parent.getItemAtPosition(position).toString();
                if (!TextUtils.isEmpty(selection)) {
                    if (selection.equals(getString(R.string.pending))) {
                        pending = true;
                        attachListData(startcode, endcode);
                    } else if (selection.equals(getString(R.string.completed))) {
                        pending = false;
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

        ((MainActivity) getActivity()).navigationView.setCheckedItem(R.id.purchase_menu);
        toolbar.setTitle("Purchases");
        searchedittext.setHint("Search for purchase using referenceid");
        pending = true;
    }

    @Override
    protected void AddIntent() {
        Intent intent = new Intent(getContext(), AddTransactionActivity.class);
        intent.putExtra("ShopCode", user.ShopCode);
        intent.putExtra("Username", user.Name);
        intent.putExtra("UserPermission", user.PermissionLevel);
        intent.putExtra("UserLocation", user.Location);
        intent.putExtra("Useremail", user.Email);
        intent.putExtra("Mode", "Vendor");
        startActivity(intent);
    }

    @Override
    public void onStop() {
        super.onStop();
        transactionAdapter.stopListening();
    }

    @Override
    public void onStart() {
        super.onStart();
        if (transactionAdapter != null)
            transactionAdapter.startListening();
    }

    @Override
    protected void attachListData(String startcode, String endcode) {
        SDProgress(true);
        Query query =
                firebaseFirestore.collection(user.ShopCode).document("doc").collection("TransactionDetails")
                        .whereEqualTo("pending", pending).whereGreaterThanOrEqualTo("reference", startcode)
                        .whereLessThan("reference", endcode).whereEqualTo("type", "Purchase");
        if (user.PermissionLevel.compareTo("Employee") == 0) {
            query = query.whereEqualTo("locationCode", user.Location).limit(20);
        } else {
            query = query.limit(20);
        }

        PagedList.Config config = new PagedList.Config.Builder()
                .setInitialLoadSizeHint(10)
                .setPageSize(3)
                .build();

        FirestorePagingOptions<StoreTransaction> options = new FirestorePagingOptions.Builder<StoreTransaction>()
                .setQuery(query, config, StoreTransaction.class)
                .build();

        transactionAdapter = new TransactionFirestoreAdapter(options, getContext(), this, null, progressLayout, emptyview);
        transactionAdapter.startListening();
        transactionAdapter.notifyDataSetChanged();
        recyclerView.setAdapter(transactionAdapter);
        recyclerView.scheduleLayoutAnimation();
    }

    @Override
    protected void displayFirestoreIntent(DocumentSnapshot documentSnapshot, int posiiton) {
        Intent intent = new Intent(getContext(), TransactionViewActivity.class);
        StoreTransaction transaction = documentSnapshot.toObject(StoreTransaction.class);
        intent.putExtra("Transaction", transaction);
        intent.putExtra("Mode", "Purchase");
        intent.putExtra("ShopCode", user.ShopCode);
        startActivity(intent);
    }
}
