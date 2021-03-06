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

package com.toonapps.toon.view.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatToggleButton;
import androidx.appcompat.widget.SwitchCompat;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.toonapps.toon.R;
import com.toonapps.toon.controller.DeviceController;
import com.toonapps.toon.controller.GasAndElecFlowController;
import com.toonapps.toon.controller.IDeviceListener;
import com.toonapps.toon.controller.IGasAndElecFlowListener;
import com.toonapps.toon.controller.ITemperatureListener;
import com.toonapps.toon.controller.TemperatureController;
import com.toonapps.toon.entity.CurrentUsageInfo;
import com.toonapps.toon.entity.DeviceInfo;
import com.toonapps.toon.entity.ThermostatInfo;
import com.toonapps.toon.entity.UsageInfo;
import com.toonapps.toon.helper.AppSettings;
import com.toonapps.toon.helper.ErrorMessage;
import com.toonapps.toon.helper.FirebaseHelper;
import com.toonapps.toon.helper.ScreenHelper;
import com.toonapps.toon.helper.TimerHelper;

import org.json.JSONException;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class ControlsFragment extends Fragment implements ITemperatureListener, IDeviceListener, IGasAndElecFlowListener {

    private TextView txtvTemperature;
    private TextView txtvSetPoint;
    private TextView txtvNextProgram;
    private TextView txtvCurrentPowerUse;
    private TextView txtvCurrentGasUse;
    private AppCompatToggleButton btnAwayMode;
    private AppCompatToggleButton btnSleepMode;
    private AppCompatToggleButton btnComfortMode;
    private AppCompatToggleButton btnHomeMode;
    private SwitchCompat swIsProgramOn;
    private ImageView imgvCurrentPower;
    private ImageView imgvCurrentGas;
    private SimpleDateFormat simpleDateFormat;

    //prevent toggle executing onCheckedChanged
    private boolean isUpdatingUI = false;
    private boolean isUpdatingUI2 = false;

    private boolean hasThermostatErrorCodeDialogBeenShownInThisSession;
    private boolean hasThermostatComErrorCodeDialogBeenShownInThisSession;
    private SwipeRefreshLayout refreshLayout;
    private TimerHelper timerHelper;
    private Context context;
    private View view;
    private TextView txtvTotalPowerUse;
    private TextView txtvTotalGasUse;
    private OnFragmentInteractionListener mListener;
    private float lowTariff = 0;
    private float normalTariff = 0;
    private boolean useGasInfoFromDevices = false;
    private FirebaseAnalytics mFirebaseAnalytics;

    public ControlsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_controls, container, false);

        //noinspection HardCodedStringLiteral
        simpleDateFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
        setResources();

        if (getActivity() != null) {
            mFirebaseAnalytics = FirebaseAnalytics.getInstance(context);
            //noinspection HardCodedStringLiteral
            mFirebaseAnalytics.setCurrentScreen(getActivity(), "Controls fragment",null);
        }

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        // Set in onResume so the message pop's up every session
        hasThermostatErrorCodeDialogBeenShownInThisSession = false;
        hasThermostatComErrorCodeDialogBeenShownInThisSession = false;

        // Block rotation
        if (getActivity() != null) getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        TemperatureController.getInstance().subscribe(this);
        DeviceController.getInstance().subscribe(this);
        GasAndElecFlowController.getInstance().subscribe(this);

        if (!AppSettings.getInstance().isFirstStart()) {
            // Do not update when first start
            updateData(false);
        }

        if (AppSettings.getInstance().useAutoRefresh()) setTimer(true);
        else setTimer(false);

        CardView cardTotalGas = view.findViewById(R.id.cardTotalGas);
        if (!AppSettings.getInstance().showGasWidgets() && cardTotalGas.getVisibility() == View.VISIBLE) {
            cardTotalGas.setVisibility(View.GONE);
        } else if (AppSettings.getInstance().showGasWidgets() && cardTotalGas.getVisibility() == View.GONE) {
            cardTotalGas.setVisibility(View.VISIBLE);
        }

        CardView cardGasUsage = view.findViewById(R.id.cardGasUsage);
        if (!AppSettings.getInstance().showGasWidgets() && cardGasUsage.getVisibility() == View.VISIBLE) {
            cardGasUsage.setVisibility(View.GONE);
        } else if (AppSettings.getInstance().showGasWidgets() && cardGasUsage.getVisibility() == View.GONE) {
            cardGasUsage.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onPause() {
        // Allow rotation
        if (getActivity() != null) getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR);

        // Cancel all requests
        DeviceController.getInstance().cancelAllRequests();

        TemperatureController.getInstance().unsubscribe(this);
        DeviceController.getInstance().unsubscribe(this);
        GasAndElecFlowController.getInstance().unsubscribe(this);

        setTimer(false);

        super.onPause();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    public void updateData(boolean isTimerTask) {
        if (!isTimerTask) setRefreshing(true);
        TemperatureController.getInstance().updateCurrentUsageInfo();
        TemperatureController.getInstance().updateThermostatInfo();

        GasAndElecFlowController.getInstance().getTodayElecNtUsage();
        GasAndElecFlowController.getInstance().getTodayElecLtUsage();
        if (AppSettings.getInstance().showGasWidgets()) {
            // Only update if the smart meter has gas (user pref in settings)
            GasAndElecFlowController.getInstance().getTodayGasUsage();
        }

        // Try to fetch current usage info once to see if it's available (e.g. rooted Toon)
        // Disable for now an find out later on a non-rooted Toon
        /*
        if (!AppSettings.getInstance().triedCurrentUsageInfoOnce()
                || !AppSettings.getInstance().isCurrentUsageInfoAvailable()) {
            AppSettings.getInstance().setTriedCurrentUsageInfoOnce(true);
            DeviceController.getInstance().updateZWaveDevices();
        }
        */
    }

    private final View.OnClickListener onButtonClicked = new View.OnClickListener() {
        public void onClick(View v) {

            float tempSetValue = AppSettings.getInstance().getTempSetValue();

            switch(v.getId()){
                case R.id.btnAwayMode:
                    TemperatureController.getInstance().setTemperatureMode(ThermostatInfo.TemperatureMode.AWAY);
                    switchButtonState(ThermostatInfo.TemperatureMode.AWAY, false);
                    mFirebaseAnalytics.logEvent(FirebaseHelper.EVENT.MODE.BUTTON_MODE_AWAY, null);
                    break;
                case R.id.btnSleepMode:
                    TemperatureController.getInstance().setTemperatureMode(ThermostatInfo.TemperatureMode.SLEEP);
                    switchButtonState(ThermostatInfo.TemperatureMode.SLEEP, false);
                    mFirebaseAnalytics.logEvent(FirebaseHelper.EVENT.MODE.BUTTON_MODE_SLEEP, null);
                    break;
                case R.id.btnHomeMode:
                    TemperatureController.getInstance().setTemperatureMode(ThermostatInfo.TemperatureMode.HOME);
                    switchButtonState(ThermostatInfo.TemperatureMode.HOME, false);
                    mFirebaseAnalytics.logEvent(FirebaseHelper.EVENT.MODE.BUTTON_MODE_HOME, null);
                    break;
                case R.id.btnComfortMode:
                    TemperatureController.getInstance().setTemperatureMode(ThermostatInfo.TemperatureMode.COMFORT);
                    switchButtonState(ThermostatInfo.TemperatureMode.COMFORT, false);
                    mFirebaseAnalytics.logEvent(FirebaseHelper.EVENT.MODE.BUTTON_MODE_COMFORT, null);
                    break;
                case R.id.btnPlus:
                    TemperatureController.getInstance().setTemperatureHigher(tempSetValue);
                    mFirebaseAnalytics.logEvent(FirebaseHelper.EVENT.TEMPERATURE.BUTTON_TEMP_PLUS, null);
                    break;
                case R.id.btnMin:
                    TemperatureController.getInstance().setTemperatureLower(tempSetValue);
                    mFirebaseAnalytics.logEvent(FirebaseHelper.EVENT.TEMPERATURE.BUTTON_TEMP_MIN, null);
                    break;
                case R.id.cardTotalGas:
                    mFirebaseAnalytics.logEvent(FirebaseHelper.EVENT.CARD.GAS.CARD_GAS_TOTAL, null);
                case R.id.cardGasUsage:
                    if (mListener != null) {
                        mListener.onFragmentInteraction(OnFragmentInteractionListener.ACTION.START.GAS_USAGE);
                        mFirebaseAnalytics.logEvent(FirebaseHelper.EVENT.CARD.GAS.CARD_GAS_USAGE, null);
                    }
                    break;
                case R.id.cardTotalPower:
                    mFirebaseAnalytics.logEvent(FirebaseHelper.EVENT.CARD.ELEC.CARD_ELEC_TOTAL, null);
                case R.id.cardPowerUsage:
                    mFirebaseAnalytics.logEvent(FirebaseHelper.EVENT.CARD.ELEC.CARD_ELEC_USAGE, null);
                    if (mListener != null) {
                        mListener.onFragmentInteraction(OnFragmentInteractionListener.ACTION.START.ELEC_USAGE);
                    }
                    break;
            }
        }
    };

    private void setResources(){

        refreshLayout = view.findViewById(R.id.refreshLayout);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                updateData(false);
            }
        });

        CardView cardTotalPower = view.findViewById(R.id.cardTotalPower);
        cardTotalPower.setOnClickListener(onButtonClicked);

        CardView cardTotalGas = view.findViewById(R.id.cardTotalGas);
        cardTotalGas.setOnClickListener(onButtonClicked);
        if (!AppSettings.getInstance().showGasWidgets()) {
            cardTotalGas.setVisibility(View.GONE);
        }

        CardView cardPowerUsage = view.findViewById(R.id.cardPowerUsage);
        cardPowerUsage.setOnClickListener(onButtonClicked);

        CardView cardGasUsage = view.findViewById(R.id.cardGasUsage);
        cardGasUsage.setOnClickListener(onButtonClicked);
        if (!AppSettings.getInstance().showGasWidgets()) {
            cardGasUsage.setVisibility(View.GONE);
        }

        btnAwayMode = view.findViewById(R.id.btnAwayMode);
        btnAwayMode.setOnClickListener(onButtonClicked);
        btnSleepMode = view.findViewById(R.id.btnSleepMode);
        btnSleepMode.setOnClickListener(onButtonClicked);
        btnHomeMode = view.findViewById(R.id.btnHomeMode);
        btnHomeMode.setOnClickListener(onButtonClicked);
        btnComfortMode = view.findViewById(R.id.btnComfortMode);
        btnComfortMode.setOnClickListener(onButtonClicked);
        view.findViewById(R.id.btnPlus).setOnClickListener(onButtonClicked);
        view.findViewById(R.id.btnMin).setOnClickListener(onButtonClicked);

        txtvTemperature = view.findViewById(R.id.txtvTemperature);
        txtvSetPoint = view.findViewById(R.id.txtvSetPoint);
        txtvNextProgram = view.findViewById(R.id.txtvNextProgram);
        txtvCurrentPowerUse = view.findViewById(R.id.txtvCurrentPowerUse);
        txtvCurrentGasUse = view.findViewById(R.id.txtvCurrentGasUse);
        txtvTotalGasUse = view.findViewById(R.id.txtvTotalGasUse);
        txtvTotalPowerUse = view.findViewById(R.id.txtvTotalPowerUse);
        imgvCurrentPower = view.findViewById(R.id.imgvCurrentPower);
        imgvCurrentGas = view.findViewById(R.id.imgvCurrentGas);

        swIsProgramOn = view.findViewById(R.id.swIsProgramOn);
        swIsProgramOn.setOnCheckedChangeListener(onCheckedChangeListener);
    }

    private void setTimer(boolean enabled) {

        if (enabled) {
            long refreshRate = AppSettings.getInstance().getAutoRefreshValue();

            if (timerHelper != null) timerHelper.stop();
            timerHelper = new TimerHelper(getActivity(), refreshRate, new TimerHelper.TimerInterface() {
                @Override
                public void onTime() {
                    updateData(true);
                }
            });
            timerHelper.startWithDelay();
        } else if (timerHelper != null) timerHelper.stop();
    }

    private void switchButtonState(ThermostatInfo.TemperatureMode mode, boolean programChangesButton) {
        switch (mode) {
            case AWAY:
                if (programChangesButton) btnAwayMode.setChecked(true);
                btnSleepMode.setChecked(false);
                btnHomeMode.setChecked(false);
                btnComfortMode.setChecked(false);
                break;

            case SLEEP:
                if (programChangesButton) btnSleepMode.setChecked(true);
                btnAwayMode.setChecked(false);
                btnHomeMode.setChecked(false);
                btnComfortMode.setChecked(false);
                break;

            case HOME:
                if (programChangesButton) btnHomeMode.setChecked(true);
                btnAwayMode.setChecked(false);
                btnSleepMode.setChecked(false);
                btnComfortMode.setChecked(false);
                break;

            case COMFORT:
                if (programChangesButton) btnComfortMode.setChecked(true);
                btnAwayMode.setChecked(false);
                btnSleepMode.setChecked(false);
                btnHomeMode.setChecked(false);
                break;

            case NONE:
                btnComfortMode.setChecked(false);
                btnAwayMode.setChecked(false);
                btnSleepMode.setChecked(false);
                btnHomeMode.setChecked(false);
                break;
        }
    }

    private final CompoundButton.OnCheckedChangeListener onCheckedChangeListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if(!isUpdatingUI && !isUpdatingUI2) TemperatureController.getInstance().setTemperatureProgram(isChecked);
        }
    };

    private void setGasMeter(double currentGasUsage) {

        String text = String.format(Locale.getDefault(), getString(R.string.graph_gasUsage_formatted), currentGasUsage);
        txtvCurrentGasUse.setText(text);

        if(currentGasUsage >= 0.0 && currentGasUsage < 450.00){
            imgvCurrentGas.setImageResource(R.drawable.gas1_10);
        }
        else if(currentGasUsage >= 450.00 && currentGasUsage < 800.0){
            imgvCurrentGas.setImageResource(R.drawable.gas3_10);
        }
        else if(currentGasUsage >= 800.0 && currentGasUsage < 1200.0){
            imgvCurrentGas.setImageResource(R.drawable.gas5_10);
        }
        else if(currentGasUsage >= 1200.0 && currentGasUsage < 2000.0){
            imgvCurrentGas.setImageResource(R.drawable.gas7_10);
        }
        else if(currentGasUsage >= 2000.0){
            imgvCurrentGas.setImageResource(R.drawable.gas9_10);
        }
    }

    private void setPowerMeter(double currentPowerUsage, double avgPower) {

        String text = String.format(Locale.getDefault(), getString(R.string.graph_powerUsage_formatted), currentPowerUsage);
        txtvCurrentPowerUse.setText(text);

        if (avgPower > 0 && currentPowerUsage > 0) {
            // Use dynamic meter when there is an average value
            // and the current usage is more then 0
            if (currentPowerUsage >= 0 && currentPowerUsage < (avgPower / 2)) {
                imgvCurrentPower.setImageResource(R.drawable.power1_10);
            } else if (currentPowerUsage >= (avgPower / 2) && currentPowerUsage < (avgPower - (avgPower * 0.2))) {
                imgvCurrentPower.setImageResource(R.drawable.power3_10);
            } else if (currentPowerUsage >= (avgPower - (avgPower * 0.2)) && currentPowerUsage < (avgPower + (avgPower * 0.2))) {
                imgvCurrentPower.setImageResource(R.drawable.power5_10);
            } else if (currentPowerUsage >= (avgPower + (avgPower * 0.2)) && currentPowerUsage < (avgPower * 2)) {
                imgvCurrentPower.setImageResource(R.drawable.power7_10);
            } else if (currentPowerUsage >= (avgPower * 2)) {
                imgvCurrentPower.setImageResource(R.drawable.power9_10);
            }
        } else {
            // Use static meter if there is no average value or the current
            // PowerUsage is 0
            if(currentPowerUsage >= 0 && currentPowerUsage < 50){
                imgvCurrentPower.setImageResource(R.drawable.power1_10);
            }
            else if(currentPowerUsage >= 50 && currentPowerUsage < 200){
                imgvCurrentPower.setImageResource(R.drawable.power3_10);
            }
            else if(currentPowerUsage >= 200 && currentPowerUsage < 600){
                imgvCurrentPower.setImageResource(R.drawable.power5_10);
            }
            else if(currentPowerUsage >= 600 && currentPowerUsage < 1000){
                imgvCurrentPower.setImageResource(R.drawable.power7_10);
            }
            else if(currentPowerUsage >= 1000){
                imgvCurrentPower.setImageResource(R.drawable.power9_10);
            }
        }
    }

    private void setRefreshing(boolean setRefreshing) {
        if (refreshLayout != null) {
            if (setRefreshing && !refreshLayout.isRefreshing()) refreshLayout.setRefreshing(true);
            else if (!setRefreshing && refreshLayout.isRefreshing()) refreshLayout.setRefreshing(false);
        }
    }

    @Override
    public void onDeviceInfoChanged(DeviceInfo aDevicesInfo) {

        if (useGasInfoFromDevices) {
            // On some Toon's there is no gas flow in the getUsageInfo call
            // Using the info from the zwave devices info
            setGasMeter(aDevicesInfo.getGasUsed());
        } else {
            double currentElectricUsage = 0;

            if (aDevicesInfo.getElecUsageFlowHigh() > 0) {
                currentElectricUsage = aDevicesInfo.getElecUsageFlowHigh();
            } else if (aDevicesInfo.getElecUsageFlowLow() > 0) {
                currentElectricUsage = aDevicesInfo.getElecUsageFlowLow();
            } else if (aDevicesInfo.getElecUsageFlow() > 0) {
                currentElectricUsage = aDevicesInfo.getElecUsageFlow();
            }

            setPowerMeter(currentElectricUsage, 0);
            setGasMeter(aDevicesInfo.getGasUsed());
        }
    }

    @Override
    public void onTemperatureChanged(CurrentUsageInfo aCurrentUsageInfo) {
        isUpdatingUI2 = true;
        AppSettings.getInstance().setCurrentUsageInfoAvailable(true);

        float powerProduction = aCurrentUsageInfo.getPowerProduction().getValue();

        //Random random = new Random();
        //powerProduction = random.nextInt(30000) / 10f;
        //AppSettings.getInstance().setPowerProductionDialogHasShown(false);

        /*
        if (powerProduction > 0) {

            if (!AppSettings.getInstance().hasPowerProductionDialogBeenShown()) {
                showPowerProductionDialog();
            } else if (AppSettings.getInstance().showPowerProductionWidgets()) {
                enablePowerProductionFeatures(true);
                calculatePowerProduction(powerProduction);
            } else enablePowerProductionFeatures(false);

        }  else if (!AppSettings.getInstance().showPowerProductionWidgets()) {
            enablePowerProductionFeatures(false);
        }

        */

        setPowerMeter(
                aCurrentUsageInfo.getPowerUsage().getValue(),
                aCurrentUsageInfo.getPowerUsage().getAvgValue()
        );

        if (aCurrentUsageInfo.getUseGasInfoFromDevices()) {
            // The gas flow value was 'null'
            // Using device info to get the gas flow
            useGasInfoFromDevices = true;
            DeviceController.getInstance().updateZWaveDevices();
        } else {
            // Use the value for gas flow from usage info call
            useGasInfoFromDevices = false;
            setGasMeter( aCurrentUsageInfo.getGasUsage().getValue() );
        }

        isUpdatingUI2 = false;
        if (!isUpdatingUI) setRefreshing(false);
    }

    private void calculatePowerProduction(float powerProduction) {
        // TODO Calculate power production
        TextView txtvPowerProduction = view.findViewById(R.id.txtvPowerProduction);
        //noinspection HardCodedStringLiteral
        txtvPowerProduction.setText(powerProduction + " KWh" );
    }

    private void enablePowerProductionFeatures(boolean show) {
        if (show) {

            if (ScreenHelper.getDisplayDensityDpi(context, false) == ScreenHelper.DISPLAY_DENSITY.HIGH) {
                showPowerBarImages(true);           // Only show on high density screens
            } else {
                showPowerBarImages(false);          // Hide power bar images when showing
            }
            showPowerProductionWidgets(true);

        } else {

            if (ScreenHelper.getDisplayDensityDpi(context, false) == ScreenHelper.DISPLAY_DENSITY.NORMAL) {
                showPowerBarImages(true);           // Only show on normal density screens
            } else if (ScreenHelper.getDisplayDensityDpi(context, false) == ScreenHelper.DISPLAY_DENSITY.LOW){
                showPowerBarImages(false);          // Never show on small screens
            }
            showPowerProductionWidgets(false);
        }
    }

    private void showPowerProductionWidgets(boolean show) {

        CardView cardPowerProduction = view.findViewById(R.id.cardPowerProduction);
        CardView cardPowerReturn = view.findViewById(R.id.cardPowerReturn);

        int isShowingProduction = cardPowerProduction.getVisibility();

        if (show && isShowingProduction != View.VISIBLE) {

            // No point in setting to visible if already visible
            cardPowerProduction.setVisibility(View.VISIBLE);
            cardPowerReturn.setVisibility(View.VISIBLE);

        } else if (!show && isShowingProduction != View.GONE) {

            // No point in setting to gone if already gone
            cardPowerProduction.setVisibility(View.GONE);
            cardPowerReturn.setVisibility(View.GONE);

        }
    }

    private void showPowerBarImages(boolean show) {

        if (show) {
            setCardGasUsageLayoutSize(170);         // Large to be able to show power bar images
            imgvCurrentGas.setVisibility(View.VISIBLE);
            imgvCurrentPower.setVisibility(View.VISIBLE);
        } else {
            imgvCurrentGas.setVisibility(View.GONE);
            imgvCurrentPower.setVisibility(View.GONE);
            setCardGasUsageLayoutSize(70);          // Small because power bar images are gone
        }

    }

    private void setCardGasUsageLayoutSize(int size) {
        CardView cardPowerUsage = view.findViewById(R.id.cardPowerUsage);
        ViewGroup.LayoutParams cardPowerUsageParams = cardPowerUsage.getLayoutParams();

        CardView cardGasUsage = view.findViewById(R.id.cardGasUsage);
        ViewGroup.LayoutParams cardGasUsageParams = cardGasUsage.getLayoutParams();

        cardGasUsageParams.height = ScreenHelper.convertDpsToPixels(context,size);
        cardPowerUsageParams.height = ScreenHelper.convertDpsToPixels(context,size);

    }

    private void showPowerProductionDialog() {
        String message = getString(R.string.dialog_powerProduction_askForEnable_message1).concat("\n")
                .concat(getString(R.string.dialog_powerProduction_askForEnable_message2)).concat("\n")
                .concat(getString(R.string.dialog_powerProduction_askForEnable_message3));

        AlertDialog.Builder powerProductionDialog = new AlertDialog.Builder(context)
                .setTitle(R.string.dialog_powerProduction_askForEnable_title)
                .setMessage(message)
                .setCancelable(false)
                .setIcon(android.R.drawable.ic_dialog_info);
        powerProductionDialog.setPositiveButton(R.string.dialog_button_yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                AppSettings.getInstance().setShowPowerProductionWidgets(true);
                AppSettings.getInstance().setPowerProductionDialogHasShown(true);
                enablePowerProductionFeatures(true);
            }
        });
        powerProductionDialog.setNegativeButton(R.string.dialog_button_no, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                AppSettings.getInstance().setShowPowerProductionWidgets(false);
                AppSettings.getInstance().setPowerProductionDialogHasShown(true);
                enablePowerProductionFeatures(false);
            }
        });
        powerProductionDialog.show();
    }

    @Override
    public void onTemperatureChanged(ThermostatInfo aThermostatInfo) {
        isUpdatingUI = true;

        String text = String.format(
                Locale.getDefault(),
                getString(R.string.graph_temp_formatted),
                aThermostatInfo.getCurrentTemp() / 100
        );
        txtvTemperature.setText(text);

        text = String.format(Locale.getDefault(), getString(R.string.graph_temp_formatted), aThermostatInfo.getCurrentSetpoint() / 100);
        txtvSetPoint.setText(text);

        if (aThermostatInfo.getErrorFound() != 255 && !hasThermostatErrorCodeDialogBeenShownInThisSession) {
            String message = String.format(getResources().getString(R.string.ot_error_thermostat_message), aThermostatInfo.getErrorFound());
            showErrorDialog(R.string.ot_error_thermostat_title, message);
            hasThermostatErrorCodeDialogBeenShownInThisSession = true;
        }

        if (aThermostatInfo.getOtCommError() > 0 && !hasThermostatComErrorCodeDialogBeenShownInThisSession) {
            String message = String.format(getResources().getString(R.string.ot_error_com_message), aThermostatInfo.getErrorFound());
            showErrorDialog(R.string.ot_error_com_title, message);
            hasThermostatComErrorCodeDialogBeenShownInThisSession = true;
        } else {
            // Only show info when there is no OpenTherm comm error
            if (aThermostatInfo.getBurnerInfo() > -1) { // Minus one meaning the value isn't retrieved from Toon

                ImageView burnerInfo = view.findViewById(R.id.burnerInfo);

                switch(aThermostatInfo.getBurnerInfo()) {

                    case 0:
                        burnerInfo.setImageResource(R.drawable.ic_flame0);
                        break;

                    case 1:
                        if (aThermostatInfo.getCurrentModulationLevel() < 34){
                            burnerInfo.setImageResource(R.drawable.ic_flame1);
                        } else if (aThermostatInfo.getCurrentModulationLevel() < 67){
                            burnerInfo.setImageResource(R.drawable.ic_flame2);
                        } else {
                            burnerInfo.setImageResource(R.drawable.ic_flame3);
                        }
                        break;

                    case 2:
                        burnerInfo.setImageResource(R.drawable.ic_tapwater_heating);
                        break;
                }
            } else {
                // For non rooted Toon
                if (aThermostatInfo.getCurrentTemp() > aThermostatInfo.getCurrentSetpoint()) // Figure out our own whether the burner is burning or not
                    view.findViewById(R.id.burnerInfo).setVisibility(View.INVISIBLE);
                else view.findViewById(R.id.burnerInfo).setVisibility(View.VISIBLE);
            }
        }

        if (aThermostatInfo.getNextSetpoint() != 0 || aThermostatInfo.getNextTime() != null) {
            text = String.format(
                    Locale.getDefault(),
                    getString(R.string.controls_nextProgramValue),
                    simpleDateFormat.format(aThermostatInfo.getNextTime()),
                    aThermostatInfo.getNextStateString(context),
                    aThermostatInfo.getNextSetpoint() / 100
            );
            txtvNextProgram.setText(text);
        } else {
            // No Toon temperature programming used
            text = String.format(
                        Locale.getDefault(),
                        getString(R.string.controls_nextProgram_onProgramText),
                        aThermostatInfo.getCurrentSetpoint() / 100
                    );
            txtvNextProgram.setText(text);
        }

        swIsProgramOn.setChecked(aThermostatInfo.getProgramState());
        switchButtonState(aThermostatInfo.getCurrentTempMode(), true);

        isUpdatingUI = false;
        if (!isUpdatingUI2) setRefreshing(false);
    }

    @Override
    public void onUsageChanged(UsageInfo usageInfo) {
        try {
            if (usageInfo.getTime() == UsageInfo.TIME.TODAY
                    && usageInfo.getType() == UsageInfo.TYPE.ELEC) {

                // Electricity usage

                if (usageInfo.getElectTariffType() == UsageInfo.TARIFF.LOW) {
                    lowTariff = usageInfo.getTodayUsage();
                } else if (usageInfo.getElectTariffType() == UsageInfo.TARIFF.NORMAL) {
                    normalTariff = usageInfo.getTodayUsage();
                }
                if (lowTariff != 0 && normalTariff != 0) {
                    // only set text when both normal and low values are in
                    float total = normalTariff + lowTariff;
                    txtvTotalPowerUse.setText(
                            String.format(
                                    Locale.getDefault(),
                                    getString(R.string.controls_txtvTotalPowerUseValue),
                                    total
                            )
                    );
                    lowTariff = 0;
                    normalTariff = 0;
                }

            } else if (usageInfo.getTime() == UsageInfo.TIME.TODAY
                    && usageInfo.getType() == UsageInfo.TYPE.GAS) {

                // Gas usage

                float todayGasUsage = usageInfo.getTodayUsage();
                txtvTotalGasUse.setText(
                        String.format(Locale.getDefault(), getString(R.string.controls_txtvTotalGasUseValue), todayGasUsage)
                );
            }
        } catch (JSONException e) {
            onUsageError(e);
        }
    }

    @Override
    public void onTemperatureError(Exception e) {
        ErrorMessage errorMessage = new ErrorMessage(context);
        String message = errorMessage.getHumanReadableErrorMessage(e);
        Toast.makeText(context, getString(R.string.exception_message_temperature_update_error_txt) + ": " + message, Toast.LENGTH_SHORT).show();
        setRefreshing(false);
    }

    @Override
    public void onDeviceError(Exception e) {
        ErrorMessage errorMessage = new ErrorMessage(context);
        String message = errorMessage.getHumanReadableErrorMessage(e);
        Toast.makeText(context, getString(R.string.exception_message_device_update_error_txt) + ": " +  message, Toast.LENGTH_SHORT).show();
        setRefreshing(false);
    }

    @Override
    public void onUsageError(Exception e) {
        ErrorMessage errorMessage = new ErrorMessage(context);
        String message = errorMessage.getHumanReadableErrorMessage(e);
        Toast.makeText(context, getString(R.string.exception_message_device_update_error_txt) + ": " +  message, Toast.LENGTH_SHORT).show();
        setRefreshing(false);
    }

    private void showErrorDialog(int title, String message) {
        new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(R.string.dialog_button_yes, null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }
}