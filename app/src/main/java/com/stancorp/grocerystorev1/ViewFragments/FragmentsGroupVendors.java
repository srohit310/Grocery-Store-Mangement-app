package com.stancorp.grocerystorev1.ViewFragments;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
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
    protected void initialize() {
        agents = new LinkedHashMap<>();
        filteredList = new LinkedHashMap<>();
        agentAdaptor = new AgentAdapter(agents,getContext(),this,"Vendor");
        recyclerView.setAdapter(agentAdaptor);
        sortby = "Name";
    }

    @Override
    protected void setupSpinner(View view) {
        ArrayAdapter itemsortoptions = ArrayAdapter.createFromResource(getContext(),
                R.array.array_item_search_options, R.layout.spinner_user_item_text);

        itemsortoptions.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        spinner.setAdapter(itemsortoptions);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selection = parent.getItemAtPosition(position).toString();
                if (!TextUtils.isEmpty(selection)) {
                    if (selection.equals(getString(R.string.item_name))) {
                        sortby = "Name";
                    } else if (selection.equals(getString(R.string.item_code))) {
                        sortby = "codeno";
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                sortby = "Name";
            }
        });
    }

    @Override
    protected void AddIntent() {
        Intent intent = new Intent(getContext(), AddStakesholdersActivity.class);
        intent.putExtra("ShopCode",user.ShopCode);
        startActivity(intent);
    }

    @Override
    protected void attachListData(String sortby) {
        SDProgress(true);
        agents.clear();
        filteredList.clear();

        firebaseFirestore.collection(user.ShopCode).document("doc").collection("StakeHolders")
                .whereIn("AgentType", Arrays.asList("Both","Vendor")).orderBy(sortby, direction)
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
    protected void filteredlistcondition(String text) {
        filteredList = new LinkedHashMap<>();
        for(Agent agent: agents.values() ){
            if(agent.Name.toLowerCase().startsWith(text.toLowerCase())){
                filteredList.put(agent.Code,agent);
            }
        }
        agentAdaptor.filterList(filteredList);
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
