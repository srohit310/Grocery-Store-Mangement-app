package com.stancorp.grocerystorev1.Classes;

import android.net.Uri;

import com.stancorp.grocerystorev1.R;

import java.io.Serializable;

public class StoreUser implements Serializable {
    public String Name;
    public String Email;
    public String Phone;
    public Boolean PhotoUri;
    public String PermissionLevel;
    public String ShopCode;
    public String Location;
    public Boolean Registeredflag;
    public Boolean valid;

    public StoreUser(){ }

    public StoreUser(String Name, String Email, String Phone, Boolean Uri, String PermissionLvl, String ShopCode, String Location, Boolean Registeredflag){
        this.Name = Name.toLowerCase();
        this.Email = Email;
        this.Phone = Phone;
        this.PhotoUri = Uri;
        this.PermissionLevel = PermissionLvl;
        this.ShopCode = ShopCode;
        this.Location = Location;
        this.Registeredflag = Registeredflag;
        this.valid = true;
    }

    public void SetProfileImageUri(Uri PhotoUri){
        this.PhotoUri = PhotoUri != null;
    }
}
