package com.stancorp.grocerystorev1.ViewFragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.stancorp.grocerystorev1.AddActivities.AddItemActivity;
import com.stancorp.grocerystorev1.AddActivities.AddTransactionActivity;
import com.stancorp.grocerystorev1.R;

public class FragmentsGroupSales extends FragmentsGroups {

    @Override
    protected void initialize() {

    }

    @Override
    protected void setupSpinner(View view) {

    }

    @Override
    protected void AddIntent() {
        Intent intent = new Intent(getContext(), AddTransactionActivity.class);
        intent.putExtra("ShopCode",user.ShopCode);
        intent.putExtra("Mode","Sale");
        startActivity(intent);
    }

    @Override
    protected void attachListData(String sortby) {
        SDProgress(false);
    }

    @Override
    protected void filteredlistcondition(String text) {

    }

    @Override
    protected void displayIntent(int posiiton) {

    }
}
