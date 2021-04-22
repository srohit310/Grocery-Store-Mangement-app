package com.stancorp.grocerystorev1.AddActivities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.stancorp.grocerystorev1.AutoCompleteAdapter.AutoCompleteAgentAdapter;
import com.stancorp.grocerystorev1.AutoCompleteAdapter.AutoCompleteItemAdapter;
import com.stancorp.grocerystorev1.AutoCompleteAdapter.AutoCompleteLocationAdapter;
import com.stancorp.grocerystorev1.Classes.Agent;
import com.stancorp.grocerystorev1.Classes.DeliveryAddress;
import com.stancorp.grocerystorev1.Classes.ItemStockInfo;
import com.stancorp.grocerystorev1.Classes.Items;
import com.stancorp.grocerystorev1.Classes.Location;
import com.stancorp.grocerystorev1.Classes.LocationStockItem;
import com.stancorp.grocerystorev1.GlobalClass.Gfunc;
import com.stancorp.grocerystorev1.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;

public class AddTransactionActivity extends AppCompatActivity {

    String Mode;
    String ShopCode;

    Boolean Deliveryboolcheck,DeliveryboolAuto;

    LinearLayout LocationLayout;
    TextInputLayout AddState;
    TextInputLayout AddCity;
    TextInputLayout AddStreet;
    TextInputLayout AddPincode;
    ArrayList<EditText> alertEditTexts;

    Gfunc gfunc;

    LinearLayout DeliveryCheck;
    CheckBox delcheckbox;

    FirebaseFirestore firebaseFirestore;

    AutoCompleteTextView CustomerSearch;
    ProgressBar customerprogress;
    AutoCompleteAgentAdapter agentAdapter;
    LinkedHashMap<String, Agent> Stakeholders;
    Boolean Selectedagentfromdropdown;
    Handler customerhandler = new Handler();

    AutoCompleteTextView LocationSearch;
    ProgressBar locationprogress;
    AutoCompleteLocationAdapter locationAdapter;
    LinkedHashMap<String, Location> locations;
    Boolean Selectedlocationfromdropdown;
    Button Locationcancel;
    Handler locationhandler = new Handler();

    AutoCompleteTextView ItemSearch;
    ProgressBar itemprogress;
    AutoCompleteItemAdapter itemAdapter;
    LinkedHashMap<String, ItemStockInfo> items;
    Handler itemhandler = new Handler();

    final Calendar myCalendar = Calendar.getInstance();
    EditText transactionDate;
    EditText deliveryDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_transaction);

        Selectedagentfromdropdown = false;
        Selectedlocationfromdropdown = false;
        Deliveryboolcheck = true;

        gfunc = new Gfunc();

        Mode = getIntent().getStringExtra("Mode");
        ShopCode = getIntent().getStringExtra("ShopCode");

        if (Mode.compareTo("Purchase") == 0)
            getSupportActionBar().setTitle("Make Purchase");
        else
            getSupportActionBar().setTitle("Make Sale");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Stakeholders = new LinkedHashMap<>();
        locations = new LinkedHashMap<>();
        items = new LinkedHashMap<>();

        LocationLayout = findViewById(R.id.DeliveryLayout);
        AddCity = findViewById(R.id.EnterCity);
        AddState = findViewById(R.id.EnterState);
        AddStreet = findViewById(R.id.EnterStreet);
        AddPincode = findViewById(R.id.EnterPincode);

        DeliveryCheck = findViewById(R.id.DeliveryCheck);
        delcheckbox = findViewById(R.id.delcheckbox);
        delcheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    Deliveryboolcheck = true;
                    LocationLayout.setVisibility(View.VISIBLE);
                }else{
                    Deliveryboolcheck = false;
                    LocationLayout.setVisibility(View.GONE);
                }
            }
        });
        Locationcancel = findViewById(R.id.cancelLocation);
        Locationcancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LocationSearch.setEnabled(true);
                LocationSearch.setText("");
                Locationcancel.setVisibility(View.GONE);
                Selectedlocationfromdropdown = false;
            }
        });


        firebaseFirestore = FirebaseFirestore.getInstance();

        //Date - {Setting up the date}//
        //                           //
        //////////////////////////////

        Date date = new Date();
        String currentdate = (String) DateFormat.format("dd/MM/yyyy", date.getTime());
        transactionDate = findViewById(R.id.TransactionDate);
        deliveryDate = findViewById(R.id.DeliveryDate);
        transactionDate.setText(currentdate);
        setupDate(transactionDate, false);
        setupDate(deliveryDate, true);

        //Date - {Setting up the date}//
        //                           //
        //////////////////////////////


        //Autocomplete Location - {Setting up the autocompletelistene}//
        //                                                            //
        ////////////////////////////////////////////////////////////////
        locationAdapter = new AutoCompleteLocationAdapter(getApplicationContext(),locations);
        LocationSearch = findViewById(R.id.LocationsearchAuto);
        LocationSearch.setThreshold(0);
        LocationSearch.setAdapter(locationAdapter);
        locationprogress = findViewById(R.id.autolocationprogress);
        addLocations("");
        LocationSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                locationhandler.removeCallbacksAndMessages(null);
                Selectedlocationfromdropdown = false;
                if(Mode.compareTo("Vendor")==0) {
                    setLocationtexts(null, false);
                }
            }

            @Override
            public void afterTextChanged(final Editable editable) {
                LocationSearch.dismissDropDown();
                if (editable.toString().length() > 0) {
                    locationprogress.setVisibility(View.VISIBLE);
                    if (locations.containsKey(editable.toString())) {
                        locationprogress.setVisibility(View.GONE);
                        return;
                    }
                } else
                    locationprogress.setVisibility(View.GONE);

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
        LocationSearch.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Selectedlocationfromdropdown = true;
                Location location = (Location) locationAdapter.getItem(i);
                Locationcancel.setVisibility(View.VISIBLE);
                LocationSearch.setEnabled(false);
                if(Mode.compareTo("Vendor")==0)
                    setLocationtexts(location.address,true);
            }
        });

        //Autocomplete Location - {Setting up the autocompletelistene}//
        //                                                            //
        ////////////////////////////////////////////////////////////////



        //Autocomplete customer - {Setting up the autocompletelistene}//
        //                                                            //
        ////////////////////////////////////////////////////////////////
        agentAdapter = new AutoCompleteAgentAdapter(getApplicationContext(), Stakeholders);
        CustomerSearch = findViewById(R.id.AgentsearchAuto);
        CustomerSearch.setThreshold(0);
        CustomerSearch.setAdapter(agentAdapter);
        customerprogress = findViewById(R.id.autoprogress);
        addAgents("");
        CustomerSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                customerhandler.removeCallbacksAndMessages(null);
                Selectedagentfromdropdown = false;
                setLocationtexts(null,false);
                Deliveryboolcheck = false;
                delcheckbox.setChecked(true);
                DeliveryCheck.setVisibility(View.GONE);
            }

            @Override
            public void afterTextChanged(final Editable editable) {
                CustomerSearch.dismissDropDown();
                if (editable.toString().length() > 0) {
                    customerprogress.setVisibility(View.VISIBLE);
                    if (Stakeholders.containsKey(editable.toString())) {
                        customerprogress.setVisibility(View.GONE);
                        return;
                    }
                } else
                    customerprogress.setVisibility(View.GONE);

                customerhandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (editable.toString().length() > 0) {
                            addAgents(editable.toString());
                        }
                    }
                }, 1500);
            }
        });
        CustomerSearch.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Selectedagentfromdropdown = true;
                CustomerSearch.dismissDropDown();
                Agent agent = (Agent) agentAdapter.getItem(i);
                if(agent.CustomerType.compareTo("Individual")==0){
                    DeliveryCheck.setVisibility(View.VISIBLE);
                }
                setLocationtexts(agent.deladdress,true);
            }
        });
        //Autocomplete customer - {Setting up the autocompletelistene}//
        //                                                            //
        ////////////////////////////////////////////////////////////////

        //Autocomplete ITEM ITEM ITEM - {Setting up the autocompletelistene}//
        //                                                                  //
        //////////////////////////////////////////////////////////////////////

        itemAdapter = new AutoCompleteItemAdapter(getApplicationContext(),items);
        ItemSearch = findViewById(R.id.ItemsearchAuto);
        ItemSearch.setThreshold(0);
        ItemSearch.setAdapter(itemAdapter);
        itemprogress = findViewById(R.id.autoitemprogress);
        addItems("");
        ItemSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                itemhandler.removeCallbacksAndMessages(null);
            }

            @Override
            public void afterTextChanged(final Editable editable) {
                ItemSearch.dismissDropDown();
                if (editable.toString().length() > 0) {
                    itemprogress.setVisibility(View.VISIBLE);
                    if (items.containsKey(editable.toString())) {
                        itemprogress.setVisibility(View.GONE);
                        return;
                    }
                } else
                    itemprogress.setVisibility(View.GONE);

                itemhandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (editable.toString().length() > 0) {
                            addItems(editable.toString());
                        }
                    }
                }, 1500);
            }
        });
        ItemSearch.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                ItemStockInfo itemStockInfo = (ItemStockInfo) itemAdapter.getItem(i);
                if(Selectedlocationfromdropdown) {
                    fetchlocationbalancestock(itemStockInfo.Selling_Price,itemStockInfo.ItemCode);
                }else{
                    ItemSearch.setText("");
                    LocationSearch.setError("Please 'Select' Location Code");
                    LocationSearch.requestFocus();
                }
            }
        });

        //Autocomplete ITEM ITEM ITEM - {Setting up the autocompletelistene}//
        //                                                                  //
        //////////////////////////////////////////////////////////////////////

    }

    private void fetchlocationbalancestock(final String selling_price, String itemCode) {
        firebaseFirestore.collection(ShopCode).document("doc").collection("Location")
                .document(itemCode+LocationSearch.getText().toString()).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.getResult().exists()){
                            LocationStockItem locationStockItem = task.getResult().toObject(LocationStockItem.class);
                            alertitemadd(locationStockItem.Balance_Qty,selling_price);
                        }else{
                            ItemSearch.setText("");
                            ItemSearch.setError("Item Balance is 0 at location");
                        }
                    }
                });
    }


    private void alertitemadd(String balance_Qty,String selling_price) {
        AlertDialog.Builder builder = new AlertDialog.Builder(AddTransactionActivity.this, R.style.AlertDialogTheme);
        View view = LayoutInflater.from(getApplicationContext()).inflate(
                R.layout.alertdialog_additemtotransaction, (RelativeLayout) findViewById(R.id.additemalertcontainer)
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
                (EditText) view.findViewById(R.id.AddItemStock), (EditText) view.findViewById(R.id.AddItemSellingValue),
                (EditText) view.findViewById(R.id.AddItemBalanceQty)
        )));
        for (int i = 0; i < alertEditTexts.size(); i++) {
            alertEditTexts.get(i).setRawInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        }

        alertEditTexts.get(1).setText(selling_price);
        alertEditTexts.get(2).setText(balance_Qty);

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
                }

            }
        });
        if (alertDialog.getWindow() != null) {
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }
        alertDialog.show();
    }

    private void addItems(final String search) {
        items.clear();
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
                .collection("ItemStockInfo").whereGreaterThanOrEqualTo("name", startcode)
                .whereLessThan("name", endcode).whereEqualTo("valid",true)
                .limit(10).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                itemprogress.setVisibility(View.GONE);
                if (!task.getResult().isEmpty()) {
                    if (search.compareTo("") == 0) {
                        return;
                    }
                    for (DocumentSnapshot doc : task.getResult()) {
                        ItemStockInfo itemStockInfo = doc.toObject(ItemStockInfo.class);
                        items.put(itemStockInfo.ItemCode, itemStockInfo);
                    }
                    ItemSearch.showDropDown();
                    itemAdapter.updateList(items);
                } else {
                    if (search.compareTo("") == 0) {
                        ItemSearch.setEnabled(false);
                        ItemSearch.setText("No Items added");
                    } else {
                        ItemSearch.setError("No Result Found");
                    }
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.i("failaddtransac",e.getMessage());
            }
        });
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
                .whereLessThan("name", endcode)
                .limit(10).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                locationprogress.setVisibility(View.GONE);
                if (!task.getResult().isEmpty()) {
                    if (search.compareTo("") == 0) {
                        return;
                    }
                    for (DocumentSnapshot doc : task.getResult()) {
                        Location location = doc.toObject(Location.class);
                        locations.put(location.code, location);
                    }
                    LocationSearch.showDropDown();
                    locationAdapter.updateList(locations);
                } else {
                    if (search.compareTo("") == 0) {
                        LocationSearch.setEnabled(false);
                        LocationSearch.setText("No Locations added");
                    } else {
                        LocationSearch.setError("No Result Found");
                    }
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.i("failaddtransac",e.getMessage());
            }
        });
    }

    private void setLocationtexts(DeliveryAddress address,Boolean visible){
        if(!visible) {
            LocationLayout.setVisibility(View.GONE);
        }else{
            LocationLayout.setVisibility(View.VISIBLE);
        }
        if(address == null){
            address = new DeliveryAddress("","","",0);
        }
        AddCity.getEditText().setText(address.City);
        AddStreet.getEditText().setText(address.Street);
        AddState.getEditText().setText(address.State);
        AddPincode.getEditText().setText(String.valueOf(address.Pincode));
    }

    private void addAgents(final String search) {
        Stakeholders.clear();
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
                .collection("StakeHolders").whereGreaterThanOrEqualTo("Name", startcode)
                .whereLessThan("Name", endcode).whereIn("AgentType", Arrays.asList("Both","Customer"))
                .limit(10).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                customerprogress.setVisibility(View.GONE);
                if (!task.getResult().isEmpty()) {
                    if (search.compareTo("") == 0) {
                        return;
                    }
                    for (DocumentSnapshot doc : task.getResult()) {
                        Agent agent = doc.toObject(Agent.class);
                        Stakeholders.put(agent.Code, agent);
                    }
                    CustomerSearch.showDropDown();
                    agentAdapter.updateList(Stakeholders);
                } else {
                    if (search.compareTo("") == 0) {
                        CustomerSearch.setEnabled(false);
                        CustomerSearch.setText("No Customers added");
                    } else {
                        CustomerSearch.setError("No Result Found");
                    }
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.i("failaddtransac",e.getMessage());
            }
        });
    }

    private void setupDate(final EditText editText, final Boolean min) {

        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker datePicker, int year, int monthOfYear, int dayOfMonth) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel(editText);
            }
        };

        editText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(AddTransactionActivity.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH));
                if (min)
                    datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
                datePickerDialog.show();
            }
        });
    }

    private void updateLabel(EditText editText) {
        String myFormat = "dd/MM/yyyy";
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat);
        editText.setText(sdf.format(myCalendar.getTime()));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.add_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        return true;
    }
}