package com.stancorp.grocerystorev1.ViewFragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.stancorp.grocerystorev1.AdapterClasses.BaseRecyclerAdapter;
import com.stancorp.grocerystorev1.AdapterClasses.ListitemAdapter;
import com.stancorp.grocerystorev1.R;

import java.util.ArrayList;
import java.util.Objects;

public class BottomFragmentItems extends BottomSheetDialogFragment implements BaseRecyclerAdapter.OnNoteListner {

    RecyclerView recyclerView;
    TextView title;
    RecyclerView.LayoutManager layoutManager;
    ListitemAdapter listitemAdapter;
    FragmentGroupItems parentFrag;

    public static final String TAG = "ActionBottomDialog";

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String ARG_PARAM3 = "param3";

    private ArrayList<String> list;
    private String Mode;
    private int position;

    public static BottomFragmentItems newInstance(String Mode, ArrayList<String> list, int position) {

        Bundle args = new Bundle();
        BottomFragmentItems fragment = new BottomFragmentItems();
        args.putString(ARG_PARAM1, Mode);
        args.putStringArrayList(ARG_PARAM2, list);
        args.putInt(ARG_PARAM3,position);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        parentFrag = ((FragmentGroupItems) this.getParentFragment());
        list = new ArrayList<>();
        if (getArguments() != null) {
            Mode = getArguments().getString(ARG_PARAM1);
            assert Mode != null;
            list.addAll(Objects.requireNonNull(getArguments().getStringArrayList(ARG_PARAM2)));
            position = getArguments().getInt(ARG_PARAM3);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bottom_item_drawer, container, false);
        title = view.findViewById(R.id.title);
        if (Mode.compareToIgnoreCase("Category") == 0) {
            title.setText("Categories");
        } else {
            title.setText("Brands");
        }
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView = view.findViewById(R.id.recyclerView);
        layoutManager = new LinearLayoutManager(getContext());
        listitemAdapter = new ListitemAdapter(getContext(), this, list, position);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(listitemAdapter);
    }

    @Override
    public void OnNoteClick(int position) {
        if(parentFrag.Category.compareToIgnoreCase(list.get(position))!=0) {
            if (Mode.compareToIgnoreCase("Category") == 0) {
                parentFrag.Category = list.get(position);
            } else {
                parentFrag.Brand = list.get(position);
            }
            parentFrag.filterchange();
            dismiss();
        }
    }
}
