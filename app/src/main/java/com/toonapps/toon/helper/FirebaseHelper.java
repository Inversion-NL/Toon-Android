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

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.crashlytics.FirebaseCrashlytics;

import timber.log.Timber;

public class FirebaseHelper {

    private static FirebaseHelper instance;
    private static FirebaseCrashlytics mFirebaseCrashlytics;
    private static FirebaseAnalytics mFirebaseAnalytics;

    private FirebaseHelper(){}

    public static FirebaseHelper getInstance(Context context) {
        if (instance == null) {
            instance = new FirebaseHelper();
        }
        if (mFirebaseCrashlytics == null) mFirebaseCrashlytics = FirebaseCrashlytics.getInstance();
        if (mFirebaseAnalytics == null) mFirebaseAnalytics = FirebaseAnalytics.getInstance(context);
        return instance;
    }

    public interface EVENT {

        @SuppressWarnings("HardCodedStringLiteral")
        interface APP_UPDATE {
            String UPDATE_SUCCESS = "app_update_success";
            String UPDATE_FAILED = "app_update_failed";
            String UPDATE_CANCELED_BY_USERS = "app_update_canceled_by_user";
            String UPDATE_DIALOG_DISMISSED = "app_update_dialog_dismissed";
        }

        @SuppressWarnings("HardCodedStringLiteral")
        interface MODE {
            String BUTTON_MODE_AWAY = "button_mode_away";
            String BUTTON_MODE_HOME = "button_mode_home";
            String BUTTON_MODE_SLEEP = "button_mode_sleep";
            String BUTTON_MODE_COMFORT = "button_mode_comfort";
        }
        @SuppressWarnings("HardCodedStringLiteral")
        interface TEMPERATURE {
            String BUTTON_TEMP_PLUS = "button_temp_plus";
            String BUTTON_TEMP_MIN = "button_temp_min";
        }
        @SuppressWarnings("HardCodedStringLiteral")
        interface CARD {
            interface GAS {
                String CARD_GAS_TOTAL = "card_gas_total";
                String CARD_GAS_USAGE = "card_gas_usage";
            }
            @SuppressWarnings("HardCodedStringLiteral")
            interface ELEC {
                String CARD_ELEC_TOTAL = "card_elec_total";
                String CARD_ELEC_USAGE = "card_elec_usage";
            }
        }
        @SuppressWarnings("HardCodedStringLiteral")
        interface REFRESH {
            String REFRESH_BUTTON_WITH_AUTO_REFRESH_ON = "button_refresh_with_auto_refresh_on";
            String REFRESH_BUTTON_WITH_AUTO_REFRESH_OFF = "button_refresh_with_auto_refresh_off";
        }
    }

    public FirebaseAnalytics getFirebaseAnalyticsInstance() {
        if (mFirebaseAnalytics != null) return mFirebaseAnalytics;
        else throw new NullPointerException("FirebaseAnalytics is null. Did you initialize FirebaseHelper with getInstance(Context)?");
    }

    @SuppressWarnings("unused")
    public FirebaseCrashlytics getFirebaseCrashlyticsInstance() {
        if (mFirebaseCrashlytics != null) return mFirebaseCrashlytics;
        else throw new NullPointerException("FirebaseCrashlytics is null. Did you initialize FirebaseHelper with getInstance(Context)?");
    }

    public void set2CurrentScreen(Activity activity, String currentScreen) {
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.SCREEN_CLASS, activity.getClass().getName());
        bundle.putString(FirebaseAnalytics.Param.SCREEN_NAME, currentScreen);
        if (mFirebaseAnalytics != null) mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW, bundle);
        else Timber.e("mFirebaseAnalytics is null!");
    }

    public void logEvent(String event) {
        mFirebaseAnalytics.logEvent(event, null);
    }

    public void recordExceptionAndLog(Throwable t, String log) {
        if (t != null) mFirebaseCrashlytics.recordException(t);
        mFirebaseCrashlytics.log(log);
    }

    public void recordLog(String log) {
        mFirebaseCrashlytics.log(log);
    }
}