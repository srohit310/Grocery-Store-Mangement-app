package com.stancorp.grocerystorev1.ViewFragments;

import android.content.Intent;
import android.view.View;
import android.widget.Spinner;

import androidx.paging.PagedList;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.firebase.ui.firestore.paging.FirestorePagingOptions;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;
import com.stancorp.grocerystorev1.AdapterClasses.AgentFirestoreAdapter;
import com.stancorp.grocerystorev1.AddActivities.AddStakesholdersActivity;
import com.stancorp.grocerystorev1.Classes.Agent;
import com.stancorp.grocerystorev1.DisplayStakeholder.AgentViewActivity;

import java.util.Arrays;

public class FragmentsGroupVendors extends FragmentsGroups {

    AgentFirestoreAdapter agentAdaptor;

    @Override
    protected void toolbarspinnersetup(Spinner toolbarspinner) {
        toolbarspinner.setVisibility(View.GONE);
    }

    @Override
    protected void initialize() {
        toolbar.setTitle("Vendors");
        searchedittext.setHint("Search for Vendor using name");
        startcode = "!";
        endcode = "{";
        attachListData(startcode, endcode);
    }

    @Override
    protected void AddIntent() {
        Intent intent = new Intent(getContext(), AddStakesholdersActivity.class);
        intent.putExtra("ShopCode", user.ShopCode);
        intent.putExtra("ActivityMode", "Add");
        startActivity(intent);
    }

    @Override
    public void onStop() {
        super.onStop();
        agentAdaptor.stopListening();
    }

    @Override
    public void onStart() {
        super.onStart();
        if (agentAdaptor != null)
            agentAdaptor.startListening();
    }

    @Override
    protected void attachListData(String startcode, String endcode) {
        SDProgress(true);
        Query query = firebaseFirestore.collection(user.ShopCode).document("doc").collection("StakeHolders")
                .whereGreaterThanOrEqualTo("Name", startcode).whereLessThan("Name", endcode)
                .whereIn("AgentType", Arrays.asList("Both", "Vendor")).orderBy("Name", direction).limit(20);


        PagedList.Config config = new PagedList.Config.Builder()
                .setInitialLoadSizeHint(10)
                .setPageSize(3)
                .build();

        FirestorePagingOptions<Agent> options = new FirestorePagingOptions.Builder<Agent>()
                .setQuery(query, config, Agent.class)
                .build();

        agentAdaptor = new AgentFirestoreAdapter(options, getContext(), this, progressLayout);
        agentAdaptor.startListening();
        agentAdaptor.notifyDataSetChanged();
        recyclerView.setAdapter(agentAdaptor);
        recyclerView.scheduleLayoutAnimation();
    }

    @Override
    protected void displayFirestoreIntent(DocumentSnapshot documentSnapshot, int posiiton) {
        Intent intent = new Intent(getContext(), AgentViewActivity.class);
        Agent agent = documentSnapshot.toObject(Agent.class);
        intent.putExtra("Agent", agent);
        intent.putExtra("Mode", "Vendor");
        intent.putExtra("ShopCode", user.ShopCode);
        intent.putExtra("UserPermission", user.PermissionLevel);
        intent.putExtra("UserLocation", user.Location);
        startActivity(intent);
    }
}
