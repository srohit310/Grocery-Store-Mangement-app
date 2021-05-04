package com.stancorp.grocerystorev1.AdapterClasses;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.firebase.ui.firestore.paging.FirestorePagingOptions;
import com.stancorp.grocerystorev1.Classes.StoreUser;
import com.stancorp.grocerystorev1.R;

public class ManageUserFirestoreAdapter extends FirestoreBaseRecyclerAdapter<StoreUser> {

    private OnNoteListner mOnNoteListner;
    TextView UserName;
    TextView UserEmail;
    TextView UserPhone;
    LinearLayout LocationLayout;
    TextView UserLCode;
    ImageView Registeredstatus;
    Context context;

    public ManageUserFirestoreAdapter(@NonNull FirestorePagingOptions<StoreUser> options, Context context, OnNoteListner onNoteListner, RelativeLayout progressLayout) {
        super(options, context, onNoteListner, progressLayout);
        this.mOnNoteListner = onNoteListner;
        layout_id = R.layout.user_layout;
        this.context = context;
        this.progressLayout = progressLayout;
    }

    @Override
    protected void onBindViewHolder(@NonNull FirestoreBaseRecyclerAdapter.MyViewHolder holder, int position, @NonNull StoreUser user) {
        //initialize
        UserName = holder.itemView.findViewById(R.id.UserName);
        UserEmail = holder.itemView.findViewById(R.id.UserEmail);
        UserPhone = holder.itemView.findViewById(R.id.UserPhone);
        LocationLayout = holder.itemView.findViewById(R.id.LocationDetails);
        UserLCode = holder.itemView.findViewById(R.id.Userlcode);
        Registeredstatus = holder.itemView.findViewById(R.id.approvedcircle);

        UserName.setText(user.Name.substring(0, 1).toUpperCase() + user.Name.substring(1).toLowerCase());
        UserEmail.setText(user.Email);
        UserPhone.setText(user.Phone);

        if (user.PermissionLevel.compareTo("Admin") == 0) {
            LocationLayout.setVisibility(View.GONE);
        } else {
            LocationLayout.setVisibility(View.VISIBLE);
            UserLCode.setText(user.Location);
        }

        if (!user.Registeredflag) {
            Registeredstatus.setBackground(ContextCompat.getDrawable(context, R.drawable.circle_red));
        } else {
            Registeredstatus.setBackground(ContextCompat.getDrawable(context, R.drawable.circle_green));
        }
    }
}
