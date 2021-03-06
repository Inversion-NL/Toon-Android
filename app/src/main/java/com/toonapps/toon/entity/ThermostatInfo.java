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

package com.toonapps.toon.entity;

import android.content.Context;

import com.google.gson.annotations.Expose;
import com.toonapps.toon.R;

import java.util.Date;

@SuppressWarnings("unused")
public class ThermostatInfo {

    public enum TemperatureMode {
        COMFORT,
        HOME,
        SLEEP,
        AWAY,
        VACATION,
        NONE
    }

    @Expose
    private String result;
    @Expose
    private double currentTemp;
    @Expose
    private double currentSetpoint;
    @Expose
    private int currentInternalBoilerSetpoint;
    @Expose
    private int programState;
    @Expose
    private int activeState;
    @Expose
    private int nextProgram;
    @Expose
    private int nextState;
    @Expose
    private long nextTime;
    @Expose
    private int nextSetpoint;
    @Expose
    private int errorFound;
    @SuppressWarnings("FieldCanBeLocal")
    @Expose
    private int burnerInfo = -1;
    @Expose
    private int otCommError;
    @Expose
    private int currentModulationLevel;

    public String getResult() {
        return result;
    }

    public int getCurrentInternalBoilerSetpoint() {
        return currentInternalBoilerSetpoint;
    }

    public int getNextState() {
        return nextState;
    }

    public String getNextStateString(Context mContext) {
        switch (nextState) {
            case 0:
                return mContext.getString(R.string.temperature_setting_comfort);
            case 1:
                return mContext.getString(R.string.temperature_setting_home);
            case 2:
                return mContext.getString(R.string.temperature_setting_sleeping);
            case 3:
                return mContext.getString(R.string.temperature_setting_away);
            case 4:
                return mContext.getString(R.string.temperature_setting_vacation);
            default:
                return "";
        }
    }

    public int getErrorFound() {
        return errorFound;
    }

    public int getBurnerInfo() {
        return burnerInfo;
    }

    public int getOtCommError() {
        return otCommError;
    }

    public int getCurrentModulationLevel() {
        return currentModulationLevel;
    }

    public double getCurrentTemp(){
        return currentTemp;
    }

    public double getCurrentSetpoint(){
        return currentSetpoint;
    }

    public Date getNextTime(){
        if (nextTime != 0) return new Date(nextTime * 1000);
        else return null;
    }

    public double getNextSetpoint(){
        return nextSetpoint;
    }

    public TemperatureMode getCurrentTempMode(){
        switch(activeState){
            case 0:
                return TemperatureMode.COMFORT;
            case 1:
                return TemperatureMode.HOME;
            case 2:
                return TemperatureMode.SLEEP;
            case 3:
                return TemperatureMode.AWAY;
            case 4:
                return TemperatureMode.VACATION;
        }

        return TemperatureMode.NONE;
    }

    public boolean getProgramState(){
        return (programState == 1 || programState == 2);
    }
}