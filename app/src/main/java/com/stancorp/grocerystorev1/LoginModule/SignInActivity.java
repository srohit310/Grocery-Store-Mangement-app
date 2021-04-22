package com.stancorp.grocerystorev1.LoginModule;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.SignInMethodQueryResult;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.stancorp.grocerystorev1.GlobalClass.Gfunc;
import com.stancorp.grocerystorev1.R;
import com.stancorp.grocerystorev1.Classes.StoreUser;

import java.util.ArrayList;

public class SignInActivity extends AppCompatActivity {

    TextInputLayout Shop;
    TextInputLayout Email;
    TextInputLayout Password;
    Button signInButton;
    FirebaseAuth mAuth;
    ArrayList<String> hints;
    FirebaseFirestore firebaseFirestore;
    RelativeLayout ProgressLayout;
    StoreUser User;
    Button HomeButton;
    Gfunc gfunc;
    TextView Wrongcredential;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        gfunc = new Gfunc();
        mAuth = FirebaseAuth.getInstance();
        hints = new ArrayList<>();
        HomeButton = findViewById(R.id.upbutton);
        ProgressLayout = findViewById(R.id.ProgressLayout);

        firebaseFirestore = FirebaseFirestore.getInstance();

        Wrongcredential = findViewById(R.id.incorrectTextView);

        Shop = findViewById(R.id.signinshop);
        Email = findViewById(R.id.signinemail);
        Password = findViewById(R.id.signinpassword);

        signInButton = findViewById(R.id.SignInButton);
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!CheckifConnected()) {
                    new AlertDialog.Builder(SignInActivity.this,R.style.AlertDialogTheme)
                            .setTitle("Internet Connection Required")
                            .setMessage("Please connect to the internet to continue")
                            .setCancelable(false)
                            .setPositiveButton("Connect", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                                }
                            })
                            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.dismiss();
                                    finish();
                                }
                            })
                            .create().show();
                } else {
                    if (signInButton.getText().toString() == getString(R.string.NextButton)) {
                        if (Shop.getEditText().getText().toString().length() == 0) {
                            Shop.getEditText().setText("");
                            Shop.setError("Please enter shop code assigned");
                            Shop.requestFocus();
                        } else if (!gfunc.isEmailValid(Email.getEditText().getText().toString())) {
                            Email.getEditText().setText("");
                            Email.setError("Enter a valid email");
                            Email.requestFocus();
                        } else
                            Authenticate();
                    } else {
                        if (Password.getEditText().getText().toString().length() == 0) {
                            Password.getEditText().setText("");
                            Password.setError("Please enter password");
                            Password.requestFocus();
                        } else {
                            SDProgress(true);
                            mAuth.signInWithEmailAndPassword(Email.getEditText().getText().toString(), Password.getEditText().getText().toString())
                                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                        @Override
                                        public void onComplete(@NonNull Task<AuthResult> task) {
                                            if (task.isSuccessful()) {
                                                Intent data = new Intent();
                                                setResult(RESULT_OK, data);
                                                finish();
                                            } else {
                                                Password.getEditText().setText("");
                                                Password.setHint("Wrong Password entered");
                                                Password.requestFocus();
                                                SDProgress(false);
                                            }
                                        }
                                    });
                        }
                    }
                }
            }
        });
        HomeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {

        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {

        }
    }

    private void Authenticate() {
        SDProgress(true);
        Query query = firebaseFirestore.collection("UserDetails").whereEqualTo("Email",Email.getEditText().getText().toString())
                .whereEqualTo("ShopCode",Shop.getEditText().getText().toString());
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(!task.getResult().isEmpty()){
                    for (DocumentSnapshot snapshot1 : task.getResult()) {
                        User = snapshot1.toObject(StoreUser.class);
                    }
                    if(!User.Registeredflag){
                        SDProgress(false);
                        Wrongcredential.setText("Account Not Registered Yet!");
                        Wrongcredential.setVisibility(View.VISIBLE);
                    }else {
                        SDProgress(false);
                        Shop.setEnabled(false);
                        Email.setEnabled(false);
                        Password.setVisibility(View.VISIBLE);
                        signInButton.setText("SIGN IN");
                    }
                }else{
                    Shop.getEditText().setText("");
                    Email.getEditText().setText("");
                    Wrongcredential.setText("Wrong Shopcode or Email entered");
                    Wrongcredential.setVisibility(View.VISIBLE);
                    SDProgress(false);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_SHORT).show();
                SDProgress(false);
            }
        });
    }

    private boolean CheckifConnected() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public void SDProgress(boolean show){
        if(show){
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            ProgressLayout.setVisibility(View.VISIBLE);
        }else{
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            ProgressLayout.setVisibility(View.GONE);
        }
    }

}