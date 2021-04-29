package com.stancorp.grocerystorev1.ViewFragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.stancorp.grocerystorev1.Classes.StoreUser;
import com.stancorp.grocerystorev1.Classes.TransactionProperties;
import com.stancorp.grocerystorev1.MainActivity;
import com.stancorp.grocerystorev1.R;

public class MainFragment extends Fragment {

    FirebaseFirestore firebaseFirestore;
    TransactionProperties transactionProperties;
    TextView purchasependingText;
    TextView salespendingText;
    LinearLayout PurchasesFragment;
    LinearLayout SalesFragment;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.main_fragment,container,false);
        final AppCompatActivity act = (AppCompatActivity) getActivity();
        if (act.getSupportActionBar() != null) {
            Toolbar toolbar = (Toolbar) act.findViewById(R.id.toolbar);
            Spinner spinner = toolbar.findViewById(R.id.spinnertoolbar);
            spinner.setVisibility(View.GONE);
        }
        firebaseFirestore = FirebaseFirestore.getInstance();
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        purchasependingText = view.findViewById(R.id.PurchasesPendingNotext);
        salespendingText = view.findViewById(R.id.SalesPendingNotext);
        PurchasesFragment = view.findViewById(R.id.purchases_fragment);
        SalesFragment = view.findViewById(R.id.sales_fragment);
        PurchasesFragment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fLayout,
                        new FragmentsGroupPurchases()).commit();
            }
        });
        SalesFragment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fLayout,
                        new FragmentsGroupSales()).commit();
            }
        });
        fetchtransactionPending();
    }

    private void fetchtransactionPending() {
        StoreUser user = ((MainActivity) getActivity()).User;
        String documentpath;
        if(user.PermissionLevel.compareTo("Employee")==0){
            documentpath = user.Location;
        }else{
            documentpath = "Overall";
        }
        firebaseFirestore.collection(user.ShopCode).document("doc").collection("Pending")
                .document(documentpath).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.getResult().exists()) {
                            transactionProperties = task.getResult().toObject(TransactionProperties.class);
                            salespendingText.setText(String.valueOf(transactionProperties.pendingSales));
                            purchasependingText.setText(String.valueOf(transactionProperties.pendingPurchases));
                        }
                    }
                });
    }


}
