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

    protected DeviceController() {
        restClient = new RestClient(this);
        devicesListenerList = new ArrayList<>();
    }

    public static DeviceController getInstance() {
        if(instance == null) {
            instance = new DeviceController();
        }
        return instance;
    }

    public void updateDeviceInfo(){
        restClient.getZWaveDevices();
    }


    public void subscribe(IDeviceListener aListener){
        devicesListenerList.add(aListener);
    }

    private void onDevicesUpdated(DeviceInfo aDeviceInfo){
        for(IDeviceListener aListener : devicesListenerList){
            aListener.onDeviceInfoChanged(aDeviceInfo);
        }
    }

    @Override
    public void onResponse(ResponseData aResponse) {
        onDevicesUpdated(aResponse.getDevicesInfo());
    }
}
