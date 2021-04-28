package com.stancorp.grocerystorev1.DisplayStakeholder;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.stancorp.grocerystorev1.AddActivities.AddItemActivity;
import com.stancorp.grocerystorev1.Classes.AdditionalContact;
import com.stancorp.grocerystorev1.Classes.Agent;
import com.stancorp.grocerystorev1.Classes.DeliveryAddress;
import com.stancorp.grocerystorev1.GlobalClass.Gfunc;
import com.stancorp.grocerystorev1.R;

public class AgentDetails extends Fragment {

    private String ShopCode;
    private Agent agent;
    private Gfunc gfunc;

    TextView agentCode;
    TextView agentName;
    TextView agentCategory;
    TextView agentType;
    TextView agentEmail;
    TextView agentPhone;
    LinearLayout intentEmail;
    LinearLayout intentPhone;
    CardView deliveryCard;
    CardView altCard;

    TextView State;
    TextView City;
    TextView Street;
    TextView Pincode;

    TextView altName;
    TextView Department;
    TextView Designation;
    TextView altEmail;
    TextView altPhone;
    LinearLayout intentaltEmail;
    LinearLayout intentaltPhone;

    private static final String intent_shopcode = "param1";
    private static final String intent_agent = "param2";
    public static final int READ_CONTACTS = 1;
    FirebaseFirestore firebaseFirestore;

    public static AgentDetails newInstance(String Shopcode, Agent agent) {
        Bundle args = new Bundle();
        AgentDetails fragment = new AgentDetails();
        args.putString(intent_shopcode, Shopcode);
        args.putSerializable(intent_agent,agent);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        gfunc = new Gfunc();
        if(getArguments()!=null){
            ShopCode = getArguments().getString(intent_shopcode);
            agent = (Agent) getArguments().getSerializable(intent_agent);
        }
        firebaseFirestore = FirebaseFirestore.getInstance();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_agentdetails, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        agentCode = view.findViewById(R.id.DetailsAgentCodeData);
        agentName = view.findViewById(R.id.DetailsAgentNameData);
        agentCategory = view.findViewById(R.id.DetailsAgentCategoryData);
        agentType = view.findViewById(R.id.DetailsAgentBrandData);
        agentEmail = view.findViewById(R.id.DetailsAgentEmailData);
        agentPhone = view.findViewById(R.id.DetailsAgentPhoneData);
        intentEmail = view.findViewById(R.id.intentEmail);
        intentPhone = view.findViewById(R.id.intentPhone);


        deliveryCard = view.findViewById(R.id.DeliveryCardView);
        State = view.findViewById(R.id.State);
        City = view.findViewById(R.id.City);
        Street = view.findViewById(R.id.Street);
        Pincode = view.findViewById(R.id.Pincode);

        altCard = view.findViewById(R.id.AltContactCardView);
        altName = view.findViewById(R.id.altName);
        altEmail = view.findViewById(R.id.DetailsaltEmailData);
        altPhone = view.findViewById(R.id.DetailsaltPhoneData);
        Department = view.findViewById(R.id.altDepartment);
        Designation = view.findViewById(R.id.altDesignation);
        intentaltEmail = view.findViewById(R.id.intentaltemail);
        intentaltPhone = view.findViewById(R.id.intentaltphone);

        setupTextviews();
    }

    private void setupTextviews(){
        agentCode.setText(agent.Code);
        agentName.setText(gfunc.capitalize(agent.Name));
        agentEmail.setText(agent.Email);
        agentPhone.setText(String.valueOf(agent.Phoneno));
        String category = "";
        if (agent.AgentType.compareTo("Both") == 0) {
            category = "Customer / Vendor";
        }else{
            category = agent.AgentType;
        }
        agentCategory.setText(category);
        agentType.setText(agent.CustomerType);
        intentEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(getActivity(),R.style.MyDialogTheme)
                        .setTitle("Open Email Service?")
                        .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Intent intent = new Intent(Intent.ACTION_SENDTO);
                                intent.setType("text/plain");
                                intent.putExtra(Intent.EXTRA_EMAIL, new String[] { agent.Email });
                                startActivity(Intent.createChooser(intent, "Send Email"));
                            }
                        })
                        .setNegativeButton("Cancel",null)
                        .show();
            }
        });
        intentPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ContextCompat.checkSelfPermission(getActivity(),
                        Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED){
                    AdditionalContact additionalContact = new AdditionalContact(
                            agent.Name,agent.Email,agent.Phoneno,"",""
                    );
                    contactExists(additionalContact);
                }else{
                    requestpermission(Manifest.permission.READ_CONTACTS,"Allow App to Read Contact Information",READ_CONTACTS);
                }
            }
        });

        if(agent.deladdress !=null){
            deliveryCard.setVisibility(View.VISIBLE);
            DeliveryAddress deliveryAddress = agent.deladdress;
            State.setText(deliveryAddress.State);
            City.setText(deliveryAddress.City);
            Street.setText(deliveryAddress.Street);
            Pincode.setText(String.valueOf(deliveryAddress.Pincode));
        }else{
            deliveryCard.setVisibility(View.GONE);
        }

        if(agent.altcontact !=null){
            altCard.setVisibility(View.VISIBLE);
            AdditionalContact additionalContact = agent.altcontact;
            altName.setText(gfunc.capitalize(additionalContact.Name));
            altEmail.setText(additionalContact.Email);
            altPhone.setText(String.valueOf(additionalContact.Phoneno));
            Designation.setText(gfunc.capitalize(additionalContact.Designation));
            Department.setText(gfunc.capitalize(additionalContact.Department));
            intentaltEmail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(Intent.ACTION_SENDTO);
                    intent.setType("text/plain");
                    intent.putExtra(Intent.EXTRA_EMAIL, new String[] { agent.altcontact.Email });
                    startActivity(Intent.createChooser(intent, "Send Email"));
                }
            });
            intentaltPhone.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (ContextCompat.checkSelfPermission(getActivity(),Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED){
                        contactExists(agent.altcontact);
                    }else{
                        requestpermission(Manifest.permission.READ_CONTACTS,"Allow App to Read Contact Information",READ_CONTACTS);
                    }
                }
            });
        }else{
            altCard.setVisibility(View.GONE);
        }
    }

    public void contactExists(final AdditionalContact additionalContact) {
        if (additionalContact != null) {
            Uri lookupUri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(String.valueOf(additionalContact.Phoneno)));
            String[] mPhoneNumberProjection = { ContactsContract.PhoneLookup._ID, ContactsContract.PhoneLookup.NUMBER, ContactsContract.PhoneLookup.DISPLAY_NAME };
            Cursor cur = getActivity().getContentResolver().query(lookupUri, mPhoneNumberProjection, null, null, null);
            try {
                if (cur.moveToFirst()) {
                    Intent intent = new Intent(Intent.ACTION_DIAL);
                    intent.setData(Uri.parse("tel:" + String.valueOf(additionalContact.Phoneno)));
                    startActivity(intent);
                }
            } finally {
                if (cur != null) {
                    if(cur.getCount() == 0){
                        new AlertDialog.Builder(getActivity(),R.style.MyDialogTheme)
                                .setMessage("Add User To contacts or make a call")
                                .setPositiveButton("Add User", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        writetoContact(additionalContact);
                                    }
                                })
                                .setNegativeButton("Go to Dialer", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        Intent intent = new Intent(Intent.ACTION_DIAL);
                                        intent.setData(Uri.parse("tel:" + String.valueOf(additionalContact.Phoneno)));
                                        startActivity(intent);
                                    }
                                }).show();
                    }
                    cur.close();
                }
            }


        }
    }

    private void writetoContact(AdditionalContact additionalContact) {
        Intent intent = new Intent(ContactsContract.Intents.Insert.ACTION);
        intent.setType(ContactsContract.RawContacts.CONTENT_TYPE);

        intent.putExtra(ContactsContract.Intents.Insert.EMAIL,additionalContact.Email);
        intent.putExtra(ContactsContract.Intents.Insert.EMAIL_TYPE,ContactsContract.CommonDataKinds.Email.TYPE_WORK);
        intent.putExtra(ContactsContract.Intents.Insert.PHONE,String.valueOf(additionalContact.Phoneno));
        intent.putExtra(ContactsContract.Intents.Insert.PHONE_TYPE,ContactsContract.CommonDataKinds.Phone.TYPE_WORK);
        intent.putExtra(ContactsContract.Intents.Insert.NAME,additionalContact.Name);

        startActivity(intent);
    }

    private void requestpermission(final String Permission, String Message, final Integer code) {
        if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Permission)) {
            new AlertDialog.Builder(getActivity(),R.style.MyDialogTheme)
                    .setTitle("Permission Needed")
                    .setMessage(Message)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            ActivityCompat.requestPermissions(getActivity(),
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
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Permission},
                    code);
        }
    }
}
