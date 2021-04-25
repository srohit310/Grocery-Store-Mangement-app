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
    LinkedHashMap<String,Agent> filteredList;

    @Override
    protected void toolbarspinnersetup(Spinner toolbarspinner) {
        toolbarspinner.setVisibility(View.GONE);
        attachListData(startcode,endcode);

    }

    @Override
    protected void initialize() {
        agents = new LinkedHashMap<>();
        filteredList = new LinkedHashMap<>();
        searchedittext.setHint("Search for vendor using name");
        agentAdaptor = new AgentAdapter(agents,getContext(),this,"Vendor");
        recyclerView.setAdapter(agentAdaptor);
    }

    @Override
    protected void AddIntent() {
        Intent intent = new Intent(getContext(), AddStakesholdersActivity.class);
        intent.putExtra("ShopCode",user.ShopCode);
        startActivity(intent);
    }

    @Override
    protected void attachListData(String startcode,String endcode) {
        SDProgress(true);
        agents.clear();
        filteredList.clear();

        firebaseFirestore.collection(user.ShopCode).document("doc").collection("StakeHolders")
                .whereGreaterThanOrEqualTo("Name", startcode).whereLessThan("Name", endcode)
                .whereIn("AgentType", Arrays.asList("Both","Vendor")).orderBy("Name", direction)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException error) {
                        if (queryDocumentSnapshots != null && !queryDocumentSnapshots.isEmpty()) {
                            for (DocumentChange doc : queryDocumentSnapshots.getDocumentChanges()){
                                Agent agent;
                                switch (doc.getType()){
                                    case ADDED:
                                        agent =  doc.getDocument().toObject(Agent.class);
                                        agents.put(agent.Code,agent);
                                        filteredList.put(agent.Code,agent);
                                        agentAdaptor.notifyDataSetChanged();
                                        break;
                                    case MODIFIED:
                                        agent =  doc.getDocument().toObject(Agent.class);
                                        agents.put(agent.Code,agent);
                                        filteredList.put(agent.Code,agent);
                                        agentAdaptor.notifyDataSetChanged();
                                        break;
                                    case REMOVED:
                                        agent =  doc.getDocument().toObject(Agent.class);
                                        agents.remove(agent.Code);
                                        filteredList.remove(agent.Code);
                                        agentAdaptor.notifyDataSetChanged();
                                        break;
                                }
                            }
                            SDProgress(false);
                        }else{
                            SDProgress(false);
                        }
                    }
                });
    }

    @Override
    protected void displayIntent(int position) {
        Intent intent = new Intent(getContext(), AgentViewActivity.class);
        Agent agent = (Agent) filteredList.values().toArray()[position];
        intent.putExtra("AgentCode",agent.Code);
        intent.putExtra("ShopCode",user.ShopCode);
        startActivity(intent);
    }
}
