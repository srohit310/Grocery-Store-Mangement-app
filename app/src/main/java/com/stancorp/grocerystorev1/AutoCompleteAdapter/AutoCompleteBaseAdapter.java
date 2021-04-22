package com.stancorp.grocerystorev1.AutoCompleteAdapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.stancorp.grocerystorev1.Classes.Location;
import com.stancorp.grocerystorev1.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

public abstract class AutoCompleteBaseAdapter<T> extends ArrayAdapter<T> {

    public int layout_id;
    public Context mContext;
    protected LinkedHashMap<String, T> dataList = new LinkedHashMap<>();
    protected LinkedHashMap<String, T> dataListAll = new LinkedHashMap<>();
    public TextView Name;
    public TextView Code;

    public void updateList(LinkedHashMap<String, T> objects) {
        this.dataList = new LinkedHashMap<>(objects);
        this.dataListAll = new LinkedHashMap<>(objects);
        notifyDataSetChanged();
    }

    public AutoCompleteBaseAdapter(@NonNull Context context) {
        super(context, 0);
    }

    public int getCount() {
        return dataList.size();
    }

    public T getItem(int position) {
        return (T) dataList.values().toArray()[position];
    }

    public long getItemId(int position) {
        return position;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.autocomplete, parent, false);
        }
        Name = convertView.findViewById(R.id.Name);
        Code = convertView.findViewById(R.id.Code);

        setView(position);

        return convertView;
    }

    @NonNull
    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            public CharSequence convertResultToString(Object resultValue) {
                return setTextviewString(resultValue);
            }

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults filterResults = new FilterResults();
                dataList.putAll(dataListAll);
                filterResults.values = dataList;
                filterResults.count = dataList.size();

                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults results) {
                if (results != null && results.count > 0) {
                    notifyDataSetChanged();
                }
            }
        };
    }


    protected abstract CharSequence setTextviewString(Object resultValue);

    protected abstract void setView(int position);


}
