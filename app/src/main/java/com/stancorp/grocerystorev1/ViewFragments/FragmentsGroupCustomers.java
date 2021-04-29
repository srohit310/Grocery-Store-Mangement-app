package com.stancorp.grocerystorev1.ViewFragments;

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.annotation.Nullable;

import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QuerySnapshot;
import com.stancorp.grocerystorev1.AdapterClasses.AgentAdapter;
import com.stancorp.grocerystorev1.AddActivities.AddStakesholdersActivity;
import com.stancorp.grocerystorev1.Classes.Agent;
import com.stancorp.grocerystorev1.DisplayStakeholder.AgentViewActivity;
import com.stancorp.grocerystorev1.R;

import java.util.Arrays;
import java.util.LinkedHashMap;

public class FragmentsGroupCustomers extends FragmentsGroups {

    LinkedHashMap<String, Agent> agents;
    AgentAdapter agentAdaptor;
    ListenerRegistration agentListener;

    @Override
    protected void toolbarspinnersetup(Spinner toolbarspinner) {
        toolbarspinner.setVisibility(View.GONE);
        agents = new LinkedHashMap<>();
        agentAdaptor = new AgentAdapter(agents, getContext(), this, "Customer");
        attachListData(startcode, endcode);
    }

    @Override
    protected void initialize() {
        toolbar.setTitle("Customers");
        agents = new LinkedHashMap<>();
        searchedittext.setHint("Search for Customer using name");
        agentAdaptor = new AgentAdapter(agents, getContext(), this, "Customer");
        recyclerView.setAdapter(agentAdaptor);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (agents != null) {
            attachListData(startcode, endcode);
        }
    }

    @Override
    public void onPause() {
        agentListener.remove();
        super.onPause();
    }

    @Override
    protected void AddIntent() {
        Intent intent = new Intent(getContext(), AddStakesholdersActivity.class);
        intent.putExtra("ShopCode", user.ShopCode);
        intent.putExtra("ActivityMode", "Add");
        startActivity(intent);
    }

    @Override
    protected void attachListData(String startcode, String endcode) {
        SDProgress(true);
        agents.clear();
        agentListener =
                firebaseFirestore.collection(user.ShopCode).document("doc").collection("StakeHolders")
                        .whereGreaterThanOrEqualTo("Name", startcode).whereLessThan("Name", endcode)
                        .whereIn("AgentType", Arrays.asList("Both", "Customer")).orderBy("Name", direction).limit(20)
                        .addSnapshotListener(new EventListener<QuerySnapshot>() {
                            @Override
                            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException error) {
                                if (queryDocumentSnapshots != null && !queryDocumentSnapshots.isEmpty()) {
                                    for (DocumentChange doc : queryDocumentSnapshots.getDocumentChanges()) {
                                        Agent agent;
                                        switch (doc.getType()) {
                                            case ADDED:
                                                agent = doc.getDocument().toObject(Agent.class);
                                                agents.put(agent.Code, agent);
                                                agentAdaptor.notifyDataSetChanged();
                                                break;
                                            case MODIFIED:
                                                agent = doc.getDocument().toObject(Agent.class);
                                                agents.put(agent.Code, agent);
                                                agentAdaptor.notifyDataSetChanged();
                                                break;
                                            case REMOVED:
                                                agent = doc.getDocument().toObject(Agent.class);
                                                agents.remove(agent.Code);
                                                agentAdaptor.notifyDataSetChanged();
                                                break;
                                        }
                                        recyclerView.scheduleLayoutAnimation();
                                    }
                                    SDProgress(false);
                                } else {
                                    SDProgress(false);
                                }
                            }
                        });
    }

    @Override
    protected void displayIntent(int position) {
        Intent intent = new Intent(getContext(), AgentViewActivity.class);
        Agent agent = (Agent) agents.values().toArray()[position];
        intent.putExtra("Agent", agent);
        intent.putExtra("Mode", "Customer");
        intent.putExtra("ShopCode", user.ShopCode);
        intent.putExtra("UserPermission",user.PermissionLevel);
        intent.putExtra("UserLocation",user.Location);
        startActivity(intent);
    }
}
