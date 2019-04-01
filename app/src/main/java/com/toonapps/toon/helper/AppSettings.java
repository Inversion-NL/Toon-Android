package com.toonapps.toon.helper;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;

public class AppSettings {

    private static AppSettings instance;
    private  SharedPreferences sharedPref;

    private AppSettings() {
    }

    public static AppSettings getInstance() {
        if (instance == null) {
            instance = new AppSettings();
        }
        return instance;
    }

    public void initialize(Context aContext){
        sharedPref = PreferenceManager.getDefaultSharedPreferences(aContext);
    }

    public String getUrl(){
        return sharedPref.getString("pref_key_url", "");
    }

    public int getPort() {
        String url = getUrl();
        if (!TextUtils.isEmpty(url) && url.length() > 7) {
            url = url.substring(7);
            String splitted[] = url.split(":");

            int port;
            try {
                port = Integer.valueOf(splitted[1]);
            } catch (NumberFormatException e) {
                port = 0;
            }
            return port;

        } else return 0;
    }

    public String getAddress() {
        String url = getUrl();
        if (!TextUtils.isEmpty(url) && url.length() > 7) {
            url = url.substring(7);
            String splitted[] = url.split(":");
            return splitted[0];
        } else return "";
    }

    public void setUrl(String url) {
        sharedPref.edit().putString("pref_key_url", url).apply();
    }

    public String getApiToken(){
        return sharedPref.getString("pref_key_token", "");
    }

    public boolean useRedirectService(){
        return sharedPref.getBoolean("pref_key_use_redirection_service", false);
    }

    public boolean isFirstStart() {
        return sharedPref.getBoolean("pref_key_isFirstStart", true);
    }

    public void setFirstStart(boolean isFirstStart) {
        sharedPref.edit().putBoolean("pref_key_isFirstStart", isFirstStart).apply();
    }
}