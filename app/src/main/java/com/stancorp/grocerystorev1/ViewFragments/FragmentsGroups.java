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
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
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
import com.stancorp.grocerystorev1.MainActivity;
import com.stancorp.grocerystorev1.R;

public abstract class FragmentsGroups extends Fragment implements BaseRecyclerAdapter.OnNoteListner {

    public FloatingActionButton mfloat;
    public RecyclerView recyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    public FirebaseFirestore firebaseFirestore;
    public  RelativeLayout progressLayout;
    private static final int RC_ADD = 251;
    public StoreUser user;
    public EditText searchedittext;
    public SearchView searchbarView;
    public String filterby;
    public String startcode,endcode;
    public Query.Direction direction;
    public EditText searchBox;
    public Spinner sortSpinner;
    public Spinner toolbarspinner;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        View view = inflater.inflate(R.layout.fragment_group,container,false);
        final AppCompatActivity act = (AppCompatActivity) getActivity();
        if (act.getSupportActionBar() != null) {
            Toolbar toolbar = (Toolbar) act.findViewById(R.id.toolbar);
            toolbarspinner = toolbar.findViewById(R.id.spinnertoolbar);
            toolbarspinner.setVisibility(View.VISIBLE);
        }
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        searchBox = view.findViewById(R.id.SearchText);
        startcode = "!";
        endcode = "{";
        mfloat = view.findViewById(R.id.floatingActionButton);
        mfloat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AddIntent();
            }
        });
        direction = Query.Direction.ASCENDING;
        progressLayout = ((AppCompatActivity) getActivity()).findViewById(R.id.ProgressLayout);
        firebaseFirestore = FirebaseFirestore.getInstance();
        recyclerView = view.findViewById(R.id.recyclerView);
        mLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(mLayoutManager);
        fetchUserData();
    }

    protected abstract void toolbarspinnersetup(Spinner toolbarspinner);

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
                    initialize();
                    toolbarspinnersetup(toolbarspinner);
                    mfloat.setVisibility(View.VISIBLE);
                }
            });
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.fragments_menu, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        searchbarView = (SearchView) searchItem.getActionView();
        searchedittext = (EditText) searchbarView.findViewById(androidx.appcompat.R.id.search_src_text);
        searchedittext.setHintTextColor(ContextCompat.getColor(getContext(),R.color.hintColor));
        searchedittext.setTextSize(16f);
        searchbarView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String search) {
                if(search.length()>0){
                    int strlength = search.length();
                    String strFrontCode = search.substring(0, strlength - 1);
                    String strEndCode = search.substring(strlength - 1, search.length());
                    startcode = search;
                    endcode = strFrontCode + Character.toString((char) (strEndCode.charAt(0) + 1));
                    attachListData(startcode,endcode);
                }
                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                if(s.length() == 0){
                    startcode = "!";
                    endcode = "{";
                    attachListData(startcode,endcode);
                }
                return false;
            }
        });
        super.onCreateOptionsMenu(menu,inflater);
    }

    protected abstract void AddIntent();

    protected abstract void attachListData(String startcode,String endcode);

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
