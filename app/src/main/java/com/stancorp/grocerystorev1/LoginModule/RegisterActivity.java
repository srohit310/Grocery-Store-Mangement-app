package com.stancorp.grocerystorev1.LoginModule;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Transaction;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.stancorp.grocerystorev1.GlobalClass.Gfunc;
import com.stancorp.grocerystorev1.R;
import com.stancorp.grocerystorev1.Classes.StoreInfo;
import com.stancorp.grocerystorev1.Classes.StoreUser;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    Button addbutton;
    StoreUser user;
    StoreInfo info;
    String Code;
    Long Shopno;
    public static final int RC_PROFILE_PHOTO_PICKER = 214;
    public static final int STOREAGE_PERMISSION_CODE = 21;
    private int RegisterType;
    Snackbar snackbar;

    //Widgets used in layout
    ImageView AdminImage;
    EditText ShopCode, ShopName;
    TextInputLayout UserName, UserEmail, UserPhone, UserPassword, ConfirmPassword;
    TextView BoldtextShop, BoldtextShopDescription, textShopCode, textShopName;
    TextView BoldtextUser, BoldtextUserDescription;
    Spinner PermissionLvlSpinner;
    Button Imagecancel;
    RelativeLayout ProgressLayout;
    TextView ProgressText;
    String flagAlert;
    ArrayList<String> hints;
    Uri photoUri;
    FirebaseAuth firebaseAuth;
    FirebaseStorage firebaseStorage;
    StorageReference photoStorageReference;
    CoordinatorLayout RegisterLayout;
    SwitchCompat registerSwitch;
    String permission;

    FirebaseFirestore firebaseFirestore;
    Gfunc gfunc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        gfunc = new Gfunc();

        RegisterType = 1;
        flagAlert = "Cancel";
        permission = "Admin";
        hints = new ArrayList<>();
        photoUri = null;

        FirebaseFirestore.setLoggingEnabled(true);
        firebaseFirestore = FirebaseFirestore.getInstance();

        if (!CheckifConnected()) {
            new AlertDialog.Builder(RegisterActivity.this, R.style.AlertDialogTheme)
                    .setTitle("Internet Connection Required")
                    .setCancelable(false)
                    .setMessage("Please connect to the internet to continue")
                    .setPositiveButton("Connect", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                            finish();
                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                            finish();
                        }
                    })
                    .create().show();
        }

        //Progress Layout
        ProgressLayout = findViewById(R.id.ProgressLayout);
        ProgressText = findViewById(R.id.ProgressText);

        registerSwitch = findViewById(R.id.RegisterSwitch);
        RegisterLayout = findViewById(R.id.RegisterLayout);

        snackbar = Snackbar.make(RegisterLayout, "ShopCode, Email or Permission Lvl was wrong", BaseTransientBottomBar.LENGTH_SHORT);
        snackbar.setDuration(5000);

        //initializing widgets
        PermissionLvlSpinner = findViewById(R.id.AddUserSpinner);
        setupspinner(0);

        //initializing text views
        BoldtextShop = findViewById(R.id.registeractivityRegisterText);
        BoldtextShopDescription = findViewById(R.id.RegisterShopDescription);
        textShopCode = findViewById(R.id.ShopCodeTextView);
        textShopName = findViewById(R.id.ShopNameTextView);
        BoldtextUser = findViewById(R.id.registeractivityAdminText);
        BoldtextUserDescription = findViewById(R.id.registerAdminDescription);

        // Edittexts initialization as well as assigning them the correction hint
        ShopCode = findViewById(R.id.AddShopCode);
        ShopName = findViewById(R.id.AddShopName);
        UserName = findViewById(R.id.AddAdminName);
        UserEmail = findViewById(R.id.AddAdminEmail);
        UserPhone = findViewById(R.id.AddAdminPhone);
        UserPassword = findViewById(R.id.AddAdminPassword);
        ConfirmPassword = findViewById(R.id.AddAdminConfirmPassword);


        AdminImage = findViewById(R.id.AdminImageSource);
        AdminImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ContextCompat.checkSelfPermission(RegisterActivity.this,
                        Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    CropImage.activity()
                            .setGuidelines(CropImageView.Guidelines.ON)
                            .setAspectRatio(1, 1)
                            .start(RegisterActivity.this);
                } else
                    requeststoragepermission();
            }
        });
        Imagecancel = findViewById(R.id.cancelimage);
        Imagecancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AdminImage.setForeground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_baseline_image_24));
                Glide.with(AdminImage.getContext())
                        .clear(AdminImage);
                photoUri = null;
            }
        });

        //initializing firebase
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();
        photoStorageReference = firebaseStorage.getReference();

        setSwitchCheck();

        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

    private void setSwitchCheck() {
        registerSwitch.setChecked(false);
        registerSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    RegisterType = 2;
                    BoldtextShop.setText("Shop Code");
                    BoldtextShopDescription.setText("Enter the shop code of the shop to be registered to");
                    textShopCode.setVisibility(View.VISIBLE);
                    ShopCode.setVisibility(View.VISIBLE);
                    textShopCode.setText("Shop Code");
                    textShopName.setVisibility(View.GONE);
                    ShopCode.setEnabled(true);
                    ShopCode.setText("");
                    ShopName.setVisibility(View.GONE);
                    BoldtextUser.setVisibility(View.GONE);
                    PermissionLvlSpinner.setVisibility(View.VISIBLE);
                    BoldtextUserDescription.setText("Select Admin/Employee and fill the fields below (Enter the Email given to the shop admin)");
                    UserName.setVisibility(View.GONE);
                    UserPhone.setVisibility(View.GONE);
                } else {
                    RegisterType = 1;
                    permission = "Admin";
                    BoldtextShop.setText("Shop Details");
                    BoldtextShopDescription.setText(R.string.details_shop_description);
                    textShopCode.setVisibility(View.GONE);
                    ShopCode.setVisibility(View.GONE);
                    textShopName.setVisibility(View.VISIBLE);
                    ShopName.setVisibility(View.VISIBLE);
                    BoldtextUser.setVisibility(View.VISIBLE);
                    PermissionLvlSpinner.setVisibility(View.GONE);
                    BoldtextUserDescription.setText(R.string.details_admin_description);
                    UserName.setVisibility(View.VISIBLE);
                    UserPhone.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    private void setupspinner(int position) {

        ArrayAdapter genderSpinnerAdapter = ArrayAdapter.createFromResource(getApplicationContext(),
                R.array.array_permission_options, R.layout.spinner_item_text);

        genderSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        PermissionLvlSpinner.setAdapter(genderSpinnerAdapter);
        PermissionLvlSpinner.setSelection(position);
        PermissionLvlSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selection = parent.getItemAtPosition(position).toString();
                if (!TextUtils.isEmpty(selection)) {
                    if (selection.equals(getString(R.string.permission_admin))) {
                        permission = "Admin";
                    } else if (selection.equals(getString(R.string.permission_employee))) {
                        permission = "Employee";
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                permission = "Admin";
            }
        });
    }

    private void requeststoragepermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            new AlertDialog.Builder(this, R.style.MyDialogTheme)
                    .setTitle("Permission Needed")
                    .setMessage("In order to access you gallery permission to read external storage is required")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            ActivityCompat.requestPermissions(RegisterActivity.this,
                                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, STOREAGE_PERMISSION_CODE);
                        }
                    })
                    .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    })
                    .create().show();
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    STOREAGE_PERMISSION_CODE);
        }
    }

    private boolean CheckifConnected() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                photoUri = result.getUri();
                AdminImage.setForeground(null);
                Glide.with(AdminImage.getContext())
                        .load(photoUri)
                        .into(AdminImage);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onBackPressed() {
        String Title = "";
        if (flagAlert == "Register")
            Title = "Register Shop ?";
        else if (flagAlert == "Cancel")
            Title = "Cancel registration";
        new AlertDialog.Builder(this, R.style.MyDialogTheme)
                .setTitle(Title)
                .setPositiveButton("Accept", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (flagAlert == "Register") {
                            registershop();
                        } else if (flagAlert == "Cancel")
                            finishAffinity();
                    }
                })
                .setNegativeButton("Decline", null)
                .show();
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {

        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.add_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.Save:
                flagAlert = "Register";
                onBackPressed();
                return true;

            case android.R.id.home:
                flagAlert = "Cancel";
                onBackPressed();
                return true;
        }
        return false;
    }

    private void registershop() {
        Boolean flag = true;
        flagAlert = "Cancel";
        if (RegisterType == 2 && ShopCode.getText().toString().length() == 0) {
            ShopCode.setText("");
            ShopCode.setError("Enter more characters");
            ShopCode.requestFocus();
            flag = false;
        } else if (RegisterType == 1 && ShopName.getText().toString().length() < 5) {
            ShopName.setText("");
            ShopName.setError("Enter at least 5 characters");
            ShopName.requestFocus();
            flag = false;
        } else if (RegisterType == 1 && UserName.getEditText().getText().toString().length() < 5) {
            UserName.getEditText().setText("");
            UserName.getEditText().setError("Enter at least 5 characters");
            UserName.requestFocus();
            flag = false;
        } else if (!gfunc.isEmailValid(UserEmail.getEditText().getText().toString())) {
            UserEmail.getEditText().setText("");
            UserEmail.getEditText().setError("Enter a valid email");
            UserEmail.requestFocus();
            flag = false;
        } else if (RegisterType == 1 && UserPhone.getEditText().getText().toString().length() != 10) {
            UserPhone.getEditText().setText("");
            UserPhone.getEditText().setError("Please enter 10 digits");
            UserPhone.requestFocus();
            flag = false;
        } else if (UserPassword.getEditText().getText().toString().length() < 8 || UserPassword.getEditText().getText().toString().length() > 16) {
            UserPassword.getEditText().setText("");
            UserPassword.getEditText().setError("8 - 16 characters");
            UserPassword.requestFocus();
            flag = false;
        } else if (ConfirmPassword.getEditText().getText().toString().compareTo(UserPassword.getEditText().getText().toString()) != 0) {
            ConfirmPassword.getEditText().setText("");
            ConfirmPassword.setHint("Passwords does not match");
            ConfirmPassword.requestFocus();
            flag = false;
        }
        if (flag) {
            SDProgress(true, "Checking input");
            final Boolean Photoexists = (photoUri != null);
            final Query query = firebaseFirestore.collection("UserDetails").whereEqualTo("Email", UserEmail.getEditText().getText().toString());
            query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (RegisterType == 2) {
                        if (!task.getResult().isEmpty()) {
                            StoreUser tempUser = new StoreUser();
                            String key = "";
                            for (DocumentSnapshot dataSnapshot : task.getResult()) {
                                tempUser = dataSnapshot.toObject(StoreUser.class);
                                key = dataSnapshot.getId();
                            }
                            if (ShopCode.getText().toString().compareTo(tempUser.ShopCode) == 0 && permission.compareTo(tempUser.PermissionLevel) == 0) {
                                tempUser.PhotoUri = Photoexists;
                                if(!tempUser.Registeredflag) {
                                    RegisterUser(tempUser, key);
                                }else{
                                    Snackbar registered = Snackbar.make(RegisterLayout, "User is already Registered!", BaseTransientBottomBar.LENGTH_SHORT);
                                    registered.setDuration(5000);
                                    SDProgress(false,"");
                                    ShopCode.setText("");
                                    UserEmail.getEditText().setText("");
                                    registered.show();
                                }
                            } else {
                                SDProgress(false, "");
                                snackbar.show();
                                ShopCode.setText("");
                                UserEmail.getEditText().setText("");
                            }
                        } else {
                            SDProgress(false, "");
                            snackbar.show();
                            ShopCode.setText("");
                            UserEmail.getEditText().setText("");
                        }
                    } else {
                        if(task.getResult().isEmpty()) {
                            makeobjects();
                        }else{
                            SDProgress(false, "");
                            UserEmail.getEditText().setText("");
                            UserEmail.getEditText().setError("Email already exists");
                            UserEmail.requestFocus();
                        }
                    }
                }
            });

        }


    }

    private void makeobjects() {
        info = new StoreInfo(ShopName.getText().toString(), "");
        user = new StoreUser(UserName.getEditText().getText().toString().trim().toLowerCase(), UserEmail.getEditText().getText().toString().trim().toLowerCase(),
                UserPhone.getEditText().getText().toString(), photoUri != null, permission, "",
                "All", true);
        RegisterUser(user, null);
    }

    private void RegisterUser(final StoreUser user, final String key) {
        SDProgress(true,"Storing info");
        firebaseFirestore.runTransaction(new Transaction.Function<Void>() {
            @Nullable
            @Override
            public Void apply(@NonNull Transaction transaction) throws FirebaseFirestoreException {
                if (key == null) {
                    if (transaction.get(firebaseFirestore.collection("index").document("maxShopCodeIndex")).exists()) {
                        Shopno = (Long) transaction.get(firebaseFirestore.collection("index").document("maxShopCodeIndex")).get("ShopCode");
                        Code = "SH" + String.valueOf(Shopno + 1);
                        Map<String, Long> shopcodeindex = new HashMap<>();
                        shopcodeindex.put("ShopCode", Shopno + 1);
                        transaction.set(firebaseFirestore.collection("index").document("maxShopCodeIndex"), shopcodeindex);
                    } else {
                        Code = "SH1";
                        Shopno = 1L;
                        Map<String, Long> shopcodeindex = new HashMap<>();
                        shopcodeindex.put("ShopCode", 1L);
                        transaction.set(firebaseFirestore.collection("index").document("maxShopCodeIndex"), shopcodeindex);
                    }
                    info.Code = Code;
                    user.ShopCode = Code;
                    transaction.set(firebaseFirestore.collection("StoreInfo").document(), info);
                    transaction.set(firebaseFirestore.collection("UserDetails").document(), (user));
                } else {
                    user.Registeredflag = true;
                    transaction.set(firebaseFirestore.collection("UserDetails").document(key), (user));
                }
                if (photoUri != null) {
                    photoStorageReference.child(Code).child("ProfileImages").child(UserEmail.getEditText().getText().toString())
                            .putFile(photoUri);
                }
                return null;
            }
        }).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    SDProgress(true, "Registering");
                    firebaseAuth.createUserWithEmailAndPassword(user.Email, UserPassword.getEditText().getText().toString())
                            .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                                @Override
                                public void onSuccess(AuthResult authResult) {
                                    SDProgress(false, "");
                                    if (RegisterType == 1) {
                                        showshopcodeassigned();
                                    }else{
                                        Toast.makeText(getApplicationContext(), "Registered account", Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent();
                                        setResult(RESULT_OK, intent);
                                        finish();
                                    }
                                }
                            });
                } else {
                    SDProgress(false, "");
                    Toast.makeText(getApplicationContext(), "Could Not register", Toast.LENGTH_SHORT).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.i("failfirestore", e.getMessage());
            }
        });
    }


    private void showshopcodeassigned() {
        new AlertDialog.Builder(RegisterActivity.this, R.style.MyDialogTheme)
                .setMessage("Shop code assigned is " + Code)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent intent = new Intent();
                        setResult(RESULT_OK, intent);
                        finish();
                    }
                }).setCancelable(false)
                .show();
    }

    public void SDProgress(boolean show, String Text) {
        if (show) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            ProgressLayout.setVisibility(View.VISIBLE);
            ProgressText.setText(Text);
        } else {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            RegisterLayout.setEnabled(true);
            ProgressLayout.setVisibility(View.GONE);
        }
    }
}