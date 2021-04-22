package com.stancorp.grocerystorev1.GlobalClass;

import android.content.Context;
import android.content.DialogInterface;

import androidx.appcompat.app.AlertDialog;

import com.stancorp.grocerystorev1.R;

import java.util.regex.Pattern;

public class Gfunc {

    public Gfunc(){ }

    public boolean isEmailValid(String email)
    {
        if(email.length() == 0){
            return false;
        }
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\."+
                "[a-zA-Z0-9_+&*-]+)*@" +
                "(?:[a-zA-Z0-9-]+\\.)+[a-z" +
                "A-Z]{2,7}$";

        Pattern pat = Pattern.compile(emailRegex);
        if (email == null)
            return false;
        return pat.matcher(email).matches();
    }

    public boolean checkifcharexistsmorethanonce(String s, char a) {
        int count = 0;
        if(s.length()==0){
            return false;
        }
        boolean charflag = false;
        for (int i = 0; i < s.length(); i++) {
            if (s.charAt(i) == a) {
                count++;
            }
            if (count == 2) {
                charflag = true;
                break;
            }
        }
        return charflag;
    }

    public String capitalize(String s){
        String result="";
        String temp[];
        temp = (s.split(" "));
        for(String joins : temp){
            result += joins.substring(0, 1).toUpperCase() + joins.substring(1).toLowerCase() + " ";
        }
        return result;
    }
}
