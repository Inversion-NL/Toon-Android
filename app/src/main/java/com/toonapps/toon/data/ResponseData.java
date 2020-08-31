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

import com.toonapps.toon.entity.CurrentUsageInfo;
import com.toonapps.toon.entity.DeviceInfo;
import com.toonapps.toon.entity.ResultInfo;
import com.toonapps.toon.entity.ThermostatInfo;
import com.toonapps.toon.entity.UsageInfo;

public class ResponseData {

    private ThermostatInfo thermostatInfo;
    private DeviceInfo deviceInfo;
    private ResultInfo resultInfo;
    private CurrentUsageInfo currentUsageInfo;
    private UsageInfo usageInfo;

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

    public void setUsageInfo(UsageInfo usageInfo) {
        this.usageInfo = usageInfo;
    }

    public UsageInfo getUsageInfo() {
        return usageInfo;
    }
}