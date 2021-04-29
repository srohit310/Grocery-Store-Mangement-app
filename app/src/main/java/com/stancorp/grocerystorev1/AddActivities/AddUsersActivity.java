package com.stancorp.grocerystorev1.AddActivities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.stancorp.grocerystorev1.AutoCompleteAdapter.AutoCompleteLocationAdapter;
import com.stancorp.grocerystorev1.Classes.Location;
import com.stancorp.grocerystorev1.Classes.StoreUser;
import com.stancorp.grocerystorev1.GlobalClass.Gfunc;
import com.stancorp.grocerystorev1.R;

import java.security.Permission;
import java.util.LinkedHashMap;

public class AddUsersActivity extends AppCompatActivity {

    Spinner PermissionLvlSpinner;
    TextInputLayout UserName;
    TextInputLayout UserEmail;
    TextInputLayout UserPhone;
    String permission;
    String ShopCode;
    String location;
    StoreUser user;
    Gfunc gfunc;
    ImageView UpButton;
    RelativeLayout ProgressLayout;

    FirebaseFirestore firebaseFirestore;
    AutoCompleteTextView LocationSearch;
    ProgressBar locationprogress;
    Button Register;
    AutoCompleteLocationAdapter locationAdapter;
    LinkedHashMap<String, Location> locations;
    Boolean Selectedlocationfromdropdown;
    Handler locationhandler = new Handler();
    LinearLayout addEmployeeLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_users);
        permission = "Admin";
        ShopCode = getIntent().getStringExtra("ShopCode");
        gfunc = new Gfunc();
        ProgressLayout = findViewById(R.id.ProgressLayout);

        PermissionLvlSpinner = findViewById(R.id.PermissionLvlSpinner);
        UserName = findViewById(R.id.AddAdminName);
        UserEmail = findViewById(R.id.AddAdminEmail);
        UserPhone = findViewById(R.id.AddAdminPhone);

        locations = new LinkedHashMap<>();
        Register = findViewById(R.id.AddUserButton);
        Register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(AddUsersActivity.this,R.style.MyDialogTheme)
                        .setTitle("Confirm User Details")
                        .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                checkInput();
                            }
                        })
                        .setNegativeButton("No",null).show();
            }
        });

        UpButton = findViewById(R.id.upbutton);
        UpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(AddUsersActivity.this,R.style.MyDialogTheme)
                        .setTitle("Discard User")
                        .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                finish();
                            }
                        })
                        .setNegativeButton("No",null).show();
            }
        });

        setupspinner(0);

        firebaseFirestore = FirebaseFirestore.getInstance();
        locationAdapter = new AutoCompleteLocationAdapter(getApplicationContext(), locations);
        addEmployeeLocation = findViewById(R.id.AddEmployeeLocation);
        LocationSearch = findViewById(R.id.LocationsearchAuto);
        LocationSearch.setThreshold(0);
        LocationSearch.setAdapter(locationAdapter);
        locationprogress = findViewById(R.id.autolocationprogress);
        LocationSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                locationhandler.removeCallbacksAndMessages(null);
                Selectedlocationfromdropdown = false;
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
            }
        });
    }

    private void checkInput() {
        location = "All";
        Boolean flag = true;
        if (UserName.getEditText().getText().toString().length() < 8 || UserName.getEditText().getText().toString().length() > 16) {
            UserName.getEditText().setText("");
            UserName.getEditText().setError("Enter between 8 to 16 characters");
            UserName.requestFocus();
            flag = false;
        } else if (!gfunc.isEmailValid(UserEmail.getEditText().getText().toString())) {
            UserEmail.getEditText().setText("");
            UserEmail.getEditText().setError("Enter a valid email");
            UserEmail.requestFocus();
            flag = false;
        } else if (UserPhone.getEditText().getText().toString().length() != 10) {
            UserPhone.getEditText().setText("");
            UserPhone.getEditText().setError("Please enter 10 digits");
            UserPhone.requestFocus();
            flag = false;
        } else if (permission.compareTo("Employee") == 0 && !Selectedlocationfromdropdown) {
            LocationSearch.setText("");
            LocationSearch.setError("Select a location from Dropdown");
            LocationSearch.requestFocus();
            flag = false;
        }
        if (flag) {
            RegisterUser();
        } else {
            SDProgress(false, true);
        }
    }

    private void RegisterUser() {
        user = new StoreUser(UserName.getEditText().getText().toString().toLowerCase(), UserEmail.getEditText().getText().toString()
                , UserPhone.getEditText().getText().toString(), false, permission, ShopCode, location, false);
        if(permission.compareTo("Employee")==0){
            user.Location = LocationSearch.getText().toString();
        }
        CheckEmailDuplicate();
    }

    private void CheckEmailDuplicate() {
        SDProgress(true, true);
        firebaseFirestore.collection("UserDetails").whereEqualTo("Email", UserEmail.getEditText().getText().toString())
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (!task.getResult().isEmpty()) {
                    UserEmail.getEditText().setText("");
                    UserEmail.getEditText().setError("Email already exists");
                    UserEmail.requestFocus();
                    ProgressLayout.setVisibility(View.GONE);
                    SDProgress(false, true);
                } else {
                    AddUsertoDb();
                }
            }
        });
    }

    private void AddUsertoDb() {
        firebaseFirestore.collection("UserDetails").add(user).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
            @Override
            public void onComplete(@NonNull Task<DocumentReference> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(getApplicationContext(), "User Added", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), "Could not add User", Toast.LENGTH_SHORT).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.i("failuseraddition", e.getMessage());
            }
        });
        SDProgress(false, true);
        finish();
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

        int limit = search.compareTo("") == 0 ? 1 : 10;

        firebaseFirestore.collection(ShopCode).document("doc")
                .collection("LocationDetails").whereGreaterThanOrEqualTo("name", startcode)
                .whereLessThan("name", endcode)
                .limit(limit).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
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


    private void setupspinner(int position) {

        ArrayAdapter PermissionSpinnerAdapter = ArrayAdapter.createFromResource(getApplicationContext(),
                R.array.array_permission_options, R.layout.spinner_item_text);

        PermissionSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        PermissionLvlSpinner.setAdapter(PermissionSpinnerAdapter);
        PermissionLvlSpinner.setSelection(position);
        PermissionLvlSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selection = parent.getItemAtPosition(position).toString();
                if (!TextUtils.isEmpty(selection)) {
                    if (selection.equals(getString(R.string.permission_admin))) {
                        permission = "Admin";
                        LocationSearch.setEnabled(false);
                        LocationSearch.setHint("Access To All Locations");
                    } else if (selection.equals(getString(R.string.permission_employee))) {
                        permission = "Employee";
                        LocationSearch.setEnabled(true);
                        LocationSearch.setHint("Search for Location using Name");
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                permission = "Admin";
            }
        });
    }

    public void SDProgress(boolean show, boolean alertDialog) {
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