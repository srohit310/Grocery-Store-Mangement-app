package com.stancorp.grocerystorev1.AdapterClasses;

import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.stancorp.grocerystorev1.Classes.Agent;
import com.stancorp.grocerystorev1.Classes.Items;
import com.stancorp.grocerystorev1.GlobalClass.Gfunc;
import com.stancorp.grocerystorev1.R;

import java.util.LinkedHashMap;

public class AgentAdapter extends BaseRecyclerAdapter {

    private LinkedHashMap<String, Agent> agentsArrayList;
    private BaseRecyclerAdapter.OnNoteListner mOnNoteListner;

    TextView Name;
    TextView Code;
    TextView Type;
    TextView Phoneno;
    Context context;
    LinearLayout customertypeLayout;
    String agenttype;

    Gfunc gfunc;

    public AgentAdapter(LinkedHashMap<String, Agent> agents, Context context, OnNoteListner onNoteListner, String Type) {
        super(context, onNoteListner);
        dataList = agents;
        gfunc = new Gfunc();
        this.context = context;
        agentsArrayList = agents;
        this.mOnNoteListner = onNoteListner;
        layout_id = R.layout.agent_layout;
        this.agenttype = Type;
    }

    @Override
    public void onBindViewHold(int position, MyViewHolder holder) {
        Agent agent = (Agent) dataList.values().toArray()[position];

        Name = holder.itemView.findViewById(R.id.agentName);
        Code = holder.itemView.findViewById(R.id.agentCode);
        Type = holder.itemView.findViewById(R.id.CusType);
        Phoneno = holder.itemView.findViewById(R.id.Phoneno);
        customertypeLayout = holder.itemView.findViewById(R.id.CustomerType);

        if(agent != null){
            Name.setText(gfunc.capitalize(agent.Name));
            Code.setText(agent.Code);
            if(agenttype == "Customer"){
                Type.setText(agent.CustomerType);
            }else{
                customertypeLayout.setVisibility(View.GONE);
            }
            Phoneno.setText(String.valueOf(agent.Phoneno));
        }
    }
}
