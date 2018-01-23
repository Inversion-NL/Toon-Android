package com.toonapps.toon.data;

import com.toonapps.toon.entity.DeviceInfo;
import com.toonapps.toon.entity.ThermostatInfo;
import com.google.gson.Gson;

public class Converter {

    public static ResponseData convertFromTemperature(String aJson){
        Gson gson = new Gson();
        ResponseData responseData = new ResponseData();

        ThermostatInfo thermostatInfo = gson.fromJson(aJson, ThermostatInfo.class);
        responseData.setThermostatInfo(thermostatInfo);

        return responseData;
    }

    public static ResponseData convertFromDeviceInfo(String aJson) {
        Gson gson = new Gson();
        ResponseData responseData = new ResponseData();

        DeviceInfo devicesInfo = gson.fromJson(aJson, DeviceInfo.class);
        responseData.setDeviceInfo(devicesInfo);

        return responseData;
    }
}
