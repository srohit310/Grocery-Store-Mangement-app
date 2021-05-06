package com.stancorp.grocerystorev1.ViewFragments;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.Guideline;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.stancorp.grocerystorev1.AdapterClasses.BaseRecyclerAdapter;
import com.stancorp.grocerystorev1.AdapterClasses.FirestoreBaseRecyclerAdapter;
import com.stancorp.grocerystorev1.Classes.StoreUser;
import com.stancorp.grocerystorev1.MainActivity;
import com.stancorp.grocerystorev1.R;

public abstract class FragmentsGroups extends Fragment implements FirestoreBaseRecyclerAdapter.OnNoteListner {

    public FloatingActionButton mfloat;
    public RecyclerView recyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    public FirebaseFirestore firebaseFirestore;
    public  RelativeLayout progressLayout;
    private static final int RC_ADD = 251;
    public StoreUser user;
    public ConstraintLayout emptyview;
    public EditText searchedittext;
    public ListenerRegistration registration;
    public SearchView searchbarView;
    public String filterby;
    public String startcode,endcode;
    public Query.Direction direction;
    public EditText searchBox;
    public Spinner toolbarspinner;
    public TextView categoryfilter;
    public TextView brandfilter;
    public boolean Flag;
    public LinearLayout itemfilterlayout;

    public Toolbar toolbar;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        View view = inflater.inflate(R.layout.fragment_group,container,false);
        firebaseFirestore = FirebaseFirestore.getInstance();
        final AppCompatActivity act = (AppCompatActivity) getActivity();
        emptyview = view.findViewById(R.id.emptyLayout);
        if (act.getSupportActionBar() != null) {
            toolbar = (Toolbar) act.findViewById(R.id.toolbar);
            toolbarspinner = toolbar.findViewById(R.id.spinnertoolbar);
            toolbarspinner.setVisibility(View.VISIBLE);
        }
        return view;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle bundle) {
        super.onSaveInstanceState(bundle);
        bundle.putSerializable("User", user);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        searchBox = view.findViewById(R.id.SearchText);
        itemfilterlayout = view.findViewById(R.id.filteritem);
        itemfilterlayout.setVisibility(View.GONE);
        categoryfilter = view.findViewById(R.id.Categorytext);
        brandfilter = view.findViewById(R.id.BrandText);
        startcode = "!";
        endcode = "{";
        Flag = true;
        if (savedInstanceState != null) {
            Log.i("beforecrashv1", "onassign");
            int currentPID = android.os.Process.myPid();
            if (currentPID != savedInstanceState.getInt("PID")) {
                user = (StoreUser) savedInstanceState.getSerializable("User");
            }
        }else{
            user = ((MainActivity)getActivity()).User;
        }
        mfloat = view.findViewById(R.id.floatingActionButton);
        mfloat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AddIntent();
            }
        });
        direction = Query.Direction.ASCENDING;
        progressLayout = ((AppCompatActivity) getActivity()).findViewById(R.id.ProgressLayout);
        recyclerView = view.findViewById(R.id.recyclerView);
        mLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(mLayoutManager);
        mfloat.setVisibility(View.VISIBLE);
        toolbarspinnersetup(toolbarspinner);
    }

    protected abstract void toolbarspinnersetup(Spinner toolbarspinner);

    protected abstract void initialize();

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if(newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE){
        }else if(newConfig.orientation == Configuration.ORIENTATION_PORTRAIT){
        }
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
                    Flag = false;
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
                if(s.length() == 0 && !Flag){
                    startcode = "!";
                    Flag = true;
                    endcode = "{";
                    attachListData(startcode,endcode);
                }
                return false;
            }
        });
        initialize();
        super.onCreateOptionsMenu(menu,inflater);
    }

    protected abstract void AddIntent();

    protected abstract void attachListData(String startcode,String endcode);

    @Override
    public void OnNoteClick(DocumentSnapshot documentSnapshot ,int position) {
        displayFirestoreIntent(documentSnapshot, position);
    }

    protected abstract void displayFirestoreIntent(DocumentSnapshot documentSnapshot, int posiiton);

    public void SDProgress(boolean show){
        if(show){
            progressLayout.animate()
                    .alpha(1.0f)
                    .setDuration(300)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                            progressLayout.setVisibility(View.VISIBLE);
                        }
                    });
        }else{
            progressLayout.animate()
                    .alpha(0.0f)
                    .setDuration(300)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                            progressLayout.setVisibility(View.GONE);
                        }
                    });
        }
    }
}
