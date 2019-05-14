package com.toonapps.toon.helper;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.toonapps.toon.R;

public class AppSettings {

    @SuppressWarnings("HardCodedStringLiteral")
    private final String PREF_KEY_URL = "pref_key_url";
    @SuppressWarnings("HardCodedStringLiteral")
    private final String PREF_KEY_PROTOCOL = "pref_key_protocol";
    @SuppressWarnings("HardCodedStringLiteral")
    private final String PREF_KEY_PORT = "pref_key_port";
    @SuppressWarnings("HardCodedStringLiteral")
    private final String PREF_KEY_TOKEN = "pref_key_token";
    @SuppressWarnings("HardCodedStringLiteral")
    private final String PREF_KEY_IS_FIRST_START = "pref_key_isFirstStart";
    @SuppressWarnings("HardCodedStringLiteral")
    private final String PREF_KEY_USE_REDIRECTION_SERVICE = "pref_key_use_redirection_service";
    @SuppressWarnings("HardCodedStringLiteral")
    private final String PREF_KEY_TEMP_SET_VALUE = "pref_key_tempSetValue";
    @SuppressWarnings("HardCodedStringLiteral")
    private final String PREF_KEY_IS_USAGE_INFO_AVAILABLE = "pref_key_isCurrentUsageInfoAvailable";
    @SuppressWarnings("HardCodedStringLiteral")
    private final String PREF_KEY_TRIED_USAGE_INFO_ONCE = "pref_key_triedCurrentUsageInfoOnce";
    @SuppressWarnings("HardCodedStringLiteral")
    private final String PREF_KEY_USE_AUTO_REFRESH = "pref_key_autoRefresh";
    @SuppressWarnings("HardCodedStringLiteral")
    private final String PREF_KEY_AUTO_REFRESH_PERIOD = "pref_key_autoRefreshPeriod";
    @SuppressWarnings("HardCodedStringLiteral")
    private final String PREF_KEY_NEXT_PROGRAM_OR_TEMP = "pref_key_nextProgramOrTemp";
    @SuppressWarnings("HardCodedStringLiteral")
    private final String PREF_KEY_ADDRESS = "pref_key_address";
    @SuppressWarnings("HardCodedStringLiteral")
    private final String PREF_KEY_IS_DOUBLE_TARIFF_METER = "pref_key_isDoubleTariffMeter";
    @SuppressWarnings("HardCodedStringLiteral")
    private final String PREF_KEY_HAS_DRAWER_PEEKED = "pref_key_hasDrawerPeeked";

    private static AppSettings instance;
    private SharedPreferences sharedPref;
    private Context context;

    private AppSettings() {
    }

    public static AppSettings getInstance() {
        if (instance == null) {
            instance = new AppSettings();
        }
        return instance;
    }

    public void initialize(Context aContext){
        context = aContext;
        sharedPref = PreferenceManager.getDefaultSharedPreferences(aContext);
    }

    public void setProtocol(String protocol){
        sharedPref.edit().putString(PREF_KEY_PROTOCOL, protocol).apply();
    }

    public String getProtocol(){
        return sharedPref.getString(PREF_KEY_PROTOCOL, context.getString(R.string.connectionWizard_login_protocol_http));
    }

    public void setPort(int port) {
        sharedPref.edit().putInt(PREF_KEY_PORT,  port).apply();
    }

    @SuppressWarnings("HardCodedStringLiteral")
    public int getPort() {
        int port = sharedPref.getInt(PREF_KEY_PORT, 0);
        if (port != 0) return port;

        // Migrate from old settings
        String url = sharedPref.getString(PREF_KEY_URL, "");
        if (url == null) return 0;
        if (url.isEmpty()) return 0;
        if (url.startsWith("http://")) {
            url = url.substring(7); // Strip http://
        } else if (url.startsWith("https://")) {
            url = url.substring(8); // Strip https://
        } else return 0;
        if (url.contains(":")){
            String[] splitted = url.split(":");
            try {
                port = Integer.valueOf(splitted[1]);
            } catch (NumberFormatException e) {
                port = 0;
            }
            setPort(port);
            setAddress(splitted[0]);
            return port;
        } else return 0;
    }

    @SuppressWarnings("HardCodedStringLiteral")
    public String getAddress(){
        String address = sharedPref.getString(PREF_KEY_ADDRESS, "");
        if (address != null && !address.isEmpty()) return address;

        // Migrate from old settings
        String url = sharedPref.getString(PREF_KEY_URL, "");
        if (url == null) return "";
        if (url.isEmpty()) return "";
        if (url.startsWith("http://")) {
            url = url.substring(7); // Strip http://
        } else if (url.startsWith("https://")) {
            url = url.substring(8); // Strip https://
        } else return "";
        if (url.contains(":")){
            String[] splitted = url.split(":");
            address = splitted[0];
            String port = splitted[1];
            setPort(Integer.valueOf(port));
            setAddress(address);
            return address;
        } else return "";
}

    public void setAddress(String address){
        sharedPref.edit().putString(PREF_KEY_ADDRESS, address).apply();
    }

    public String getUrl(){
        return getProtocol() + "://" + getAddress() + ":" + getPort();
    }

    public float getTempSetValue() {
        String value = sharedPref.getString(PREF_KEY_TEMP_SET_VALUE, "0.5");
        if(value == null) value = "0.5"; // To circumvent null pointer exception
        return Float.valueOf(value);
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

    public void setCurrentUsageInfoAvailable(boolean isCurrentUsageInfoAvailable) {
        sharedPref.edit().putBoolean(PREF_KEY_IS_USAGE_INFO_AVAILABLE, isCurrentUsageInfoAvailable).apply();
    }

    public boolean isCurrentUsageInfoAvailable() {
        return sharedPref.getBoolean(PREF_KEY_IS_USAGE_INFO_AVAILABLE, false);
    }

    public void setTriedCurrentUsageInfoOnce(boolean triedCurrentUsageInfoOnce) {
        sharedPref.edit().putBoolean(PREF_KEY_TRIED_USAGE_INFO_ONCE, triedCurrentUsageInfoOnce).apply();
    }

    public boolean triedCurrentUsageInfoOnce() {
        return sharedPref.getBoolean(PREF_KEY_TRIED_USAGE_INFO_ONCE, false);
    }

    public String whatValueToUseOnNextProgram() {
        //noinspection HardCodedStringLiteral
        return sharedPref.getString(PREF_KEY_NEXT_PROGRAM_OR_TEMP, "Temperature");
    }

    public boolean useAutoRefresh() {
        return sharedPref.getBoolean(PREF_KEY_USE_AUTO_REFRESH, true);
    }

    public long getAutoRefreshValue() {
        String value = sharedPref.getString(PREF_KEY_AUTO_REFRESH_PERIOD, "15");
        if(value == null) value = "15"; // To circumvent null pointer exception
        return Long.valueOf(value) * 1000; // in milliseconds
    }

    public boolean isMeterDoubleTariff() {
        return sharedPref.getBoolean(PREF_KEY_IS_DOUBLE_TARIFF_METER, false);
    }

    public void setMeterIsDoubleTariff(boolean meterIsDoubleTariff) {
        sharedPref.edit().putBoolean(PREF_KEY_IS_DOUBLE_TARIFF_METER, meterIsDoubleTariff).apply();
    }

    public void setDrawerHasPeeked(boolean drawerHasPeeked) {
        sharedPref.edit().putBoolean(PREF_KEY_HAS_DRAWER_PEEKED, drawerHasPeeked).apply();
    }

    public boolean hasDrawerPeeked() {
        return sharedPref.getBoolean(PREF_KEY_HAS_DRAWER_PEEKED, false);
    }
}