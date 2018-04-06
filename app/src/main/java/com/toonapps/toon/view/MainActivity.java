package com.toonapps.toon.view;

import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import com.toonapps.toon.R;
import com.toonapps.toon.controller.DeviceController;
import com.toonapps.toon.controller.IDeviceListener;
import com.toonapps.toon.controller.ITemperatureListener;
import com.toonapps.toon.controller.TemperatureController;
import com.toonapps.toon.entity.DeviceInfo;
import com.toonapps.toon.entity.ThermostatInfo;
import com.toonapps.toon.helper.AppSettings;

import java.text.DecimalFormat;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;

public class MainActivity extends AppCompatActivity implements ITemperatureListener, IDeviceListener {

    private TextView txtvTemperature;
    private TextView txtvSetPoint;
    private TextView txtvNextProgram;
    private TextView txtvCurrentPowerUse;
    private TextView txtvCurrentGasUse;
    private TextView txtvTotalPowerUse;
    private TextView txtvTotalGasUse;
    private Button btnAwayMode;
    private Button btnSleepMode;
    private Button btnCozyMode;
    private Button btnHomeMode;
    private Switch swIsProgramOn;
    private ImageView imgvCurrentPower;
    private ImageView imgvCurrentGas;
    private DecimalFormat decimalFormat = new DecimalFormat("#0.00");
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");

    private AlphaAnimation animButtonClick = new AlphaAnimation(1F, 0.8F);
    //prevent toggle executing onCheckedChanged
    private boolean isUpdatingUI = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setResources();
        AppSettings.getInstance().initialize(this.getApplicationContext());

        TemperatureController.getInstance().subscribe(this);
        DeviceController.getInstance().subscribe(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateData();
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
        try {
            TemperatureController.getInstance().updateThermostatInfo();
            DeviceController.getInstance().updateDeviceInfo();
        } catch(Exception e){
            // TODO Notify user there was a problem fetching data
        }
    }

    private void setResources(){
        btnAwayMode = ((Button)findViewById(R.id.btnAwayMode));
        btnAwayMode.setOnClickListener(onButtonClicked);
        btnSleepMode = ((Button)findViewById(R.id.btnSleepMode));
        btnSleepMode.setOnClickListener(onButtonClicked);
        btnHomeMode = ((Button)findViewById(R.id.btnHomeMode));
        btnHomeMode.setOnClickListener(onButtonClicked);
        btnCozyMode = ((Button)findViewById(R.id.btnCozyMode));
        btnCozyMode.setOnClickListener(onButtonClicked);
        findViewById(R.id.btnPlus).setOnClickListener(onButtonClicked);
        findViewById(R.id.btnMin).setOnClickListener(onButtonClicked);

        txtvTemperature = (TextView)findViewById(R.id.txtvTemperature);
        txtvSetPoint = (TextView)findViewById(R.id.txtvSetPoint);
        txtvNextProgram = (TextView)findViewById(R.id.txtvNextProgram);
        txtvCurrentPowerUse = (TextView)findViewById(R.id.txtvCurrentPowerUse);
        txtvCurrentGasUse = (TextView)findViewById(R.id.txtvCurrentGasUse);
        txtvTotalPowerUse = (TextView)findViewById(R.id.txtvTotalPowerUse);
        txtvTotalGasUse = (TextView)findViewById(R.id.txtvTotalGasUse);
        imgvCurrentPower = (ImageView)findViewById(R.id.imgvCurrentPower);
        imgvCurrentGas = (ImageView)findViewById(R.id.imgvCurrentGas);

        swIsProgramOn = (Switch)findViewById(R.id.swIsProgramOn);
        swIsProgramOn.setOnCheckedChangeListener(onCheckedChangeListener);
    }

    private OnClickListener onButtonClicked = new OnClickListener() {
        public void onClick(View v) {
        v.startAnimation(animButtonClick);
        switch(v.getId()){
            case R.id.btnAwayMode:
                TemperatureController.getInstance().setTemperatureMode(ThermostatInfo.TemperatureMode.AWAY);
                break;
            case R.id.btnSleepMode:
                TemperatureController.getInstance().setTemperatureMode(ThermostatInfo.TemperatureMode.SLEEP);
                break;
            case R.id.btnHomeMode:
                TemperatureController.getInstance().setTemperatureMode(ThermostatInfo.TemperatureMode.HOME);
                break;
            case R.id.btnCozyMode:
                TemperatureController.getInstance().setTemperatureMode(ThermostatInfo.TemperatureMode.COMFORT);
                break;
            case R.id.btnPlus:
                TemperatureController.getInstance().setTemperatureHigher(0.5);
                break;
            case R.id.btnMin:
                TemperatureController.getInstance().setTemperatureLower(0.5);
                break;
        }
        }
    };

    private CompoundButton.OnCheckedChangeListener onCheckedChangeListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if(!isUpdatingUI) {
                TemperatureController.getInstance().setTemperatureProgram(isChecked);
            }
        }
    };

    @Override
    public void onTemperatureChanged(ThermostatInfo aThermostatInfo) {
        isUpdatingUI = true;

        txtvTemperature.setText(decimalFormat.format(aThermostatInfo.getCurrentTemp() / 100) + "°");
        txtvSetPoint.setText(decimalFormat.format(aThermostatInfo.getCurrentSetpoint() / 100) + "°");

        if(aThermostatInfo.getCurrentTemp() > aThermostatInfo.getCurrentSetpoint()){
            findViewById(R.id.imgFire).setVisibility(View.INVISIBLE);
        }
        else{
            findViewById(R.id.imgFire).setVisibility(View.VISIBLE);
        }

        Object[] args = {
                simpleDateFormat.format(aThermostatInfo.getNextProgram()),
                decimalFormat.format(aThermostatInfo.getNextSetpoint() / 100)
        };
        MessageFormat fmt = new MessageFormat("Om {0} uur \n op {1}° graden");
        txtvNextProgram.setText(fmt.format(args));

        swIsProgramOn.setChecked(aThermostatInfo.getProgramState());
        swIsProgramOn.setText((aThermostatInfo.getProgramState() ? "Programma aan " : "Programma uit "));

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

    private void clearButtonColors(){
        btnAwayMode.setBackgroundColor(ContextCompat.getColor(this, R.color.white));
        btnSleepMode.setBackgroundColor(ContextCompat.getColor(this, R.color.white));
        btnHomeMode.setBackgroundColor(ContextCompat.getColor(this, R.color.white));
        btnCozyMode.setBackgroundColor(ContextCompat.getColor(this, R.color.white));
    }

    @Override
    public void onDeviceInfoChanged(DeviceInfo aDevicesInfo) {
        double currentElecUsage = 0.0;
        double currentGasUsage = 0.0;

        if(aDevicesInfo.getElecUsageFlowHigh() > 0){
            currentElecUsage = aDevicesInfo.getElecUsageFlowHigh();
            txtvCurrentPowerUse.setText(decimalFormat.format(aDevicesInfo.getElecUsageFlowHigh()) + " watt");
        }
        else{
            currentElecUsage = aDevicesInfo.getElecUsageFlowLow();
            txtvCurrentPowerUse.setText(decimalFormat.format(aDevicesInfo.getElecUsageFlowLow()) + " watt");
        }

        if(currentElecUsage >= 0 && currentElecUsage < 50){
            imgvCurrentPower.setImageResource(R.drawable.power1_10);
        }
        else if(currentElecUsage >= 50 && currentElecUsage < 200){
            imgvCurrentPower.setImageResource(R.drawable.power3_10);
        }
        else if(currentElecUsage >= 200 && currentElecUsage < 600){
            imgvCurrentPower.setImageResource(R.drawable.power5_10);
        }
        else if(currentElecUsage >= 600 && currentElecUsage < 1000){
            imgvCurrentPower.setImageResource(R.drawable.power7_10);
        }
        else if(currentElecUsage >= 1000){
            imgvCurrentPower.setImageResource(R.drawable.power9_10);
        }

        currentGasUsage = aDevicesInfo.getGasUsed();
        txtvCurrentGasUse.setText(decimalFormat.format(currentGasUsage) + " m3");

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
}
