package com.stancorp.grocerystorev1.AutoCompleteAdapter;

import android.content.Context;

import androidx.annotation.NonNull;

import com.stancorp.grocerystorev1.Classes.Location;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

public class AutoCompleteLocationAdapter extends AutoCompleteBaseAdapter<Location> {

    public AutoCompleteLocationAdapter(Context context, LinkedHashMap<String,Location> locations) {
        super(context);
        this.mContext = context;
        this.dataList = new LinkedHashMap<String, Location>(locations);
        this.dataListAll = new LinkedHashMap<String, Location>(locations);
    }

    @Override
    protected CharSequence setTextviewString(Object resultValue) {
        return ((Location) resultValue).code;
    }

    @Override
    protected void setView(int position) {
        Location location = getItem(position);

        if(location!=null){
            Name.setText(location.name);
            Code.setText(location.code);
        }
    }
}
