package com.stancorp.grocerystorev1.ui.main;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.stancorp.grocerystorev1.Classes.Agent;
import com.stancorp.grocerystorev1.Classes.Itemtransaction;
import com.stancorp.grocerystorev1.Classes.StoreTransaction;
import com.stancorp.grocerystorev1.DisplayStakeholder.AgentDetails;
import com.stancorp.grocerystorev1.DisplayStakeholder.AgentPending;
import com.stancorp.grocerystorev1.DisplayTransactions.TransactionDetails;
import com.stancorp.grocerystorev1.DisplayTransactions.Transactionitemsdisplay;
import com.stancorp.grocerystorev1.R;

import java.util.ArrayList;

public class TransactionSectionsPagerAdapter extends FragmentStatePagerAdapter {

    @StringRes
    private static final int[] TAB_TITLES = new int[]{R.string.tab_text_1,R.string.tab_text_4};
    private final Context mContext;
    private StoreTransaction transaction;
    private String ShopCode;

    public TransactionSectionsPagerAdapter(Context context, @NonNull FragmentManager fm, int behavior,
                                           StoreTransaction transaction,String ShopCode) {
        super(fm, behavior);
        this.mContext =context;
        this.transaction = transaction;
        this.ShopCode = ShopCode;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        Fragment fragment = null;
        switch (position){
            case 0 :
                fragment = TransactionDetails.newInstance(transaction);
                break;
            case 1:
                fragment = Transactionitemsdisplay.newInstance(ShopCode,transaction.code,transaction.totalPrice,transaction.type,
                        transaction.totalProfit);
                break;
        }
        return fragment;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return mContext.getResources().getString(TAB_TITLES[position]);
    }

    @Override
    public int getCount() {
        return 2;
    }
}
