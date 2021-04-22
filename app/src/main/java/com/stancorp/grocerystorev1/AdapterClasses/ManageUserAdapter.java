package com.stancorp.grocerystorev1.AdapterClasses;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.SignInMethodQueryResult;
import com.stancorp.grocerystorev1.Classes.StoreUser;
import com.stancorp.grocerystorev1.R;

import java.util.LinkedHashMap;

public class ManageUserAdapter extends BaseRecyclerAdapter {

    private BaseRecyclerAdapter.OnNoteListner mOnNoteListner;
    TextView UserName;
    TextView UserEmail;
    TextView UserPhone;
    LinearLayout LocationLayout;
    TextView UserLCode;
    ImageView Registeredstatus;
    Context context;

    public ManageUserAdapter(LinkedHashMap<String,StoreUser> storeUsers, BaseRecyclerAdapter.OnNoteListner onNoteListner, Context context) {
        super(context, onNoteListner);
        dataList = storeUsers;
        this.mOnNoteListner = onNoteListner;
        layout_id = R.layout.user_layout;
        this.context = context;
    }

    @Override
    public void onBindViewHold(int position, MyViewHolder holder) {
        StoreUser user = (StoreUser) dataList.values().toArray()[position];
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
            UserLCode.setText(user.Location);
        }

        if (!user.Registeredflag) {
            Registeredstatus.setBackground(ContextCompat.getDrawable(context, R.drawable.circle_red));
        } else {
            Registeredstatus.setBackground(ContextCompat.getDrawable(context, R.drawable.circle_green));
        }


    }
}
