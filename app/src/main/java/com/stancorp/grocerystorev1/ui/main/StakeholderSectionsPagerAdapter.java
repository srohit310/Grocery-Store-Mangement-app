package com.stancorp.grocerystorev1.ui.main;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.stancorp.grocerystorev1.Classes.Agent;
import com.stancorp.grocerystorev1.DisplayStakeholder.AgentDetails;
import com.stancorp.grocerystorev1.DisplayStakeholder.AgentPending;
import com.stancorp.grocerystorev1.R;

public class StakeholderSectionsPagerAdapter extends FragmentStatePagerAdapter {

    @StringRes
    private static final int[] TAB_TITLES = new int[]{R.string.tab_text_1,R.string.tab_text_pending};
    private final Context mContext;
    private String agentCode;
    private Agent agent;
    private String ShopCode;

    public StakeholderSectionsPagerAdapter(Context context, @NonNull FragmentManager fm, int behavior, String agentCode,
                                           String Shopcode, Agent agent) {
        super(fm, behavior);
        mContext = context;
        this.agentCode = agentCode;
        this.ShopCode = Shopcode;
        this.agent = agent;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        Fragment fragment = null;
        switch (position){
            case 0 :
                fragment = AgentDetails.newInstance(ShopCode,agent);
                break;
            case 1:
                fragment = AgentPending.newInstance(ShopCode,agent.Code);
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
