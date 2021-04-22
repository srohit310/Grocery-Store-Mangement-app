package com.stancorp.grocerystorev1.AutoCompleteAdapter;

import android.content.Context;

import androidx.annotation.NonNull;

import com.stancorp.grocerystorev1.Classes.Agent;
import com.stancorp.grocerystorev1.Classes.ItemStockInfo;

import java.util.HashMap;
import java.util.LinkedHashMap;

public class AutoCompleteAgentAdapter extends AutoCompleteBaseAdapter<Agent> {

    public AutoCompleteAgentAdapter(@NonNull Context context, LinkedHashMap<String, Agent> objects) {
        super(context);
        this.mContext = context;
        this.dataList = new LinkedHashMap<String, Agent>(objects);
        this.dataListAll = new LinkedHashMap<String, Agent>(objects);
    }

    @Override
    protected CharSequence setTextviewString(Object resultValue) {
        return ((Agent) resultValue).Code;
    }

    @Override
    protected void setView(int position) {
        Agent agent = getItem(position);

        if(agent!=null){
            Name.setText(agent.Name);
            Code.setText(agent.Code);
        }
    }
}
