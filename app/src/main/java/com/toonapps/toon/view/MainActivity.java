/*
 * Copyright (c) 2021
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

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import com.google.android.gms.oss.licenses.OssLicensesMenuActivity;
import com.google.android.material.navigation.NavigationView;
import com.google.android.play.core.install.model.ActivityResult;
import com.toonapps.toon.BuildConfig;
import com.toonapps.toon.R;
import com.toonapps.toon.helper.AppSettings;
import com.toonapps.toon.helper.AppUpdateHelper;
import com.toonapps.toon.helper.FirebaseHelper;
import com.toonapps.toon.helper.FirebaseMessagingHelper;
import com.toonapps.toon.view.fragments.ControlsFragment;
import com.toonapps.toon.view.fragments.OnFragmentInteractionListener;
import com.toonapps.toon.view.fragments.UsageGraphFragment;

import timber.log.Timber;

public class MainActivity extends AppCompatActivity
        implements
            NavigationView.OnNavigationItemSelectedListener,
            OnFragmentInteractionListener {

    private DrawerLayout drawerLayout;
    private NavController navController;
    private final int REQUEST_CODE_INTRO = 100;
    private final int REQUEST_CODE_APP_UPDATE = 200;
    private final String REQUEST_TYPE = "type";
    private NavigationView navigationView;
    private FirebaseHelper mFirebaseHelper;
    private AppSettings mAppSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        mAppSettings = AppSettings.getInstance();
        mAppSettings.initialize(this.getApplicationContext());

        mFirebaseHelper = FirebaseHelper.getInstance(this);
        FirebaseMessagingHelper.getFCMInstanceId(this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (BuildConfig.DEBUG) Timber.plant(new Timber.DebugTree());
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (mAppSettings.isFirstStart()) {
            Intent intent = new Intent(this, ConnectionWizardActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivityForResult(intent, REQUEST_CODE_INTRO);
        } else {
            initDrawer();
            setDrawerOptions();
            if (!mAppSettings.hasDrawerPeeked()) peekDrawer();
            AppUpdateHelper.checkForAppUpdate(this, REQUEST_CODE_APP_UPDATE);
            AppUpdateHelper.checkIfAppUpdatedSuccessfully(this, REQUEST_CODE_APP_UPDATE);
        }
    }

    private void initDrawer() {
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.navigationView);
        navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, drawerLayout);
        NavigationUI.setupWithNavController(navigationView, navController);
        navigationView.setNavigationItemSelectedListener(this);
        setDrawerOptions();
    }

    private void setDrawerOptions() {
        String version = String.format(getString(R.string.drawerMenu_appVersion), BuildConfig.VERSION_NAME);
        Menu nav = navigationView.getMenu();

        MenuItem menuInfo = nav.findItem(R.id.menu_info);
        menuInfo.setTitle(version);

        /*

        Disable since there is no way to get the gas data from Toon anymore

        MenuItem menuGasUsage = nav.findItem(R.id.menu_gasUsage);
        menuGasUsage.setVisible(mAppSettings.showGasWidgets());
        */
    }

    private void peekDrawer() {

        int startDelay = 3000;
        int openTime = 2000;

        new Handler().postDelayed(() -> drawerLayout.openDrawer(GravityCompat.START), startDelay );

        new Handler().postDelayed(() -> drawerLayout.closeDrawer(GravityCompat.START), startDelay + openTime );

        mAppSettings.setDrawerHasPeeked(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_activity_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                drawerLayout.openDrawer(GravityCompat.START);
                return true;
            case R.id.action_refresh:
                if (mAppSettings.useAutoRefresh()) {
                    mFirebaseHelper.logEvent(FirebaseHelper.EVENT.REFRESH.REFRESH_BUTTON_WITH_AUTO_REFRESH_ON);
                } else
                    mFirebaseHelper.logEvent(FirebaseHelper.EVENT.REFRESH.REFRESH_BUTTON_WITH_AUTO_REFRESH_OFF);
                updateDataInFragment();
                return true;
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Intro
        if (requestCode == REQUEST_CODE_INTRO) {
            if (resultCode == RESULT_OK) {
                mAppSettings.setFirstStart(false);
                updateDataInFragment();
            } else {
                // Cancelled the intro. You can then e.g. finish this activity too.
                finish();
            }

        // In app update
        } else if (requestCode == REQUEST_CODE_APP_UPDATE) {
            if (resultCode != RESULT_OK) {
                // If the update is cancelled or fails,
                // you can request to start the update again.
                if (resultCode == RESULT_CANCELED) {
                    mFirebaseHelper.logEvent(FirebaseHelper.EVENT.APP_UPDATE.UPDATE_CANCELED_BY_USERS);
                } else if (resultCode == ActivityResult.RESULT_IN_APP_UPDATE_FAILED) {
                    mFirebaseHelper.logEvent(FirebaseHelper.EVENT.APP_UPDATE.UPDATE_FAILED);
            }

            } else {
                mFirebaseHelper.logEvent(FirebaseHelper.EVENT.APP_UPDATE.UPDATE_SUCCESS);
            }
        }
    }

    private void updateDataInFragment() {
        try {
            @SuppressWarnings("ConstantConditions")
            Fragment current =
                    getSupportFragmentManager()
                            .findFragmentById(R.id.nav_host_fragment)
                            .getChildFragmentManager()
                            .getFragments()
                            .get(0);
            if (current instanceof ControlsFragment)
                ((ControlsFragment) current).updateData(false);
            if (current instanceof UsageGraphFragment)
                ((UsageGraphFragment) current).updateGraphData();
        } catch (Exception e){
            //noinspection HardCodedStringLiteral
            mFirebaseHelper.recordExceptionAndLog(e, "Exception while getting getting the fragment for updating data in fragment");
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        //menuItem.setChecked(true);

        drawerLayout.closeDrawers();

        Bundle bundle = new Bundle();

        switch (menuItem.getItemId()) {
            case R.id.menu_controls:
                startFragment(R.id.fragment_controls, bundle);
                break;

            case R.id.menu_elecUsage:
                bundle.putInt(REQUEST_TYPE, UsageGraphFragment.TYPE.ELEC);
                startFragment(R.id.fragment_usage_elec, bundle);
                break;

            /*

            Disabled since there is no way to get the gas data anymore

            case R.id.menu_gasUsage:
                bundle.putInt(REQUEST_TYPE, UsageGraphFragment.TYPE.GAS);
                startFragment(R.id.fragment_usage_gas, bundle);
                break;

             */

            case R.id.menu_settings:
                startActivity(new Intent(this, SettingsActivity.class));
                break;

            case R.id.menu_openSource:
                startActivity(new Intent(this, OssLicensesMenuActivity.class));
                break;
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout != null) {

            if (drawerLayout.isDrawerOpen(GravityCompat.START))
                drawerLayout.closeDrawer(GravityCompat.START);
            else super.onBackPressed();

        } else super.onBackPressed();
    }

    @Override
    public void onFragmentInteraction(int action) {
        Bundle bundle = new Bundle();

        switch (action) {
            case ACTION.START.ELEC_USAGE:
                bundle.putInt(REQUEST_TYPE, UsageGraphFragment.TYPE.ELEC);
                startFragment(R.id.fragment_usage_elec, bundle);
                break;
            case ACTION.START.GAS_USAGE:
                bundle.putInt(REQUEST_TYPE, UsageGraphFragment.TYPE.GAS);
                startFragment(R.id.fragment_usage_gas, bundle);
                break;
        }
    }

    private void startFragment(int fragmentId, Bundle bundle) {
        NavOptions.Builder navBuilder = new NavOptions.Builder();
        NavOptions navOptions;
        navOptions = navBuilder.setPopUpTo(fragmentId, true).build();
        // Clear the back stack

        navController.navigate(fragmentId, bundle, navOptions);
    }
}