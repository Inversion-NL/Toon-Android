package com.toonapps.toon.view;

import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;

import androidx.annotation.Nullable;

import com.toonapps.toon.BuildConfig;
import com.toonapps.toon.R;

public class SettingsActivity extends PreferenceActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);

        //TODO start settings fragment instead of starting the welcome wizard
        //TODO migrate to new settings fragment

        String versionName = BuildConfig.VERSION_NAME;
        @SuppressWarnings("HardCodedStringLiteral")
        Preference appVersion =
                getPreferenceScreen().findPreference("key_pref_appInfo_appVersion");
        appVersion.setSummary(versionName);
    }
}
