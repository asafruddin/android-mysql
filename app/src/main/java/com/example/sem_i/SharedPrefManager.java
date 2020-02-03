package com.example.sem_i;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPrefManager {
    public static final String SP_SEMI_APP = "spSemiApp";
    public static final String SP_email = "spEmail";
    public static final String SP_LOGINED = "spLogined";

    SharedPreferences sp;
    SharedPreferences.Editor spEditor;

    public SharedPrefManager(Context context){
        sp = context.getSharedPreferences(SP_SEMI_APP, context.MODE_PRIVATE);
        spEditor = sp.edit();
    }

    public SharedPrefManager() {

    }

    public void saveSpString(String keySP, String value){
        spEditor.putString(keySP, value);
        spEditor.commit();
    }

    public void saveSPInt(String keySp, int value){
        spEditor.putInt(keySp, value);
        spEditor.commit();
    }

    public void saveSPBolean(String keySP, boolean value){
        spEditor.putBoolean(keySP, value);
        spEditor.commit();
    }

    public String getSP_email(){
        return sp.getString(SP_email, "");
    }

    public Boolean getLogined(){
        return sp.getBoolean(SP_LOGINED, false);
    }
}
