package com.toonapps.toon.entity;

import com.google.gson.annotations.SerializedName;

@SuppressWarnings({"unused", "HardCodedStringLiteral"})
public class DeviceInfo {
    @SerializedName("dev_settings_device")
    private DeviceSettings devSettings;

    @SerializedName(value="dev_2", alternate={"dev_3"})
    private Device device;

    @SerializedName(value="dev_2.1", alternate={"dev_3.1"})
    private GasDevice gasDevice;

    @SerializedName(value="dev_2.2", alternate={"dev_3.2"})
    private PowerDevice powerDevice1;

    @SerializedName(value="dev_2.3", alternate={"dev_3.3"})
    private PowerDevice powerDevice2;

    @SerializedName(value="dev_2.4", alternate={"dev_3.4"})
    private PowerDevice powerDevice3;

    @SerializedName(value="dev_2.5", alternate={"dev_3.5"})
    private PowerDevice powerDevice4;

    @SerializedName(value="dev_2.6", alternate={"dev_3.6"})
    private PowerDevice powerDevice5;

    public double getGasUsed(){
        return gasDevice.CurrentGasFlow / 100;
    }

    public double getGasUsedQuantity(){
        return gasDevice.CurrentGasQuantity / 1000;
    }

    public double getElecUsageFlow(){
        return powerDevice1.CurrentElectricityFlow;
    }

    public double getElecUsageQuantity(){
        return powerDevice1.CurrentElectricityQuantity;
    }

    public double getElecUsageFlowLow(){
        return powerDevice4.CurrentElectricityFlow;
    }

    public double getElecUsageFlowLowQuantity() {
        return powerDevice4.CurrentElectricityQuantity / 1000;
    }

    public double getElecUsageFlowHigh(){
        return powerDevice2.CurrentElectricityFlow;
    }

    public double getElecUsageFlowHighQuantity(){
        return powerDevice2.CurrentElectricityQuantity / 1000;
    }

    public double getElecProdFlowLow(){
        return powerDevice5.CurrentElectricityFlow;
    }

    public double getElecProdFlowLowQuantity(){
        return powerDevice5.CurrentElectricityQuantity / 1000;
    }

    public double getElecProdFlowHigh(){
        return powerDevice3.CurrentElectricityFlow;
    }

    public double getElecProdFlowHighQuantity(){
        return powerDevice3.CurrentElectricityQuantity / 1000;
    }

    public class DeviceSettings {
        protected String uuid;
        protected String name;
        protected String internalAddress;
        protected String type;
        protected int supportsCrc;
        protected String location;
    }

    private class Device extends DeviceSettings {
        private String ccList;
        private String supportedCC;
        private int IsConnected;
        private String DeviceName;
        private int HealthValue;
    }

    private class GasDevice extends DeviceSettings {
        private double CurrentGasFlow;
        private double CurrentGasQuantity;
    }

    private class PowerDevice extends DeviceSettings {
        private double CurrentElectricityFlow;
        private double CurrentElectricityQuantity;
    }
}