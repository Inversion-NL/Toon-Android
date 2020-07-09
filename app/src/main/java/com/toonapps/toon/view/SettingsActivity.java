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

package com.toonapps.toon.view;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import com.toonapps.toon.R;
import com.toonapps.toon.view.fragments.settings.SettingsAdvancedFragment;
import com.toonapps.toon.view.fragments.settings.SettingsFragment;
import com.toonapps.toon.view.fragments.settings.TroubleshootingFragment;

public class SettingsActivity extends AppCompatActivity implements
        PreferenceFragmentCompat.OnPreferenceStartFragmentCallback {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_settings);

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.settings_container, new SettingsFragment())
                .commit();
    }

    @Override
    public boolean onPreferenceStartFragment(PreferenceFragmentCompat caller, Preference pref) {

        //noinspection HardCodedStringLiteral
        if (pref.getKey().equals("pref_key_advanced_settings_fragment")) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.settings_container, new SettingsAdvancedFragment())
                    .addToBackStack(null)
                    .commit();
        } else //noinspection HardCodedStringLiteral
            if (pref.getKey().equals("key_pref_appInfo_troubleshooting")) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.settings_container, new TroubleshootingFragment())
                    .addToBackStack(null)
                    .commit();
        }
        return true;
    }
}