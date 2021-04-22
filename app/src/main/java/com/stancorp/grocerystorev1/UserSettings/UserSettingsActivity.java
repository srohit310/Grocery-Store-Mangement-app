package com.stancorp.grocerystorev1.UserSettings;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.stancorp.grocerystorev1.Classes.StoreUser;
import com.stancorp.grocerystorev1.R;

public class UserSettingsActivity extends AppCompatActivity {

    StoreUser user;
    EditText UpdateName;
    TextView permission;
    ImageView profileImage;
    TextView DisplayName;
    TextView DisplayEmail;
    RelativeLayout progressLayout;
    DatabaseReference databaseReference;
    StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_settings);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        user = (StoreUser) getIntent().getSerializableExtra("UserData");
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        databaseReference = FirebaseDatabase.getInstance().getReference();
        storageReference = FirebaseStorage.getInstance().getReference();

        //initilaize views
        UpdateName = findViewById(R.id.UpdateName);
        permission = findViewById(R.id.PermissionLvl);
        profileImage = findViewById(R.id.userimageView);
        DisplayEmail = findViewById(R.id.DisplayEmail);
        DisplayName = findViewById(R.id.DisplayName);
        progressLayout = findViewById(R.id.ProgressLayout);

        setTexts();
    }

    private void setTexts() {
        progressLayout.setVisibility(View.VISIBLE);
        UpdateName.setText(user.Name);
        permission.setText(user.PermissionLevel);
        if(user.PermissionLevel.toLowerCase().compareTo("admin")==0){
            permission.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.green));
        }else{
            permission.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.aqua));
        }
        DisplayName.setText(user.Name);
        DisplayEmail.setText(user.Email);
        if(user.PhotoUri){
            storageReference.child(user.ShopCode).child("ProfileImages").child(user.Email)
                    .getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    profileImage.setForeground(null);
                    Glide.with(profileImage.getContext())
                            .load(uri)
                            .into(profileImage);
                    progressLayout.setVisibility(View.GONE);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_SHORT).show();
                    progressLayout.setVisibility(View.GONE);
                }
            });
        }else{
            progressLayout.setVisibility(View.GONE);
        }
    }
}