package com.toonapps.toon.helper;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;

public class AppSettings {

    @SuppressWarnings("HardCodedStringLiteral")
    private static final String PREF_KEY_URL = "pref_key_url";
    @SuppressWarnings("HardCodedStringLiteral")
    private static final String PREF_KEY_TOKEN = "pref_key_token";
    @SuppressWarnings("HardCodedStringLiteral")
    private static final String PREF_KEY_IS_FIRST_START = "pref_key_isFirstStart";
    @SuppressWarnings("HardCodedStringLiteral")
    private static final String PREF_KEY_USE_REDIRECTION_SERVICE = "pref_key_use_redirection_service";
    @SuppressWarnings("HardCodedStringLiteral")
    private static final String PREF_KEY_TEMP_SET_VALUE = "pref_key_tempSetValue";
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
        return sharedPref.getString(PREF_KEY_URL, "");
    }

    public void setUrl(String url) {
        sharedPref.edit().putString(PREF_KEY_URL, url).apply();
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

    public float getTempSetValue() {
        String value = sharedPref.getString(PREF_KEY_TEMP_SET_VALUE, "5");
        if(value == null) value = "5"; // To circumvent null pointer exception
        float returnValue = (Float.valueOf(value));
        return returnValue / 10; // 1/10 of the value
    }

    public String getApiToken(){
        return sharedPref.getString(PREF_KEY_TOKEN, "");
    }

    public boolean useRedirectService(){
        return sharedPref.getBoolean(PREF_KEY_USE_REDIRECTION_SERVICE, false);
    }

    public boolean isFirstStart() {
        return sharedPref.getBoolean(PREF_KEY_IS_FIRST_START, true);
    }

    public void setFirstStart(boolean isFirstStart) {
        sharedPref.edit().putBoolean(PREF_KEY_IS_FIRST_START, isFirstStart).apply();
    }
}