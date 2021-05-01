package com.stancorp.grocerystorev1.ViewFragments;

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

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

public class FragmentsGroupVendors extends FragmentsGroups {

    public FragmentsGroupCustomers customerFragment;
    LinkedHashMap<String, Agent> agents;
    AgentAdapter agentAdaptor;
    ListenerRegistration agentListener;

    @Override
    protected void toolbarspinnersetup(Spinner toolbarspinner) {
        toolbarspinner.setVisibility(View.GONE);
    }

    @Override
    protected void initialize() {
        if (agents == null) {
            agents = new LinkedHashMap<>();
            startcode = "!";
            endcode = "{";
            agentAdaptor = new AgentAdapter(agents, getContext(), this, "Vendor");
            attachListData(startcode, endcode);
        }
        searchedittext.setHint("Search for vendor using name");
        recyclerView.setAdapter(agentAdaptor);
    }

    @Override
    protected void AddIntent() {
        Intent intent = new Intent(getContext(), AddStakesholdersActivity.class);
        intent.putExtra("ShopCode", user.ShopCode);
        intent.putExtra("ActivityMode", "Add");
        startActivity(intent);
    }

    @Override
    public void onResume() {
        super.onResume();
        startcode = "!";
        endcode = "{";
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
    protected void attachListData(String startcode, String endcode) {
        SDProgress(true);
        agents.clear();
        agentListener =
                firebaseFirestore.collection(user.ShopCode).document("doc").collection("StakeHolders")
                        .whereGreaterThanOrEqualTo("Name", startcode).whereLessThan("Name", endcode)
                        .whereIn("AgentType", Arrays.asList("Both", "Vendor")).orderBy("Name", direction)
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
        intent.putExtra("AgentCode", agent.Code);
        intent.putExtra("Agent", agent);
        intent.putExtra("Mode", "Vendor");
        intent.putExtra("UserPermission", user.PermissionLevel);
        intent.putExtra("UserLocation", user.Location);
        intent.putExtra("ShopCode", user.ShopCode);
        startActivity(intent);
    }
}
