package com.stancorp.grocerystorev1.ViewFragments;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.stancorp.grocerystorev1.AdapterClasses.BaseRecyclerAdapter;
import com.stancorp.grocerystorev1.Classes.StoreUser;
import com.stancorp.grocerystorev1.R;

public abstract class FragmentsGroups extends Fragment implements BaseRecyclerAdapter.OnNoteListner {

    public FloatingActionButton mfloat;
    public RecyclerView recyclerView;
    private EditText editText;
    private RecyclerView.LayoutManager mLayoutManager;
    public FirebaseFirestore firebaseFirestore;
    public  RelativeLayout progressLayout;
    private static final int RC_ADD = 251;
    public StoreUser user;
    public String sortby;
    RadioGroup SortGroup;
    public Query.Direction direction;
    public EditText searchBox;
    public Spinner spinner;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        return inflater.inflate(R.layout.fragment_group,container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        searchBox = view.findViewById(R.id.SearchText);
        mfloat = view.findViewById(R.id.floatingActionButton);
        mfloat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AddIntent();
            }
        });
        direction = Query.Direction.ASCENDING;
        progressLayout = view.findViewById(R.id.ProgressLayout);
        firebaseFirestore = FirebaseFirestore.getInstance();
        editText = view.findViewById(R.id.SearchText);
        editText.addTextChangedListener(new TextWatcher() {
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
        recyclerView = view.findViewById(R.id.recyclerView);
        mLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(mLayoutManager);
        initialize();
        fetchUserData();
    }

    protected abstract void initialize();

    private void fetchUserData() {
        SDProgress(true);
            firebaseFirestore.collection("UserDetails").whereEqualTo("Email",FirebaseAuth.getInstance().getCurrentUser().getEmail())
                    .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    for(DocumentSnapshot snapshot1: task.getResult()){
                        user = snapshot1.toObject(StoreUser.class);
                    }
                    mfloat.setVisibility(View.VISIBLE);
                    attachListData(sortby);
                }
            });
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.fragments_menu, menu);
        super.onCreateOptionsMenu(menu,inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.Sort:
                alertDialogSort();
                return true;
        }
        return false;
    }

    protected void alertDialogSort(){
        final AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), R.style.AlertDialogTheme);
        final View view = LayoutInflater.from(getContext()).inflate(
                R.layout.alertdialog_sort, (RelativeLayout) getView().findViewById(R.id.addsortalertcontainer)
        );
        builder.setView(view);
        spinner = view.findViewById(R.id.Sortoptions);
        final AlertDialog alertDialog = builder.create();
        view.findViewById(R.id.CancelAddItemButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });
        SortGroup = view.findViewById(R.id.radioGroup);
        direction = Query.Direction.ASCENDING;
        SortGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                int selectedId = SortGroup.getCheckedRadioButtonId();
                RadioButton SelectedSort = (RadioButton) view.findViewById(selectedId);
                if (SelectedSort.getText().toString().compareTo("Ascending") == 0) {
                    direction = Query.Direction.ASCENDING;
                } else {
                    direction = Query.Direction.DESCENDING;
                }
            }
        });
        setupSpinner(view);

        view.findViewById(R.id.ConfirmAddAdminButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attachListData(sortby);
                alertDialog.dismiss();
            }
        });
        if (alertDialog.getWindow() != null) {
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }
        alertDialog.show();
    }

    protected abstract void setupSpinner(View view);

    protected abstract void AddIntent();

    protected abstract void attachListData(String sortby);

    private void filter(String text){
        filteredlistcondition(text);
    }

    protected abstract void filteredlistcondition(String text);

    @Override
    public void OnNoteClick(int position) {
        displayIntent(position);
    }

    protected abstract void displayIntent(int posiiton);

    public void SDProgress(boolean show){
        if(show){
            progressLayout.setVisibility(View.VISIBLE);
        }else{
            progressLayout.setVisibility(View.GONE);
        }
    }
}
