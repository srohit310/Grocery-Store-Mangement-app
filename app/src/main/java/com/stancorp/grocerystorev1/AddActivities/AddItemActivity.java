package com.stancorp.grocerystorev1.AddActivities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Transaction;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.stancorp.grocerystorev1.AutoCompleteAdapter.AutoCompleteLocationAdapter;
import com.stancorp.grocerystorev1.Classes.Brand;
import com.stancorp.grocerystorev1.Classes.Category;
import com.stancorp.grocerystorev1.Classes.ItemStockInfo;
import com.stancorp.grocerystorev1.Classes.Items;
import com.stancorp.grocerystorev1.Classes.Location;
import com.stancorp.grocerystorev1.Classes.LocationStockItem;
import com.stancorp.grocerystorev1.Classes.Unit;
import com.stancorp.grocerystorev1.DisplayItems.ScanSkuCodeActivity;
import com.stancorp.grocerystorev1.GlobalClass.Gfunc;
import com.stancorp.grocerystorev1.R;
import com.stancorp.grocerystorev1.SmallRecyclerViewAdapter.CodesLocationRecyclerAdapter;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.security.Permission;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;

public class AddItemActivity extends AppCompatActivity {

    TextView CodeText;
    AutoCompleteTextView LocationSearch;
    AutoCompleteLocationAdapter locationStockAdapter;
    Handler locationhandler = new Handler();
    RecyclerView recyclerView;
    CodesLocationRecyclerAdapter codesRecyclerAdapter;
    RecyclerView.LayoutManager mLayoutManager;
    Button HelpInitialStock;

    Button LocationAdd;
    Button Debug;
    String ShopCode;
    String Mode;
    ArrayList<EditText> editTexts;
    ArrayList<AutoCompleteTextView> autoeditViews;
    CheckBox BoolSku;
    Button Scan;
    ImageView ItemImage;
    Uri photouri;
    CardView salesview, ItemStockview;
    LinearLayout SKUswitchview;

    ArrayList<String> Units;
    AutoCompleteTextView unittext;
    ArrayAdapter UnitAdapter;
    ArrayList<String> Categorys;
    AutoCompleteTextView categorytext;
    ArrayAdapter categoryAdapter;
    ArrayList<String> Brands;
    AutoCompleteTextView brandtext;
    ArrayAdapter brandAdapter;

    ArrayList<String> ItemDetailsString;
    LinkedHashMap<String, Location> locations;

    LinkedHashMap<String, LocationStockItem> locationStockItems;
    ArrayList<String> LocationCodes;
    LinkedHashMap<String, Float> StockValue;

    Gfunc gfunc;
    float total_balance = (float) 0;
    float total_cost = (float) 0;

    RadioGroup Validity;
    Boolean Photoexists;
    RadioButton valid;
    Boolean ItemValid;
    Items item;
    ItemStockInfo itemStockInfo;
    Boolean ImageUpdated;
    String Default_Reorder_qty;

    //AddItemAlertDialogViews
    ArrayList<EditText> alertEditTexts;

    //Firebase
    FirebaseFirestore firebaseFirestore;
    FirebaseStorage firebaseStorage;
    StorageReference photostorage;

    //Progress
    RelativeLayout ProgressLayout;
    ProgressBar AutoProgress;

    private static final int RC_PHOTO_PICKER = 4;
    private static final int RC_SKU_CODE_PICKER = 6;
    public static final int STOREAGE_PERMISSION_CODE = 21;
    public static final int CAMERA_PERMISSION_CODE = 22;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_additem);

        ShopCode = getIntent().getStringExtra("ShopCode");
        Mode = getIntent().getStringExtra("Mode");
        ProgressLayout = findViewById(R.id.ProgressLayout);

        //SupportActionBar
        if (checkmode("Add"))
            getSupportActionBar().setTitle("New Item");
        else getSupportActionBar().setTitle("Edit Item");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        gfunc = new Gfunc();

        //Arraylists initialization
        Units = new ArrayList<>();
        Categorys = new ArrayList<>();
        Brands = new ArrayList<>();
        locations = new LinkedHashMap<>();
        locationStockItems = new LinkedHashMap<>();
        LocationCodes = new ArrayList<>();
        StockValue = new LinkedHashMap<>();

        ItemDetailsString = new ArrayList<>(Arrays.asList("Item Code", "Name", "Selling Price", "Purchase Price",
                "Stock Type", "Default Reorder Quantity", "Reorder Level", "Excess Level", "Unit", "Category", "Brand"));
        editTexts = new ArrayList<EditText>(Arrays.<EditText>asList(
                (EditText) findViewById(R.id.AddEditText1), (EditText) findViewById(R.id.AddEditText2),
                (EditText) findViewById(R.id.AddEditText3), (EditText) findViewById(R.id.AddEditText4),
                (EditText) findViewById(R.id.AddEditText5), (EditText) findViewById(R.id.AddEditText6),
                (EditText) findViewById(R.id.AddEditText7), (EditText) findViewById(R.id.AddEditText8)
        ));
        for (int i = 2; i < editTexts.size(); i++) {
            if (i != 4)
                editTexts.get(i).setRawInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        }
        autoeditViews = new ArrayList<>(Arrays.<AutoCompleteTextView>asList(
                (AutoCompleteTextView) findViewById(R.id.unitEditText1), (AutoCompleteTextView) findViewById(R.id.unitEditText2)
                , (AutoCompleteTextView) findViewById(R.id.unitEditText3)
        ));


        //firebase
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();
        photostorage = firebaseStorage.getReference();

        // Initalizing indivitual views
        salesview = findViewById(R.id.Salesinfoview);
        ItemStockview = findViewById(R.id.Additemstocklayout);
        CodeText = findViewById(R.id.AddTextview1);
        Scan = findViewById(R.id.ScanSku);
        Scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ContextCompat.checkSelfPermission(AddItemActivity.this,
                        Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED){
                    startActivityForResult(new Intent(getApplicationContext(), ScanSkuCodeActivity.class),RC_SKU_CODE_PICKER);
                }else
                    requeststoragepermission(Manifest.permission.CAMERA,"In order to scan Barcode" +
                            " permission to use camera is required",CAMERA_PERMISSION_CODE);
            }
        });
        LocationSearch = findViewById(R.id.LocationsearchAuto);
        Validity = findViewById(R.id.radioGroup);
        ItemValid = true;
        Validity.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                int selectedId = Validity.getCheckedRadioButtonId();
                valid = (RadioButton) findViewById(selectedId);
                if (valid.getText().toString().compareTo("Valid") == 0) {
                    ItemValid = true;
                } else ItemValid = false;
            }
        });

        // Setting up ListView
        recyclerView = findViewById(R.id.LocationStockList);
        mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        codesRecyclerAdapter = new CodesLocationRecyclerAdapter(getApplicationContext(), locationStockItems, LocationCodes, StockValue);
        recyclerView.setAdapter(codesRecyclerAdapter);

        //Helper Button
        Debug = findViewById(R.id.Debug);
        Debug.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                debug();
            }
        });
        Debug.setVisibility(View.GONE);

        HelpInitialStock = findViewById(R.id.HelpInitialStock);
        HelpInitialStock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String Message = "Set the inital Stock Quantity of this Item at the various locations of the shop" + "\n" +
                        "(For Locations not added, intial stock will be set to zero and reorder quantity will be set to default)";
                HelperAlertDialog(Message);
            }
        });

        //Setting up AutoCompleteTextView for location search
        AutoProgress = findViewById(R.id.autoprogress);
        LocationSearch.setThreshold(0);
        addLocations("");
        LocationSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                locationhandler.removeCallbacksAndMessages(null);
            }

            @Override
            public void afterTextChanged(final Editable editable) {
                LocationSearch.dismissDropDown();
                if (editable.toString().length() > 0) {
                    AutoProgress.setVisibility(View.VISIBLE);
                    if (locations.containsKey(editable.toString())) {
                        AutoProgress.setVisibility(View.GONE);
                        return;
                    }
                } else
                    AutoProgress.setVisibility(View.GONE);

                locationhandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (editable.toString().length() > 0) {
                            addLocations(editable.toString());
                        }
                    }
                }, 1500);
            }
        });
        locationStockAdapter = new AutoCompleteLocationAdapter(getApplicationContext(), locations);
        LocationSearch.setAdapter(locationStockAdapter);
        if (checkmode("Edit")) {
            Validity.setVisibility(View.GONE);
            item = (Items) getIntent().getSerializableExtra("Item");
            itemStockInfo = (ItemStockInfo) getIntent().getSerializableExtra("ItemStockInfo");
            salesview.setVisibility(View.GONE);
            Fillviews();
        }

        //Setting up AutoCompleteTextView for codesmaster
        unittext = findViewById(R.id.unitEditText1);
        unittext.setThreshold(0);
        categorytext = findViewById(R.id.unitEditText2);
        categorytext.setThreshold(0);
        brandtext = findViewById(R.id.unitEditText3);
        brandtext.setThreshold(0);
        addCodesMaster();


        //Buttons initialization
        SKUswitchview = findViewById(R.id.Skuswitchlayout);
        LocationAdd = findViewById(R.id.LocationaddButton);
        BoolSku = findViewById(R.id.BooleanSku);
        LocationAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addItemStockinLocation();
            }
        });
        if (checkmode("Add")) {
            BoolSku.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    if (b) {
                        CodeText.setText("SKU *");
                        Scan.setVisibility(View.VISIBLE);
                    } else {
                        CodeText.setText("Item Code *");
                        Scan.setVisibility(View.GONE);
                    }
                }
            });
        } else {
            SKUswitchview.setVisibility(View.GONE);
            editTexts.get(0).setEnabled(false);
        }

        //ItemImage
        ImageUpdated = false;
        photouri = null;
        ItemImage = findViewById(R.id.ItemImage);
        ItemImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ContextCompat.checkSelfPermission(AddItemActivity.this,
                        Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    CropImage.activity()
                            .setGuidelines(CropImageView.Guidelines.ON)
                            .setAspectRatio(1, 1)
                            .start(AddItemActivity.this);
                } else
                    requeststoragepermission(Manifest.permission.READ_EXTERNAL_STORAGE,"In order to access gallery permission" +
                            " to read external storage is required",STOREAGE_PERMISSION_CODE);
            }
        });

    }

    private void Fillviews() {
        SDProgress(true);
        editTexts.get(0).setText(item.ItemCode);
        editTexts.get(1).setText(item.name);
        editTexts.get(2).setText(String.valueOf(item.Selling_Price));
        editTexts.get(3).setText(item.Buying_Price);
        editTexts.get(4).setText(item.Stock_Type);
        editTexts.get(5).setText(itemStockInfo.Default_Reorder_Quantity);
        editTexts.get(6).setText(item.Reorder_Lvl);
        editTexts.get(7).setText(item.Excess_LvL);
        autoeditViews.get(0).setText(item.Unit);
        autoeditViews.get(1).setText(item.Category);
        autoeditViews.get(2).setText(item.Brand);
        if (item.Imguri) {
            photostorage.child(ShopCode).child(item.ItemCode).getDownloadUrl()
                    .addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            photouri = uri;
                            ItemImage.setForeground(null);
                            Glide.with(ItemImage.getContext())
                                    .load(photouri)
                                    .into(ItemImage);
                            SDProgress(false);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    SDProgress(false);
                }
            });
        } else {
            SDProgress(false);
        }
    }

    private void debug() {
        Toast.makeText(getApplicationContext(), String.valueOf(Units.size()), Toast.LENGTH_SHORT).show();
    }

    private void addItemStockinLocation() {
        firebaseFirestore.collection(ShopCode).document("doc").collection("LocationDetails")
                .whereEqualTo("code", LocationSearch.getText().toString()).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.getResult().isEmpty()) {
                            LocationSearch.setText("");
                            LocationSearch.requestFocus();
                            LocationSearch.setError("Location Code does not exist");
                        } else {
                            Boolean Locationexists = true;
                            if (LocationCodes.contains(LocationSearch.getText().toString())) {
                                Locationexists = false;
                                LocationSearch.setText("");
                                LocationSearch.requestFocus();
                                LocationSearch.setError("Location Code already entered");
                            }
                            if (Locationexists) {
                                checkmodeadditem();
                            }
                        }
                    }
                });

    }

    private void checkmodeadditem(){
        if(checkmode("Edit")){
            firebaseFirestore.collection(ShopCode).document("doc").collection("Location")
                    .document(item.ItemCode+LocationSearch.getText().toString()).get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if(task.getResult().exists()){
                                LocationSearch.setText("");
                                LocationSearch.requestFocus();
                                LocationSearch.setError("Initial stock already set");
                            }else{
                                alertDiaglogAddItem();
                            }
                        }
                    });
        }else{
            alertDiaglogAddItem();
        }
    }

    private void alertDiaglogAddItem() {
        AlertDialog.Builder builder = new AlertDialog.Builder(AddItemActivity.this, R.style.AlertDialogTheme);
        View view = LayoutInflater.from(getApplicationContext()).inflate(
                R.layout.alertdialog_additem, (RelativeLayout) findViewById(R.id.additemalertcontainer)
        );
        builder.setView(view);
        final AlertDialog alertDialog = builder.create();
        view.findViewById(R.id.CancelAddItemButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });

        alertEditTexts = new ArrayList<EditText>(new ArrayList<EditText>(Arrays.<EditText>asList(
                (EditText) view.findViewById(R.id.AddItemOpeningStock), (EditText) view.findViewById(R.id.AddItemOpeningValue),
                (EditText) view.findViewById(R.id.AddItemReorderQty)
        )));
        for (int i = 0; i < alertEditTexts.size(); i++) {
            alertEditTexts.get(i).setRawInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        }

        view.findViewById(R.id.ConfirmAddAdminButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Boolean flag = true;
                for (int i = 0; i < alertEditTexts.size(); i++) {
                    String text = alertEditTexts.get(i).getText().toString();
                    if (text.length() == 0 || gfunc.checkifcharexistsmorethanonce(text, '.')) {
                        alertEditTexts.get(i).setText("");
                        alertEditTexts.get(i).requestFocus();
                        alertEditTexts.get(i).setError("Enter Valid Value");
                        flag = false;
                        break;
                    }
                }
                if (flag) {
                    String tempCode = LocationSearch.getText().toString();
                    Float tempOpenValue = Float.parseFloat(alertEditTexts.get(1).getText().toString());
                    LocationStockItem tempLCI = new LocationStockItem(alertEditTexts.get(0).getText().toString(),
                            alertEditTexts.get(2).getText().toString(), true, "", tempCode);
                    locationStockItems.put(tempCode, tempLCI);
                    LocationCodes.add(tempCode);
                    StockValue.put(tempCode, tempOpenValue);
                    codesRecyclerAdapter.notifyDataSetChanged();
                    alertDialog.dismiss();
                }

            }
        });
        if (alertDialog.getWindow() != null) {
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }
        alertDialog.show();
    }

    private void addLocations(final String search) {
        locations.clear();
        String strFrontCode = "", strEndCode = "", startcode = "", endcode = "";
        int strlength = search.length();

        if (search.compareTo("") != 0) {
            strFrontCode = search.substring(0, strlength - 1);
            strEndCode = search.substring(strlength - 1, search.length());
            startcode = search;
            endcode = strFrontCode + Character.toString((char) (strEndCode.charAt(0) + 1));
        } else {
            startcode = "a";
            endcode = "{";
        }

        firebaseFirestore.collection(ShopCode).document("doc")
                .collection("LocationDetails").whereGreaterThanOrEqualTo("name", startcode)
                .whereLessThan("name", endcode).limit(10).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                AutoProgress.setVisibility(View.GONE);
                if (!task.getResult().isEmpty()) {
                    for (DocumentSnapshot doc : task.getResult()) {
                        Location location = doc.toObject(Location.class);
                        locations.put(location.code, location);
                    }
                    LocationSearch.showDropDown();
                    locationStockAdapter.updateList(locations);
                } else {
                    if (search.compareTo("") == 0) {
                        LocationSearch.setEnabled(false);
                        LocationSearch.setText("No location added");
                        LocationAdd.setEnabled(false);
                        LocationAdd.setAlpha(0.5f);
                    } else {
                        LocationSearch.setError("No Result Found");
                    }
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder alert = new AlertDialog.Builder(this, R.style.MyDialogTheme)
                .setPositiveButton("Accept", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                })
                .setNegativeButton("Decline", null);
        if (checkmode("Add")) {
            alert.setTitle("Discard Item");
        } else {
            alert.setTitle("Discard Changes");
        }
        alert.show();
    }

    private void addCodesMaster() {

        //Units
        Units = new ArrayList<>();
        firebaseFirestore.collection(ShopCode).document("doc").collection("CodesMaster").document("doc").collection("Units")
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (!task.getResult().isEmpty()) {
                    for (DocumentSnapshot postSnapshot : task.getResult()) {
                        Unit unit = postSnapshot.toObject(Unit.class);
                        Units.add(unit.unit);
                    }
                    UnitAdapter = new ArrayAdapter(AddItemActivity.this, android.R.layout.simple_list_item_1, Units);
                    unittext.setAdapter(UnitAdapter);
                }
            }
        });
        //Category
        Categorys = new ArrayList<>();
        firebaseFirestore.collection(ShopCode).document("doc").collection("CodesMaster").document("doc").collection("Categories")
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (!task.getResult().isEmpty()) {
                    for (DocumentSnapshot postSnapshot : task.getResult()) {
                        Category category = postSnapshot.toObject(Category.class);
                        Categorys.add(category.category);
                    }
                    categoryAdapter = new ArrayAdapter(AddItemActivity.this, android.R.layout.simple_list_item_1, Categorys);
                    categorytext.setAdapter(categoryAdapter);
                }
            }
        });

        //Brand
        Brands = new ArrayList<>();
        firebaseFirestore.collection(ShopCode).document("doc").collection("CodesMaster").document("doc").collection("Brands")
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (!task.getResult().isEmpty()) {
                    for (DocumentSnapshot postSnapshot : task.getResult()) {
                        Brand brand = postSnapshot.toObject(Brand.class);
                        Brands.add(brand.brand);
                    }
                    brandAdapter = new ArrayAdapter(AddItemActivity.this, android.R.layout.simple_list_item_1, Brands);
                    brandtext.setAdapter(brandAdapter);
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                photouri = result.getUri();
                ItemImage.setForeground(null);
                Glide.with(ItemImage.getContext())
                        .load(photouri)
                        .into(ItemImage);
                ImageUpdated = true;
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
        if(requestCode == RC_SKU_CODE_PICKER){
            if(resultCode == RESULT_OK){
                editTexts.get(0).setText(data.getStringExtra("SKUCODE"));
            }
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
                AlertDialog.Builder alert = new AlertDialog.Builder(AddItemActivity.this, R.style.MyDialogTheme);
                if (checkmode("Add")) {
                    alert.setMessage("Confirm Item Details");
                } else {
                    alert.setMessage("Confirm Changes");
                }
                alert.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Checkinputs();
                    }
                }).setNegativeButton("Cancel", null).show();
                return true;
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return false;
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {

        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {

        }
    }

    public void Checkinputs() {
        boolean confirm = true;
        for (int i = 0; i < editTexts.size(); i++) {
            if (editTexts.get(i).getText().toString().compareTo("") == 0 && i != 4) {
                editTexts.get(i).setError("Enter " + ItemDetailsString.get(i));
                editTexts.get(i).requestFocus();
                confirm = false;
                break;
            }
            if ((i == 2 || i == 3 || i == 5 || i == 6 || i == 7)) {
                if (gfunc.checkifcharexistsmorethanonce(editTexts.get(i).getText().toString(), '.')) {
                    editTexts.get(i).setError("Enter Valid Decimal");
                    editTexts.get(i).requestFocus();
                    editTexts.get(i).setText("");
                    confirm = false;
                    break;
                }
            }
        }
        if (confirm)
            for (int i = 0; i < autoeditViews.size(); i++) {
                if (autoeditViews.get(i).getText().toString().compareTo("") == 0) {
                    autoeditViews.get(i).setError("Enter " + ItemDetailsString.get(i + 7));
                    autoeditViews.get(i).requestFocus();
                    confirm = false;
                    break;
                }
            }
        if (confirm) {
            Float ExcessLvl = Float.parseFloat(editTexts.get(7).getText().toString());
            Float ReorderLvl = Float.parseFloat(editTexts.get(6).getText().toString());
            if (ExcessLvl <= ReorderLvl) {
                editTexts.get(7).setError("Excess Level > Reorder Level");
                editTexts.get(7).requestFocus();
                editTexts.get(7).setText("");
                confirm = false;
            }
        }

        if (confirm) {
            createObjects();
        }
    }

    private void createObjects() {
        SDProgress(true);
        total_balance = 0f;
        total_cost = 0f;
        for (int i = 0; i < locationStockItems.size(); i++) {
            LocationStockItem temp = (LocationStockItem) locationStockItems.values().toArray()[i];
            Float LocBalanceQty = Float.parseFloat(temp.Balance_Qty);
            total_balance += LocBalanceQty;
            total_cost += LocBalanceQty * (Float) StockValue.values().toArray()[i];
        }
        Photoexists = (photouri != null);
        item = new Items(editTexts.get(0).getText().toString().trim(), editTexts.get(1).getText().toString().trim().toLowerCase()
                , autoeditViews.get(0).getText().toString().trim(), autoeditViews.get(1).getText().toString().trim()
                , autoeditViews.get(2).getText().toString().trim(), editTexts.get(2).getText().toString().trim()
                , editTexts.get(3).getText().toString().trim(), editTexts.get(4).getText().toString().trim(), ItemValid
                , Photoexists,"", editTexts.get(6).getText().toString().trim(), editTexts.get(7).getText().toString().trim());

        itemStockInfo = new ItemStockInfo(item.ItemCode, item.name,
                String.valueOf(total_cost), String.valueOf(total_balance), editTexts.get(5).getText().toString().trim(),
                String.valueOf(item.Selling_Price), ItemValid);

        Default_Reorder_qty = editTexts.get(5).getText().toString().trim();

        if (Photoexists) {
            photostorage.child(ShopCode).child(item.ItemCode)
                    .putFile(photouri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    photostorage.child(ShopCode).child(item.ItemCode).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            item.ImgUriString = uri.toString();
                            executetransaction();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getApplicationContext(),"Image Uri Could not be Fetched",Toast.LENGTH_SHORT).show();
                            executetransaction();
                        }
                    });
                }
            });
        } else {
            executetransaction();
        }

        executetransaction();
    }

    private void executetransaction() {
        final DocumentReference doc = firebaseFirestore.collection(ShopCode).document("doc").collection("Items")
                .document(editTexts.get(0).getText().toString());
        firebaseFirestore.runTransaction(new Transaction.Function<Void>() {
            @Nullable
            @Override
            public Void apply(@NonNull Transaction transaction) throws FirebaseFirestoreException {
                if (transaction.get(doc).exists() && checkmode("Add")) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            editTexts.get(0).setText("");
                            editTexts.get(0).setError("Item Code already exists");
                        }
                    });
                } else {
                    save(transaction);
                }

                return null;
            }
        }).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    if (checkmode("Add")) {
                        Toast.makeText(getApplicationContext(), "Item Added", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getApplicationContext(), "Item Updated", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    if (checkmode("Add")) {
                        Toast.makeText(getApplicationContext(), "Item Could not be Added", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getApplicationContext(), "Item Could not be Updated", Toast.LENGTH_SHORT).show();
                    }
                }
                finish();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.i("Failfirestore", e.getMessage());
                finish();
            }
        });
    }

    private void save(final Transaction transaction) throws FirebaseFirestoreException {
        DocumentReference doc ;
        doc = firebaseFirestore.collection(ShopCode).document("doc").collection("ItemStockInfo")
                .document(item.ItemCode);
        if(checkmode("Add")) {
            transaction.set(doc, itemStockInfo);
        }else{
            ItemStockInfo tempstockinfo = transaction.get(doc).toObject(ItemStockInfo.class);
            float totalbalanceqty = Float.parseFloat(tempstockinfo.Total_Balance_Quantity) + total_balance;
            float totalprice = Float.parseFloat(tempstockinfo.Total_Price) + total_cost;
            tempstockinfo.Total_Balance_Quantity = String.valueOf(totalbalanceqty);
            tempstockinfo.Total_Price = String.valueOf(totalprice);
            transaction.set(doc,tempstockinfo);
        }
        doc = firebaseFirestore.collection(ShopCode).document("doc").collection("Items")
                .document(item.ItemCode);
        transaction.set(doc, item);
        for (int i = 0; i < LocationCodes.size(); i++) {
            LocationStockItem temploc = (LocationStockItem) locationStockItems.values().toArray()[i];
            temploc.ItemCode = item.ItemCode;
            doc = firebaseFirestore.collection(ShopCode).document("doc").collection("Location").document(item.ItemCode + temploc.LocationCode);
            transaction.set(doc, temploc);
        }
        checkifCodesMastersExists(transaction);

    }

    private void checkifCodesMastersExists(Transaction transaction) {
        //Unit
        String unit = item.Unit.toLowerCase();
        boolean unitexists = Units.contains(unit);
        if (!unitexists) {
            Unit newunit = new Unit(unit);
            transaction.set(firebaseFirestore.collection(ShopCode).document("doc").collection("CodesMaster")
                    .document("doc").collection("Units").document(), newunit);
        }
        //Category
        String category = item.Category.toLowerCase();
        boolean categoryexists = Categorys.contains(category);
        if (!categoryexists) {
            Category newcategory = new Category(category);
            transaction.set(firebaseFirestore.collection(ShopCode).document("doc").collection("CodesMaster")
                    .document("doc").collection("Categories").document(), newcategory);
        }
        //Brand
        String brand = item.Brand.toLowerCase();
        boolean brandexists = Brands.contains(brand);
        if (!brandexists) {
            Brand newbrand = new Brand(brand);
            transaction.set(firebaseFirestore.collection(ShopCode).document("doc").collection("CodesMaster")
                    .document("doc").collection("Brands").document(), newbrand);
        }

    }

    private void requeststoragepermission(final String Permission, String Message, final Integer code) {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Permission)) {
            new AlertDialog.Builder(this)
                    .setTitle("Permission Needed")
                    .setMessage(Message)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            ActivityCompat.requestPermissions(AddItemActivity.this,
                                    new String[]{Permission}, code);
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
                    new String[]{Permission},
                    code);
        }
    }

    private void HelperAlertDialog(String Message) {
        new AlertDialog.Builder(this, R.style.MyDialogTheme)
                .setMessage(Message)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                })
                .show();
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

    public boolean checkmode(String mode) {
        if (Mode.compareTo(mode) == 0) {
            return true;
        } else return false;
    }
}