package com.toonapps.toon.data;

import com.google.gson.Gson;
import com.toonapps.toon.entity.DeviceInfo;
import com.toonapps.toon.entity.ThermostatInfo;

public class Converter {

    public static ResponseData convertFromTemperature(String aJson) {
        Gson gson = new Gson();
        ResponseData responseData = new ResponseData();

        try {
            ThermostatInfo thermostatInfo = gson.fromJson(aJson, ThermostatInfo.class);
            responseData.setThermostatInfo(thermostatInfo);

            return responseData;
        } catch (Exception e) {
            return null;
        }
    }

    public static ResponseData convertFromDeviceInfo(String aJson) throws com.google.gson.JsonSyntaxException , IllegalStateException{
        Gson gson = new Gson();
        ResponseData responseData = new ResponseData();
        try {
            DeviceInfo devicesInfo = gson.fromJson(aJson, DeviceInfo.class);
            responseData.setDeviceInfo(devicesInfo);

            return responseData;
        } catch (Exception e) {
            return null;
        }
    }
}