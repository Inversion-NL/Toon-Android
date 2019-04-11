package com.toonapps.toon.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.toonapps.toon.R;
import com.toonapps.toon.controller.DeviceController;
import com.toonapps.toon.controller.IDeviceListener;
import com.toonapps.toon.controller.ITemperatureListener;
import com.toonapps.toon.controller.TemperatureController;
import com.toonapps.toon.entity.CurrentUsageInfo;
import com.toonapps.toon.entity.DeviceInfo;
import com.toonapps.toon.entity.ThermostatInfo;
import com.toonapps.toon.helper.AppSettings;
import com.toonapps.toon.helper.ErrorMessage;
import com.toonapps.toon.helper.TimerHelper;

import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatToggleButton;
import androidx.appcompat.widget.SwitchCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

public class MainActivity extends AppCompatActivity implements ITemperatureListener, IDeviceListener {

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
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm"); // TODO internationalize date and time

    //prevent toggle executing onCheckedChanged
    private boolean isUpdatingUI = false;
    private boolean isUpdatingUI2 = false;
    private int REQUEST_CODE_INTRO = 100;
    private SwipeRefreshLayout refreshLayout;
    private TimerHelper timerHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setResources();
        AppSettings.getInstance().initialize(this.getApplicationContext());

        if (AppSettings.getInstance().isFirstStart()) {
            Intent intent = new Intent(this, ConnectionWizardActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivityForResult(intent, REQUEST_CODE_INTRO);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!AppSettings.getInstance().isFirstStart()) {
            TemperatureController.getInstance().subscribe(this);
            DeviceController.getInstance().subscribe(this);
            updateData(false);
            if (AppSettings.getInstance().useAutoRefresh()) setTimer(true);
            else setTimer(false);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        TemperatureController.getInstance().unsubscribe(this);
        DeviceController.getInstance().unsubscribe(this);
        setTimer(false);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_refresh:
                updateData(false);
                break;
            case R.id.action_settings:
                startActivity(new Intent(this, SettingsActivity.class));
                break;
        }

        return true;
    }

    private void updateData(boolean isTimerTask){
        if (!isTimerTask) setRefreshing(true);
        TemperatureController.getInstance().updateCurrentUsageInfo();
        TemperatureController.getInstance().updateThermostatInfo();

        // Try to fetch current usage info once to see if it's available (e.g. rooted Toon)
        if (!AppSettings.getInstance().triedCurrentUsageInfoOnce()
                || !AppSettings.getInstance().isCurrentUsageInfoAvailable()) {
            AppSettings.getInstance().setTriedCurrentUsageInfoOnce(true);
            DeviceController.getInstance().updateDeviceInfo();
        }
    }

    private OnClickListener onButtonClicked = new OnClickListener() {
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
            }
        }
    };

    private void setResources(){

        refreshLayout = findViewById(R.id.refreshLayout);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                updateData(false);
            }
        });

        btnAwayMode = findViewById(R.id.btnAwayMode);
        btnAwayMode.setOnClickListener(onButtonClicked);
        btnSleepMode = findViewById(R.id.btnSleepMode);
        btnSleepMode.setOnClickListener(onButtonClicked);
        btnHomeMode = findViewById(R.id.btnHomeMode);
        btnHomeMode.setOnClickListener(onButtonClicked);
        btnComfortMode = findViewById(R.id.btnComfortMode);
        btnComfortMode.setOnClickListener(onButtonClicked);
        findViewById(R.id.btnPlus).setOnClickListener(onButtonClicked);
        findViewById(R.id.btnMin).setOnClickListener(onButtonClicked);

        txtvTemperature = findViewById(R.id.txtvTemperature);
        txtvSetPoint = findViewById(R.id.txtvSetPoint);
        txtvNextProgram = findViewById(R.id.txtvNextProgram);
        txtvCurrentPowerUse = findViewById(R.id.txtvCurrentPowerUse);
        txtvCurrentGasUse = findViewById(R.id.txtvCurrentGasUse);
        TextView txtvTotalGasUse = findViewById(R.id.txtvTotalGasUse);
        TextView txtvTotalPowerUse = findViewById(R.id.txtvTotalPowerUse);
        imgvCurrentPower = findViewById(R.id.imgvCurrentPower);
        imgvCurrentGas = findViewById(R.id.imgvCurrentGas);

        swIsProgramOn = findViewById(R.id.swIsProgramOn);
        swIsProgramOn.setOnCheckedChangeListener(onCheckedChangeListener);
    }

    private void setTimer(boolean enabled) {

        if (enabled) {
            long refreshRate = AppSettings.getInstance().getAutoRefreshValue();

            if (timerHelper != null) timerHelper.stop();
            timerHelper = new TimerHelper(this, refreshRate, new TimerHelper.TimerInterface() {
                @Override
                public void onTime() {
                    updateData(true);
                }
            });
            timerHelper.start();
        } else {
            if (timerHelper != null) timerHelper.stop();
        }
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
        }
    }

    private CompoundButton.OnCheckedChangeListener onCheckedChangeListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if(!isUpdatingUI && !isUpdatingUI2) TemperatureController.getInstance().setTemperatureProgram(isChecked);
        }
    };

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
        setGasMeter(aDevicesInfo.getGasUsed(), 0);
    }

    @Override
    public void onTemperatureChanged(ThermostatInfo aThermostatInfo) {
        isUpdatingUI = true;

        String text = String.format(Locale.getDefault(), "%.01f°", aThermostatInfo.getCurrentTemp() / 100);
        txtvTemperature.setText(text);

        text = String.format(Locale.getDefault(), getString(R.string.nextProgramTemp), aThermostatInfo.getCurrentSetpoint() / 100);

        txtvSetPoint.setText(text);

        if (aThermostatInfo.getBurnerInfo() > -1) { // Minus one meaning the value isn't retrieved from Toon
            switch(aThermostatInfo.getBurnerInfo()) {

                case 0:
                    findViewById(R.id.imgFire).setVisibility(View.INVISIBLE);
                    break;

                case 1:
                    findViewById(R.id.imgFire).setVisibility(View.VISIBLE);
                    break;
            }
        } else {
            // For non rooted Toon
            if (aThermostatInfo.getCurrentTemp() > aThermostatInfo.getCurrentSetpoint()) // Figure out our own whether the burner is burning or not
                findViewById(R.id.imgFire).setVisibility(View.INVISIBLE);
            else findViewById(R.id.imgFire).setVisibility(View.VISIBLE);
        }

        if (aThermostatInfo.getNextSetpoint() != 0 || aThermostatInfo.getNextTime() != null) {
            if (AppSettings.getInstance().whatValueToUseOnNextProgram().equals("Temperature")){
                Object[] args = {
                        simpleDateFormat.format(aThermostatInfo.getNextTime()),
                        String.format(Locale.getDefault(), "%.01f°", aThermostatInfo.getNextSetpoint() / 100)
                };
                MessageFormat fmt = new MessageFormat(getString(R.string.nextProgramValue));
                txtvNextProgram.setText(fmt.format(args));
            } else {
                Object[] args = {
                        simpleDateFormat.format(aThermostatInfo.getNextTime()),
                        aThermostatInfo.getNextStateString(this)
                };
                MessageFormat fmt = new MessageFormat(getString(R.string.nextProgramValue));
                txtvNextProgram.setText(fmt.format(args));
            }
        } else {
            // No Toon temperature programming used
            text = String.format(
                    Locale.getDefault(),
                    getString(R.string.nextProgramTemp),
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
    public void onTemperatureChanged(CurrentUsageInfo aCurrentUsageInfo) {
        isUpdatingUI2 = true;
        AppSettings.getInstance().setCurrentUsageInfoAvailable(true);

        setPowerMeter(
                aCurrentUsageInfo.getPowerUsage().getValue(),
                aCurrentUsageInfo.getPowerUsage().getAvgValue()
        );
        setGasMeter(
                aCurrentUsageInfo.getGasUsage().getValue(),
                aCurrentUsageInfo.getGasUsage().getAvgValue()
        );

        isUpdatingUI2 = false;
        if (!isUpdatingUI) setRefreshing(false);
    }

    private void setGasMeter(double currentGasUsage, double avgGas) {

        String text = String.format(Locale.getDefault(), "%.00f m3", currentGasUsage);
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

        String text = String.format(Locale.getDefault(), "%.00f watt", currentPowerUsage);
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
    public void onTemperatureError(Exception e) {
        String message = ErrorMessage.getInstance(this).getHumanReadableErrorMessage(e);
        Toast.makeText(this, getString(R.string.exception_message_temperature_update_error_txt) + ": " + message, Toast.LENGTH_SHORT).show();
        setRefreshing(false);
    }

    @Override
    public void onDeviceError(Exception e) {
        String message = ErrorMessage.getInstance(this).getHumanReadableErrorMessage(e);
        Toast.makeText(this, getString(R.string.exception_message_device_update_error_txt) + ": " +  message, Toast.LENGTH_SHORT).show();
        setRefreshing(false);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_INTRO) {
            if (resultCode == RESULT_OK) {
                AppSettings.getInstance().setFirstStart(false);
                updateData(false);
            } else {
                // Cancelled the intro. You can then e.g. finish this activity too.
                finish();
            }
        }
    }
}