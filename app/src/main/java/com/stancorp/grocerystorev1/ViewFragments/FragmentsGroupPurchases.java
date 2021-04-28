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
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.stancorp.grocerystorev1.AdapterClasses.TransactionAdapter;
import com.stancorp.grocerystorev1.AddActivities.AddTransactionActivity;
import com.stancorp.grocerystorev1.Classes.Agent;
import com.stancorp.grocerystorev1.Classes.StoreTransaction;
import com.stancorp.grocerystorev1.DisplayStakeholder.AgentViewActivity;
import com.stancorp.grocerystorev1.DisplayTransactions.TransactionViewActivity;
import com.stancorp.grocerystorev1.R;

import java.util.LinkedHashMap;

public class FragmentsGroupPurchases extends FragmentsGroups {

    LinkedHashMap<String, StoreTransaction> transactions;
    FirebaseFirestore firebaseFirestore;
    TransactionAdapter transactionAdapter;
    ListenerRegistration transactionListener;
    Boolean pending;

    @Override
    protected void toolbarspinnersetup(Spinner toolbarspinner) {
        ArrayAdapter itemfilteroptions = ArrayAdapter.createFromResource(getContext(),
                R.array.array_transaction_filter_options, R.layout.spinner_item_text);

        itemfilteroptions.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        toolbarspinner.setAdapter(itemfilteroptions);
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
        transactions = new LinkedHashMap<>();
        searchedittext.setHint("Search for purchase using referenceid");
        firebaseFirestore = FirebaseFirestore.getInstance();
        transactionAdapter = new TransactionAdapter(transactions, getContext(), this, null);
        recyclerView.setAdapter(transactionAdapter);
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
    public void onResume() {
        super.onResume();
        startcode = "!";
        endcode = "{";
        if (transactions != null) {
            attachListData(startcode, endcode);
        }
    }

    @Override
    public void onPause() {
        transactionListener.remove();
        super.onPause();
    }

    @Override
    protected void attachListData(String startcode, String endcode) {
        SDProgress(true);
        transactions.clear();
        transactionAdapter.notifyDataSetChanged();
        Query query =
                firebaseFirestore.collection(user.ShopCode).document("doc").collection("TransactionDetails")
                        .whereEqualTo("pending", pending).whereGreaterThanOrEqualTo("reference", startcode)
                        .whereLessThan("reference", endcode).whereEqualTo("type", "Purchase");
        if (user.PermissionLevel.compareTo("Employee") == 0) {
            query = query.whereEqualTo("locationCode", user.Location).limit(20);
        } else {
            query = query.limit(20);
        }
        transactionListener = query.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Log.i("failfirestore", error.getMessage());
                }
                if (queryDocumentSnapshots != null && !queryDocumentSnapshots.isEmpty()) {
                    for (DocumentChange doc : queryDocumentSnapshots.getDocumentChanges()) {
                        StoreTransaction transaction;
                        switch (doc.getType()) {
                            case ADDED:
                                transaction = doc.getDocument().toObject(StoreTransaction.class);
                                transactions.put(transaction.code, transaction);
                                transactionAdapter.notifyDataSetChanged();
                                break;
                            case MODIFIED:
                                transaction = doc.getDocument().toObject(StoreTransaction.class);
                                transactions.put(transaction.code, transaction);
                                transactionAdapter.notifyDataSetChanged();
                                break;
                            case REMOVED:
                                transaction = doc.getDocument().toObject(StoreTransaction.class);
                                transactions.remove(transaction.code);
                                transactionAdapter.notifyDataSetChanged();
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
    protected void displayIntent(int position) {
        Intent intent = new Intent(getContext(), TransactionViewActivity.class);
        StoreTransaction transaction = (StoreTransaction) transactions.values().toArray()[position];
        intent.putExtra("Transaction", transaction);
        intent.putExtra("Mode", "Purchase");
        intent.putExtra("ShopCode", user.ShopCode);
        startActivity(intent);
    }
}
