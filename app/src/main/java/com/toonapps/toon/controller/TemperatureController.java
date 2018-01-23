package com.toonapps.toon.controller;

import android.util.Log;

import com.toonapps.toon.data.IRestClientResponseHandler;
import com.toonapps.toon.data.ResponseData;
import com.toonapps.toon.data.RestClient;
import com.toonapps.toon.entity.ThermostatInfo;

import java.util.ArrayList;
import java.util.List;

public class TemperatureController implements IRestClientResponseHandler {

    private List<ITemperatureListener> tempListenerList;
    private RestClient restClient;
    private ThermostatInfo currentThermostatInfo;

    private static TemperatureController instance = null;
    protected TemperatureController() {
        restClient = new RestClient(this);
        tempListenerList = new ArrayList<>();
    }

    public static TemperatureController getInstance() {
        if(instance == null) {
            instance = new TemperatureController();
        }
        return instance;
    }

    public ThermostatInfo getCurrentThermostatInfo(){
        return currentThermostatInfo;
    }

    public void updateThermostatInfo(){
        try {
            restClient.getThermostatInfo();
        }catch(Exception e){
            Log.d("Exception occured", e.getMessage());
        }
    }

    public void setTemperatureHigher(double anAmount){
        double setpoint = currentThermostatInfo.getCurrentSetpoint();
        setpoint += (anAmount * 100);

        setTemperature(setpoint);
    }

    public void setTemperatureLower(double anAmount){
        double setpoint = currentThermostatInfo.getCurrentSetpoint();
        setpoint -= (anAmount * 100);

        setTemperature(setpoint);
    }

    private void setTemperature(double anAmount){
        restClient.setSetpoint((int)anAmount);

        updateThermostatInfo();
    }

    public void setTemperatureProgram(boolean isProgramOn){
        restClient.setSchemeState(isProgramOn);

        updateThermostatInfo();
    }

    public void setTemperatureMode(ThermostatInfo.TemperatureMode aMode){
        int mode = aMode.ordinal();

        try {
            restClient.setSchemeTemperatureState(mode);
        }catch(Exception e){
            Log.d("Exception occured", e.getMessage());
        }

        updateThermostatInfo();
    }

    public void subscribe(ITemperatureListener aListener){
        tempListenerList.add(aListener);
    }

    public void unsubscribe(ITemperatureListener aListener){
        tempListenerList.remove(aListener);
    }

    public void onTemperatureUpdated(ThermostatInfo aThermostatInfo){
        for(ITemperatureListener aListener : tempListenerList){
            aListener.onTemperatureChanged(aThermostatInfo);
        }
    }

    @Override
    public void onResponse(ResponseData aResponse) {
        if(aResponse.getThermostatInfo() != null) {
            currentThermostatInfo = aResponse.getThermostatInfo();

            onTemperatureUpdated(currentThermostatInfo);
        }
    }
}
