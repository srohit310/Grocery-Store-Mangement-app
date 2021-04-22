package com.stancorp.grocerystorev1.DisplayStakeholder;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.content.res.Configuration;
import android.os.Bundle;

import com.google.android.material.tabs.TabLayout;
import com.stancorp.grocerystorev1.GlobalClass.Gfunc;
import com.stancorp.grocerystorev1.R;
import com.stancorp.grocerystorev1.ui.main.StakeholderSectionsPagerAdapter;

public class AgentViewActivity extends AppCompatActivity {

    String AgentCode;
    String ShopCode;
    Gfunc gfunc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_view);
        AgentCode = getIntent().getStringExtra("AgentCode");
        ShopCode = getIntent().getStringExtra("ShopCode");
        gfunc = new Gfunc();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(gfunc.capitalize(AgentCode));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        StakeholderSectionsPagerAdapter stakeholderSectionsPagerAdapter = new StakeholderSectionsPagerAdapter(this,
                getSupportFragmentManager(), FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT , AgentCode, ShopCode);
        ViewPager viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(stakeholderSectionsPagerAdapter);
        TabLayout tabs = findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {

        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {

        }
    }
}