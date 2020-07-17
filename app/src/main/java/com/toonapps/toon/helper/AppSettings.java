/*
 * Copyright (c) 2020
 * Licensed to the Apache Software Foundation (ASF) under one or more contributor license agreements
 * See the NOTICE file distributed with this work for additional information regarding copyright ownership
 * The ASF licenses this file to you under the Apache License, Version 2.0 (the  "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing software distributed under the License is
 * distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied.  See the License for the specific language governing permissions and limitations
 * under the License.
 */

package com.toonapps.toon.helper;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.toonapps.toon.R;

@SuppressWarnings("unused")
public class AppSettings {

    @SuppressWarnings("HardCodedStringLiteral")
    private final String PREF_KEY_URL = "pref_key_url";
    @SuppressWarnings("HardCodedStringLiteral")
    private final String PREF_KEY_PROTOCOL = "pref_key_protocol";
    @SuppressWarnings("HardCodedStringLiteral")
    private final String PREF_KEY_ADDRESS = "pref_key_address";
    @SuppressWarnings("HardCodedStringLiteral")
    private final String PREF_KEY_PORT = "pref_key_port";
    @SuppressWarnings({"HardCodedStringLiteral", "FieldCanBeLocal"})
    private final String PREF_KEY_HTTP_HEADER_VALUE = "pref_key_token";
    @SuppressWarnings({"HardCodedStringLiteral", "FieldCanBeLocal"})
    private final String PREF_KEY_HTTP_HEADER_KEY = "pref_key_httpHeaderKey";
    @SuppressWarnings({"HardCodedStringLiteral", "FieldCanBeLocal"})
    private final String PREF_KEY_USE_HTTP_HEADER = "pref_key_use_redirection_service";
    @SuppressWarnings("HardCodedStringLiteral")
    private final String PREF_KEY_IS_DOUBLE_TARIFF_METER = "pref_key_isDoubleTariffMeter";
    @SuppressWarnings("HardCodedStringLiteral")
    private final String PREF_KEY_IS_FIRST_START = "pref_key_isFirstStart";
    @SuppressWarnings({"HardCodedStringLiteral", "FieldCanBeLocal"})
    private final String PREF_KEY_TEMP_SET_VALUE = "pref_key_tempSetValue";
    @SuppressWarnings("HardCodedStringLiteral")
    private final String PREF_KEY_IS_USAGE_INFO_AVAILABLE = "pref_key_isCurrentUsageInfoAvailable";
    @SuppressWarnings("HardCodedStringLiteral")
    private final String PREF_KEY_TRIED_USAGE_INFO_ONCE = "pref_key_triedCurrentUsageInfoOnce";
    @SuppressWarnings({"HardCodedStringLiteral", "FieldCanBeLocal"})
    private final String PREF_KEY_USE_AUTO_REFRESH = "pref_key_autoRefresh";
    @SuppressWarnings({"HardCodedStringLiteral", "FieldCanBeLocal"})
    private final String PREF_KEY_AUTO_REFRESH_PERIOD = "pref_key_autoRefreshPeriod";
    @SuppressWarnings({"HardCodedStringLiteral", "FieldCanBeLocal"})
    private final String PREF_KEY_NEXT_PROGRAM_OR_TEMP = "pref_key_nextProgramOrTemp";
    @SuppressWarnings("HardCodedStringLiteral")
    private final String PREF_KEY_HAS_DRAWER_PEEKED = "pref_key_hasDrawerPeeked";
    @SuppressWarnings("HardCodedStringLiteral")
    private final String PREF_KEY_HIDE_GAS_WIDGETS = "pref_key_hasGasMeter";
    @SuppressWarnings("HardCodedStringLiteral")
    private final String PREF_KEY_SHOW_POWER_PRODUCTION_WIDGETS = "pref_key_showPowerProductionWidgets";
    @SuppressWarnings("HardCodedStringLiteral")
    private final String PREF_KEY_HAS_POWER_PRODUCTION_DIALOG_SHOWN = "pref_key_hasPowerProductionDialogShown";

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
                port = Integer.parseInt(splitted[1]);
            } catch (NumberFormatException e) {
                FirebaseHelper.getInstance().recordExceptionAndLog(e, "Exception while parsing the string to an integer in app settings");
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
            setPort(Integer.parseInt(port));
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

    @SuppressWarnings("HardCodedStringLiteral")
    public String getHttpHeaderKey(){
        return sharedPref.getString(PREF_KEY_HTTP_HEADER_KEY, "apikey");
    }

    public void setHttpHeaderKey(String httpHeaderKey){
        sharedPref.edit().putString(PREF_KEY_HTTP_HEADER_KEY, httpHeaderKey).apply();
    }

    public String getHttpHeaderValue(){
        return sharedPref.getString(PREF_KEY_HTTP_HEADER_VALUE, "");
    }

    public void setHttpHeaderValue(String httpHeaderValue){
        sharedPref.edit().putString(PREF_KEY_HTTP_HEADER_VALUE, httpHeaderValue).apply();
    }

    public float getTempSetValue() {
        String value = sharedPref.getString(PREF_KEY_TEMP_SET_VALUE, "0.5");
        if(value == null) value = "0.5"; // To circumvent null pointer exception
        return Float.parseFloat(value);
    }

    public boolean useHttpHeader(){
        return sharedPref.getBoolean(PREF_KEY_USE_HTTP_HEADER, false);
    }

    public void setUseHttpHeader(boolean useHttpHeader) {
        sharedPref.edit().putBoolean(PREF_KEY_USE_HTTP_HEADER, useHttpHeader).apply();
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
        return Long.parseLong(value) * 1000; // in milliseconds
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

    public void setShowGasWidgets(boolean hideGasWidgets) {
        sharedPref.edit().putBoolean(PREF_KEY_HIDE_GAS_WIDGETS, hideGasWidgets).apply();
    }

    public boolean showGasWidgets() {
        return sharedPref.getBoolean(PREF_KEY_HIDE_GAS_WIDGETS, true);
    }

    public void setShowPowerProductionWidgets(boolean showPowerProductionWidgets) {
        sharedPref.edit().putBoolean(PREF_KEY_SHOW_POWER_PRODUCTION_WIDGETS, showPowerProductionWidgets).apply();
    }

    public boolean showPowerProductionWidgets() {
        return sharedPref.getBoolean(PREF_KEY_SHOW_POWER_PRODUCTION_WIDGETS, false);
    }

    public void setPowerProductionDialogHasShown(boolean hideGasWidgets) {
        sharedPref.edit().putBoolean(PREF_KEY_HAS_POWER_PRODUCTION_DIALOG_SHOWN, hideGasWidgets).apply();
    }

    public boolean hasPowerProductionDialogBeenShown() {
        return sharedPref.getBoolean(PREF_KEY_HAS_POWER_PRODUCTION_DIALOG_SHOWN, false);
    }
}