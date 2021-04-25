package com.stancorp.grocerystorev1.AddActivities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;

import android.content.DialogInterface;
import android.content.res.Configuration;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Transaction;
import com.stancorp.grocerystorev1.Classes.AdditionalContact;
import com.stancorp.grocerystorev1.Classes.Agent;
import com.stancorp.grocerystorev1.Classes.DeliveryAddress;
import com.stancorp.grocerystorev1.Classes.maxindex;
import com.stancorp.grocerystorev1.GlobalClass.Gfunc;
import com.stancorp.grocerystorev1.R;

import java.util.ArrayList;
import java.util.Arrays;

public class AddStakesholdersActivity extends AppCompatActivity {

    Gfunc gfunc;
    //Classes
    Agent agent;
    DeliveryAddress deliveryAddress;
    AdditionalContact additionalContact;

    //Boolean
    Boolean delivery;
    Boolean additionalcontact;

    //layouts
    LinearLayout deliveryLayout;
    LinearLayout contactLayout;
    LinearLayout customertypeLayout;
    LinearLayout personnameLayout;
    LinearLayout companynameLayout;
    CardView altcontact;
    RelativeLayout ProgressLayout;

    //firestore
    FirebaseFirestore firebaseFirestore;

    //Button
    Button deliveryexpand;
    Button contactexpand;

    //Spinner
    Spinner SelectType;

    //Radiogroup
    RadioGroup radioGroup;
    RadioButton customerRadioButton;

    //Arrayedittexts
    ArrayList<EditText> agentMainDetails;
    ArrayList<EditText> deliveryDetails;
    ArrayList<EditText> additionalcontactDetails;

    //String
    String agentType;
    String customerType;
    String shopcode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_stakesholders);

        gfunc = new Gfunc();
        shopcode = getIntent().getStringExtra("ShopCode");

        //supportactionbar
        getSupportActionBar().setTitle("Add Customer / Vendor");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Arraylist initialization
        agentMainDetails = new ArrayList<EditText>(Arrays.<EditText>asList(
                (EditText) findViewById(R.id.firstName), (EditText) findViewById(R.id.lastName),
                (EditText) findViewById(R.id.companyName), (EditText) findViewById(R.id.email),
                (EditText) findViewById(R.id.phoneno)
        ));
        deliveryDetails = new ArrayList<EditText>(Arrays.<EditText>asList(
                (EditText) findViewById(R.id.streetEditText), (EditText) findViewById(R.id.cityEditText),
                (EditText) findViewById(R.id.stateEditText), (EditText) findViewById(R.id.pincodeEditText)
        ));
        additionalcontactDetails = new ArrayList<EditText>(Arrays.<EditText>asList(
                (EditText) findViewById(R.id.Namealt), (EditText) findViewById(R.id.Emailalt),
                (EditText) findViewById(R.id.Phonenoalt), (EditText) findViewById(R.id.additionalDesignation),
                (EditText) findViewById(R.id.additionalDept)
        ));

        //layouts initalized
        ProgressLayout = findViewById(R.id.ProgressLayout);
        deliveryLayout = findViewById(R.id.Visibledelivery);
        contactLayout = findViewById(R.id.Visiblealtcontact);
        deliveryLayout.setVisibility(View.GONE);
        contactLayout.setVisibility(View.GONE);
        customertypeLayout = findViewById(R.id.customertypeLayout);
        personnameLayout = findViewById(R.id.personNameLayout);
        companynameLayout = findViewById(R.id.companynameLayout);
        altcontact = findViewById(R.id.altContactPerson);

        //Buttons initialized
        deliveryexpand = findViewById(R.id.deliverydropdown);
        contactexpand = findViewById(R.id.contactdropdown);
        deliveryexpand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (deliveryLayout.getVisibility() == View.VISIBLE) {
                    deliveryexpand.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_baseline_arrow_drop_down_24));
                    deliveryLayout.setVisibility(View.GONE);
                } else {
                    deliveryexpand.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_baseline_arrow_drop_up_24));
                    deliveryLayout.setVisibility(View.VISIBLE);
                }
            }
        });
        contactexpand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (contactLayout.getVisibility() == View.VISIBLE) {
                    contactexpand.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_baseline_arrow_drop_down_24));
                    contactLayout.setVisibility(View.GONE);
                } else {
                    contactexpand.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_baseline_arrow_drop_up_24));
                    contactLayout.setVisibility(View.VISIBLE);
                }
            }
        });

        //RadioGroup
        radioGroup = findViewById(R.id.radioGroup);
        customerType = "Business";
        agentType = "Customer";
        int selectedId = radioGroup.getCheckedRadioButtonId();
        customerRadioButton = (RadioButton) findViewById(selectedId);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                setcheckedLayout();
            }
        });

        //Spinner
        SelectType = findViewById(R.id.AgentType);
        setupSpinner();

        //Firebase
        firebaseFirestore = FirebaseFirestore.getInstance();
    }

    private void setcheckedLayout() {
        int selectedId = radioGroup.getCheckedRadioButtonId();
        customerRadioButton = (RadioButton) findViewById(selectedId);
        if (customerRadioButton.getText().toString().compareTo("Business") == 0) {
            altcontact.setVisibility(View.VISIBLE);
            companynameLayout.setVisibility(View.VISIBLE);
            personnameLayout.setVisibility(View.GONE);
            customerType = "Business";
        } else {
            altcontact.setVisibility(View.GONE);
            companynameLayout.setVisibility(View.GONE);
            personnameLayout.setVisibility(View.VISIBLE);
            customerType = "Individual";
        }
    }

    private void setupSpinner() {
        ArrayAdapter AgentSelectorAdapter = ArrayAdapter.createFromResource(getApplicationContext(),
                R.array.array_agent_type, android.R.layout.simple_spinner_item);

        AgentSelectorAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        SelectType.setAdapter(AgentSelectorAdapter);
        SelectType.setSelection(0);
        SelectType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selection = parent.getItemAtPosition(position).toString();
                if (!TextUtils.isEmpty(selection)) {
                    if (selection.equals(getString(R.string.Customer))) {
                        agentType = "Customer";
                        customertypeLayout.setVisibility(View.VISIBLE);
                        setcheckedLayout();
                    } else if (selection.equals(getString(R.string.Vendor)) || selection.equals(getString(R.string.Both))) {
                        if (selection.equals(getString(R.string.Vendor)))
                            agentType = "Vendor";
                        else
                            agentType = "Both";
                        customerType = "Business";
                        customertypeLayout.setVisibility(View.GONE);
                        personnameLayout.setVisibility(View.GONE);
                        companynameLayout.setVisibility(View.VISIBLE);
                        altcontact.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
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
                AlertDialog.Builder alert = new AlertDialog.Builder(AddStakesholdersActivity.this, R.style.MyDialogTheme);
                if (true) {
                    alert.setMessage("Confirm Details");
                } else {
                    alert.setMessage("Confirm Changes");
                }
                alert.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        checkInputs();
                    }
                }).setNegativeButton("Cancel", null).show();
                return true;
            case android.R.id.home:
                new AlertDialog.Builder(AddStakesholdersActivity.this,R.style.MyDialogTheme)
                        .setTitle("Discard Stakeholder")
                        .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                onBackPressed();
                            }
                        })
                        .setNegativeButton("Cancel",null)
                        .show();
                return true;
        }
        return false;
    }

    private void checkInputs() {
        Boolean flag = true;
        delivery = false;
        additionalcontact = false;
        if (customerType.compareTo("Individual") != 0) {
            if (agentMainDetails.get(2).getText().toString().length() == 0) {
                agentMainDetails.get(2).setText("");
                agentMainDetails.get(2).setError("Enter Company Name");
                agentMainDetails.get(2).requestFocus();
                flag = false;
            }

        }
        if (customerType.compareTo("Individual") == 0) {
            for (int i = 0; i < 2; i++)
                if (agentMainDetails.get(i).getText().toString().length() == 0) {
                    agentMainDetails.get(i).setText("");
                    agentMainDetails.get(i).setError("Enter Name");
                    agentMainDetails.get(i).requestFocus();
                    flag = false;
                }
        }
        if (!gfunc.isEmailValid(agentMainDetails.get(3).getText().toString()) && flag) {
            agentMainDetails.get(3).setText("");
            agentMainDetails.get(3).setError("Enter Valid Email");
            agentMainDetails.get(3).requestFocus();
            flag = false;
        }
        if (agentMainDetails.get(4).getText().toString().length() == 0 && flag) {
            agentMainDetails.get(4).setText("");
            agentMainDetails.get(4).setError("Enter Phone number");
            agentMainDetails.get(4).requestFocus();
            flag = false;
        }
        for (int i = 0; i < deliveryDetails.size(); i++) {
            if (deliveryDetails.get(i).getText().toString().length() > 0)
                delivery = true;
        }
        if (delivery && flag) {
            for (int i = 0; i < deliveryDetails.size(); i++) {
                if (deliveryDetails.get(i).getText().toString().length() == 0) {
                    deliveryDetails.get(i).setText("");
                    deliveryDetails.get(i).setError("Please fill this Field");
                    deliveryDetails.get(i).requestFocus();
                    flag = false;
                    break;
                }
            }
        }
        if (altcontact.getVisibility() == View.VISIBLE) {
            for (int i = 0; i < additionalcontactDetails.size(); i++) {
                if (additionalcontactDetails.get(i).getText().toString().length() > 0)
                    additionalcontact = true;
            }
        }
        if (additionalcontact && flag) {
            for (int i = 0; i < additionalcontactDetails.size(); i++) {
                if (additionalcontactDetails.get(i).getText().toString().length() == 0) {
                    additionalcontactDetails.get(i).setText("");
                    additionalcontactDetails.get(i).setError("Please fill this Field");
                    additionalcontactDetails.get(i).requestFocus();
                    flag = false;
                    break;
                }
                if (i == 1 && flag && !gfunc.isEmailValid(additionalcontactDetails.get(i).getText().toString())) {
                    additionalcontactDetails.get(i).setText("");
                    additionalcontactDetails.get(i).setError("Enter Valid Email");
                    additionalcontactDetails.get(i).requestFocus();
                    flag = false;
                    break;
                }
            }
        }
        if (flag) {
            createobjects();
        }
    }

    private void createobjects() {
        deliveryAddress = null;
        additionalContact = null;

        if (delivery) {
            deliveryAddress = new DeliveryAddress(deliveryDetails.get(0).getText().toString().trim(), deliveryDetails.get(1).getText().toString().trim(),
                    deliveryDetails.get(2).getText().toString().trim(), Long.parseLong(deliveryDetails.get(3).getText().toString().trim()));
        }
        if (additionalcontact) {
            additionalContact = new AdditionalContact(additionalcontactDetails.get(0).getText().toString().trim(),
                    additionalcontactDetails.get(1).getText().toString().trim(), Long.parseLong(additionalcontactDetails.get(2).getText().toString().trim()),
                    additionalcontactDetails.get(3).getText().toString().trim(), additionalcontactDetails.get(4).getText().toString().trim());
        }
        String Name = "";
        if (customerType.compareTo("Business") == 0) {
            Name = agentMainDetails.get(2).getText().toString().trim();
        } else if (customerType.compareTo("Individual") == 0) {
            Name = agentMainDetails.get(0).getText().toString().trim() + " " + agentMainDetails.get(1).getText().toString().trim();
        }
        agent = new Agent(Name.toLowerCase(),agentType,customerType,agentMainDetails.get(3).getText().toString().trim(),
                Long.parseLong(agentMainDetails.get(4).getText().toString().trim()),deliveryAddress,additionalContact,true);

        savetodb();
    }

    private void savetodb() {
        SDProgress(true);
        firebaseFirestore.runTransaction(new Transaction.Function<Void>() {
            @Nullable
            @Override
            public Void apply(@NonNull Transaction transaction) throws FirebaseFirestoreException {
                if (transaction.get(firebaseFirestore.collection(shopcode).document("maxIndex")).exists()) {
                    maxindex max = (maxindex) transaction.get(firebaseFirestore.collection(shopcode).document("maxIndex"))
                            .toObject(maxindex.class);
                    if(agentType.compareTo("Vendor")==0) {
                        agent.Code = "VEN-" + String.valueOf(max.vendorCode + 1);
                        max.vendorCode = max.vendorCode + 1;
                    }else if(agentType.compareTo("Customer")==0){
                        agent.Code = "CUS-" + String.valueOf(max.customerCode + 1);
                        max.customerCode = max.customerCode + 1;
                    }else if(agentType.compareTo("Both")==0){
                        agent.Code = "AGENT-" + String.valueOf(max.agentCode + 1);
                        max.agentCode = max.agentCode + 1;
                    }
                    agent.codeno = max.vendorCode + max.customerCode + max.agentCode;
                    transaction.set(firebaseFirestore.collection(shopcode).document("maxIndex"), max);
                } else {
                    maxindex max = new maxindex();
                    if(agentType.compareTo("Vendor")==0) {
                        agent.Code = "VEN-1";
                        max = new maxindex(0,0,0,0,0,1);
                    }else if(agentType.compareTo("Customer")==0){
                        agent.Code = "CUS-1";
                        max = new maxindex(0,0,0,0,1,0);
                    }else if(agentType.compareTo("Both")==0){
                        agent.Code = "AGENT-1";
                        max = new maxindex(1,0,0,0,0,0);
                    }
                    agent.codeno = 1;
                    transaction.set(firebaseFirestore.collection(shopcode).document("maxIndex"), max);
                }
                transaction.set(firebaseFirestore.collection(shopcode).document("doc")
                        .collection("StakeHolders").document(),agent);
                return null;
            }
        }).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(getApplicationContext(),"Agent added to database",Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(getApplicationContext(),"Failed to add to database",Toast.LENGTH_SHORT).show();
                }
                SDProgress(false);
                displaycodegenerated();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.i("failaddstakeholer",e.getMessage());
            }
        });
    }

    private void displaycodegenerated() {
        String display;
        if(agentType.compareTo("Both")==0){
            display = "Customer / Vendor";
        }else {
            display = agentType;
        }
        AlertDialog.Builder alert = new AlertDialog.Builder(AddStakesholdersActivity.this, R.style.MyDialogTheme);
        alert.setMessage("Code assigned to "+ display + " is "+ agent.Code);
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
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {

        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {

        }
    }
}