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

package com.toonapps.toon.controller;

import com.toonapps.toon.data.IRestClientResponseHandler;
import com.toonapps.toon.data.ResponseData;
import com.toonapps.toon.data.RestClient;
import com.toonapps.toon.entity.DeviceInfo;

import java.util.ArrayList;
import java.util.List;

public class DeviceController implements IRestClientResponseHandler {

    private final RestClient restClient;
    private final List<IDeviceListener> devicesListenerList;

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

    public void updateZWaveDevices() {
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
