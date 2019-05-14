package com.toonapps.toon.view.fragments;

import android.content.Context;
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
import androidx.appcompat.widget.AppCompatToggleButton;
import androidx.appcompat.widget.SwitchCompat;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

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
import com.toonapps.toon.helper.TimerHelper;

import org.json.JSONException;

import java.text.MessageFormat;
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
    private SwipeRefreshLayout refreshLayout;
    private TimerHelper timerHelper;
    private Context context;
    private View view;
    private TextView txtvTotalPowerUse;
    private TextView txtvTotalGasUse;
    private OnFragmentInteractionListener mListener;
    private float lowTariff = 0;
    private float normalTariff = 0;

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

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        // Block rotation
        if (getActivity() != null) getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        TemperatureController.getInstance().subscribe(this);
        DeviceController.getInstance().subscribe(this);
        GasAndElecFlowController.getInstance().subscribe(this);

        updateData(false);

        if (AppSettings.getInstance().useAutoRefresh()) setTimer(true);
        else setTimer(false);
    }

    @Override
    public void onPause() {
        super.onPause();

        // Allow rotation
        if (getActivity() != null) getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR);

        TemperatureController.getInstance().unsubscribe(this);
        DeviceController.getInstance().unsubscribe(this);
        GasAndElecFlowController.getInstance().unsubscribe(this);

        setTimer(false);
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
        GasAndElecFlowController.getInstance().getTodayGasUsage();

        // Try to fetch current usage info once to see if it's available (e.g. rooted Toon)
        // Disable for now an find out later on a non-rooted Toon
        /*
        if (!AppSettings.getInstance().triedCurrentUsageInfoOnce()
                || !AppSettings.getInstance().isCurrentUsageInfoAvailable()) {
            AppSettings.getInstance().setTriedCurrentUsageInfoOnce(true);
            DeviceController.getInstance().updateDeviceInfo();
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
                    break;
                case R.id.btnSleepMode:
                    TemperatureController.getInstance().setTemperatureMode(ThermostatInfo.TemperatureMode.SLEEP);
                    switchButtonState(ThermostatInfo.TemperatureMode.SLEEP, false);
                    break;
                case R.id.btnHomeMode:
                    TemperatureController.getInstance().setTemperatureMode(ThermostatInfo.TemperatureMode.HOME);
                    switchButtonState(ThermostatInfo.TemperatureMode.HOME, false);
                    break;
                case R.id.btnComfortMode:
                    TemperatureController.getInstance().setTemperatureMode(ThermostatInfo.TemperatureMode.COMFORT);
                    switchButtonState(ThermostatInfo.TemperatureMode.COMFORT, false);
                    break;
                case R.id.btnPlus:
                    TemperatureController.getInstance().setTemperatureHigher(tempSetValue);
                    break;
                case R.id.btnMin:
                    TemperatureController.getInstance().setTemperatureLower(tempSetValue);
                    break;
                case R.id.cardTotalGas:
                case R.id.cardGasUsage:
                    if (mListener != null) {
                        mListener.onFragmentInteraction(OnFragmentInteractionListener.ACTION.START.GAS_USAGE);
                    }
                    break;
                case R.id.cardTotalPower:
                case R.id.cardPowerUsage:
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

        CardView cardPowerUsage = view.findViewById(R.id.cardPowerUsage);
        cardPowerUsage.setOnClickListener(onButtonClicked);

        CardView cardGasUsage = view.findViewById(R.id.cardGasUsage);
        cardGasUsage.setOnClickListener(onButtonClicked);

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
        double currentElectricUsage = 0;

        if(aDevicesInfo.getElecUsageFlowHigh() > 0){
            currentElectricUsage = aDevicesInfo.getElecUsageFlowHigh();
        } else if(aDevicesInfo.getElecUsageFlowLow() > 0){
            currentElectricUsage = aDevicesInfo.getElecUsageFlowLow();
        } else if(aDevicesInfo.getElecUsageFlow() > 0){
            currentElectricUsage = aDevicesInfo.getElecUsageFlow();
        }

        setPowerMeter(currentElectricUsage, 0);
        setGasMeter(aDevicesInfo.getGasUsed());
    }

    @Override
    public void onTemperatureChanged(CurrentUsageInfo aCurrentUsageInfo) {
        isUpdatingUI2 = true;
        AppSettings.getInstance().setCurrentUsageInfoAvailable(true);

        setPowerMeter(
                aCurrentUsageInfo.getPowerUsage().getValue(),
                aCurrentUsageInfo.getPowerUsage().getAvgValue()
        );
        setGasMeter(
                aCurrentUsageInfo.getGasUsage().getValue()
        );

        isUpdatingUI2 = false;
        if (!isUpdatingUI) setRefreshing(false);
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

        if (aThermostatInfo.getErrorFound() != 255) {
            // TODO show fragment with error
        }

        if (aThermostatInfo.getOtCommError() > 0) {
            // TODO show fragment with error
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
            //noinspection HardCodedStringLiteral
            if (AppSettings.getInstance().whatValueToUseOnNextProgram().equals("Temperature")){
                Object[] args = {
                        simpleDateFormat.format(aThermostatInfo.getNextTime()),
                        String.format(Locale.getDefault(), getString(R.string.graph_temp_formatted), aThermostatInfo.getNextSetpoint() / 100)
                };
                MessageFormat fmt = new MessageFormat(getString(R.string.controls_nextProgramValue));
                txtvNextProgram.setText(fmt.format(args));
            } else {
                Object[] args = {
                        simpleDateFormat.format(aThermostatInfo.getNextTime()),
                        aThermostatInfo.getNextStateString(context)
                };
                MessageFormat fmt = new MessageFormat(getString(R.string.controls_nextProgramValue));
                txtvNextProgram.setText(fmt.format(args));
            }
        } else {
            // No Toon temperature programming used
            text = String.format(
                    Locale.getDefault(),
                    getString(R.string.graph_temp_formatted),
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
}