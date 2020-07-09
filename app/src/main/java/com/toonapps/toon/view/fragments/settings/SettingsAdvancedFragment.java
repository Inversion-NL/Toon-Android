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

package com.toonapps.toon.view.fragments.settings;

import android.content.Intent;
import android.os.Bundle;

import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import com.google.android.gms.oss.licenses.OssLicensesMenuActivity;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.toonapps.toon.BuildConfig;
import com.toonapps.toon.R;

public class SettingsAdvancedFragment extends PreferenceFragmentCompat {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.preferences_advanced, rootKey);

        String versionName = BuildConfig.VERSION_NAME;

        @SuppressWarnings("HardCodedStringLiteral")
        Preference appVersion = findPreference("key_pref_appInfo_appVersion");
        if (appVersion != null) appVersion.setSummary(versionName);

        @SuppressWarnings("HardCodedStringLiteral")
        Preference appLibraries = findPreference("key_pref_appInfo_appLibraries");
        if (appLibraries != null) {
            appLibraries.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                public boolean onPreferenceClick(Preference preference) {
                    startActivity(new Intent(getContext(), OssLicensesMenuActivity.class));
                    return true;
                }
            });
        }

        if (getActivity() != null)
            //noinspection HardCodedStringLiteral
            FirebaseAnalytics.getInstance(getActivity())
                    .setCurrentScreen(getActivity(), "Advanced settings fragment",null);
    }
}