package com.toonapps.toon.view.fragments;

import android.os.Bundle;

import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import com.toonapps.toon.BuildConfig;
import com.toonapps.toon.R;

public class SettingsFragment extends PreferenceFragmentCompat {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.preferences, rootKey);

        String versionName = BuildConfig.VERSION_NAME;

        @SuppressWarnings("HardCodedStringLiteral")
        Preference appVersion = findPreference("key_pref_appInfo_appVersion");
        if (appVersion != null) appVersion.setSummary(versionName);

    }
}