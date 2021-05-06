package com.stancorp.grocerystorev1.ViewFragments;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.stancorp.grocerystorev1.Classes.DaySaleInfo;
import com.stancorp.grocerystorev1.Classes.StoreUser;
import com.stancorp.grocerystorev1.Classes.TransactionProperties;
import com.stancorp.grocerystorev1.GlobalClass.Gfunc;
import com.stancorp.grocerystorev1.MainActivity;
import com.stancorp.grocerystorev1.R;

public class MainFragment extends Fragment {

    FirebaseFirestore firebaseFirestore;
    TransactionProperties transactionProperties;
    TextView purchasependingText;
    TextView salespendingText;
    String documentpath;
    ImageView profitimg;
    TextView profitMade;
    ImageView prevprofitimg;
    TextView prevprofitMade;
    TextView purchasesMade;
    TextView salesMade;
    Gfunc gfunc;
    StoreUser user;
    private RelativeLayout progressLayout;
    LinearLayout PurchasesFragment;
    LinearLayout SalesFragment;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.main_fragment, container, false);
        final AppCompatActivity act = (AppCompatActivity) getActivity();
        gfunc = new Gfunc();
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
        progressLayout = ((AppCompatActivity) getActivity()).findViewById(R.id.ProgressLayout);
        profitimg = view.findViewById(R.id.RsImage);
        profitMade = view.findViewById(R.id.profit);
        prevprofitimg = view.findViewById(R.id.PreviousImage);
        prevprofitMade = view.findViewById(R.id.Previousprofit);
        salesMade = view.findViewById(R.id.SalesNotext);
        purchasesMade = view.findViewById(R.id.PurchasesNotext);

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
        if(((MainActivity) getActivity()).User != null)
            fetchtransactionPending();
    }

    private void fetchtransactionPending() {
        SDProgress(true);
        user = ((MainActivity) getActivity()).User;
        if (user.PermissionLevel.compareTo("Employee") == 0) {
            documentpath = user.Location;
        } else {
            documentpath = "Overall";
        }
        firebaseFirestore.collection(user.ShopCode).document("doc").collection("Pending")
                .document(documentpath).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.getResult().exists()) {
                            transactionProperties = task.getResult().toObject(TransactionProperties.class);
                            salespendingText.setText(String.valueOf(transactionProperties.pendingSales));
                            purchasependingText.setText(String.valueOf(transactionProperties.pendingPurchases));
                        }
                        getCurrentDayReport();
                    }
                });
    }

    private void getCurrentDayReport() {
        firebaseFirestore.collection(user.ShopCode).document("doc").collection("ProfitforDay")
                .document(documentpath).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.getResult().exists()) {
                    DaySaleInfo daySaleInfo = task.getResult().toObject(DaySaleInfo.class);
                    if (daySaleInfo.date.compareTo(gfunc.getCurrentDate("dd/MM/yyyy")) == 0) {
                        profitMade.setText(String.valueOf(gfunc.roundof(daySaleInfo.dayProfit, 2)));
                        if (daySaleInfo.dayProfit >= 0) {
                            profitimg.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.greenrs));
                            profitMade.setTextColor(ContextCompat.getColor(getContext(), R.color.green));
                        } else {
                            profitimg.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.redrs));
                            profitMade.setTextColor(ContextCompat.getColor(getContext(), R.color.Red));
                        }
                        purchasesMade.setText(String.valueOf(daySaleInfo.noofPurchases));
                        salesMade.setText(String.valueOf(daySaleInfo.noofSales));
                    } else if (daySaleInfo.date.compareTo(gfunc.getYesterdayDate("dd/MM/yyyy")) == 0) {
                        prevprofitMade.setText(String.valueOf(gfunc.roundof(daySaleInfo.dayProfit, 2)));
                        if (daySaleInfo.dayProfit >= 0) {
                            prevprofitimg.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.greenrs));
                            prevprofitMade.setTextColor(ContextCompat.getColor(getContext(), R.color.green));
                        } else {
                            prevprofitimg.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.redrs));
                            prevprofitMade.setTextColor(ContextCompat.getColor(getContext(), R.color.Red));
                        }
                    }
                }
                SDProgress(false);
            }
        });
    }

    public void SDProgress(boolean show) {
        if (show) {
            progressLayout.animate()
                    .alpha(1.0f)
                    .setDuration(300)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                            progressLayout.setVisibility(View.VISIBLE);
                        }
                    });
        } else {
            progressLayout.animate()
                    .alpha(0.0f)
                    .setDuration(300)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                            progressLayout.setVisibility(View.GONE);
                        }
                    });
        }
    }


}
