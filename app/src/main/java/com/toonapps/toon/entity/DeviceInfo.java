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

import com.google.gson.annotations.SerializedName;

@SuppressWarnings({"unused", "HardCodedStringLiteral"})

/*
    x.1 = gas
    x.2 (imported analog elec measured)
    x.3(PV measured zon op toon)
    x.4(P1 imported elec) NT (Normal Tarif)
    x.5(P1 exported elec) NT (Normal Tarif)
    x.6(P1 imported elec) LT (lower Tarif)
    x.7(P1 exported elec) LT (lower Tarif)
    x.8 city heat
    x.9 water meter

    als je P1 hebt aangesloten is x.2 altijd NaN

    owh en powerUsage is dus, indien zon op toon is geconfigureed, gecalculeerd
    en dus niet wat de P1 zegt wat geimporteerd is

    powerUsage = (P1 powerUsage) +  (PV kWh meter power generated) - (P1 powerProduction)

    en normaal gesproken is altijd een van beide (P1 powerusage of P1 powerproduction) 0 en de andere een waarde (je importeerd of produceerd volgens de P1 meter, nooit tegelijk)

    Ook bij 3 fase is de totaal altijd een van de twee 0. Er zijn een tijdje foute slimme meters geweest die dat fout deden bij 3 fase.
    De afspraak is namelijk dat de slimme meter intern de fases moet optellen en productie en verbruik tegen elkaar moet afwegen en dat pas rapporteren.
    als je dus op fase 1 3000 watt produceerd en op fase 2 1000 watt verbruikt dan is je netto levering 2000 watt en netto import 0 watt  (ook al importeer je 1000 watt)
 */

public class DeviceInfo {
    @SerializedName("dev_settings_device")
    private DeviceSettings devSettings;

    @SerializedName(value="dev_2", alternate={"dev_3", "dev_4", "dev_5", "dev_6", "dev_7", "dev_8", "dev_9", "dev_10", "dev_11", "dev_12", "dev_13", "dev_14", "dev_15", "dev_16", "dev_17", "dev_19", "dev_20"})
    private Device device;

    @SerializedName(value="dev_2.1", alternate={"dev_3.1", "dev_4.1", "dev_5.1", "dev_6.1", "dev_7.1", "dev_8.1", "dev_9.1", "dev_10.1", "dev_11.1", "dev_12.1", "dev_13.1", "dev_14.1", "dev_15.1", "dev_16.1", "dev_17.1", "dev_19.1", "dev_20.1"})
    private GasDevice gasDevice;

    @SerializedName(value="dev_2.2", alternate={"dev_3.2", "dev_4.2", "dev_5.2", "dev_6.2", "dev_7.2", "dev_8.2", "dev_9.2", "dev_10.2", "dev_11.2", "dev_12.2", "dev_13.2", "dev_14.2", "dev_15.2", "dev_16.2", "dev_17.2", "dev_19.2", "dev_20.2"})
    private PowerDevice powerDevice1;

    @SerializedName(value="dev_2.3", alternate={"dev_3.3", "dev_4.3", "dev_5.3", "dev_6.3", "dev_7.3", "dev_8.3", "dev_9.3", "dev_10.3", "dev_11.3", "dev_12.3", "dev_13.3", "dev_14.3", "dev_15.3", "dev_16.3", "dev_17.3", "dev_19.3", "dev_20.3"})
    private PowerDevice powerDevice2;

    @SerializedName(value="dev_2.4", alternate={"dev_3.4", "dev_4.4", "dev_5.4", "dev_6.4", "dev_7.4", "dev_8.4", "dev_9.4", "dev_10.4", "dev_11.4", "dev_12.4", "dev_13.4", "dev_14.4", "dev_15.4", "dev_16.4", "dev_17.4", "dev_19.4", "dev_20.4"})
    private PowerDevice powerDevice3;

    @SerializedName(value="dev_2.5", alternate={"dev_3.5", "dev_4.5", "dev_5.5", "dev_6.5", "dev_7.5", "dev_8.5", "dev_9.5", "dev_10.5", "dev_11.5", "dev_12.5", "dev_13.5", "dev_14.5", "dev_15.5", "dev_16.5", "dev_17.5", "dev_19.5", "dev_20.5"})
    private PowerDevice powerDevice4;

    @SerializedName(value="dev_2.6", alternate={"dev_3.6", "dev_4.6", "dev_5.6", "dev_6.6", "dev_7.6", "dev_8.6", "dev_9.6", "dev_10.6", "dev_11.6", "dev_12.6", "dev_13.6", "dev_14.6", "dev_15.6", "dev_16.6", "dev_17.6", "dev_19.6", "dev_20.6"})
    private PowerDevice powerDevice5;

    @SerializedName(value="dev_2.7", alternate={"dev_3.7", "dev_4.7", "dev_5.7", "dev_6.7", "dev_7.7", "dev_8.7", "dev_9.7", "dev_10.7", "dev_11.7", "dev_12.7", "dev_13.7", "dev_14.7", "dev_15.7", "dev_16.7", "dev_17.7", "dev_19.7", "dev_20.7"})
    private PowerDevice powerDevice6;

    @SerializedName(value="dev_2.8", alternate={"dev_3.8", "dev_4.8", "dev_5.8", "dev_6.8", "dev_7.8", "dev_8.8", "dev_9.8", "dev_10.8", "dev_11.8", "dev_12.8", "dev_13.8", "dev_14.8", "dev_15.8", "dev_16.8", "dev_17.8", "dev_19.8", "dev_20.8"})
    private HeatDevice heatDevice;

    public double getGasUsed(){
        return gasDevice.CurrentGasFlow;
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
        private String[] nodeFlags;
        protected String location;
        private String DeviceName;
    }

    private class Device extends DeviceSettings {
        private String ccList;
        private String supportedCC;
        private int IsConnected;
        private int HealthValue;
        private String CurrentSensorStatus;
    }

    private class GasDevice extends DeviceSettings {
        private double CurrentGasFlow;
        private double CurrentGasQuantity;
    }

    private class PowerDevice extends DeviceSettings {
        private double CurrentElectricityFlow;
        private double CurrentElectricityQuantity;
    }

    private class HeatDevice extends DeviceSettings {
        private double CurrentHeatQuantity;
    }
}