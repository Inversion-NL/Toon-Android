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

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.widget.Toast;

import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.toonapps.toon.R;
import com.toonapps.toon.helper.AppSettings;

public class SettingsFragment extends PreferenceFragmentCompat {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.preferences, rootKey);

        if (getActivity() != null)
            //noinspection HardCodedStringLiteral
            FirebaseAnalytics.getInstance(getActivity())
                    .setCurrentScreen(getActivity(), "Settings fragment",null);

        String firebaseId = AppSettings.getInstance().getFirebaseInstanceId();
        Preference appVersion = findPreference("pref_key_firebaseInstanceId");
        if (appVersion != null) {
            appVersion.setOnPreferenceClickListener(preference -> {

                try {

                    @SuppressWarnings("ConstantConditions")
                    ClipboardManager clipboard = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
                    ClipData clip = ClipData.newPlainText("Firebase messaging ID", firebaseId);
                    clipboard.setPrimaryClip(clip);

                    Toast.makeText(getContext(), R.string.toast_firebaseICopiedToClipboard, Toast.LENGTH_LONG).show();

                } catch (Exception ignored){}

                return true;
            });
        }
    }
}