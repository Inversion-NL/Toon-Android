package com.toonapps.toon.data;

import com.google.gson.Gson;
import com.toonapps.toon.entity.CurrentUsageInfo;
import com.toonapps.toon.entity.DeviceInfo;
import com.toonapps.toon.entity.ResultInfo;
import com.toonapps.toon.entity.ThermostatInfo;

class Converter {

    static ResponseData convertFromTemperature(String aJson) {
        Gson gson = new Gson();
        ResponseData responseData = new ResponseData();

        ThermostatInfo thermostatInfo = gson.fromJson(aJson, ThermostatInfo.class);
        responseData.setThermostatInfo(thermostatInfo);

        return responseData;
    }

    static ResponseData convertFromDeviceInfo(String aJson) throws com.google.gson.JsonSyntaxException , IllegalStateException{
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

    static ResponseData convertCurrentUsageData(String aJson) throws com.google.gson.JsonSyntaxException , IllegalStateException{
        Gson gson = new Gson();
        ResponseData responseData = new ResponseData();

        CurrentUsageInfo currentUsageInfo = gson.fromJson(aJson, CurrentUsageInfo.class);
        responseData.setCurrentUsageInfo(currentUsageInfo);

        return responseData;
    }
}