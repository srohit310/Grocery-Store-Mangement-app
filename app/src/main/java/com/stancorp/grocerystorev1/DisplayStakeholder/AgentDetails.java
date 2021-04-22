package com.stancorp.grocerystorev1.DisplayStakeholder;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.firestore.FirebaseFirestore;

public class AgentDetails extends Fragment {

    private String ShopCode;
    private static final String intent_shopcode = "param1";
    FirebaseFirestore firebaseFirestore;

    public static AgentDetails newInstance(String Shopcode) {
        Bundle args = new Bundle();
        AgentDetails fragment = new AgentDetails();
        args.putString(intent_shopcode, Shopcode);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments()!=null){
            ShopCode = getArguments().getString(intent_shopcode);
        }
        firebaseFirestore = FirebaseFirestore.getInstance();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }
}
