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
import com.toonapps.toon.entity.UsageInfo;

import java.util.ArrayList;
import java.util.List;

public class GasAndElecFlowController implements IRestClientResponseHandler {

    private final List<IGasAndElecFlowListener> gasAndElecFlowListeners;
    private final RestClient restClient;

    private static GasAndElecFlowController instance = null;

    private GasAndElecFlowController() {
        restClient = new RestClient(this);
        gasAndElecFlowListeners = new ArrayList<>();
    }

    public static GasAndElecFlowController getInstance() {
        if(instance == null) {
            instance = new GasAndElecFlowController();
        }
        return instance;
    }

    public void updateElecFlow(long startTime, long endTime){
        try {
            restClient.getElecFlow(startTime, endTime);
        } catch(Exception e){
            onError(e);
        }
    }

    public void getTodayElecNtUsage(){
        try {
            restClient.getTodayElecNtUsage();
        } catch(Exception e){
            onError(e);
        }
    }

    public void getTodayElecLtUsage(){
        try {
            restClient.getTodayElecLtUsage();
        } catch(Exception e){
            onError(e);
        }
    }

    public void getTodayGasUsage(){
        try {
            restClient.getTodayGasUsage();
        } catch(Exception e){
            onError(e);
        }
    }

    public void updateGasFlow(long startTime, long endTime){
        try {
            restClient.getGasFlow(startTime, endTime);
        } catch(Exception e){
            onError(e);
        }
    }

    public void subscribe(IGasAndElecFlowListener aListener){
        gasAndElecFlowListeners.add(aListener);
    }

    public void unsubscribe(IGasAndElecFlowListener aListener){
        gasAndElecFlowListeners.remove(aListener);
    }

    private void onElecUsageInfoChanged(UsageInfo aUsageInfo){
        for(IGasAndElecFlowListener aListener : gasAndElecFlowListeners){
            aListener.onUsageChanged(aUsageInfo);
        }
    }

    private void onError(Exception e){
        for(IGasAndElecFlowListener aListener : gasAndElecFlowListeners){
            aListener.onUsageError(e);
        }
    }

    @Override
    public void onResponse(ResponseData aResponse) {
        if (aResponse != null) if (aResponse.getUsageInfo() != null) {
            onElecUsageInfoChanged(aResponse.getUsageInfo());
        } else onError(new NullPointerException());
    }

    @Override
    public void onResponseError(Exception e) {
        onError(e);
    }
}