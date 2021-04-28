package com.stancorp.grocerystorev1.DisplayTransactions;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.stancorp.grocerystorev1.Classes.Itemtransaction;
import com.stancorp.grocerystorev1.Classes.StoreTransaction;
import com.stancorp.grocerystorev1.GlobalClass.Gfunc;
import com.stancorp.grocerystorev1.R;
import com.stancorp.grocerystorev1.ui.main.TransactionSectionsPagerAdapter;

import java.util.ArrayList;

public class TransactionViewActivity extends AppCompatActivity {

    String ShopCode;
    String Mode;
    StoreTransaction storeTransaction;
    Gfunc gfunc;
    FirebaseFirestore firebaseFirestore;
    ListenerRegistration itemlistlistener;

    TextView agentText;
    TextView totalPriceText;
    TextView agentDisplay;
    TextView totalPriceDisplay;
    TextView reference;
    ImageView img;
    RelativeLayout ProgressLayout;

    ArrayList<Itemtransaction> itemslist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view);
        ShopCode = getIntent().getStringExtra("ShopCode");
        Mode = getIntent().getStringExtra("Mode");
        storeTransaction = (StoreTransaction) getIntent().getSerializableExtra("Transaction");
        gfunc = new Gfunc();
        firebaseFirestore = FirebaseFirestore.getInstance();
        ProgressLayout = findViewById(R.id.ProgressLayout);
        itemslist = new ArrayList<>();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(gfunc.capitalize(storeTransaction.code));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        agentText = findViewById(R.id.Categorytext);
        totalPriceText = findViewById(R.id.BrandText);
        agentDisplay = findViewById(R.id.ItemCategory);
        totalPriceDisplay = findViewById(R.id.ItemBrand);
        reference = findViewById(R.id.ItemName);
        img = findViewById(R.id.ItemDetailsImage);
        img.setVisibility(View.GONE);

        setUpTextviews();
        setPageAdapter();
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