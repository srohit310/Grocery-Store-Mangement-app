package com.stancorp.grocerystorev1.AdapterClasses;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.stancorp.grocerystorev1.GlobalClass.Gfunc;
import com.stancorp.grocerystorev1.R;

import java.util.ArrayList;

public class ListitemAdapter extends BaseRecyclerAdapter {

    ArrayList<String> list;
    TextView listitemtext;
    ImageView checkmark;
    int itemposition;

    public ListitemAdapter(Context context, OnNoteListner onNoteListner, ArrayList<String> list,
                           int itemposition) {
        super(context, onNoteListner);
        this.list = list;
        this.layout_id = R.layout.listtext_layout;
        this.itemposition = itemposition;
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public void onBindViewHold(int position, MyViewHolder holder) {
        String item = list.get(position);
        Gfunc gfunc = new Gfunc();
        listitemtext = holder.itemView.findViewById(R.id.listitemtext);
        checkmark = holder.itemView.findViewById(R.id.checkbox);
        if (item != null) {
            listitemtext.setText(gfunc.capitalize(item));
            if(itemposition == position){
                checkmark.setVisibility(View.VISIBLE);
            }
        }
    }
}
