package com.stancorp.grocerystorev1.DisplayItems;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Handler;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Transaction;
import com.google.firebase.firestore.WriteBatch;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.stancorp.grocerystorev1.AddActivities.AddItemActivity;
import com.stancorp.grocerystorev1.AutoCompleteAdapter.AutoCompleteLocationAdapter;
import com.stancorp.grocerystorev1.Classes.ItemAdjustmentClass;
import com.stancorp.grocerystorev1.Classes.ItemStockInfo;
import com.stancorp.grocerystorev1.Classes.Items;
import com.stancorp.grocerystorev1.Classes.Location;
import com.stancorp.grocerystorev1.Classes.LocationStockItem;
import com.stancorp.grocerystorev1.GlobalClass.Gfunc;
import com.stancorp.grocerystorev1.R;
import com.stancorp.grocerystorev1.ui.main.ItemSectionsPagerAdapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;

public class ItemViewActivity extends AppCompatActivity {
    Items iteminfo;
    String Shopcode;
    String UserName;
    private StorageReference photostorageReference;
    ImageView imageView;
    TextView Name;
    TextView Brand;
    TextView Category;
    RelativeLayout ProgressLayout;
    FirebaseFirestore firebaseFirestore;
    ListenerRegistration itemlistenerRegistration;
    ListenerRegistration itemstocklistenerRegisteration;
    Gfunc gfunc;

    Button add;
    AutoCompleteTextView locationsearch;
    EditText adjustmentamount;
    float adjustedamount;
    RadioGroup Mode;
    RadioButton SelectedMode;
    TextView Locationtext;
    AlertDialog alertDialog;
    ProgressBar AutoProgress;

    String Reason;
    String LocationCode;
    private String modeselected;
    private ItemStockInfo itemStockInfo;

    LinkedHashMap<String, Location> locations;
    AutoCompleteLocationAdapter locationAdapter;
    ItemAdjustmentClass itemAdjustmentClass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view);
        gfunc = new Gfunc();
        locations = new LinkedHashMap<>();
        locationAdapter = new AutoCompleteLocationAdapter(getApplicationContext(), locations);
        LocationCode = "";

        firebaseFirestore = FirebaseFirestore.getInstance();

        ProgressLayout = findViewById(R.id.ProgressLayout);
        Name = findViewById(R.id.ItemName);
        Category = findViewById(R.id.ItemCategory);
        Brand = findViewById(R.id.ItemBrand);

        //getting the intent data
        Intent intent = getIntent();
        Shopcode = intent.getStringExtra("ShopCode");
        iteminfo = (Items) intent.getSerializableExtra("Item");
        UserName = intent.getStringExtra("UserName");

        addItemStockInfo();

        if(iteminfo.Valid){
            invalidateOptionsMenu();
        }

        //Setting up item image
        imageView = findViewById(R.id.ItemDetailsImage);
        SDProgress(true);
        if (iteminfo.Imguri) {
            photostorageReference = FirebaseStorage.getInstance().getReference().child(Shopcode).child(iteminfo.ItemCode);
            photostorageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    imageView.setForeground(null);
                    Glide.with(imageView.getContext())
                            .load(uri)
                            .into(imageView);
                }
            });
        } else {
            imageView.setVisibility(View.GONE);
        }

        setText();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(gfunc.capitalize(iteminfo.ItemCode));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void setPagerAdapter(){
        ItemSectionsPagerAdapter itemSectionsPagerAdapter = new ItemSectionsPagerAdapter(this, getSupportFragmentManager(),
                iteminfo, Shopcode, UserName,itemStockInfo);
        ViewPager viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(itemSectionsPagerAdapter);
        TabLayout tabs = findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);
    }

    private void setText() {
        Name.setText(gfunc.capitalize(iteminfo.name));
        Category.setText(gfunc.capitalize(iteminfo.Category));
        Brand.setText(gfunc.capitalize(iteminfo.Brand));
    }

    private void addLocations(final String search) {
        locations.clear();
        String strFrontCode="",strEndCode="",startcode="",endcode="";
        int strlength = search.length();

        if(search.compareTo("")!=0) {
            strFrontCode = search.substring(0, strlength - 1);
            strEndCode = search.substring(strlength - 1, search.length());
            startcode = search;
            endcode = strFrontCode + Character.toString((char) (strEndCode.charAt(0) + 1));
        }else{
            startcode = "a";
            endcode = "{";
        }

        firebaseFirestore.collection(Shopcode).document("doc")
                .collection("LocationDetails").whereGreaterThanOrEqualTo("name",startcode)
                .whereLessThan("name",endcode).limit(10).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                AutoProgress.setVisibility(View.GONE);
                if(!task.getResult().isEmpty()){
                    if(search.compareTo("")==0){
                        return;
                    }
                    for(DocumentSnapshot doc : task.getResult()){
                        Location location = doc.toObject(Location.class);
                        locations.put(location.code, location);
                    }
                    locationsearch.showDropDown();
                    locationAdapter.updateList(locations);
                }else{
                    if(search.compareTo("")==0){
                        locationsearch.setEnabled(false);
                        locationsearch.setText("No location added");
                        add.setEnabled(false);
                        add.setAlpha(0.5f);
                    }else {
                        locationsearch.setError("No Result Found");
                    }
                }
            }
        });
    }

    @Override
    protected void onPause() {
        itemlistenerRegistration.remove();
        itemstocklistenerRegisteration.remove();
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        addItemStockInfo();
    }

    private void addItemStockInfo() {
        SDProgress(true);
        Query query = firebaseFirestore.collection(Shopcode).document("doc").collection("ItemStockInfo")
                .whereEqualTo("ItemCode", iteminfo.ItemCode);
        itemlistenerRegistration =
                query.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException error) {
                if (queryDocumentSnapshots != null && !queryDocumentSnapshots.isEmpty()) {
                    for (DocumentChange doc : queryDocumentSnapshots.getDocumentChanges()) {
                        ItemStockInfo temp;
                        switch (doc.getType()) {
                            case ADDED:
                                temp = doc.getDocument().toObject(ItemStockInfo.class);
                                itemStockInfo = temp;
                                break;
                            case MODIFIED:
                                temp = doc.getDocument().toObject(ItemStockInfo.class);
                                itemStockInfo = temp;
                                break;
                        }
                    }
                }
                SDProgress(false);
                itemchildlistener();
            }
        });
    }



    private void itemchildlistener() {
        SDProgress(true);
        itemstocklistenerRegisteration =
        firebaseFirestore.collection(Shopcode).document("doc").collection("Items")
                .whereEqualTo("ItemCode", iteminfo.ItemCode).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException error) {
                if (queryDocumentSnapshots != null && !queryDocumentSnapshots.isEmpty()) {
                    for (DocumentChange doc : queryDocumentSnapshots.getDocumentChanges()) {
                        switch (doc.getType()) {
                            case MODIFIED:
                                Items tempitem = doc.getDocument().toObject(Items.class);
                                iteminfo = tempitem;
                                setText();
                                break;
                        }
                    }
                }
                SDProgress(false);
                setPagerAdapter();
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.itemdetialsmenu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.Adjustment:
                alertaddadjustment();
                return true;
            case R.id.Edit:
                Intent intent = new Intent(getApplicationContext(), AddItemActivity.class);
                intent.putExtra("ShopCode", Shopcode);
                intent.putExtra("Mode", "Edit");
                intent.putExtra("Item", iteminfo);
                intent.putExtra("ItemStockInfo", itemStockInfo);
                startActivity(intent);
                return true;
            case R.id.Invalid:
                confirmMakeInvalid();
                return true;
        }

        return (super.onOptionsItemSelected(item));
    }

    private void confirmMakeInvalid() {
        new AlertDialog.Builder(ItemViewActivity.this, R.style.MyDialogTheme)
                .setTitle("Confirm Action")
                .setMessage("You cannot revert this action once done")
                .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        invalidate();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void invalidate() {
        WriteBatch batch = firebaseFirestore.batch();
        batch.update(firebaseFirestore.collection(Shopcode).document("doc").collection("Items")
                .document(iteminfo.ItemCode), "Valid", false);
        batch.update(firebaseFirestore.collection(Shopcode).document("doc").collection("ItemStockInfo")
                .document(iteminfo.ItemCode), "valid", false);
        batch.commit().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(getApplicationContext(), "Item " + iteminfo.name + "is invalid", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), "Failed to invalidate. Try again later", Toast.LENGTH_SHORT).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.i("failvalidate", e.getMessage());
            }
        });
        finish();
    }

    private void alertaddadjustment() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AlertDialogTheme);
        final View alertview = LayoutInflater.from(getApplicationContext()).inflate(
                R.layout.alertdialog_adjustment, (RelativeLayout) findViewById(R.id.addadjustmentalertcontainer)
        );
        builder.setView(alertview);
        adjustmentamount = alertview.findViewById(R.id.AmountAdjusted);
        adjustmentamount.setRawInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);

        locationsearch = alertview.findViewById(R.id.getLocation);
        locationsearch.setAdapter(locationAdapter);
        locationsearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(final Editable editable) {
                locationsearch.dismissDropDown();
                if(editable.toString().length()>0) {
                    AutoProgress.setVisibility(View.VISIBLE);
                    if(locations.containsKey(editable.toString())){
                        AutoProgress.setVisibility(View.GONE);
                        return;
                    }
                }
                else
                    AutoProgress.setVisibility(View.GONE);

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if(editable.toString().length() > 0){
                            addLocations(editable.toString());
                        }
                    }
                }, 1500);
            }
        });
        locationsearch.setThreshold(0);
        locationsearch.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                locationsearch.showDropDown();
            }
        });
        Locationtext = alertview.findViewById(R.id.Locationtextview);
        Spinner reasonspinner = alertview.findViewById(R.id.Reason);
        setupspinner(reasonspinner);

        AutoProgress = alertview.findViewById(R.id.autoprogress);

        Mode = alertview.findViewById(R.id.radioGroup);
        modeselected = "Quantity";

        add = findViewById(R.id.ConfirmAdjustmentButton);

        addLocations("");

        Mode.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                int selectedId = Mode.getCheckedRadioButtonId();
                SelectedMode = (RadioButton) alertview.findViewById(selectedId);
                if (SelectedMode.getText().toString().compareTo("Quantity") == 0) {
                    modeselected = "Quantity";
                    locationsearch.setVisibility(View.VISIBLE);
                    Locationtext.setVisibility(View.VISIBLE);
                } else {
                    modeselected = "Price";
                    locationsearch.setVisibility(View.GONE);
                    Locationtext.setVisibility(View.GONE);
                }
            }
        });

        alertDialog = builder.create();
        alertview.findViewById(R.id.CancelAdjustmentButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });

        alertview.findViewById(R.id.ConfirmAdjustmentButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                Boolean flag = true;
                String text = adjustmentamount.getText().toString();

                if (text.length() == 0 || gfunc.checkifcharexistsmorethanonce(text, '.')) {
                    adjustmentamount.setText("");
                    adjustmentamount.requestFocus();
                    adjustmentamount.setError("Enter Valid Value");
                    flag = false;
                }
                if (flag && modeselected.compareTo("Quantity") == 0) {
                    text = locationsearch.getText().toString();
                    if (text.length() == 0) {
                        locationsearch.setText("");
                        locationsearch.requestFocus();
                        locationsearch.setError("Enter Location");
                        flag = false;
                    }
                }
                if (flag) {
                    adjustedamount = Float.parseFloat(adjustmentamount.getText().toString());
                    LocationCode = locationsearch.getText().toString();
                    String currentdate = gfunc.getCurrentDate();
                    itemAdjustmentClass = new ItemAdjustmentClass(modeselected, iteminfo.ItemCode, LocationCode,
                            currentdate, Reason, "", UserName);
                }
                if (flag && modeselected.compareTo("Quantity") == 0) {
                    firebaseFirestore.collection(Shopcode).document("doc").collection("LocationDetails")
                            .whereEqualTo("code",LocationCode).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if(!task.getResult().isEmpty()){
                                firebaseFirestore.collection(Shopcode).document("doc").collection("Location")
                                        .whereEqualTo("ItemCode",iteminfo.ItemCode).whereEqualTo("LocationCode",LocationCode)
                                        .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        if(!task.getResult().isEmpty()){
                                            adjustquantity();
                                        }else{
                                            locationsearch.setText("");
                                            locationsearch.requestFocus();
                                            locationsearch.setError("Add inital stock via Edit or make a purchase");
                                        }
                                    }
                                });
                            }else{
                                locationsearch.setText("");
                                locationsearch.requestFocus();
                                locationsearch.setError("Location Does not exist");
                            }
                        }
                    });
                } else if (flag && modeselected.compareTo("Price") == 0) {
                    adjustprice();
                }
            }
        });

        if (alertDialog.getWindow() != null) {
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }
        alertDialog.show();
    }

    private void adjustquantity() {
        firebaseFirestore.runTransaction(new Transaction.Function<Void>() {
            @Nullable
            @Override
            public Void apply(@NonNull Transaction transaction) throws FirebaseFirestoreException {
                DocumentReference doc = firebaseFirestore.collection(Shopcode).document("doc").collection("Location")
                        .document(iteminfo.ItemCode+LocationCode);
                LocationStockItem locationStockItem = transaction.get(doc).toObject(LocationStockItem.class);
                Float Bal = Float.valueOf(locationStockItem.Balance_Qty);
                locationStockItem.Balance_Qty = String.valueOf(adjustedamount);
                adjustedamount = adjustedamount - Bal;
                itemAdjustmentClass.AmountAdjusted = String.valueOf(adjustedamount);

                //adjusting amount in itemstockinfo

                Float T_bal = Float.parseFloat(itemStockInfo.Total_Balance_Quantity);
                Float T_Price = Float.parseFloat(itemStockInfo.Total_Price);
                if (T_bal != 0) {
                    T_Price = T_Price + (T_Price / T_bal) * (adjustedamount);
                } else {
                    T_Price = (float) 0;
                }
                T_bal = T_bal + (adjustedamount);
                itemStockInfo.Total_Balance_Quantity = String.valueOf(T_bal);
                itemStockInfo.Total_Price = String.valueOf(T_Price);
                transaction.set(firebaseFirestore.collection(Shopcode).document("doc").collection("ItemAdjustments")
                        .document(), itemAdjustmentClass);
                transaction.set(firebaseFirestore.collection(Shopcode).document("doc").collection("Location")
                        .document(iteminfo.ItemCode+LocationCode), locationStockItem);
                transaction.set(firebaseFirestore.collection(Shopcode).document("doc").collection("ItemStockInfo")
                        .document(iteminfo.ItemCode), itemStockInfo);
                return null;
            }
        }).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(getApplicationContext(), "Quantity Adjusted", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), "Failed to adjust. Try again later", Toast.LENGTH_SHORT).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.i("failquantiyadjust", e.getMessage());
            }
        });
        alertDialog.dismiss();
    }

    private void adjustprice() {
        Float Sp_change = Float.parseFloat(itemStockInfo.Selling_Price);
        itemStockInfo.Selling_Price = String.valueOf(adjustedamount);
        WriteBatch batch = firebaseFirestore.batch();
        batch.update(firebaseFirestore.collection(Shopcode).document("doc").collection("Items")
                .document(iteminfo.ItemCode), "Selling_Price", adjustedamount);
        adjustedamount = adjustedamount - Sp_change;
        itemAdjustmentClass.AmountAdjusted = String.valueOf(adjustedamount);
        batch.set(firebaseFirestore.collection(Shopcode).document("doc").collection("ItemAdjustments")
                .document(), itemAdjustmentClass);
        batch.set(firebaseFirestore.collection(Shopcode).document("doc").collection("ItemStockInfo")
                .document(iteminfo.ItemCode), itemStockInfo);
        batch.commit().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(getApplicationContext(), "Price Adjusted", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), "Failed to adjust. Try again later", Toast.LENGTH_SHORT).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.i("failpriceadjust", e.getMessage());
            }
        });
        Toast.makeText(getApplicationContext(), "Price Adjusted", Toast.LENGTH_SHORT).show();
        alertDialog.dismiss();
    }

    private void setupspinner(Spinner reasonspinner) {
        ArrayAdapter reasonSpinnerAdapter = ArrayAdapter.createFromResource(getApplicationContext(),
                R.array.array_reason_options, R.layout.spinner_user_item_text);

        final ArrayList<Integer> stringRes = new ArrayList<>(Arrays.asList(R.string.fire, R.string.stole, R.string.damaged,
                R.string.written_off, R.string.Stocktaking, R.string.revalutaion));

        reasonSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        reasonspinner.setAdapter(reasonSpinnerAdapter);
        reasonspinner.setSelection(0);
        reasonspinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selection = parent.getItemAtPosition(position).toString();
                if (!TextUtils.isEmpty(selection)) {
                    for (int i = 0; i < stringRes.size(); i++) {
                        if (selection.equals(getString(stringRes.get(i)))) {
                            Reason = getString(stringRes.get(i));
                            break;
                        }
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Reason = getString(R.string.fire);
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

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {

        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {

        }
    }
}