package com.stancorp.grocerystorev1.UserSettings;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.stancorp.grocerystorev1.AddActivities.AddItemActivity;
import com.stancorp.grocerystorev1.Classes.LocationStockItem;
import com.stancorp.grocerystorev1.Classes.StoreInfo;
import com.stancorp.grocerystorev1.Classes.StoreUser;
import com.stancorp.grocerystorev1.GlobalClass.Gfunc;
import com.stancorp.grocerystorev1.R;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.ArrayList;
import java.util.Arrays;

public class UserSettingsActivity extends AppCompatActivity {

    StoreUser user;
    EditText UpdateName;
    TextView permission;
    ImageView profileImage;
    Button shopinfopush;
    LinearLayout shopinfolayout;
    TextView DisplayName;
    TextView ShopCode;
    AlertDialog alertDialog;
    TextView ShopName;
    Button feedback;
    Button ChangePassword;
    TextView DisplayEmail;
    RelativeLayout progressLayout;
    DatabaseReference databaseReference;
    StorageReference storageReference;
    FirebaseFirestore firebaseFirestore;
    ArrayList<TextInputLayout> textInputLayouts;

    Uri photoUri;

    public static final int STOREAGE_PERMISSION_CODE = 21;

    Gfunc gfunc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_settings);
        firebaseFirestore = FirebaseFirestore.getInstance();

        gfunc = new Gfunc();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        user = (StoreUser) getIntent().getSerializableExtra("UserData");
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        photoUri = null;

        retrieveshopinfo();

        databaseReference = FirebaseDatabase.getInstance().getReference();
        storageReference = FirebaseStorage.getInstance().getReference();
        ShopCode = findViewById(R.id.ShopCode);
        ShopName = findViewById(R.id.ShopName);
        shopinfopush = findViewById(R.id.shopinfoPush);
        shopinfolayout = findViewById(R.id.shopinfoLayout);
        shopinfopush.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(shopinfolayout.getVisibility() == View.VISIBLE) {
                    shopinfolayout.setVisibility(View.GONE);
                }else
                    shopinfolayout.setVisibility(View.VISIBLE);
            }
        });

        //initilaize views
        UpdateName = findViewById(R.id.UpdateName);
        permission = findViewById(R.id.PermissionLvl);
        profileImage = findViewById(R.id.userimageView);
        DisplayEmail = findViewById(R.id.DisplayEmail);
        DisplayName = findViewById(R.id.DisplayName);
        progressLayout = findViewById(R.id.ProgressLayout);
        feedback = findViewById(R.id.Feedbackintent);
        feedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(UserSettingsActivity.this,R.style.MyDialogTheme)
                        .setTitle("Open Email Service?")
                        .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Intent intent = new Intent(Intent.ACTION_SENDTO);
                                intent.setType("message/rfc822");
                                intent.putExtra(Intent.EXTRA_EMAIL, new String[] { "rohit.pilakkottil@gmail.com" });
                                intent.putExtra(Intent.EXTRA_SUBJECT, "Grocery Store Management app FeedBack");
                                startActivity(Intent.createChooser(intent, "Send Email"));
                            }
                        })
                        .setNegativeButton("Cancel",null)
                        .show();
            }
        });

        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ContextCompat.checkSelfPermission(UserSettingsActivity.this,
                        Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    CropImage.activity()
                            .setGuidelines(CropImageView.Guidelines.ON)
                            .setAspectRatio(1, 1)
                            .start(UserSettingsActivity.this);
                } else
                    requestpermission(Manifest.permission.READ_EXTERNAL_STORAGE,"In order to access gallery permission" +
                            " to read external storage is required",STOREAGE_PERMISSION_CODE);
            }
        });

        ChangePassword = findViewById(R.id.changePassword);
        ChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changePasswordAlertDialog();
            }
        });

        setTexts();
    }

    private void changePasswordAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(UserSettingsActivity.this, R.style.AlertDialogTheme);
        View view = LayoutInflater.from(UserSettingsActivity.this).inflate(
                R.layout.alertdialog_changepassword, (RelativeLayout) findViewById(R.id.additemalertcontainer)
        );
        builder.setView(view);
        alertDialog = builder.create();

        textInputLayouts = new ArrayList<TextInputLayout>(new ArrayList<TextInputLayout>(Arrays.<TextInputLayout>asList(
                (TextInputLayout) view.findViewById(R.id.signinoldpassword),
                (TextInputLayout) view.findViewById(R.id.signinnewpassword), (TextInputLayout) view.findViewById(R.id.signinnewrepeatpassword)
        )));

        view.findViewById(R.id.CancelAddAdminButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });

        view.findViewById(R.id.ConfirmAddAdminButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Boolean flag = true;
                for (int i = 0; i < textInputLayouts.size(); i++) {
                    String text = textInputLayouts.get(i).getEditText().getText().toString();
                    if (textInputLayouts.get(i).getEditText().getText().toString().length() == 0) {
                        textInputLayouts.get(i).getEditText().setText("");
                        textInputLayouts.get(i).getEditText().requestFocus();
                        textInputLayouts.get(i).setError("Please fill this field");
                        flag = false;
                        break;
                    }
                }
                String oldpassword = textInputLayouts.get(0).getEditText().getText().toString();
                final String newpassword = textInputLayouts.get(1).getEditText().getText().toString();
                String confirmnewpassword = textInputLayouts.get(2).getEditText().getText().toString();
                if(flag && newpassword.compareTo(confirmnewpassword)!=0){
                    textInputLayouts.get(2).getEditText().setText("");
                    textInputLayouts.get(2).getEditText().requestFocus();
                    textInputLayouts.get(2).setError("Passwords does not Match");
                    flag = false;
                }
                if (flag) {
                    AuthCredential credential = EmailAuthProvider.getCredential(user.Email,oldpassword);
                    final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    user.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                user.updatePassword(newpassword).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful()){
                                            Toast.makeText(getApplicationContext(),"Changed Password",Toast.LENGTH_SHORT).show();
                                        }else{
                                            Toast.makeText(getApplicationContext(),"Could not Change Password",Toast.LENGTH_SHORT).show();
                                        }
                                        alertDialog.dismiss();
                                    }
                                });
                            }else{
                                textInputLayouts.get(0).getEditText().setText("");
                                textInputLayouts.get(0).setError("Wrong password");
                                textInputLayouts.get(0).requestFocus();
                            }
                        }
                    });
                }

            }
        });
        if (alertDialog.getWindow() != null) {
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }
        alertDialog.show();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return false;
    }

    private void retrieveshopinfo() {
        firebaseFirestore.collection("StoreInfo").whereEqualTo("Code",user.ShopCode).get()
        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.getResult().isEmpty()){
                    Toast.makeText(getApplicationContext(),"Couldnt retrieve shop info",Toast.LENGTH_SHORT).show();
                }else{
                    for(DocumentSnapshot documentSnapshot : task.getResult()){
                        StoreInfo storeInfo = documentSnapshot.toObject(StoreInfo.class);
                        ShopCode.setText(storeInfo.Code);
                        ShopName.setText(storeInfo.Name);
                    }
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        Boolean flag = checkinputchange();
        if (flag) {
            new AlertDialog.Builder(UserSettingsActivity.this, R.style.MyDialogTheme)
                    .setTitle("Save Changes")
                    .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            checkinput();
                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Intent intent = new Intent();
                            setResult(RESULT_CANCELED,intent);
                            finish();
                        }
                    }).show();
        } else {
            finish();
        }
    }

    private void checkinput() {
        if (UpdateName.getText().toString().length() < 8 || UpdateName.getText().toString().length() > 16) {
            UpdateName.setError("Enter a Name having 8 - 16 characters");
            UpdateName.requestFocus();
        }else{
            SDProgress(true);
            if(photoUri!=null){
                storageReference.child(user.ShopCode).child("ProfileImages").child(user.Email)
                        .putFile(photoUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if(task.isSuccessful()){
                            user.PhotoUri = true;
                        }else{
                            photoUri = null;
                        }
                        updatetodatabase();
                    }
                });
            }else
                updatetodatabase();
        }
    }

    private void updatetodatabase() {
        firebaseFirestore.collection("UserDetails").whereEqualTo("Email",user.Email).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.getResult().isEmpty()){
                            Toast.makeText(getApplicationContext(),"Couldn't retrieve information from database",
                                    Toast.LENGTH_SHORT).show();
                        }else{
                            String key="";
                            for(DocumentSnapshot doc:task.getResult()) {
                                key = doc.getId();
                            }
                            user.Name = UpdateName.getText().toString().toLowerCase().trim();
                            firebaseFirestore.collection("UserDetails").document(key)
                                    .set(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    Intent intent = new Intent();
                                    if(task.isSuccessful()){
                                        Toast.makeText(getApplicationContext(),"User info updated",Toast.LENGTH_SHORT).show();
                                        intent.putExtra("Name",user.Name);
                                        intent.putExtra("imgchange",photoUri!=null);
                                        setResult(RESULT_OK,intent);
                                        finish();
                                    }else{
                                        Toast.makeText(getApplicationContext(),"Could not update info",Toast.LENGTH_SHORT).show();
                                        setResult(RESULT_CANCELED,intent);
                                        finish();
                                    }
                                    SDProgress(false);
                                }
                            });
                        }
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                photoUri = result.getUri();
                profileImage.setForeground(null);
                Glide.with(profileImage.getContext())
                        .load(photoUri)
                        .into(profileImage);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private boolean checkinputchange() {
        if (UpdateName.getText().toString().trim().compareToIgnoreCase(user.Name) != 0) {
            return true;
        }
        if(photoUri!=null){
            return true;
        }
        return false;
    }

    private void setTexts() {
        SDProgress(true);
        UpdateName.setText(gfunc.capitalize(user.Name));
        permission.setText(user.PermissionLevel);
        if (user.PermissionLevel.toLowerCase().compareTo("admin") == 0) {
            permission.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.green));
        } else {
            permission.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.aqua));
        }
        DisplayName.setText(gfunc.capitalize(user.Name));
        DisplayEmail.setText(user.Email);
        if (user.PhotoUri) {
            storageReference.child(user.ShopCode).child("ProfileImages").child(user.Email)
                    .getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    profileImage.setForeground(null);
                    Glide.with(profileImage.getContext())
                            .load(uri)
                            .into(profileImage);
                    SDProgress(false);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    SDProgress(false);
                }
            });
        } else {
            SDProgress(false);
        }
    }

    public void SDProgress(boolean show) {
        if (show) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            progressLayout.setVisibility(View.VISIBLE);
        } else {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            progressLayout.setVisibility(View.GONE);
        }
    }

    private void requestpermission(final String Permission, String Message, final Integer code) {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Permission)) {
            new AlertDialog.Builder(this,R.style.MyDialogTheme)
                    .setTitle("Permission Needed")
                    .setMessage(Message)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            ActivityCompat.requestPermissions(UserSettingsActivity.this,
                                    new String[]{Permission}, code);
                        }
                    })
                    .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    })
                    .create().show();
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Permission},
                    code);
        }
    }
}