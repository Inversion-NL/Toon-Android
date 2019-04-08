package com.toonapps.toon.entity;

import com.google.gson.annotations.Expose;

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
    private int nextSetPoint;
    @Expose
    private int errorFound;
    @Expose
    private int burnerInfo = -1;
    @Expose
    private int otCommError;
    @Expose
    private int currentModulationLevel;

    //TODO Do something with error found, otCommError and currentModulationLevel

    public String getResult() {
        return result;
    }

    public int getCurrentInternalBoilerSetpoint() {
        return currentInternalBoilerSetpoint;
    }

    public int getNextState() {
        return nextState;
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

    public Date getNextProgram(){
        return new Date(nextTime * 1000);
    }

    public double getNextSetPoint(){
        return nextSetPoint;
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