package com.toonapps.toon.controller;

import com.toonapps.toon.entity.DeviceInfo;

public interface IDeviceListener {

    void onDeviceInfoChanged(DeviceInfo aDevicesInfo);
    void onDeviceError(Exception e);
}