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

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.toonapps.toon.application.AppController;
import com.toonapps.toon.entity.UsageInfo;
import com.toonapps.toon.helper.AppSettings;
import com.toonapps.toon.helper.TimeHelper;
import com.toonapps.toon.helper.ToonException;

import java.util.HashMap;
import java.util.Map;

public class RestClient {

    private String httpHeaderKey;
    private String url;
    private String httpHeaderValue;
    private String separator;
    private final IRestClientResponseHandler responseHandler;
    private final IRestClientDebugResponseHandler responseDebugHandler;

    public RestClient(IRestClientResponseHandler aResponseHandler){
        getDataFromSharedPreferences();
        responseHandler = aResponseHandler;
        responseDebugHandler = null;
    }

    public RestClient(IRestClientDebugResponseHandler aResponseDebugHandler){
        getDataFromSharedPreferences();
        responseDebugHandler = aResponseDebugHandler;
        responseHandler = null;
    }

    private void getDataFromSharedPreferences() {
        url =  AppSettings.getInstance().getUrl();
        httpHeaderValue = AppSettings.getInstance().getHttpHeaderValue();
        httpHeaderKey = AppSettings.getInstance().getHttpHeaderKey();
        separator = "/";
    }

    public void setSchemeTemperatureState(int aMode) {
        getDataFromSharedPreferences();

        //noinspection SpellCheckingInspection,HardCodedStringLiteral
        url = url + separator + "happ_thermstat?action=changeSchemeState&state=2&temperatureState=" + aMode;

        StringRequest request =
            new StringRequest(
                Request.Method.POST,
                url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        ResponseData responseData = Converter.convertResultData(response);
                        if (responseHandler != null) responseHandler.onResponse(responseData);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        errorToResponseHandler(error);
                    }
                }
            ){
                @Override
                public Map<String, String> getHeaders() {
                    HashMap<String, String> headers = new HashMap<>();
                    headers.put(httpHeaderKey, httpHeaderValue);
                    return headers;
                }
            };
        AppController.getInstance().getRequestQueue().add(request);
    }

    public void getElecFlow(long startTime, long endTime) {

        getDataFromSharedPreferences();
        //noinspection HardCodedStringLiteral
        url = url + separator + "hcb_rrd?action=getRrdData&loggerName=elec_flow&rra=5min&readableTime=0&nullForNaN=1&from=" + startTime + "&to=" + endTime;

        StringRequest request =
            new StringRequest(
                Request.Method.GET,
                url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        UsageInfo usageInfo =
                                new UsageInfo(
                                        response,
                                        UsageInfo.TYPE.ELEC,
                                        UsageInfo.TARIFF.NULL,
                                        UsageInfo.TIME.PERIOD
                                );
                        ResponseData responseData = new ResponseData();
                        responseData.setUsageInfo(usageInfo);

                        if (responseHandler != null) responseHandler.onResponse(responseData);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        errorToResponseHandler(error);
                    }
                }
            ){
                @Override
                public Map<String, String> getHeaders() {
                    HashMap<String, String> headers = new HashMap<>();
                    headers.put(httpHeaderKey, httpHeaderValue);
                    return headers;
                }
            };
        AppController.getInstance().getRequestQueue().add(request);
    }

    public void getTodayElecNtUsage() {

        getDataFromSharedPreferences();
        long midnight = TimeHelper.getMidnight().getTimeInMillis() / 1000;
        long now = TimeHelper.getNow().getTimeInMillis() / 1000;

        //noinspection HardCodedStringLiteral
        url = url + separator + "hcb_rrd?action=getRrdData&loggerName=elec_quantity_nt&rra=5yrhours&readableTime=0&nullForNaN=1&from=" + midnight + "&to=" + now;

        StringRequest request =
            new StringRequest(
                Request.Method.GET,
                url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        UsageInfo usageInfo =
                            new UsageInfo(
                                response,
                                UsageInfo.TYPE.ELEC,
                                UsageInfo.TARIFF.NORMAL,
                                UsageInfo.TIME.TODAY
                            );

                        ResponseData responseData = new ResponseData();
                        responseData.setUsageInfo(usageInfo);

                        if (responseHandler != null) responseHandler.onResponse(responseData);

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        errorToResponseHandler(error);
                    }
                }
            ){
                @Override
                public Map<String, String> getHeaders() {
                    HashMap<String, String> headers = new HashMap<>();
                    headers.put(httpHeaderKey, httpHeaderValue);
                    return headers;
                }
            };
        AppController.getInstance().getRequestQueue().add(request);
    }

    public void getTodayElecLtUsage() {

        getDataFromSharedPreferences();
        long midnight = TimeHelper.getMidnight().getTimeInMillis() / 1000;
        long now = TimeHelper.getNow().getTimeInMillis() / 1000;

        //noinspection HardCodedStringLiteral
        url = url + separator + "hcb_rrd?action=getRrdData&loggerName=elec_quantity_lt&rra=5yrhours&readableTime=0&nullForNaN=1&from=" + midnight + "&to=" + now;

        StringRequest request =
            new StringRequest(
                Request.Method.GET,
                url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        UsageInfo usageInfo =
                            new UsageInfo(
                                response,
                                UsageInfo.TYPE.ELEC,
                                UsageInfo.TARIFF.LOW,
                                UsageInfo.TIME.TODAY
                            );

                        ResponseData responseData = new ResponseData();
                        responseData.setUsageInfo(usageInfo);

                        if (responseHandler != null) responseHandler.onResponse(responseData);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        errorToResponseHandler(error);
                    }
                }
            ){
                @Override
                public Map<String, String> getHeaders() {
                    HashMap<String, String> headers = new HashMap<>();
                    headers.put(httpHeaderKey, httpHeaderValue);
                    return headers;
                }
            };
        AppController.getInstance().getRequestQueue().add(request);
    }

    public void getTodayGasUsage() {

        getDataFromSharedPreferences();
        long midnight = TimeHelper.getMidnight().getTimeInMillis() / 1000;
        long now = TimeHelper.getNow().getTimeInMillis() / 1000;

        //noinspection HardCodedStringLiteral
        url = url + separator + "hcb_rrd?action=getRrdData&loggerName=gas_quantity&rra=5yrhours&readableTime=0&nullForNaN=1&from=" + midnight + "&to=" + now;

        StringRequest request =
                new StringRequest(
                        Request.Method.GET,
                        url,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                UsageInfo usageInfo =
                                        new UsageInfo(
                                                response,
                                                UsageInfo.TYPE.GAS,
                                                0,
                                                UsageInfo.TIME.TODAY
                                        );
                                ResponseData responseData = new ResponseData();
                                responseData.setUsageInfo(usageInfo);

                                if (responseHandler != null) responseHandler.onResponse(responseData);
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                errorToResponseHandler(error);
                            }
                        }
                ){
                    @Override
                    public Map<String, String> getHeaders() {
                        HashMap<String, String> headers = new HashMap<>();
                        headers.put(httpHeaderKey, httpHeaderValue);
                        return headers;
                    }
                };
        AppController.getInstance().getRequestQueue().add(request);
    }

    public void getGasFlow(long startTime, long endTime) {

        getDataFromSharedPreferences();
        //noinspection HardCodedStringLiteral
        url = url + separator + "hcb_rrd?action=getRrdData&loggerName=gas_flow&rra=5min&readableTime=0&nullForNaN=1&from=" + startTime + "&to=" + endTime;

        StringRequest request =
            new StringRequest(
                Request.Method.GET,
                url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        UsageInfo usageInfo =
                                new UsageInfo(
                                        response,
                                        UsageInfo.TYPE.GAS,
                                        0,
                                        UsageInfo.TIME.PERIOD
                                );
                        ResponseData responseData = new ResponseData();
                        responseData.setUsageInfo(usageInfo);

                        if (responseHandler != null) responseHandler.onResponse(responseData);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        errorToResponseHandler(error);
                    }
                }
            ){
                @Override
                public Map<String, String> getHeaders() {
                    HashMap<String, String> headers = new HashMap<>();
                    headers.put(httpHeaderKey, httpHeaderValue);
                    return headers;
                }
            };
        AppController.getInstance().getRequestQueue().add(request);
    }

    public void setSchemeState(boolean anIsProgramOn){
        getDataFromSharedPreferences();
        int isProgramOn = (anIsProgramOn) ? 1 : 0;

        //noinspection HardCodedStringLiteral
        url = url + separator + "happ_thermstat?action=changeSchemeState&state=" + isProgramOn;

        StringRequest request =
            new StringRequest(
                Request.Method.POST,
                url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        ResponseData responseData = Converter.convertResultData(response);
                        if (responseHandler != null) responseHandler.onResponse(responseData);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        errorToResponseHandler(error);
                    }
                }
            ){
                @Override
                public Map<String, String> getHeaders() {
                    HashMap<String, String> headers = new HashMap<>();
                    headers.put(httpHeaderKey, httpHeaderValue);
                    return headers;
                }
            };
        AppController.getInstance().getRequestQueue().add(request);
    }

    public void setSetpoint(int aTemperature){
        getDataFromSharedPreferences();

        //noinspection HardCodedStringLiteral
        url = url + separator + "happ_thermstat?action=setSetpoint&Setpoint=" + aTemperature;

        StringRequest request =
            new StringRequest(
                Request.Method.POST,
                url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        ResponseData responseData = Converter.convertResultData(response);
                        if (responseHandler != null) responseHandler.onResponse(responseData);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        errorToResponseHandler(error);
                    }
                }
            ){
                @Override
                public Map<String, String> getHeaders() {
                    HashMap<String, String> headers = new HashMap<>();
                    headers.put(httpHeaderKey, httpHeaderValue);
                    return headers;
                }
            };
        AppController.getInstance().getRequestQueue().add(request);
    }

    public void getThermostatInfo(){
        getDataFromSharedPreferences();
        //noinspection HardCodedStringLiteral
        url = url + separator + "happ_thermstat?action=getThermostatInfo";

        StringRequest request =
            new StringRequest(
                Request.Method.GET,
                url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        ResponseData responseData = Converter.convertFromTemperature(response);
                        if (responseHandler != null) responseHandler.onResponse(responseData);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        errorToResponseHandler(error);
                    }
                }
            ){
                @Override
                public Map<String, String> getHeaders() {
                    HashMap<String, String> headers = new HashMap<>();
                    headers.put(httpHeaderKey, httpHeaderValue);
                    return headers;
                }
            };
        AppController.getInstance().getRequestQueue().add(request);
    }

    public void getDebugThermostatInfo(){
        getDataFromSharedPreferences();
        //noinspection HardCodedStringLiteral
        url = url + separator + "happ_thermstat?action=getThermostatInfo";

        StringRequest request =
                new StringRequest(
                        Request.Method.GET,
                        url,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                if (responseDebugHandler != null) responseDebugHandler.onResponse(response);
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                responseDebugHandler.onResponseError(error);
                            }
                        }
                ){
                    @Override
                    public Map<String, String> getHeaders() {
                        HashMap<String, String> headers = new HashMap<>();
                        headers.put(httpHeaderKey, httpHeaderValue);
                        return headers;
                    }
                };
        AppController.getInstance().getRequestQueue().add(request);
    }

    public void getDebugZwaveDevices(){
        getDataFromSharedPreferences();
        //noinspection HardCodedStringLiteral
        url = url + separator + "hdrv_zwave?action=getDevices.json";

        StringRequest request =
                new StringRequest(
                        Request.Method.GET,
                        url,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                if (responseDebugHandler != null) responseDebugHandler.onResponse(response);
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                responseDebugHandler.onResponseError(error);
                            }
                        }
                ){
                    @Override
                    public Map<String, String> getHeaders() {
                        HashMap<String, String> headers = new HashMap<>();
                        headers.put(httpHeaderKey, httpHeaderValue);
                        return headers;
                    }
                };
        AppController.getInstance().getRequestQueue().add(request);
    }

    public void getDebugCurrentUsage(){
        getDataFromSharedPreferences();
        //noinspection HardCodedStringLiteral
        url = url + separator + "happ_pwrusage?action=GetCurrentUsage";

        StringRequest request =
                new StringRequest(
                        Request.Method.GET,
                        url,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                if (responseDebugHandler != null) responseDebugHandler.onResponse(response);
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                responseDebugHandler.onResponseError(error);
                            }
                        }
                ){
                    @Override
                    public Map<String, String> getHeaders() {
                        HashMap<String, String> headers = new HashMap<>();
                        headers.put(httpHeaderKey, httpHeaderValue);
                        return headers;
                    }
                };
        AppController.getInstance().getRequestQueue().add(request);
    }

    public void getZWaveDevices() {
        getDataFromSharedPreferences();

        //noinspection HardCodedStringLiteral
        url = url + separator + "hdrv_zwave?action=getDevices.json";

        StringRequest request =
            new StringRequest(
                Request.Method.GET,
                url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        ResponseData responseData;
                        try {
                            responseData = Converter.convertFromDeviceInfo(response);
                            if (responseHandler != null) responseHandler.onResponse(responseData);
                        } catch (Exception error) {
                            errorToResponseHandler(error);
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        errorToResponseHandler(error);
                    }
                }
            ){
                @Override
                public Map<String, String> getHeaders() {
                    HashMap<String, String> headers = new HashMap<>();
                    headers.put(httpHeaderKey, httpHeaderValue);
                    return headers;
                }
            };
        AppController.getInstance().getRequestQueue().add(request);
    }

    public void getUsageInfo() {
        getDataFromSharedPreferences();

        //noinspection HardCodedStringLiteral
        url = url + separator + "happ_pwrusage?action=GetCurrentUsage";

        StringRequest request =
            new StringRequest(
                Request.Method.GET,
                url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        ResponseData responseData = Converter.convertCurrentUsageData(response);
                        if (responseHandler != null) responseHandler.onResponse(responseData);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        errorToResponseHandler(error);
                    }
                }
            ){
                @Override
                public Map<String, String> getHeaders() {
                    HashMap<String, String> headers = new HashMap<>();
                    headers.put(httpHeaderKey, httpHeaderValue);
                    return headers;
                }
            };
        AppController.getInstance().getRequestQueue().add(request);
    }

    private void errorToResponseHandler(VolleyError error) {
        Throwable cause = error.getCause();

        if (responseHandler != null) {

            if (cause instanceof UnsupportedOperationException)
                responseHandler.onResponseError(new ToonException(ToonException.UNSUPPORTED, error));

            if (error.networkResponse != null) {
                switch (error.networkResponse.statusCode) {
                    case 401:
                        responseHandler.onResponseError(new ToonException(ToonException.FORBIDDEN, error));
                        break;

                    case 403:
                        responseHandler.onResponseError(new ToonException(ToonException.UNAUTHORIZED, error));
                        break;

                    case 404:
                        responseHandler.onResponseError(new ToonException(ToonException.NOT_FOUND, error));
                        break;

                    default:
                        responseHandler.onResponseError(new ToonException(ToonException.UNHANDLED, error));
                }
            }
        }
    }

    private void errorToResponseHandler(Exception error) {
        if (responseHandler != null) responseHandler.onResponseError(new ToonException(ToonException.GETDEVICESERROR, error));
    }
}