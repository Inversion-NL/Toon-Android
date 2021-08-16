/*
 * Copyright (c) 2021
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

@SuppressWarnings({"unused", "HardCodedStringLiteral"})
public class CurrentUsageInfo {

    private String result;
    private PowerProduction powerProduction;
    private GasUsage gasUsage;
    private PowerUsage powerUsage;
    private Boolean useGasInfoFromDevices;

    public Boolean getUseGasInfoFromDevices() {
        return useGasInfoFromDevices;
    }

    public void setUseGasInfoFromDevices(Boolean useGasInfoFromDevices) {
        this.useGasInfoFromDevices = useGasInfoFromDevices;
    }

    public boolean isSuccess() {
        //noinspection HardCodedStringLiteral
        return result.equals("ok");
    }

    public PowerUsage getPowerUsage(){
        return powerUsage;
    }

    public PowerProduction getPowerProduction() {
        return powerProduction;
    }

    public GasUsage getGasUsage() {
        return gasUsage;
    }

    public class PowerUsage {
        private float value;
        private float avgValue;

        public float getValue() {
            return value;
        }

        public float getAvgValue() {
            return avgValue;
        }
    }

    public class PowerProduction {
        private float value;
        private float avgValue;

        public float getValue() {
            return value;
        }

        public float getAvgValue() {
            return avgValue;
        }
    }

    public class GasUsage {
        private float value;
        private float avgValue;

        public float getValue() {
            return value;
        }

        public float getAvgValue() {
            return avgValue;
        }
    }
}
