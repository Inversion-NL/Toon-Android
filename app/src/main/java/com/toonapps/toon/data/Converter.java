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

package com.toonapps.toon.data;

import com.google.gson.Gson;
import com.toonapps.toon.entity.CurrentUsageInfo;
import com.toonapps.toon.entity.DeviceInfo;
import com.toonapps.toon.entity.ResultInfo;
import com.toonapps.toon.entity.ThermostatInfo;

import org.json.JSONException;
import org.json.JSONObject;

class Converter {

    static ResponseData convertFromTemperature(String aJson) {
        Gson gson = new Gson();
        ResponseData responseData = new ResponseData();

        try {

            ThermostatInfo thermostatInfo = gson.fromJson(aJson, ThermostatInfo.class);
            responseData.setThermostatInfo(thermostatInfo);
            return responseData;

        } catch (Exception e) {

            e.printStackTrace();
            return responseData;
        }
    }

    static ResponseData convertCurrentUsageData(String aJson) throws com.google.gson.JsonSyntaxException , IllegalStateException{
        Gson gson = new Gson();
        ResponseData responseData = new ResponseData();
        CurrentUsageInfo currentUsageInfo;

        try {
            currentUsageInfo = gson.fromJson(aJson, CurrentUsageInfo.class);
        } catch (Exception e) {
            e.printStackTrace();
            return responseData;
        }

        try {
            // Test if gasUsage is available

            JSONObject obj = new JSONObject(aJson);
            @SuppressWarnings("HardCodedStringLiteral")
            JSONObject gasUsage = obj.getJSONObject("gasUsage");

            //noinspection HardCodedStringLiteral
            if (gasUsage.getString("value").equals("null")) {
                currentUsageInfo.setUseGasInfoFromDevices(true);
            } else currentUsageInfo.setUseGasInfoFromDevices(false);
        } catch (JSONException e) {
            e.printStackTrace();
            currentUsageInfo.setUseGasInfoFromDevices(false);
        }

        responseData.setCurrentUsageInfo(currentUsageInfo);
        return responseData;
    }

    static ResponseData convertFromDeviceInfo(String aJson) {
        Gson gson = new Gson();
        ResponseData responseData = new ResponseData();

        DeviceInfo devicesInfo = gson.fromJson(aJson, DeviceInfo.class);
        responseData.setDeviceInfo(devicesInfo);

        return responseData;
    }

    static ResponseData convertResultData(String aJson) throws com.google.gson.JsonSyntaxException , IllegalStateException{
        Gson gson = new Gson();
        ResponseData responseData = new ResponseData();

        ResultInfo resultInfo = gson.fromJson(aJson, ResultInfo.class);
        responseData.setResultInfo(resultInfo);

        return responseData;
    }
}