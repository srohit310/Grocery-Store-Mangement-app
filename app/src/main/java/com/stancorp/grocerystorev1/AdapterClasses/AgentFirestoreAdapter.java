package com.stancorp.grocerystorev1.AdapterClasses;

import android.content.Context;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.firebase.ui.firestore.paging.FirestorePagingOptions;
import com.stancorp.grocerystorev1.Classes.Agent;
import com.stancorp.grocerystorev1.GlobalClass.Gfunc;
import com.stancorp.grocerystorev1.R;

public class AgentFirestoreAdapter extends FirestoreBaseRecyclerAdapter<Agent> {

    TextView Name;
    TextView Code;
    TextView Type;
    TextView Phoneno;
    Context context;
    LinearLayout customertypeLayout;
    Gfunc gfunc;


    public AgentFirestoreAdapter(@NonNull FirestorePagingOptions<Agent> options, Context context, OnNoteListner onNoteListner, RelativeLayout progressLayout) {
        super(options, context, onNoteListner, progressLayout);
        gfunc = new Gfunc();
        this.context = context;
        this.mOnNoteListner = onNoteListner;
        layout_id = R.layout.agent_layout;
        this.progressLayout = progressLayout;
    }

    @Override
    protected void onBindViewHolder(@NonNull FirestoreBaseRecyclerAdapter.MyViewHolder holder, int position, @NonNull Agent agent) {

        Name = holder.itemView.findViewById(R.id.agentName);
        Code = holder.itemView.findViewById(R.id.agentCode);
        Type = holder.itemView.findViewById(R.id.CusType);
        Phoneno = holder.itemView.findViewById(R.id.Phoneno);
        customertypeLayout = holder.itemView.findViewById(R.id.CustomerType);

        if (agent != null) {
            Name.setText(gfunc.capitalize(agent.Name));
            Code.setText(agent.Code);
            Type.setText(agent.CustomerType);
            Phoneno.setText(String.valueOf(agent.Phoneno));
        }
    }
}
