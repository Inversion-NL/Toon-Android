package com.toonapps.toon.data;

import com.toonapps.toon.entity.CurrentUsageInfo;
import com.toonapps.toon.entity.DeviceInfo;
import com.toonapps.toon.entity.ResultInfo;
import com.toonapps.toon.entity.ThermostatInfo;

public class ResponseData {

    private ThermostatInfo thermostatInfo;
    private DeviceInfo deviceInfo;
    private ResultInfo resultInfo;
    private CurrentUsageInfo currentUsageInfo;

    ResponseData(){
    }

    public CurrentUsageInfo getCurrentUsageInfo() {
        return currentUsageInfo;
    }

    public void setCurrentUsageInfo(CurrentUsageInfo currentUsageInfo) {
        this.currentUsageInfo = currentUsageInfo;
    }

    public ResultInfo getResultInfo() {
        return resultInfo;
    }

    public void setResultInfo(ResultInfo resultInfo) {
        this.resultInfo = resultInfo;
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

    public DeviceInfo getDeviceInfo(){
        return deviceInfo;
    }
}