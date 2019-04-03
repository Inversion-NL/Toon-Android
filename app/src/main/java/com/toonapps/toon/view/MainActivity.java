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
import com.toonapps.toon.entity.DeviceInfo;
import com.toonapps.toon.entity.ThermostatInfo;
import com.toonapps.toon.helper.AppSettings;
import com.toonapps.toon.helper.ErrorMessage;

import java.text.DecimalFormat;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatToggleButton;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.content.ContextCompat;

public class MainActivity extends AppCompatActivity implements ITemperatureListener, IDeviceListener {

    private TextView txtvTemperature;
    private TextView txtvSetPoint;
    private TextView txtvNextProgram;
    private TextView txtvCurrentPowerUse;
    private TextView txtvCurrentGasUse;
    private AppCompatToggleButton btnAwayMode;
    private AppCompatToggleButton btnSleepMode;
    private AppCompatToggleButton btnCozyMode;
    private AppCompatToggleButton btnHomeMode;
    private SwitchCompat swIsProgramOn;
    private ImageView imgvCurrentPower;
    private ImageView imgvCurrentGas;
    private DecimalFormat decimalFormat = new DecimalFormat("#0.00");
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm"); // TODO internationalize date and time

    //prevent toggle executing onCheckedChanged
    private boolean isUpdatingUI = false;
    private int REQUEST_CODE_INTRO = 100;

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
            updateData();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        TemperatureController.getInstance().unsubscribe(this);
        DeviceController.getInstance().unsubscribe(this);
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
                updateData();
                break;
            case R.id.action_settings:
                startActivity(new Intent(this, SettingsActivity.class));
                break;
        }

        return true;
    }

    private void updateData(){
        TemperatureController.getInstance().updateThermostatInfo();
        DeviceController.getInstance().updateDeviceInfo();
    }

    private void setResources(){
        btnAwayMode = findViewById(R.id.btnAwayMode);
        btnAwayMode.setOnClickListener(onButtonClicked);
        btnSleepMode = findViewById(R.id.btnSleepMode);
        btnSleepMode.setOnClickListener(onButtonClicked);
        btnHomeMode = findViewById(R.id.btnHomeMode);
        btnHomeMode.setOnClickListener(onButtonClicked);
        btnCozyMode = findViewById(R.id.btnCozyMode);
        btnCozyMode.setOnClickListener(onButtonClicked);
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

    private OnClickListener onButtonClicked = new OnClickListener() {
        public void onClick(View v) {

        int tempSetValue = AppSettings.getInstance().getTempSetValue();

        switch(v.getId()){
            case R.id.btnAwayMode:
                TemperatureController.getInstance().setTemperatureMode(ThermostatInfo.TemperatureMode.AWAY);
                setButtonState(ThermostatInfo.TemperatureMode.AWAY);
                break;
            case R.id.btnSleepMode:
                TemperatureController.getInstance().setTemperatureMode(ThermostatInfo.TemperatureMode.SLEEP);
                setButtonState(ThermostatInfo.TemperatureMode.SLEEP);
                break;
            case R.id.btnHomeMode:
                TemperatureController.getInstance().setTemperatureMode(ThermostatInfo.TemperatureMode.HOME);
                setButtonState(ThermostatInfo.TemperatureMode.HOME);
                break;
            case R.id.btnCozyMode:
                TemperatureController.getInstance().setTemperatureMode(ThermostatInfo.TemperatureMode.COMFORT);
                setButtonState(ThermostatInfo.TemperatureMode.COMFORT);
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

    private void setButtonState(ThermostatInfo.TemperatureMode mode) {
        switch (mode) {
            case AWAY:
                btnSleepMode.setChecked(false);
                btnHomeMode.setChecked(false);
                btnCozyMode.setChecked(false);
                break;

            case SLEEP:
                btnAwayMode.setChecked(false);
                btnHomeMode.setChecked(false);
                btnCozyMode.setChecked(false);
                break;

            case HOME:
                btnAwayMode.setChecked(false);
                btnSleepMode.setChecked(false);
                btnCozyMode.setChecked(false);
                break;

            case COMFORT:
                btnAwayMode.setChecked(false);
                btnSleepMode.setChecked(false);
                btnHomeMode.setChecked(false);
                break;
        }
    }

    private CompoundButton.OnCheckedChangeListener onCheckedChangeListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if(!isUpdatingUI) TemperatureController.getInstance().setTemperatureProgram(isChecked);
        }
    };

    @Override
    public void onTemperatureChanged(ThermostatInfo aThermostatInfo) {
        isUpdatingUI = true;

        String tempText = decimalFormat.format(aThermostatInfo.getCurrentTemp() / 100) + "°";
        txtvTemperature.setText(tempText);

        String setPointText = decimalFormat.format(aThermostatInfo.getCurrentSetpoint() / 100) + "°";
        txtvSetPoint.setText(setPointText);

        if(aThermostatInfo.getCurrentTemp() > aThermostatInfo.getCurrentSetpoint())
            findViewById(R.id.imgFire).setVisibility(View.INVISIBLE);
        else findViewById(R.id.imgFire).setVisibility(View.VISIBLE);

        Object[] args = {
                simpleDateFormat.format(aThermostatInfo.getNextProgram()),
                decimalFormat.format(aThermostatInfo.getNextSetPoint() / 100)
        };
        MessageFormat fmt = new MessageFormat(getString(R.string.nextProgramValue));
        txtvNextProgram.setText(fmt.format(args));

        swIsProgramOn.setChecked(aThermostatInfo.getProgramState());
        String followProgramText = getString(R.string.temperature_setting_followProgram);
        String dontFollowProgramText = getString(R.string.temperature_setting_dontFollowProgram);
        swIsProgramOn.setText((aThermostatInfo.getProgramState() ? followProgramText : dontFollowProgramText));

        clearButtonColors();
        switch(aThermostatInfo.getCurrentTempMode()){
            case AWAY:
                btnAwayMode.setBackgroundColor(ContextCompat.getColor(this, R.color.colorPresetAccent));
                break;
            case COMFORT:
                btnCozyMode.setBackgroundColor(ContextCompat.getColor(this, R.color.colorPresetAccent));
                break;
            case SLEEP:
                btnSleepMode.setBackgroundColor(ContextCompat.getColor(this, R.color.colorPresetAccent));
                break;
            case HOME:
                btnHomeMode.setBackgroundColor(ContextCompat.getColor(this, R.color.colorPresetAccent));
                break;
        }

        isUpdatingUI = false;
    }

    @Override
    public void onTemperatureError(Exception e) {
        String message = ErrorMessage.getInstance(this).getHumanReadableErrorMessage(e);
        Toast.makeText(this, getString(R.string.temperature_update_error_txt) + ": " + message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDeviceError(Exception e) {
        String message = ErrorMessage.getInstance(this).getHumanReadableErrorMessage(e);
        Toast.makeText(this, getString(R.string.device_update_error_txt) + ": " +  message, Toast.LENGTH_SHORT).show();
    }

    private void clearButtonColors(){
        btnAwayMode.setBackgroundColor(ContextCompat.getColor(this, R.color.white));
        btnSleepMode.setBackgroundColor(ContextCompat.getColor(this, R.color.white));
        btnHomeMode.setBackgroundColor(ContextCompat.getColor(this, R.color.white));
        btnCozyMode.setBackgroundColor(ContextCompat.getColor(this, R.color.white));
    }

    @Override
    public void onDeviceInfoChanged(DeviceInfo aDevicesInfo) {
        double currentElectricUsage;
        double currentGasUsage;

        String text = "N/A";
        currentElectricUsage = 0;

        if(aDevicesInfo.getElecUsageFlowHigh() > 0){
            currentElectricUsage = aDevicesInfo.getElecUsageFlowHigh();
            text = decimalFormat.format(aDevicesInfo.getElecUsageFlowHigh()) + " watt";
        } else if(aDevicesInfo.getElecUsageFlowLow() > 0){
            currentElectricUsage = aDevicesInfo.getElecUsageFlowLow();
            text = decimalFormat.format(aDevicesInfo.getElecUsageFlowLow()) + " watt";
        } else if(aDevicesInfo.getElecUsageFlow() > 0){
            currentElectricUsage = aDevicesInfo.getElecUsageFlow();
            text = decimalFormat.format(aDevicesInfo.getElecUsageFlow()) + " watt";
        }
        txtvCurrentPowerUse.setText(text);

        if(currentElectricUsage >= 0 && currentElectricUsage < 50){
            imgvCurrentPower.setImageResource(R.drawable.power1_10);
        }
        else if(currentElectricUsage >= 50 && currentElectricUsage < 200){
            imgvCurrentPower.setImageResource(R.drawable.power3_10);
        }
        else if(currentElectricUsage >= 200 && currentElectricUsage < 600){
            imgvCurrentPower.setImageResource(R.drawable.power5_10);
        }
        else if(currentElectricUsage >= 600 && currentElectricUsage < 1000){
            imgvCurrentPower.setImageResource(R.drawable.power7_10);
        }
        else if(currentElectricUsage >= 1000){
            imgvCurrentPower.setImageResource(R.drawable.power9_10);
        }

        currentGasUsage = aDevicesInfo.getGasUsed();
        text = decimalFormat.format(currentGasUsage) + " m3";
        txtvCurrentGasUse.setText(text);

        if(currentGasUsage >= 0.0 && currentGasUsage < 0.50){
            imgvCurrentGas.setImageResource(R.drawable.gas1_10);
        }
        else if(currentGasUsage >= 0.50 && currentGasUsage < 1.0){
            imgvCurrentGas.setImageResource(R.drawable.gas3_10);
        }
        else if(currentGasUsage >= 1.0 && currentGasUsage < 2.0){
            imgvCurrentGas.setImageResource(R.drawable.gas5_10);
        }
        else if(currentGasUsage >= 2.0 && currentGasUsage < 5.0){
            imgvCurrentGas.setImageResource(R.drawable.gas7_10);
        }
        else if(currentGasUsage >= 5.0){
            imgvCurrentGas.setImageResource(R.drawable.gas9_10);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_INTRO) {
            if (resultCode == RESULT_OK) {
                AppSettings.getInstance().setFirstStart(false);
                updateData();
            } else {
                // Cancelled the intro. You can then e.g. finish this activity too.
                finish();
            }
        }
    }
}
