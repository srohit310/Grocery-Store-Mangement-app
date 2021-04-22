package com.stancorp.grocerystorev1.ui.main;

import android.content.Context;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.stancorp.grocerystorev1.Classes.Items;
import com.stancorp.grocerystorev1.DisplayItems.ItemAdjustment;
import com.stancorp.grocerystorev1.DisplayItems.ItemDetails;
import com.stancorp.grocerystorev1.DisplayItems.ItemStock;
import com.stancorp.grocerystorev1.R;

/**
 * A [FragmentPagerAdapter] that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
public class ItemSectionsPagerAdapter extends FragmentPagerAdapter {

    @StringRes
    private static final int[] TAB_TITLES = new int[]{R.string.tab_text_1, R.string.tab_text_2, R.string.tab_text_3};
    private final Context mContext;
    private Items Item;
    private String ShopCode;
    private String Username;

    public ItemSectionsPagerAdapter(Context context, FragmentManager fm, Items Item, String Shopcode, String Username) {
        super(fm);
        mContext = context;
        this.Item = new Items(Item);
        this.ShopCode = Shopcode;
        this.Username = Username;
    }

    @Override
    public Fragment getItem(int position) {
        // getItem is called to instantiate the fragment for the given page.
        // Return a PlaceholderFragment (defined as a static inner class below).
        Fragment fragment = null;
        switch (position){
            case 0 :
                fragment = ItemDetails.newInstance(Item,ShopCode);
                break;
            case 1:
                fragment = ItemAdjustment.newInstance(Item,ShopCode,Username);
                break;
            case 2:
                fragment = ItemStock.newInstance(Item,ShopCode);
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
        if(Item.Valid)
            return 3;
        else
            return 2;
    }
}