package com.stancorp.grocerystorev1.ViewFragments;

import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.stancorp.grocerystorev1.AdapterClasses.BaseRecyclerAdapter;
import com.stancorp.grocerystorev1.AdapterClasses.ManageUserAdapter;
import com.stancorp.grocerystorev1.Classes.StoreUser;
import com.stancorp.grocerystorev1.GlobalClass.Gfunc;
import com.stancorp.grocerystorev1.R;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Objects;


public class FragmentGroupManageUsers extends Fragment implements BaseRecyclerAdapter.OnNoteListner {
    RecyclerView adminrecyclerview;
    RecyclerView employeerecyclerview;
    FloatingActionButton fabaddUser;
    TextInputLayout UserName;
    TextInputLayout UserEmail;
    TextInputLayout UserPhone;
    TextInputLayout UserLocation;
    StoreUser Currentuser;
    AlertDialog alertDialog;

    EditText searchedittext;

    TextView EmptyAdmin;
    TextView EmptyEmployee;
    ImageView EmptyAdminImage;
    ImageView EmptyEmployeeImage;

    RelativeLayout ProgressLayout;
    RelativeLayout ProgressAlertLayout;

    Spinner PermissionLvlSpinner;
    Spinner ListPermissionLvL;
    ArrayList<String> hints;

    RecyclerView adminList, employeeList;
    ManageUserAdapter adminadapter, employeeadapter;
    RecyclerView.LayoutManager madminLayoutManager, memployeeLayoutManager;
    LinkedHashMap<String, StoreUser> admin;
    LinkedHashMap<String, StoreUser> employee;
    LinkedHashMap<String, StoreUser> adminfilteredlist;
    LinkedHashMap<String, StoreUser> employeefilteredlist;
    String optionselected;

    StoreUser user;
    private Uri photoUri;
    String permission;
    String Location;

    FirebaseFirestore firebaseFirestore;

    Gfunc gfunc;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_user_details, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        permission = "Admin";
        Location = "All";
        admin = new LinkedHashMap<>();
        employee = new LinkedHashMap<>();
        adminfilteredlist = new LinkedHashMap<>();
        employeefilteredlist = new LinkedHashMap<>();

        gfunc = new Gfunc();

        ListPermissionLvL = view.findViewById(R.id.ChangeUserlvl);

        //Initalizing layout and progressbar
        ProgressLayout = view.findViewById(R.id.ProgressLayout);

        firebaseFirestore = FirebaseFirestore.getInstance();
        EmptyAdmin = view.findViewById(R.id.emptyadmintextview);
        EmptyEmployee = view.findViewById(R.id.emptyemployeetextview);
        EmptyAdminImage = view.findViewById(R.id.EmptyAdminImage);
        EmptyEmployeeImage = view.findViewById(R.id.EmptyEmployeeImage);

        searchedittext = view.findViewById(R.id.SearchText);
        searchedittext.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                filter(editable.toString());
            }
        });

        madminLayoutManager = new LinearLayoutManager(getContext());
        memployeeLayoutManager = new LinearLayoutManager(getContext());
        adminrecyclerview = view.findViewById(R.id.AdminsListView);
        employeerecyclerview = view.findViewById(R.id.EmployeesListView);
        adminrecyclerview.setLayoutManager(madminLayoutManager);
        employeerecyclerview.setLayoutManager(memployeeLayoutManager);
        adminadapter = new ManageUserAdapter(admin, this, getContext());
        employeeadapter = new ManageUserAdapter(employee, this, getContext());
        adminrecyclerview.setAdapter(adminadapter);
        employeerecyclerview.setAdapter(employeeadapter);

        optionselected = "Admin";
        setupListSpinner();
        fetchUserData();

        fabaddUser = view.findViewById(R.id.floatingActionButton);
        hints = new ArrayList<>();
        fabaddUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PerformUserFunctions(view, 0);
            }
        });

    }

    private void setupListSpinner() {
        ArrayAdapter PermissionSpinnerAdapter = ArrayAdapter.createFromResource(Objects.requireNonNull(getContext()),
                R.array.array_permission_options, R.layout.spinner_user_item_text);

        PermissionSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        ListPermissionLvL.setAdapter(PermissionSpinnerAdapter);
        ListPermissionLvL.setSelection(0);
        ListPermissionLvL.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selection = parent.getItemAtPosition(position).toString();
                if (!TextUtils.isEmpty(selection)) {
                    if (selection.equals(getString(R.string.permission_admin))) {
                        optionselected = "Admin";
                        adminrecyclerview.setVisibility(View.VISIBLE);
                        employeerecyclerview.setVisibility(View.GONE);
                        EmptyEmployee.setVisibility(View.GONE);
                        EmptyEmployeeImage.setVisibility(View.GONE);
                        if (admin.size() == 0) {
                            adminrecyclerview.setVisibility(View.GONE);
                            EmptyAdmin.setVisibility(View.VISIBLE);
                            EmptyAdminImage.setVisibility(View.VISIBLE);
                            EmptyAdminImage.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    PerformUserFunctions(view, 0);
                                }
                            });
                        }
                    } else if (selection.equals(getString(R.string.permission_employee))) {
                        optionselected = "Employee";
                        adminrecyclerview.setVisibility(View.GONE);
                        employeerecyclerview.setVisibility(View.VISIBLE);
                        EmptyAdmin.setVisibility(View.GONE);
                        EmptyAdminImage.setVisibility(View.GONE);
                        if (employee.size() == 0) {
                            employeerecyclerview.setVisibility(View.GONE);
                            EmptyEmployee.setVisibility(View.VISIBLE);
                            EmptyEmployeeImage.setVisibility(View.VISIBLE);
                            EmptyEmployeeImage.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    PerformUserFunctions(view, 1);
                                }
                            });
                        }
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void filter(String text) {
        adminfilteredlist = new LinkedHashMap<>();
        employeefilteredlist = new LinkedHashMap<>();
        for (StoreUser user : admin.values()) {
            if (text.toLowerCase().compareTo(user.Name.substring(0
                    , user.Name.length() < text.length() ? user.Name.length() : text.length()).toLowerCase()) == 0) {
                adminfilteredlist.put(user.Email, user);
            }
        }
        for (StoreUser user : employee.values()) {
            if (text.toLowerCase().compareTo(user.Name.substring(0, user.Name.length() < text.length() ?
                    user.Name.length() : text.length()).toLowerCase()) == 0) {
                employeefilteredlist.put(user.Email, user);
            }
        }
        adminadapter.filterList(adminfilteredlist);
        employeeadapter.filterList(employeefilteredlist);
    }

    private void attachDatabaseListener() {
        SDProgress(true, false);
        firebaseFirestore.collection("UserDetails").whereEqualTo("ShopCode", Currentuser.ShopCode)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (value != null && !value.isEmpty()) {
                            for (DocumentChange doc : value.getDocumentChanges()) {
                                StoreUser temp;
                                switch (doc.getType()) {
                                    case ADDED:
                                        temp = doc.getDocument().toObject(StoreUser.class);
                                        if (temp.Email.compareTo(Currentuser.Email) != 0) {
                                            if (temp.PermissionLevel.compareTo("Admin") == 0) {
                                                admin.put(temp.Email, temp);
                                                adminfilteredlist.put(temp.Email, temp);
                                                adminadapter.notifyDataSetChanged();

                                                if(admin.size()>0 && optionselected.compareTo("Admin")==0){
                                                    adminrecyclerview.setVisibility(View.VISIBLE);
                                                    EmptyAdmin.setVisibility(View.GONE);
                                                    EmptyAdminImage.setVisibility(View.GONE);
                                                }
                                            } else {
                                                employee.put(temp.Email, temp);
                                                employeefilteredlist.put(temp.Email, temp);
                                                employeeadapter.notifyDataSetChanged();

                                                if(employee.size()>0 && optionselected.compareTo("Employee")==0){
                                                    employeerecyclerview.setVisibility(View.VISIBLE);
                                                    EmptyEmployee.setVisibility(View.GONE);
                                                    EmptyEmployeeImage.setVisibility(View.GONE);
                                                }
                                            }
                                        }
                                        break;
                                    case MODIFIED:
                                        temp = doc.getDocument().toObject(StoreUser.class);
                                        if (temp.Email.compareTo(Currentuser.Email) != 0) {
                                            if (temp.PermissionLevel.compareTo("Admin") == 0) {
                                                admin.put(temp.Email, temp);
                                                adminfilteredlist.put(temp.Email, temp);
                                                adminadapter.notifyDataSetChanged();
                                            } else {
                                                employee.put(temp.Email, temp);
                                                employeefilteredlist.put(temp.Email, temp);
                                                employeeadapter.notifyDataSetChanged();
                                            }
                                        }
                                        break;
                                    case REMOVED:
                                        temp = doc.getDocument().toObject(StoreUser.class);
                                        if (temp.PermissionLevel.compareTo("Admin") == 0) {
                                            admin.remove(temp.Email);
                                            adminfilteredlist.remove(temp.Email);
                                            adminadapter.notifyDataSetChanged();

                                            if(admin.size()==0 && optionselected.compareTo("Admin")==0){
                                                adminrecyclerview.setVisibility(View.GONE);
                                                EmptyAdmin.setVisibility(View.VISIBLE);
                                                EmptyAdminImage.setVisibility(View.VISIBLE);
                                            }
                                        } else {
                                            employee.remove(temp.Email);
                                            employeefilteredlist.remove(temp.Email);
                                            employeeadapter.notifyDataSetChanged();

                                            if(employee.size()==0 && optionselected.compareTo("Employee")==0){
                                                employeerecyclerview.setVisibility(View.GONE);
                                                EmptyEmployee.setVisibility(View.VISIBLE);
                                                EmptyEmployeeImage.setVisibility(View.VISIBLE);
                                            }
                                        }
                                        break;
                                }
                            }
                        }
                        SDProgress(false, false);
                    }
                });
    }

    private void fetchUserData() {

        SDProgress(true, false);
        firebaseFirestore.collection("UserDetails").whereEqualTo("Email", FirebaseAuth.getInstance().getCurrentUser().getEmail())
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                for (DocumentSnapshot doc : task.getResult()) {
                    Currentuser = doc.toObject(StoreUser.class);
                }
                attachDatabaseListener();
            }
        });
    }

    private void PerformUserFunctions(View itemview, int position) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), R.style.AlertDialogTheme);
        View view = LayoutInflater.from(getContext()).inflate(
                R.layout.alertdialog_adduser, (RelativeLayout) itemview.findViewById(R.id.adduseralertcontainer)
        );
        builder.setView(view);
        alertDialog = builder.create();
        view.findViewById(R.id.CancelAdjustmentButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });

        ProgressAlertLayout = view.findViewById(R.id.ProgressAlertLayout);

        PermissionLvlSpinner = view.findViewById(R.id.AddTitle);
        setupspinner(position);
        UserName = view.findViewById(R.id.AddAdminName);
        UserEmail = view.findViewById(R.id.AddAdminEmail);
        UserPhone = view.findViewById(R.id.AddAdminPhone);
        UserLocation = view.findViewById(R.id.AddEmployeeLocation);

        view.findViewById(R.id.ConfirmAddAdminButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Location = "All";
                Boolean flag = true;
                if (UserName.getEditText().getText().toString().length() < 8 || UserName.getEditText().getText().toString().length()>16) {
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
                } else if (permission.compareTo("Employee") == 0 && UserLocation.getEditText().getText().toString().length() == 0) {
                    UserLocation.getEditText().setText("");
                    UserLocation.getEditText().setError("Please enter a valid code (LOC-X)");
                    UserLocation.requestFocus();
                    flag = false;
                }
                if (flag) {
                    RegisterUser();
                } else {
                    SDProgress(false,true);
                }

            }
        });
        if (alertDialog.getWindow() != null) {
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }
        alertDialog.show();
    }

    private void RegisterUser() {
        user = new StoreUser(UserName.getEditText().getText().toString(), UserEmail.getEditText().getText().toString()
                , UserPhone.getEditText().getText().toString(), false, permission, Currentuser.ShopCode, Location, false);
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
                    UserEmail.setError("Email already exists");
                    UserEmail.requestFocus();
                    ProgressLayout.setVisibility(View.GONE);
                    SDProgress(false, true);
                } else {
                    if (permission.compareTo("Employee") == 0) {
                        checkLocationExists();
                    } else if (permission.compareTo("Admin") == 0) {
                        AddUsertoDb();
                    }
                }
            }
        });
    }

    private void checkLocationExists() {
        firebaseFirestore.collection(Currentuser.ShopCode).document("doc").collection("LocationDetails")
                .whereEqualTo("code", UserLocation.getEditText().getText().toString()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (!task.getResult().isEmpty()) {
                    Location = UserLocation.getEditText().getText().toString();
                    AddUsertoDb();
                } else {
                    UserLocation.getEditText().setText("");
                    UserLocation.setError("Location code does not exist");
                    UserLocation.requestFocus();
                    SDProgress(false, true);
                }
            }
        });
    }

    private void AddUsertoDb() {
        firebaseFirestore.collection("UserDetails").add(user).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
            @Override
            public void onComplete(@NonNull Task<DocumentReference> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(getContext(), "User Added", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), "Could not add User", Toast.LENGTH_SHORT).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.i("failuseraddition", e.getMessage());
            }
        });
        SDProgress(false, true);
        alertDialog.dismiss();
    }

    private void setupspinner(int position) {

        ArrayAdapter PermissionSpinnerAdapter = ArrayAdapter.createFromResource(getContext(),
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
                        UserLocation.setVisibility(View.GONE);
                    } else if (selection.equals(getString(R.string.permission_employee))) {
                        permission = "Employee";
                        UserLocation.setVisibility(View.VISIBLE);
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
            getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            if (alertDialog) {
                ProgressAlertLayout.setVisibility(View.VISIBLE);
            }
            else {
                ProgressLayout.setVisibility(View.VISIBLE);
            }
        } else {
            getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            if (alertDialog) {
                ProgressAlertLayout.setVisibility(View.GONE);
            }
            else {
                ProgressLayout.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void OnNoteClick(int position) {

    }
}
