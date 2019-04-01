package com.toonapps.toon.view;

import android.os.Bundle;
import android.preference.PreferenceActivity;

import com.toonapps.toon.R;

import androidx.annotation.Nullable;

public class SettingsActivity extends PreferenceActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
    }
}
