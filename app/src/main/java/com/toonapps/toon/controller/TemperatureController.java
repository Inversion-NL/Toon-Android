package com.toonapps.toon.controller;

import com.toonapps.toon.data.IRestClientResponseHandler;
import com.toonapps.toon.data.ResponseData;
import com.toonapps.toon.data.RestClient;
import com.toonapps.toon.entity.CurrentUsageInfo;
import com.toonapps.toon.entity.ThermostatInfo;
import com.toonapps.toon.helper.ToonException;

import java.util.ArrayList;
import java.util.List;

public class TemperatureController implements IRestClientResponseHandler {

    private final List<ITemperatureListener> tempListenerList;
    private final RestClient restClient;
    private ThermostatInfo currentThermostatInfo;

    private static TemperatureController instance = null;

    private TemperatureController() {
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
        restClient.getThermostatInfo();
    }

    public void updateCurrentUsageInfo(){
        restClient.getUsageInfo();
    }

    public void setTemperatureHigher(double anAmount){
        if (currentThermostatInfo != null) {
            double setPoint = currentThermostatInfo.getCurrentSetpoint();
            setPoint += (anAmount * 100);
            setTemperature(setPoint);
        } else onError(new NullPointerException());
    }

    public void setTemperatureLower(double anAmount){
        if (currentThermostatInfo != null) {
            double setPoint = currentThermostatInfo.getCurrentSetpoint();
            setPoint -= (anAmount * 100);
            setTemperature(setPoint);
        } else onError(new NullPointerException());
    }

    private void setTemperature(double anAmount){
        restClient.setSetpoint((int)anAmount);
        //updateThermostatInfo();
    }

    public void setTemperatureProgram(boolean isProgramOn){
        restClient.setSchemeState(isProgramOn);
        //updateThermostatInfo();
    }

    public void setTemperatureMode(ThermostatInfo.TemperatureMode aMode){
        int mode = aMode.ordinal();
        restClient.setSchemeTemperatureState(mode);
        //updateThermostatInfo();
    }

    public void subscribe(ITemperatureListener aListener){
        tempListenerList.add(aListener);
    }

    public void unsubscribe(ITemperatureListener aListener){
        tempListenerList.remove(aListener);
    }

    private void onTemperatureUpdated(ThermostatInfo aThermostatInfo){
        for(ITemperatureListener aListener : tempListenerList){
            aListener.onTemperatureChanged(aThermostatInfo);
        }
    }

    private void onTemperatureUpdated(CurrentUsageInfo currentUsageInfo){
        for(ITemperatureListener aListener : tempListenerList){
            aListener.onTemperatureChanged(currentUsageInfo);
        }
    }

    @Override
    public void onResponse(ResponseData aResponse) {
        if (aResponse != null) {
            if (aResponse.getThermostatInfo() != null) {
                // Data retrieved is thermostat information
                currentThermostatInfo = aResponse.getThermostatInfo();
                onTemperatureUpdated(currentThermostatInfo);
            } else if (aResponse.getResultInfo() != null) {
                // Data retrieved is result information
                if (!aResponse.getResultInfo().isSuccess())
                    onError(new ToonException(ToonException.UNHANDLED, null));
                else updateThermostatInfo();
            } else if(aResponse.getCurrentUsageInfo() != null) {
                // Data retrieved is thermostat information
                onTemperatureUpdated(aResponse.getCurrentUsageInfo());
            } else onError(new NullPointerException());
        }
    }

    @Override
    public void onResponseError(Exception e) {
        onError(e);
    }

    private void onError(Exception e){
        for(ITemperatureListener aListener : tempListenerList){
            aListener.onTemperatureError(e);
        }
    }
}
