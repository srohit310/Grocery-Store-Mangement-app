package com.stancorp.grocerystorev1.DisplayTransactions;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.content.res.Configuration;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.Batch;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Transaction;
import com.google.firebase.firestore.WriteBatch;
import com.stancorp.grocerystorev1.Classes.Agent;
import com.stancorp.grocerystorev1.Classes.Itemtransaction;
import com.stancorp.grocerystorev1.Classes.StoreTransaction;
import com.stancorp.grocerystorev1.Classes.TransactionProperties;
import com.stancorp.grocerystorev1.GlobalClass.Gfunc;
import com.stancorp.grocerystorev1.R;
import com.stancorp.grocerystorev1.ui.main.TransactionSectionsPagerAdapter;

import java.util.ArrayList;

public class TransactionViewActivity extends AppCompatActivity {

    String ShopCode;
    String Mode;
    StoreTransaction storeTransaction;
    String storeTransactionCode;
    Gfunc gfunc;
    FirebaseFirestore firebaseFirestore;
    ListenerRegistration registration;

    TextView agentText;
    TextView totalPriceText;
    TextView agentDisplay;
    TextView totalPriceDisplay;
    TextView reference;
    ImageView img;
    RelativeLayout ProgressLayout;
    Toolbar toolbar;

    ArrayList<Itemtransaction> itemslist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view);
        ShopCode = getIntent().getStringExtra("ShopCode");
        Mode = getIntent().getStringExtra("Mode");
        storeTransactionCode = getIntent().getStringExtra("TransactionCode");
        gfunc = new Gfunc();
        firebaseFirestore = FirebaseFirestore.getInstance();
        ProgressLayout = findViewById(R.id.ProgressLayout);
        itemslist = new ArrayList<>();

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(gfunc.capitalize(storeTransactionCode));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        agentText = findViewById(R.id.Categorytext);
        totalPriceText = findViewById(R.id.BrandText);
        agentDisplay = findViewById(R.id.ItemCategory);
        totalPriceDisplay = findViewById(R.id.ItemBrand);
        reference = findViewById(R.id.ItemName);
        img = findViewById(R.id.ItemDetailsImage);
        img.setVisibility(View.GONE);
    }


    @Override
    protected void onPause() {
        registration.remove();
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        retrievetransactiondetails();
    }

    private void retrievetransactiondetails() {
        registration =
                firebaseFirestore.collection(ShopCode).document("doc")
                        .collection("TransactionDetails").whereEqualTo("code", storeTransactionCode)
                        .addSnapshotListener(new EventListener<QuerySnapshot>() {
                            @Override
                            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException error) {
                                if (queryDocumentSnapshots != null && !queryDocumentSnapshots.isEmpty()) {
                                    for (DocumentChange doc : queryDocumentSnapshots.getDocumentChanges()) {
                                        StoreTransaction temptransaction;
                                        switch (doc.getType()) {
                                            case ADDED:
                                                temptransaction = doc.getDocument().toObject(StoreTransaction.class);
                                                storeTransaction = temptransaction;
                                                break;
                                            case MODIFIED:
                                                temptransaction = doc.getDocument().toObject(StoreTransaction.class);
                                                storeTransaction = temptransaction;
                                                break;
                                        }
                                        setUpTextviews();
                                        invalidateOptionsMenu();
                                        setPageAdapter();
                                        SDProgress(false);
                                    }
                                }
                            }
                        });
    }

    private void setPageAdapter() {
        TransactionSectionsPagerAdapter transactionSectionsPagerAdapter = new TransactionSectionsPagerAdapter(this,
                getSupportFragmentManager(), FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT, storeTransaction, ShopCode);
        ViewPager viewPager = findViewById(R.id.view_pager);
        viewPager.setOffscreenPageLimit(2);
        viewPager.setAdapter(transactionSectionsPagerAdapter);
        TabLayout tabs = findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);
    }

    private void setUpTextviews() {
        if (Mode.compareTo("Sale") == 0)
            agentText.setText("CUSTOMER");
        else
            agentText.setText("VENDOR");
        totalPriceText.setText("TOTAL PRICE");
        agentDisplay.setText(storeTransaction.stakeholderCode);
        reference.setText(storeTransaction.reference);
        totalPriceDisplay.setText(String.valueOf(storeTransaction.totalPrice) + " INR");
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {

        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {

        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem target = menu.findItem(R.id.Markdelcomplete);
        if (storeTransaction != null && !storeTransaction.pending) {
            target.setEnabled(false);
            target.setTitle("Delivery Complete");
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.transactions, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.Markdelcomplete:
                if (storeTransaction.DeliveryDate.compareTo(gfunc.getCurrentDate("dd/MM/yyyy")) > 0) {
                    Toast.makeText(this, "Delivery Date has not yet arrived", Toast.LENGTH_SHORT).show();
                } else {
                    completeDelivery();
                }
                return true;
        }
        return false;
    }

    private void completeDelivery() {
        SDProgress(true);
        firebaseFirestore.runTransaction(new Transaction.Function<Void>() {
            @Nullable
            @Override
            public Void apply(@NonNull Transaction transaction) throws FirebaseFirestoreException {
                int overallpending = 0;
                int locationpending = 0;
                TransactionProperties overallprop = (TransactionProperties) transaction.get(firebaseFirestore.collection(ShopCode)
                        .document("doc").collection("Pending").document("Overall"))
                        .toObject(TransactionProperties.class);
                if (overallprop != null) {
                    if (Mode.compareTo("Purchase") == 0) {
                        overallpending = overallprop.pendingPurchases;
                    } else {
                        overallpending = overallprop.pendingSales;
                    }
                }
                TransactionProperties locationprop = (TransactionProperties) transaction.get(firebaseFirestore.collection(ShopCode)
                        .document("doc").collection("Pending").document(storeTransaction.locationCode))
                        .toObject(TransactionProperties.class);
                if (locationprop != null) {
                    if (Mode.compareTo("Purchase") == 0) {
                        locationpending = locationprop.pendingPurchases;
                    } else {
                        locationpending = locationprop.pendingSales;
                    }
                }
                overallpending -= 1;
                locationpending -= 1;
                if (Mode.compareTo("Purchase") == 0) {
                    overallprop.pendingPurchases = overallpending;
                    locationprop.pendingPurchases = locationpending;
                } else {
                    overallprop.pendingSales = overallpending;
                    locationprop.pendingSales = locationpending;
                }
                transaction.set(firebaseFirestore.collection(ShopCode).document("doc").collection("Pending")
                        .document("Overall"), overallprop);
                transaction.set(firebaseFirestore.collection(ShopCode).document("doc").collection("Pending")
                        .document(storeTransaction.locationCode), locationprop);
                transaction.update(firebaseFirestore.collection(ShopCode).document("doc").collection("TransactionDetails")
                        .document(storeTransaction.code), "pending", false);
                return null;
            }
        }).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(getApplicationContext(), "Transaction has been Completed", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), "Delivery could not be marked as complete", Toast.LENGTH_SHORT).show();
                }
                finish();
                SDProgress(false);
            }
        });

    }

    public void SDProgress(boolean show) {
        if (show) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            ProgressLayout.setVisibility(View.VISIBLE);
        } else {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            ProgressLayout.setVisibility(View.GONE);
        }
    }
}