package com.toonapps.toon.data;

import com.toonapps.toon.entity.DeviceInfo;
import com.toonapps.toon.entity.ThermostatInfo;

public class ResponseData {

    private ThermostatInfo thermostatInfo;
    private DeviceInfo deviceInfo;

    public ResponseData(){
    }

    public void setThermostatInfo(ThermostatInfo aThermostatInfo){
        thermostatInfo = aThermostatInfo;
    }

    public ThermostatInfo getThermostatInfo(){
        return thermostatInfo;
    }

    public void setDeviceInfo(DeviceInfo aDeviceInfo){
        deviceInfo = aDeviceInfo;
    }

    public DeviceInfo getDevicesInfo(){
        return deviceInfo;
    }
}
