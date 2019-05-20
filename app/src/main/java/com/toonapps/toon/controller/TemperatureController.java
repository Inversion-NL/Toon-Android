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

    private List<ITemperatureListener> tempListenerList;
    private RestClient restClient;
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
        try {
            restClient.getThermostatInfo();
        } catch(Exception e){
            onError(e);
        }
    }

    public void updateCurrentUsageInfo(){
        try {
            restClient.getUsageInfo();
        } catch (Exception e){
            onError(e);
        }
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
            updateThermostatInfo();
        }catch(Exception e){
            onError(e);
        }
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

    private void onError(Exception e){
        for(ITemperatureListener aListener : tempListenerList){
            aListener.onTemperatureError(e);
        }
    }

    @Override
    public void onResponse(ResponseData aResponse) {
        if (aResponse != null) if (aResponse.getThermostatInfo() != null) {
            // Data retrieved is thermostat information
            currentThermostatInfo = aResponse.getThermostatInfo();
            onTemperatureUpdated(currentThermostatInfo);
        } else if (aResponse.getResultInfo() != null) {
            // Data retrieved is result information
            if (!aResponse.getResultInfo().isSuccess())
                onError(new ToonException(ToonException.UNHANDLED));
        } else if(aResponse.getCurrentUsageInfo() != null) {
            // Data retrieved is thermostat information
            onTemperatureUpdated(aResponse.getCurrentUsageInfo());
        } else onError(new NullPointerException());
    }

    @Override
    public void onResponseError(Exception e) {
        onError(e);
    }
}
