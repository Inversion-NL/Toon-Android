package com.toonapps.toon.helper;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class AppSettings {

    private static AppSettings instance = null;
    private Context mContext;
    private  SharedPreferences sharedPref;

    protected AppSettings(){

    }

    public static AppSettings getInstance() {
        if(instance == null) {
            instance = new AppSettings();
        }
        return instance;
    }

    public void initialize(Context aContext){
        mContext = aContext;
        sharedPref = PreferenceManager.getDefaultSharedPreferences(mContext);
    }

    public String getUrl(){
        return sharedPref.getString("pref_key_url", "192.168.0.0:8080");
    }

    public String getApiToken(){
        return sharedPref.getString("pref_key_token", null);
    }

    public boolean useRedirectService(){
        return sharedPref.getBoolean("pref_key_use_redirection_service", false);
    }
}
