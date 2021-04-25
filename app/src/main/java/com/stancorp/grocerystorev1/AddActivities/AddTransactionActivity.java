package com.stancorp.grocerystorev1.AddActivities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
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
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Transaction;
import com.stancorp.grocerystorev1.AdapterClasses.BaseRecyclerAdapter;
import com.stancorp.grocerystorev1.AdapterClasses.ItemTransactionRecyclerAdapter;
import com.stancorp.grocerystorev1.AutoCompleteAdapter.AutoCompleteAgentAdapter;
import com.stancorp.grocerystorev1.AutoCompleteAdapter.AutoCompleteItemAdapter;
import com.stancorp.grocerystorev1.AutoCompleteAdapter.AutoCompleteLocationAdapter;
import com.stancorp.grocerystorev1.Classes.Agent;
import com.stancorp.grocerystorev1.Classes.DeliveryAddress;
import com.stancorp.grocerystorev1.Classes.ItemStockInfo;
import com.stancorp.grocerystorev1.Classes.Itemtransaction;
import com.stancorp.grocerystorev1.Classes.Location;
import com.stancorp.grocerystorev1.Classes.LocationStockItem;
import com.stancorp.grocerystorev1.Classes.StoreTransaction;
import com.stancorp.grocerystorev1.Classes.maxindex;
import com.stancorp.grocerystorev1.GlobalClass.Gfunc;
import com.stancorp.grocerystorev1.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class AddTransactionActivity extends AppCompatActivity implements BaseRecyclerAdapter.OnNoteListner {

    String Mode;
    String ShopCode;
    String Username;
    String Useremail;
    RelativeLayout ProgressLayout;
    String CurrentDate;
    String type;

    Boolean Deliveryboolcheck;

    LinearLayout LocationLayout;
    ArrayList<TextInputLayout> textInputLayouts;
    ArrayList<EditText> alertEditTexts;

    Gfunc gfunc;

    LinearLayout DeliveryCheck;
    CheckBox delcheckbox;
    EditText ReferenceCode;

    FirebaseFirestore firebaseFirestore;

    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    LinkedHashMap<String, Itemtransaction> transactionitemlist;
    HashMap<String,String> itemreqorderqty;
    public float totalcost;
    List<String> Units;
    ItemTransactionRecyclerAdapter transactionitemAdapter;
    public TextView TotalCostText;
    public RelativeLayout BillTop;
    public LinearLayout TotalPriceLayout;

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

    //For creating objects
    ArrayList<Itemtransaction> itemtransactions;
    StoreTransaction storeTransaction;

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
        Username = getIntent().getStringExtra("Username");
        Useremail = getIntent().getStringExtra("Useremail");
        CurrentDate = "";
        ProgressLayout = findViewById(R.id.ProgressLayout);

        if (Mode.compareTo("Vendor") == 0)
            getSupportActionBar().setTitle("Make Purchase");
        else
            getSupportActionBar().setTitle("Make Sale");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Stakeholders = new LinkedHashMap<>();
        locations = new LinkedHashMap<>();
        items = new LinkedHashMap<>();
        itemreqorderqty = new HashMap<>();

        LocationLayout = findViewById(R.id.DeliveryLayout);
        textInputLayouts = new ArrayList<>(Arrays.asList((TextInputLayout) findViewById(R.id.EnterState), (TextInputLayout) findViewById(R.id.EnterCity),
                (TextInputLayout) findViewById(R.id.EnterStreet), (TextInputLayout) findViewById(R.id.EnterPincode)));
        ReferenceCode = findViewById(R.id.Referenceid);

        DeliveryCheck = findViewById(R.id.DeliveryCheck);
        delcheckbox = findViewById(R.id.delcheckbox);
        delcheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(Mode.compareTo("Customer")==0) {
                    if (b) {
                        Deliveryboolcheck = true;
                        LocationLayout.setVisibility(View.VISIBLE);
                    } else {
                        Deliveryboolcheck = false;
                        LocationLayout.setVisibility(View.GONE);
                    }
                }
            }
        });
        Locationcancel = findViewById(R.id.cancelLocation);


        firebaseFirestore = FirebaseFirestore.getInstance();

        //Recycler View - {Setting up the the items list}//
        //                                              //
        /////////////////////////////////////////////////

        recyclerView = findViewById(R.id.ItemList);
        layoutManager = new LinearLayoutManager(getApplicationContext());
        transactionitemlist = new LinkedHashMap<>();
        Units = new ArrayList<>();
        totalcost = 0.0f;
        TotalCostText = findViewById(R.id.TotalCostText);
        TotalCostText.setText(String.valueOf(totalcost) + " INR");
        transactionitemAdapter = new ItemTransactionRecyclerAdapter(this, getApplicationContext(), this,
                transactionitemlist,itemreqorderqty);
        recyclerView.setAdapter(transactionitemAdapter);
        recyclerView.setLayoutManager(layoutManager);
        BillTop = findViewById(R.id.billtop);
        TotalPriceLayout = findViewById(R.id.totalpricelayout);
        BillTop.setVisibility(View.GONE);
        TotalPriceLayout.setVisibility(View.GONE);

        Locationcancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LocationSearch.setEnabled(true);
                LocationSearch.setText("");
                Locationcancel.setVisibility(View.GONE);
                Selectedlocationfromdropdown = false;
                transactionitemlist.clear();
                totalcost = 0.0f;
                BillTop.setVisibility(View.GONE);
                TotalPriceLayout.setVisibility(View.GONE);
            }
        });

        //Recycler View - {Setting up the the items list}//
        //                                              //
        /////////////////////////////////////////////////


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
        locationAdapter = new AutoCompleteLocationAdapter(getApplicationContext(), locations);
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
                if (Mode.compareTo("Vendor") == 0) {
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
                if (Mode.compareTo("Vendor") == 0)
                    setLocationtexts(location.address, true);
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
                if (Mode.compareTo("Customer") == 0)
                    setLocationtexts(null, false);
                Deliveryboolcheck = true;
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
                InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(CustomerSearch.getWindowToken(), 0);
                Agent agent = (Agent) agentAdapter.getItem(i);
                if (agent.CustomerType.compareTo("Individual") == 0) {
                    DeliveryCheck.setVisibility(View.VISIBLE);
                }
                if (Mode.compareTo("Customer") == 0) {
                    Toast.makeText(getApplicationContext(),"",Toast.LENGTH_SHORT).show();
                    setLocationtexts(agent.deladdress, true);
                }
            }
        });
        //Autocomplete customer - {Setting up the autocompletelistene}//
        //                                                            //
        ////////////////////////////////////////////////////////////////

        //Autocomplete ITEM ITEM ITEM - {Setting up the autocompletelistene}//
        //                                                                  //
        //////////////////////////////////////////////////////////////////////

        itemAdapter = new AutoCompleteItemAdapter(getApplicationContext(), items);
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
                if (Selectedlocationfromdropdown) {
                    SDProgress(true);
                    fetchlocationbalancestock(itemStockInfo);
                } else {
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

    private void fetchlocationbalancestock(final ItemStockInfo itemStockInfo) {
        firebaseFirestore.collection(ShopCode).document("doc").collection("Location")
                .document(itemStockInfo.ItemCode + LocationSearch.getText().toString()).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        SDProgress(false);
                        if (task.getResult().exists() || Mode.compareTo("Vendor")==0) {
                            LocationStockItem locationStockItem = task.getResult().toObject(LocationStockItem.class);
                            if (transactionitemlist.containsKey(ItemSearch.getText().toString())) {
                                ItemSearch.setText("");
                                ItemSearch.setError("Item already added");
                                ItemSearch.requestFocus();
                            } else
                                alertitemadd(locationStockItem != null?locationStockItem.Balance_Qty:"nothing", itemStockInfo);
                        } else {
                            ItemSearch.setText("");
                            ItemSearch.setError("Item Balance is 0 at location");
                        }
                    }
                });
    }


    private void alertitemadd(final String balance_Qty, ItemStockInfo itemStockInfo) {
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
        TextView Balance = view.findViewById(R.id.Balancelvl);
        alertEditTexts = new ArrayList<EditText>(new ArrayList<EditText>(Arrays.<EditText>asList(
                (EditText) view.findViewById(R.id.AddItemStock), (EditText) view.findViewById(R.id.AddItemSellingValue),
                (EditText) view.findViewById(R.id.AddItemBalanceQty)
        )));
        for (int i = 0; i < alertEditTexts.size(); i++) {
            alertEditTexts.get(i).setRawInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        }

        if(Mode.compareTo("Customer")==0) {
            alertEditTexts.get(1).setText(itemStockInfo.Selling_Price);
            alertEditTexts.get(2).setText(balance_Qty);
        }else{
            alertEditTexts.get(1).setHint("Purchase Price / Unit");
            if(balance_Qty.compareTo("nothing") ==0 ){
                Balance.setText("Change\nReorder lvl");
                alertEditTexts.get(2).setText(itemStockInfo.Default_Reorder_Quantity);
                alertEditTexts.get(2).setEnabled(true);
            }else {
                Balance.setVisibility(View.GONE);
                alertEditTexts.get(2).setText(balance_Qty);
                alertEditTexts.get(2).setVisibility(View.GONE);
            }
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
                if (Mode.compareTo("Customer")==0 &&
                        Float.parseFloat(alertEditTexts.get(0).getText().toString()) > Float.parseFloat(alertEditTexts.get(2).getText().toString())) {
                    alertEditTexts.get(0).setText("");
                    alertEditTexts.get(0).requestFocus();
                    alertEditTexts.get(0).setError("Entered value above balance stock available");
                    flag = false;
                }
                if (flag) {
                    Itemtransaction itemtransaction = new Itemtransaction(ItemSearch.getText().toString(), alertEditTexts.get(1).getText().toString(),
                            alertEditTexts.get(0).getText().toString());
                    transactionitemlist.put(ItemSearch.getText().toString(), itemtransaction);
                    if(balance_Qty.compareTo("nothing") ==0 ){
                        itemreqorderqty.put(itemtransaction.itemCode,alertEditTexts.get(2).getText().toString());
                    }
                    totalcost += itemtransaction.Price * itemtransaction.quantity;
                    TotalCostText.setText(String.valueOf(totalcost) + " INR");
                    BillTop.setVisibility(View.VISIBLE);
                    TotalPriceLayout.setVisibility(View.VISIBLE);
                    transactionitemAdapter.notifyDataSetChanged();
                    ItemSearch.setText("");
                    alertDialog.dismiss();
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
                .whereLessThan("name", endcode).whereEqualTo("valid", true)
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
                Log.i("failaddtransac", e.getMessage());
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
                Log.i("failaddtransac", e.getMessage());
            }
        });
    }

    private void setLocationtexts(DeliveryAddress address, Boolean visible) {
        if (!visible) {
            LocationLayout.setVisibility(View.GONE);
        } else {
            LocationLayout.setVisibility(View.VISIBLE);
        }
        if (address == null) {
            address = new DeliveryAddress("", "", "", 0);
        }
        textInputLayouts.get(1).getEditText().setText(address.City);
        textInputLayouts.get(2).getEditText().setText(address.Street);
        textInputLayouts.get(0).getEditText().setText(address.State);
        textInputLayouts.get(3).getEditText().setText(String.valueOf(address.Pincode));
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
                .whereLessThan("Name", endcode).whereIn("AgentType", Arrays.asList("Both", Mode))
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
                Log.i("failaddtransac", e.getMessage());
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
    public void onBackPressed() {
        new AlertDialog.Builder(AddTransactionActivity.this, R.style.MyDialogTheme)
                .setTitle("Discard Transaction")
                .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.add_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
            case R.id.Save:
                new AlertDialog.Builder(AddTransactionActivity.this, R.style.MyDialogTheme)
                        .setTitle("Confirm Transaction")
                        .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                checkInputs();
                            }
                        })
                        .setNegativeButton("Cancel", null)
                        .show();
                break;
        }
        return true;
    }

    private void checkInputs() {
        Boolean flag = true;
        if (!Selectedagentfromdropdown) {
            CustomerSearch.setText("");
            CustomerSearch.setError("Select a " + Mode);
            CustomerSearch.requestFocus();
            flag = false;
        } else if (ReferenceCode.getText().toString().length() == 0) {
            ReferenceCode.setText("");
            ReferenceCode.setError("Enter Reference code");
            ReferenceCode.requestFocus();
            flag = false;
        } else if (!Selectedlocationfromdropdown) {
            LocationSearch.setText("");
            LocationSearch.setError("Select a Location");
            LocationSearch.requestFocus();
            flag = false;
        } else if (transactionitemlist.size() == 0) {
            ItemSearch.setText("");
            ItemSearch.setError("Include at least 1 item");
            ItemSearch.requestFocus();
            flag = false;
        } else if (Deliveryboolcheck) {
            if (deliveryDate.getText().toString().length() == 0) {
                deliveryDate.setText("");
                deliveryDate.setError("Enter delivery date");
                deliveryDate.requestFocus();
                flag = false;
            }
            for (int i = 0; i < textInputLayouts.size() && flag; i++) {
                if (textInputLayouts.get(i).getEditText().getText().toString().length() == 0) {
                    textInputLayouts.get(i).getEditText().setText("");
                    textInputLayouts.get(i).getEditText().setError("Enter field");
                    textInputLayouts.get(i).getEditText().requestFocus();
                    flag = false;
                    break;
                }
            }
        }
        if (flag) {
            SDProgress(true);
            firebaseFirestore.collection(ShopCode).document("doc").collection("Transactions")
                    .whereEqualTo("reference", ReferenceCode.getText().toString()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.getResult().isEmpty()) {
                        createobjects();
                    } else {
                        ReferenceCode.setText("");
                        ReferenceCode.setError("Reference code already exists");
                        ReferenceCode.requestFocus();
                        SDProgress(false);
                    }
                }
            });
        }

    }

    private void createobjects() {

        itemtransactions = new ArrayList<>();
        itemtransactions.addAll(transactionitemlist.values());
        if (Mode.compareTo("Vendor") == 0)
            type = "Purchase";
        else
            type = "Sale";
        storeTransaction = new StoreTransaction("", 0L, type, ReferenceCode.getText().toString().trim().toLowerCase(), CustomerSearch.getText().toString(),
                LocationSearch.getText().toString(), totalcost, 0.0f, Username, Useremail, gfunc.getCurrentDate(),true);

        initiatetransaction();
    }

    private void initiatetransaction() {
        firebaseFirestore.runTransaction(new Transaction.Function<Void>() {
            @Nullable
            @Override
            public Void apply(@NonNull Transaction transaction) throws FirebaseFirestoreException {
                float TotalProfit = 0.0f;
                ArrayList<ItemStockInfo> itemStockInfoArrayList = new ArrayList<>();
                ArrayList<LocationStockItem> locationStockItems = new ArrayList<>();
                for (int i = 0; i < transactionitemlist.size(); i++) {
                    String itemcode = (String) transactionitemlist.keySet().toArray()[i];
                    Itemtransaction itemtransaction = (Itemtransaction) transactionitemlist.values().toArray()[i];
                    ItemStockInfo itemStockInfo = transaction.get(firebaseFirestore.collection(ShopCode).document("doc")
                            .collection("ItemStockInfo").document(itemcode)).toObject(ItemStockInfo.class);
                    LocationStockItem locationStockItem = transaction.get(firebaseFirestore.collection(ShopCode).document("doc")
                            .collection("Location").document(itemcode+storeTransaction.locationCode)).toObject(LocationStockItem.class);
                    if(locationStockItem == null){
                        locationStockItem = new LocationStockItem("0.0",itemreqorderqty.get(itemcode),true,
                                itemStockInfo.ItemCode, storeTransaction.locationCode);
                    }
                    itemStockInfoArrayList.add(itemStockInfo);
                    locationStockItems.add(locationStockItem);
                }
                if (transaction.get(firebaseFirestore.collection(ShopCode).document("maxIndex")).exists()) {
                    maxindex max = (maxindex) transaction.get(firebaseFirestore.collection(ShopCode).document("maxIndex"))
                            .toObject(maxindex.class);
                    if(Mode.compareTo("Vendor")==0) {
                        storeTransaction.code = "PO-" + String.valueOf(max.purchaseCode + 1);
                        max.purchaseCode = max.purchaseCode + 1;
                    }else {
                        storeTransaction.code = "SO-" + String.valueOf(max.salesCode + 1);
                        max.salesCode = max.salesCode + 1;
                    }
                    storeTransaction.codeno = max.salesCode + max.purchaseCode ;
                    transaction.set(firebaseFirestore.collection(ShopCode).document("maxIndex"), max);
                }
                for(int i=0; i<transactionitemlist.size();i++){
                    ItemStockInfo itemStockInfo = itemStockInfoArrayList.get(i);
                    Itemtransaction itemtransaction = (Itemtransaction) transactionitemlist.values().toArray()[i];
                    LocationStockItem locationStockItem = locationStockItems.get(i);
                    if(Mode.compareTo("Customer")==0) {
                        Float Purchaseprice = (Float.parseFloat(itemStockInfo.Total_Price) / Float.parseFloat(itemStockInfo.Total_Balance_Quantity));
                        Float Totalpurchasecost = Purchaseprice * itemtransaction.quantity;
                        Float totalpriceleft = Float.valueOf(itemStockInfo.Total_Price) - Totalpurchasecost;
                        itemStockInfo.Total_Price = String.valueOf(totalpriceleft);
                        Float totalbalanceleft = Float.valueOf(itemStockInfo.Total_Balance_Quantity) - itemtransaction.quantity;
                        Float balanceleftatlocation = Float.valueOf(locationStockItem.Balance_Qty) - itemtransaction.quantity;
                        locationStockItem.Balance_Qty = String.valueOf(balanceleftatlocation);
                        itemStockInfo.Total_Balance_Quantity = String.valueOf(totalbalanceleft);
                        TotalProfit += (itemtransaction.Price * itemtransaction.quantity) - Totalpurchasecost;
                    }else if(Mode.compareTo("Vendor")==0){
                        Float totalpriceleft = Float.valueOf(itemStockInfo.Total_Price) + (itemtransaction.Price * itemtransaction.quantity);
                        Float totalbalanceleft = Float.valueOf(itemStockInfo.Total_Balance_Quantity) + itemtransaction.quantity;
                        Float balanceleftatlocation = Float.valueOf(locationStockItem.Balance_Qty) + itemtransaction.quantity;
                        itemStockInfo.Total_Price = String.valueOf(totalpriceleft);
                        locationStockItem.Balance_Qty = String.valueOf(balanceleftatlocation);
                        itemStockInfo.Total_Balance_Quantity = String.valueOf(totalbalanceleft);
                    }
                    transaction.set(firebaseFirestore.collection(ShopCode).document("doc")
                            .collection("ItemStockInfo").document(itemStockInfo.ItemCode),itemStockInfo);
                    transaction.set(firebaseFirestore.collection(ShopCode).document("doc")
                            .collection("Location")
                            .document(locationStockItem.ItemCode + storeTransaction.locationCode),locationStockItem);
                }
                storeTransaction.totalProfit = TotalProfit;
                transaction.set(firebaseFirestore.collection(ShopCode).document("doc")
                        .collection("TransactionDetails").document(storeTransaction.code),storeTransaction);
                Map<String, List<Itemtransaction>> listMap = new HashMap<>();
                Collection<Itemtransaction> tempitemsstore = transactionitemlist.values();
                List<Itemtransaction> itemspartoftransaction = new ArrayList<>(tempitemsstore);
                listMap.put("ItemsList",itemspartoftransaction);
                transaction.set(firebaseFirestore.collection(ShopCode).document("doc")
                        .collection("TransactionItems").document(storeTransaction.code),listMap);
                return null;
            }
        }).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(getApplicationContext(),type+" order added to database",Toast.LENGTH_SHORT).show();
                    displaycodegenerated();
                }else{
                    Toast.makeText(getApplicationContext(),"Failed to add to database",Toast.LENGTH_SHORT).show();
                }
                SDProgress(false);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.i("failaddstakeholer",e.getMessage());
            }
        });
    }

    private void displaycodegenerated() {
        AlertDialog.Builder alert = new AlertDialog.Builder(AddTransactionActivity.this, R.style.MyDialogTheme);
        alert.setMessage("Code assigned to "+ type + " order is "+ storeTransaction.code);
        alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finish();
            }
        }).setCancelable(false).show();
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
    public void OnNoteClick(int position) {

    }
}