package com.stancorp.grocerystorev1.AdapterClasses;

import android.content.Context;
import android.widget.TextView;

import com.stancorp.grocerystorev1.Classes.Location;
import com.stancorp.grocerystorev1.R;
import java.util.LinkedHashMap;

public class LocationAdapter extends BaseRecyclerAdapter {
    private BaseRecyclerAdapter.OnNoteListner mOnNoteListner;
    TextView LocationName;
    TextView LocationCode;
    TextView LocationAddress;
    Context context;

    public LocationAdapter(LinkedHashMap<String,Location> locationsArray, BaseRecyclerAdapter.OnNoteListner onNoteListner, Context context) {
        super(context,onNoteListner);
        dataList = locationsArray;
        this.mOnNoteListner = onNoteListner;
        layout_id = R.layout.location_layout;
        this.context = context;
    }

    @Override
    public void onBindViewHold(int position, MyViewHolder holder) {
        Location location = (Location) dataList.values().toArray()[position];
        //intializing
        LocationName = holder.itemView.findViewById(R.id.Name);
        LocationCode = holder.itemView.findViewById(R.id.Code);
        LocationAddress = holder.itemView.findViewById(R.id.AddressLocation);

        LocationName.setText(location.name.substring(0, 1).toUpperCase() + location.name.substring(1).toLowerCase());
        LocationCode.setText(location.code);
        LocationAddress.setText(location.address.State+" "+location.address.City+"\n "
                +location.address.Street+" "+location.address.Pincode);
    }
}
