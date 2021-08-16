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

import com.github.mikephil.charting.data.Entry;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class UsageInfo {

    public interface TIME {
        int PERIOD = 1;
        int TODAY = 2;
    }

    public interface TYPE {
        int GAS = 1;
        int ELEC = 2;
    }

    public interface TARIFF {
        int NULL = 0;
        int NORMAL = 1;
        int LOW = 2;
    }

    private int time;
    private int type;
    private int tariff;

    private final String usageString;

    public UsageInfo(String UsageString, int type, int tariff, int time) {
        this.usageString = UsageString;
        this.time = time;
        this.type = type;
        this.tariff = tariff;
    }

    public int getTime() {
        return time;
    }

    public int getType() {
        return type;
    }

    public int getElectTariffType() {
        return tariff;
    }

    /**
     * Calculates the usage of today
     * by subtracting the meter value on midnight with the current meter value
     * @return the usage of today
     * @throws JSONException when unable to parse
     */
    @SuppressWarnings("HardCodedStringLiteral")
    public float getTodayUsage() throws JSONException {
        JSONArray jArray = toJSONArray(usageString);
        JSONObject jObject;
        String keyString;
        float beginUsage = 0, endUsage = 0;

        try {
            if (jArray.length() > 2) {

                for (int i = 0; i < jArray.length(); i++) {
                    // Find the first non null value
                    jObject = jArray.getJSONObject(i);
                    //noinspection ConstantConditions
                    keyString = (String) jObject.names().get(0);

                    if (!jObject.getString(keyString).equals("null")) {
                        beginUsage = (float) jObject.getInt(keyString);
                        break;
                    }
                }

                for (int i = jArray.length() - 1; i > 0; i--) {
                    // Find the last non null value
                    jObject = jArray.getJSONObject(jArray.length() - 1);
                    //noinspection ConstantConditions
                    keyString = (String) jObject.names().get(0);

                    if (!jObject.getString(keyString).equals("null")) {
                        endUsage = (float) jObject.getInt(keyString);
                        break;
                    }
                }

                return (endUsage - beginUsage) / 1000;
            }
            // Else return 0
            return 0;
        } catch (Exception e) {
            return 0;
        }
    }

    public ArrayList<Entry> getChartDataPoints() throws JSONException {

        ArrayList<Entry> values = new ArrayList<>();

        JSONArray jArray = toJSONArray(usageString);
        JSONObject jObject;
        String keyString;
        float valueFloat, keyFloat;

        if (jArray.length() > 1) {
            for (int i = 0; i < jArray.length(); i++) {
                jObject = jArray.getJSONObject(i);

                // because you have only one key-value pair in each object so I have used index 0
                keyString = (String) jObject.names().get(0);
                keyFloat = Float.parseFloat(keyString);

                //noinspection HardCodedStringLiteral
                if (!jObject.getString(keyString).contains("null")){
                    // Sometimes the value = null which can't be converted to float
                    valueFloat = (float) jObject.getInt(keyString);
                    values.add(new Entry(keyFloat, valueFloat));
                }
            }
        }

        return values;
    }

    private JSONArray toJSONArray(String resultString) throws JSONException {
        // Remove leading and trailing spaces
        // Replace , with },{ so JSON sees it as an array of objects
        resultString = resultString.trim().replace(",", "},{");

        if (!resultString.startsWith("[")) resultString = "[" + resultString;
        if (!resultString.endsWith("]")) resultString = resultString + "]";

        return new JSONArray(resultString);
    }
}