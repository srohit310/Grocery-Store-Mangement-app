package com.stancorp.grocerystorev1.AdapterClasses;

import android.content.Context;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.firebase.ui.firestore.paging.FirestorePagingOptions;
import com.stancorp.grocerystorev1.Classes.Location;
import com.stancorp.grocerystorev1.GlobalClass.Gfunc;
import com.stancorp.grocerystorev1.R;

public class LocationFirestoreAdapter extends FirestoreBaseRecyclerAdapter<Location>{

    TextView LocationName;
    TextView LocationCode;
    TextView LocationAddress;
    Context context;
    Gfunc gfunc;

    public LocationFirestoreAdapter(@NonNull FirestorePagingOptions<Location> options, Context context, OnNoteListner onNoteListner,
                                    RelativeLayout progressLayout, ConstraintLayout emptyview) {
        super(options, context, onNoteListner, progressLayout, emptyview);
        gfunc = new Gfunc();
        this.context = context;
        layout_id = R.layout.location_layout;
        this.mOnNoteListner = onNoteListner;
        this.progressLayout = progressLayout;
    }

    @Override
    protected void onBindViewHolder(@NonNull FirestoreBaseRecyclerAdapter.MyViewHolder holder, int position, @NonNull Location location) {
        LocationName = holder.itemView.findViewById(R.id.Name);
        LocationCode = holder.itemView.findViewById(R.id.Code);
        LocationAddress = holder.itemView.findViewById(R.id.AddressLocation);

        LocationName.setText(gfunc.capitalize(location.name));
        LocationCode.setText(location.code);
        String Address = location.address.State+" "+location.address.City+"\n "
                +location.address.Street+" "+location.address.Pincode;
        LocationAddress.setText(Address);
    }
}
