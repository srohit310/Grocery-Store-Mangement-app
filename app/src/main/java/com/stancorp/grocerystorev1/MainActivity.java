/*
 * Copyright Google Inc. All Rights Reserved.
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.stancorp.grocerystorev1;

import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.bumptech.glide.Glide;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.stancorp.grocerystorev1.Classes.StoreUser;
import com.stancorp.grocerystorev1.GlobalClass.Gfunc;
import com.stancorp.grocerystorev1.UserSettings.UserSettingsActivity;
import com.stancorp.grocerystorev1.ViewFragments.FragmentGroupItems;
import com.stancorp.grocerystorev1.ViewFragments.FragmentGroupUsers;
import com.stancorp.grocerystorev1.ViewFragments.FragmentGroupsLocations;
import com.stancorp.grocerystorev1.ViewFragments.MainFragment;
import com.stancorp.grocerystorev1.ViewFragments.FragmentsGroupCustomers;
import com.stancorp.grocerystorev1.ViewFragments.FragmentsGroupPurchases;
import com.stancorp.grocerystorev1.ViewFragments.FragmentsGroupSales;
import com.stancorp.grocerystorev1.ViewFragments.FragmentsGroupVendors;
import com.stancorp.grocerystorev1.LoginModule.LoginActivity;

import maes.tech.intentanim.CustomIntent;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "MainActivity";

    public static final String ANONYMOUS = "anonymous";
    public static final int DEFAULT_MSG_LENGTH_LIMIT = 1000;
    public static final int RC_PROFILE_PHOTO_PICKER = 213;

    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private TextView textViewemail;
    private TextView textViewname;
    private TextView textViewpermission;
    private ImageView imageView;
    public RelativeLayout ProgressLayout;
    RelativeLayout relativeLayout;

    private String mUsername;
    private String mEmail;
    private String UID;
    ListenerRegistration registration;
    public StoreUser User;
    public NavigationView navigationView;
    Gfunc gfunc;

    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;
    private FirebaseStorage firebaseStorage;
    private StorageReference photostorageReference;
    private FirebaseAuth.AuthStateListener authStateListener;
    private static final int RC_SIGN_IN = 123;
    private static final int RC_USER_SETTINGS = 2;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mUsername = ANONYMOUS;
        mEmail = ANONYMOUS;
        UID = ANONYMOUS;

        gfunc = new Gfunc();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();
        relativeLayout = findViewById(R.id.MainRelativeLayout);
        getSupportActionBar().hide();

        ProgressLayout = findViewById(R.id.ProgressLayout);

        photostorageReference = firebaseStorage.getReference();

        // Initialize Navigation Drawer
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.Open, R.string.Close);
        actionBarDrawerToggle.setDrawerIndicatorEnabled(true);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        navigationView = (NavigationView) findViewById(R.id.nav_view);

        // Setting up navigationViewListener

        final View header = navigationView.getHeaderView(0);
        textViewemail = (TextView) header.findViewById(R.id.navemail_textView);
        textViewname = (TextView) header.findViewById(R.id.navname_textView);
        textViewpermission = header.findViewById(R.id.navpermission_textView);
        imageView = (ImageView) header.findViewById(R.id.userimageView);
        navigationView.setNavigationItemSelectedListener(this);

        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    mEmail = user.getEmail();
                    UID = user.getUid();
                    textViewemail.setText(mEmail);
                    OnSignedInInitialize(user.getEmail());
                } else {
                    OnSignOutCleanUp();
                    Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                    startActivityForResult(
                            intent,
                            RC_SIGN_IN);
                    CustomIntent.customType(MainActivity.this, "bottom-to-up");
                }
            }
        };

        if (savedInstanceState != null) {
            Log.i("beforecrashv1", "onassign");
            int currentPID = android.os.Process.myPid();
            if (currentPID != savedInstanceState.getInt("PID")) {
                User = null;
                firebaseAuth.addAuthStateListener(authStateListener);
                authStateListener.onAuthStateChanged(FirebaseAuth.getInstance());
            }
        }

        if (savedInstanceState == null && User != null) {
            navigationView.setCheckedItem(R.id.dash);
            getSupportActionBar().setTitle("Main Menu");
            getSupportFragmentManager().beginTransaction().replace(R.id.fLayout,
                    new MainFragment()).commit();
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle bundle, @NonNull PersistableBundle outPersistentState) {
        super.onSaveInstanceState(bundle, outPersistentState);
        bundle.putInt("PID", android.os.Process.myPid());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.sign_out_menu:
                AuthUI.getInstance().signOut(getApplicationContext());
                return true;
            default:
                return actionBarDrawerToggle.onOptionsItemSelected(item) || super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else if (getSupportActionBar().getTitle() != "Main Menu") {
            getSupportActionBar().setTitle("Main Menu");
            navigationView.setCheckedItem(R.id.dash);
            getSupportFragmentManager().beginTransaction().replace(R.id.fLayout,
                    new MainFragment()).commit();
        } else
            super.onBackPressed();
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {

        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {

        }
    }

    @Override
    protected void onResume() {
        firebaseAuth.addAuthStateListener(authStateListener);
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        firebaseAuth.removeAuthStateListener(authStateListener);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            if (resultCode == RESULT_OK) {
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(getApplicationContext(), "signed-out", Toast.LENGTH_LONG).show();
                finish();
            }
        }
        if (requestCode == RC_USER_SETTINGS) {
            if (resultCode == RESULT_OK) {
                User.Name = data.getStringExtra("Name");
                if (data.getBooleanExtra("imgchange", false)) {
                    setProfileImage();
                }
            }
        }
    }

    private void OnSignedInInitialize(String email) {
        ProgressLayout.setVisibility(View.VISIBLE);
        actionBarDrawerToggle.setDrawerIndicatorEnabled(false);
        relativeLayout.setEnabled(false);
        mEmail = email;
        if (User == null) {
            userListener();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (User != null) {
            userListener();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (registration != null)
            registration.remove();
    }

    private void userListener() {
        registration =
                firebaseFirestore.collection("UserDetails").whereEqualTo("Email", firebaseAuth.getCurrentUser().getEmail())
                        .addSnapshotListener(new EventListener<QuerySnapshot>() {
                            @Override
                            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException error) {
                                if (queryDocumentSnapshots != null && !queryDocumentSnapshots.isEmpty()) {
                                    for (DocumentChange doc : queryDocumentSnapshots.getDocumentChanges()) {
                                        StoreUser user;
                                        switch (doc.getType()) {
                                            case ADDED:
                                                user = doc.getDocument().toObject(StoreUser.class);
                                                if (!user.valid) {
                                                    User = null;
                                                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                    startActivity(intent);
                                                    registration.remove();
                                                    FirebaseAuth.getInstance().signOut();
                                                } else if (User == null) {
                                                    User = user;
                                                    setUpTexts();
                                                    navigationView.setCheckedItem(R.id.dash);
                                                    getSupportActionBar().setTitle("Main Menu");
                                                    getSupportFragmentManager().beginTransaction().replace(R.id.fLayout,
                                                            new MainFragment()).commit();
                                                    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                                                    Log.i("beforecrashv1", "it reached there");
                                                } else {
                                                    setUpTexts();
                                                }
                                                break;
                                            case MODIFIED:
                                                user = doc.getDocument().toObject(StoreUser.class);
                                                if (!user.valid) {
                                                    Toast.makeText(getApplicationContext(), "Acoount has been made invalid", Toast.LENGTH_SHORT).show();
                                                    User = null;
                                                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                    startActivity(intent);
                                                    FirebaseAuth.getInstance().signOut();
                                                }
                                                break;
                                        }
                                    }
                                }
                            }
                        });
    }

    private void setUpTexts() {

        textViewname.setText(gfunc.capitalize(User.Name));
        textViewpermission.setText(gfunc.capitalize(User.PermissionLevel));
        if (User.PermissionLevel.compareTo("Employee") == 0) {
            Menu menu = navigationView.getMenu();
            MenuItem target = menu.findItem(R.id.Locations_menu);
            target.setVisible(false);
            target = menu.findItem(R.id.manage_users_menu);
            target.setVisible(false);
        } else {
            Menu menu = navigationView.getMenu();
            MenuItem target = menu.findItem(R.id.Locations_menu);
            target.setVisible(true);
            target = menu.findItem(R.id.manage_users_menu);
            target.setVisible(true);
        }
        if (User.PhotoUri) {
            setProfileImage();
        } else {
            Glide.with(imageView.getContext())
                    .clear(imageView);
            imageView.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_baseline_account_circle_24));
            ProgressLayout.setVisibility(View.GONE);
            actionBarDrawerToggle.setDrawerIndicatorEnabled(true);
        }
        getSupportActionBar().show();
    }

    private void setProfileImage() {

        photostorageReference.child(User.ShopCode).child("ProfileImages").child(User.Email)
                .getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                imageView.setForeground(null);
                Glide.with(MainActivity.this)
                        .load(uri)
                        .into(imageView);
                ProgressLayout.setVisibility(View.GONE);
                actionBarDrawerToggle.setDrawerIndicatorEnabled(true);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                ProgressLayout.setVisibility(View.GONE);
                actionBarDrawerToggle.setDrawerIndicatorEnabled(true);
            }
        });
    }

    private void OnSignOutCleanUp() {
        mUsername = ANONYMOUS;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull final MenuItem item) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                switch (item.getItemId()) {
                    case R.id.dash:
                        getSupportActionBar().setTitle("Main menu");
                        getSupportFragmentManager().beginTransaction().replace(R.id.fLayout,
                                new MainFragment()).commit();
                        break;
                    case R.id.item_menu:
                        getSupportActionBar().setTitle("Items");
                        getSupportFragmentManager().beginTransaction().replace(R.id.fLayout,
                                new FragmentGroupItems()).commit();
                        break;
                    case R.id.customers_menu:
                        getSupportActionBar().setTitle("Customers");
                        getSupportFragmentManager().beginTransaction().replace(R.id.fLayout,
                                new FragmentsGroupCustomers()).commit();
                        break;
                    case R.id.purchase_menu:
                        getSupportActionBar().setTitle("Purchases");
                        getSupportFragmentManager().beginTransaction().replace(R.id.fLayout,
                                new FragmentsGroupPurchases()).commit();
                        break;
                    case R.id.sales_menu:
                        getSupportActionBar().setTitle("Sales");
                        getSupportFragmentManager().beginTransaction().replace(R.id.fLayout,
                                new FragmentsGroupSales()).commit();
                        break;
                    case R.id.vendors_menu:
                        getSupportActionBar().setTitle("Vendors");
                        getSupportFragmentManager().beginTransaction().replace(R.id.fLayout,
                                new FragmentsGroupVendors()).commit();
                        break;
                    case R.id.Locations_menu:
                        getSupportActionBar().setTitle("Locations");
                        getSupportFragmentManager().beginTransaction().replace(R.id.fLayout,
                                new FragmentGroupsLocations()).commit();
                        break;
                    case R.id.manage_users_menu:
                        getSupportActionBar().setTitle("Users");
                        getSupportFragmentManager().beginTransaction().replace(R.id.fLayout,
                                new FragmentGroupUsers()).commit();
                        break;

                    case R.id.settings_change_menu:
                        Intent intent = new Intent(MainActivity.this, UserSettingsActivity.class);
                        intent.putExtra("UserData", User);
                        startActivityForResult(intent, RC_USER_SETTINGS);
                        break;
                    case R.id.sign_out_menu:
                        User = null;
                        if (registration != null) {
                            registration.remove();
                        }
                        FirebaseAuth.getInstance().signOut();
                        break;
                }
            }
        }, 250);

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    public String UID() {
        return UID;
    }

}
