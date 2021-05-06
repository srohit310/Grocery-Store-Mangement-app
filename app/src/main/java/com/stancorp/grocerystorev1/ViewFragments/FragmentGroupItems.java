package com.stancorp.grocerystorev1.ViewFragments;

import android.content.Intent;
import android.content.res.Configuration;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.paging.PagedList;
import androidx.recyclerview.widget.GridLayoutManager;

import com.firebase.ui.firestore.paging.FirestorePagingOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.stancorp.grocerystorev1.AdapterClasses.ItemFirestoreAdapter;
import com.stancorp.grocerystorev1.AddActivities.AddItemActivity;
import com.stancorp.grocerystorev1.Classes.Brand;
import com.stancorp.grocerystorev1.Classes.Category;
import com.stancorp.grocerystorev1.Classes.Items;
import com.stancorp.grocerystorev1.DisplayItems.ItemViewActivity;
import com.stancorp.grocerystorev1.R;

import java.util.ArrayList;

public class FragmentGroupItems extends FragmentsGroups {

    FirebaseFirestore firebaseFirestore;
    ItemFirestoreAdapter itemAdaptor;
    Boolean valid;
    ArrayList<String> categories;
    ArrayList<String> brands;
    String Category;
    String Brand;

    @Override
    protected void toolbarspinnersetup(Spinner toolbarspinner) {
        ArrayAdapter itemfilteroptions = ArrayAdapter.createFromResource(getContext(),
                R.array.array_item_filter_options, R.layout.spinner_item_text);

        itemfilteroptions.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        toolbarspinner.setAdapter(itemfilteroptions);
        toolbarspinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selection = parent.getItemAtPosition(position).toString();
                if (!TextUtils.isEmpty(selection)) {
                    startcode = "!";
                    endcode = "{";
                    if (selection.equals(getString(R.string.valid_items))) {
                        valid = true;
                        attachListData(startcode, endcode);
                    } else if (selection.equals(getString(R.string.invalid_items))) {
                        valid = false;
                        attachListData(startcode, endcode);
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                filterby = "valid";
            }
        });
    }

    @Override
    protected void initialize() {
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        valid = true;
        categories = new ArrayList<>();
        brands = new ArrayList<>();
        searchedittext.setHint("Search for item using name");
        firebaseFirestore = FirebaseFirestore.getInstance();
        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){
            recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 4));
        }
        if (user.PermissionLevel.compareTo("Employee") == 0) {
            mfloat.setVisibility(View.GONE);
        }
        Category = "All Categories";
        Brand = "All Brands";
        categories.add(Category);
        brands.add(Brand);
        addcategory();
    }

    private void addcategory() {
        firebaseFirestore.collection(user.ShopCode).document("doc").collection("CodesMaster")
                .document("Categories").get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.getResult().exists()){
                    Category category = task.getResult().toObject(Category.class);
                    categories.addAll(category.category);
                }
                addbrand();
            }
        });
    }

    private void addbrand() {
        firebaseFirestore.collection(user.ShopCode).document("doc").collection("CodesMaster")
                .document("Brands").get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.getResult().exists()){
                    Brand brand = task.getResult().toObject(Brand.class);
                    brands.addAll(brand.brand);
                }
                setupfilterlayout();
            }
        });
    }

    private void setupfilterlayout() {
        itemfilterlayout.setVisibility(View.VISIBLE);
        categoryfilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int position = categories.indexOf(Category);
                BottomFragmentItems bottomFragmentItems = BottomFragmentItems.newInstance("Category",categories,position);
                bottomFragmentItems.show(getChildFragmentManager(),BottomFragmentItems.TAG);
            }
        });
        brandfilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int position = brands.indexOf(Brand);
                BottomFragmentItems bottomFragmentItems = BottomFragmentItems.newInstance("Brand",brands,position);
                bottomFragmentItems.show(getChildFragmentManager(),BottomFragmentItems.TAG);
            }
        });
    }

    @Override
    protected void AddIntent() {
        Intent intent = new Intent(getContext(), AddItemActivity.class);
        intent.putExtra("ShopCode", user.ShopCode);
        intent.putExtra("Mode", "Add");
        startActivity(intent);
    }

    @Override
    public void onStop() {
        super.onStop();
        itemAdaptor.stopListening();
    }

    @Override
    public void onStart() {
        super.onStart();
        if (itemAdaptor != null)
            itemAdaptor.startListening();
    }

    public void filterchange(){
        attachListData(startcode,endcode);
    }

    @Override
    protected void attachListData(String startcode, String endcode) {
        SDProgress(true);
        Query query = firebaseFirestore.collection(user.ShopCode).document("doc").collection("Items")
                .whereEqualTo("Valid", valid).whereGreaterThanOrEqualTo("name", startcode)
                .whereLessThan("name", endcode).orderBy("name", direction).limit(50);

        if(Category.compareToIgnoreCase("All Categories")!=0){
            query = query.whereEqualTo("Category",Category);
        }
        if(Brand.compareToIgnoreCase("All Brands")!=0){
            query = query.whereEqualTo("Brand",Brand);
        }

        PagedList.Config config = new PagedList.Config.Builder()
                .setInitialLoadSizeHint(8)
                .setPageSize(4)
                .build();

        FirestorePagingOptions<Items> options = new FirestorePagingOptions.Builder<Items>()
                .setQuery(query, config , Items.class)
                .build();

        itemAdaptor = new ItemFirestoreAdapter(options, getContext(), this, progressLayout, emptyview);
        itemAdaptor.startListening();
        itemAdaptor.notifyDataSetChanged();
        recyclerView.setAdapter(itemAdaptor);
        recyclerView.scheduleLayoutAnimation();
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 4));
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        }
    }

    @Override
    protected void displayFirestoreIntent(DocumentSnapshot documentSnapshot, int position) {
        Intent intent = new Intent(getContext(), ItemViewActivity.class);
        Items item = (Items) documentSnapshot.toObject(Items.class);
        intent.putExtra("Item", item);
        intent.putExtra("ShopCode", user.ShopCode);
        intent.putExtra("UserName", user.Name);
        intent.putExtra("UserPermission", user.PermissionLevel);
        intent.putExtra("UserLocation", user.Location);
        startActivity(intent);
    }
}
