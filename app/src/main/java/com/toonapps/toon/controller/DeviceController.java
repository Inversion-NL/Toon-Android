package com.toonapps.toon.controller;

import com.toonapps.toon.data.IRestClientResponseHandler;
import com.toonapps.toon.data.ResponseData;
import com.toonapps.toon.data.RestClient;
import com.toonapps.toon.entity.DeviceInfo;

import java.util.ArrayList;
import java.util.List;

public class DeviceController implements IRestClientResponseHandler {

    private RestClient restClient;
    private List<IDeviceListener> devicesListenerList;

    private static DeviceController instance = null;

    private DeviceController() {
        restClient = new RestClient(this);
        devicesListenerList = new ArrayList<>();
    }

    public static DeviceController getInstance() {
        if(instance == null) {
            instance = new DeviceController();
        }
        return instance;
    }

    public void updateDeviceInfo() {
        try {
            restClient.getZWaveDevices();
        } catch (Exception e) {
            onError(e);
        }
    }

    public void subscribe(IDeviceListener aListener){
        devicesListenerList.add(aListener);
    }

    public void unsubscribe(IDeviceListener aListener){
        devicesListenerList.remove(aListener);
    }

    private void onDevicesUpdated(DeviceInfo aDeviceInfo){
        for(IDeviceListener aListener : devicesListenerList){
            aListener.onDeviceInfoChanged(aDeviceInfo);
        }
    }

    private void onError(Exception e){
        for(IDeviceListener aListener : devicesListenerList){
            aListener.onDeviceError(e);
        }
    }

    @Override
    public void onResponse(ResponseData aResponse) {
        if (aResponse != null) onDevicesUpdated(aResponse.getDeviceInfo());
        else onError(new NullPointerException("Device data response is null"));
    }

    @Override
    public void onResponseError(Exception e) {
        onError(e);
    }
}
