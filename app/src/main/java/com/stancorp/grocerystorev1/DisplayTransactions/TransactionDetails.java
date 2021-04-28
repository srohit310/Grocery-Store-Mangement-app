package com.stancorp.grocerystorev1.DisplayTransactions;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.firebase.firestore.FirebaseFirestore;
import com.stancorp.grocerystorev1.Classes.DeliveryAddress;
import com.stancorp.grocerystorev1.Classes.StoreTransaction;
import com.stancorp.grocerystorev1.GlobalClass.Gfunc;
import com.stancorp.grocerystorev1.R;

public class TransactionDetails extends Fragment {

    StoreTransaction transactionDetails;
    Gfunc gfunc = new Gfunc();
    FirebaseFirestore firebaseFirestore;

    View DeliveryDateLayout;
    View DeliveryFromLayout;
    View DeliveryToLayout;

    TextView Code, Reference, Type, status;
    ImageView imageStatus;
    TextView transdate, transmonth, transyear;
    TextView deltitle, deldate, delmonth, delyear;
    TextView titlefrom, madefromcode, madefromstate, madefromcity, madefromstreet, madefrompincode;
    TextView titleto, madetocode, madetostate, madetocity, madetostreet, madetopincode,madetotext;
    TextView Detailstransactor;

    private static final String intent_transactiondetails = "param1";

    public static TransactionDetails newInstance(StoreTransaction transactionDetails) {

        Bundle args = new Bundle();
        TransactionDetails fragment = new TransactionDetails();
        args.putSerializable(intent_transactiondetails, transactionDetails);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            transactionDetails = (StoreTransaction) getArguments().getSerializable(intent_transactiondetails);
        }
        firebaseFirestore = FirebaseFirestore.getInstance();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_transactiondetails, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Code = view.findViewById(R.id.DetailsTransactionCodeData);
        Reference = view.findViewById(R.id.DetailsTransactionReferenceData);
        Type = view.findViewById(R.id.DetailsTransactiontypeData);
        status = view.findViewById(R.id.DetailsTransctioncompletionData);
        imageStatus = view.findViewById(R.id.approvedcircle);

        View TransactionDateLayout = view.findViewById(R.id.TransactionDate);
        transdate = TransactionDateLayout.findViewById(R.id.Date);
        transmonth = TransactionDateLayout.findViewById(R.id.Month);
        transyear = TransactionDateLayout.findViewById(R.id.Year);

        DeliveryDateLayout = view.findViewById(R.id.DeliveryDate);
        deltitle = DeliveryDateLayout.findViewById(R.id.title);
        deldate = DeliveryDateLayout.findViewById(R.id.Date);
        delmonth = DeliveryDateLayout.findViewById(R.id.Month);
        delyear = DeliveryDateLayout.findViewById(R.id.Year);

        DeliveryFromLayout = view.findViewById(R.id.DeliveryFrom);
        titlefrom = DeliveryFromLayout.findViewById(R.id.Addresstitle);
        madefromcode = DeliveryFromLayout.findViewById(R.id.locationfromcode);
        madefromstate = DeliveryFromLayout.findViewById(R.id.State);
        madefromcity = DeliveryFromLayout.findViewById(R.id.City);
        madefromstreet = DeliveryFromLayout.findViewById(R.id.Street);
        madefrompincode = DeliveryFromLayout.findViewById(R.id.Pincode);

        DeliveryToLayout = view.findViewById(R.id.DeliveryTo);
        titleto = DeliveryToLayout.findViewById(R.id.Addresstitle);
        madetocode = DeliveryToLayout.findViewById(R.id.locationfromcode);
        madetostate = DeliveryToLayout.findViewById(R.id.State);
        madetocity = DeliveryToLayout.findViewById(R.id.City);
        madetostreet = DeliveryToLayout.findViewById(R.id.Street);
        madetopincode = DeliveryToLayout.findViewById(R.id.Pincode);
        madetotext = DeliveryToLayout.findViewById(R.id.madetotext);

        Detailstransactor = view.findViewById(R.id.DetailsTransactorData);

        setupTextViews();
    }

    private void setupTextViews() {
        Code.setText(transactionDetails.code);
        Reference.setText(transactionDetails.reference);
        Type.setText(transactionDetails.type);
        if(transactionDetails.pending) {
            status.setText("Delivery to be completed");
        }else{
            imageStatus.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.circle_green));
            status.setText("Transaction closed");
        }
        Detailstransactor.setText(gfunc.capitalize(transactionDetails.madeByName)+"\n"+transactionDetails.madeByEmail);
        String[] transdatestring = transactionDetails.DateTransactionMade.split("/");
        String transMonth = gfunc.getMonth(Integer.parseInt(transdatestring[1]));
        transdate.setText(transdatestring[0]);
        transmonth.setText(transMonth);
        transyear.setText(transdatestring[2]);
        if(transactionDetails.deliveryAddressto!=null) {
            String[] deldatestring = transactionDetails.DeliveryDate.split("/");
            String delMonth = gfunc.getMonth(Integer.parseInt(deldatestring[1]));
            deltitle.setText("Delivery Date");
            deldate.setText(deldatestring[0]);
            delmonth.setText(delMonth);
            delyear.setText(deldatestring[2]);

            DeliveryAddress deliveryAddress = transactionDetails.deliveryAddressfrom;
            titlefrom.setText("Delivery Address (From)");
            if(transactionDetails.type.compareTo("Sale")==0) {
                madefromcode.setText(transactionDetails.locationCode);
            }else{
                madefromcode.setText(transactionDetails.stakeholderCode);
            }
            madefromstate.setText(deliveryAddress.State);
            madefromcity.setText(deliveryAddress.City);
            madefromstreet.setText(deliveryAddress.Street);
            madefrompincode.setText(String.valueOf(deliveryAddress.Pincode));

            deliveryAddress = transactionDetails.deliveryAddressto;
            titleto.setText("Delivery Address (To)");
            if(transactionDetails.type.compareTo("Sale")==0) {
                madetocode.setText(transactionDetails.stakeholderCode);
            }else{
                madetocode.setText(transactionDetails.locationCode);
            }
            madetostate.setText(deliveryAddress.State);
            madetotext.setText("Made To");
            madetocity.setText(deliveryAddress.City);
            madetostreet.setText(deliveryAddress.Street);
            madetopincode.setText(String.valueOf(deliveryAddress.Pincode));
        }else{
            DeliveryDateLayout.setVisibility(View.GONE);
            DeliveryFromLayout.setVisibility(View.GONE);
            DeliveryToLayout.setVisibility(View.GONE);
        }
    }
}
