package com.stancorp.grocerystorev1.DisplayStakeholder;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.content.DialogInterface;
import android.content.Intent;
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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QuerySnapshot;
import com.stancorp.grocerystorev1.AddActivities.AddItemActivity;
import com.stancorp.grocerystorev1.AddActivities.AddStakesholdersActivity;
import com.stancorp.grocerystorev1.Classes.Agent;
import com.stancorp.grocerystorev1.Classes.Items;
import com.stancorp.grocerystorev1.GlobalClass.Gfunc;
import com.stancorp.grocerystorev1.R;
import com.stancorp.grocerystorev1.ui.main.StakeholderSectionsPagerAdapter;

public class AgentViewActivity extends AppCompatActivity {

    String AgentCode;
    String ShopCode;
    String Mode;
    Agent agent;
    Gfunc gfunc;
    FirebaseFirestore firebaseFirestore;
    ListenerRegistration agentlistener;

    TextView categoryText;
    TextView brandText;
    TextView Category;
    TextView Brand;
    TextView Name;
    ImageView img;
    RelativeLayout ProgressLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view);
        AgentCode = getIntent().getStringExtra("AgentCode");
        ShopCode = getIntent().getStringExtra("ShopCode");
        Mode = getIntent().getStringExtra("Mode");
        agent = (Agent) getIntent().getSerializableExtra("Agent");
        gfunc = new Gfunc();
        firebaseFirestore = FirebaseFirestore.getInstance();
        ProgressLayout = findViewById(R.id.ProgressLayout);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(gfunc.capitalize(AgentCode));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        categoryText = findViewById(R.id.Categorytext);
        brandText = findViewById(R.id.BrandText);
        Category = findViewById(R.id.ItemCategory);
        Brand = findViewById(R.id.ItemBrand);
        Name = findViewById(R.id.ItemName);
        img = findViewById(R.id.ItemDetailsImage);
        img.setVisibility(View.GONE);

        setUpTextviews();
        setupchangelistener();
    }

    private void setPageAdapter() {
        StakeholderSectionsPagerAdapter stakeholderSectionsPagerAdapter = new StakeholderSectionsPagerAdapter(this,
                getSupportFragmentManager(), FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT, AgentCode, ShopCode, agent);
        ViewPager viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(stakeholderSectionsPagerAdapter);
        TabLayout tabs = findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);
    }

    @Override
    protected void onPause() {
        agentlistener.remove();
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setupchangelistener();
    }

    private void setupchangelistener() {
        SDProgress(true);
        agentlistener =
                firebaseFirestore.collection(ShopCode).document("doc").collection("StakeHolders")
                        .whereEqualTo("Code", agent.Code).addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException error) {
                        if (queryDocumentSnapshots != null && !queryDocumentSnapshots.isEmpty()) {
                            for (DocumentChange doc : queryDocumentSnapshots.getDocumentChanges()) {
                                switch (doc.getType()) {
                                    case MODIFIED:
                                        Agent tempagent = doc.getDocument().toObject(Agent.class);
                                        agent = tempagent;
                                        setUpTextviews();
                                        break;
                                    case REMOVED:
                                        Toast.makeText(getApplicationContext(), "Agent has been removed", Toast.LENGTH_SHORT).show();
                                        finish();
                                        break;
                                }
                            }
                        }
                        SDProgress(false);
                        setPageAdapter();
                    }
                });
    }

    private void setUpTextviews() {
        brandText.setText("Type");
        Name.setText(gfunc.capitalize(agent.Name));
        String category = "";
        if (agent.AgentType.compareTo("Both") == 0) {
            category = "Customer / Vendor";
        } else {
            category = agent.AgentType;
        }
        Category.setText(gfunc.capitalize(category));
        Brand.setText(gfunc.capitalize(agent.CustomerType));
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.stakeholderdetailsmenu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.Edit:
                Intent intent = new Intent(getApplicationContext(), AddStakesholdersActivity.class);
                intent.putExtra("ShopCode", ShopCode);
                intent.putExtra("ActivityMode", "Edit");
                intent.putExtra("Agent", agent);
                startActivity(intent);
                break;
            case R.id.Delete:
                checkifpartoftransaction();
        }
        return true;
    }

    private void checkifpartoftransaction() {
        firebaseFirestore.collection(ShopCode).document("doc").collection("TransactionDetails")
                .whereEqualTo("stakeholderCode", agent.Code).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                AlertDialog.Builder aletdialog = new AlertDialog.Builder(AgentViewActivity.this, R.style.MyDialogTheme);
                if (task.getResult().isEmpty()) {
                    aletdialog
                            .setTitle("Delete Agent")
                            .setMessage("Are you sure you want to delete this agent")
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    deleteAgent();
                                }
                            }).setNegativeButton("Cancel", null).show();
                } else {
                    aletdialog
                            .setTitle("Cannot Delete Agent")
                            .setMessage("This agent is already a part of a transaction")
                            .setPositiveButton("OK", null).show();
                }
            }
        });
    }

    private void deleteAgent() {
        firebaseFirestore.collection(ShopCode).document("doc").collection("StakeHolders")
                .document(agent.Code).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(getApplicationContext(), "Agent Deleted", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(getApplicationContext(), "Failed to Delete Agent", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}