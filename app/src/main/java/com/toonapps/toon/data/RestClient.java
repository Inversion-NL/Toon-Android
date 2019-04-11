package com.toonapps.toon.data;

import android.os.AsyncTask;

import com.toonapps.toon.helper.AppSettings;
import com.toonapps.toon.helper.ToonException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class RestClient {

    @SuppressWarnings("HardCodedStringLiteral")
    private static final String API_KEY = "Api-Key";
    private String url;
    private String token;
    private String seperator;
    private IRestClientResponseHandler responseHandler;

    public interface TYPE {
        interface GET {
            int THERMOSTAT_INFO = 0;
            int ZWAVE_DEVICES = 1;
            int USAGE_INFO = 2;
        }
        interface SET {
            int SCHEME_STATE = 10;
            int SET_POINT = 11;
        }
    }

    public RestClient(IRestClientResponseHandler aResponseHandler){
        url =  AppSettings.getInstance().getUrl();
        token = AppSettings.getInstance().getApiToken();
        seperator = AppSettings.getInstance().useRedirectService() ? "?" : "/";

        responseHandler = aResponseHandler;
    }

    private void getDataFromSharedPreferences() {
        url =  AppSettings.getInstance().getUrl();
        token = AppSettings.getInstance().getApiToken();
        seperator = AppSettings.getInstance().useRedirectService() ? "?" : "/";
    }

    public RestClient(String anUrl, IRestClientResponseHandler aResponseHandler){
        url = anUrl;
        responseHandler = aResponseHandler;
    }

    public void setSchemeTemperatureState(int aMode){
        getDataFromSharedPreferences();
        Request request = new Request.Builder()
                .url(url + seperator + "happ_thermstat?action=changeSchemeState&state=2&temperatureState=" + aMode)
                .addHeader(API_KEY,  token)
                .build();

        new RestClientExecutor(request, TYPE.SET.SCHEME_STATE).execute();
    }

    public void setSchemeState(boolean anIsProgramOn){
        getDataFromSharedPreferences();
        int isProgramOn = (anIsProgramOn) ? 1 : 0;

        try {
        Request request = new Request.Builder()
                .url(url + seperator + "happ_thermstat?action=changeSchemeState&state=" + isProgramOn)
                .addHeader(API_KEY, token)
                .build();

        new RestClientExecutor(request, TYPE.SET.SCHEME_STATE).execute();
        } catch (Exception e) {
            if (responseHandler != null) responseHandler.onResponseError(e);
        }
    }

    public void setSetpoint(int aTemperature){
        getDataFromSharedPreferences();
        Request request = new Request.Builder()
                .url(url + seperator + "happ_thermstat?action=setSetpoint&Setpoint=" + aTemperature)
                .addHeader(API_KEY, token)
                .build();

        new RestClientExecutor(request, TYPE.SET.SET_POINT).execute();
    }

    public void getThermostatInfo(){
        getDataFromSharedPreferences();
        Request request = new Request.Builder()
                .url(url + seperator + "happ_thermstat?action=getThermostatInfo")
                .addHeader(API_KEY, token)
                .build();

        new RestClientExecutor(request, TYPE.GET.THERMOSTAT_INFO).execute();
    }

    public void getZWaveDevices() throws IllegalArgumentException {
        getDataFromSharedPreferences();
        Request request = new Request.Builder()
                .url(url + seperator + "hdrv_zwave?action=getDevices.json")
                .addHeader(API_KEY, token)
                .build();

        new RestClientExecutor(request, TYPE.GET.ZWAVE_DEVICES).execute();
    }

    public void getUsageInfo() throws IllegalArgumentException {
        getDataFromSharedPreferences();
        Request request = new Request.Builder()
                .url(url + seperator + "happ_pwrusage?action=GetCurrentUsage")
                .addHeader(API_KEY, token)
                .build();

        new RestClientExecutor(request, TYPE.GET.USAGE_INFO).execute();
    }

    public class RestClientExecutor extends AsyncTask{

        private OkHttpClient client;
        private Request request;
        private int method;

        RestClientExecutor(Request aRequest, int aMethod){
            client = new OkHttpClient.Builder()
                .retryOnConnectionFailure(false)
                .build();
            request = aRequest;
            method = aMethod;
        }

        @Override
        protected Object doInBackground(Object[] params) {
            try {
                Response response = client.newCall(request).execute();
                if (response.isSuccessful()) return response.body().string();
                else return response;
            } catch (Exception e){
                // Connection timeouts and such are handled here
                return e;
            }
        }

        @Override
        protected void onPostExecute(Object o) {
            if(o instanceof String) {
                ResponseData responseData = null;

                switch (method) {
                    case TYPE.GET.THERMOSTAT_INFO:
                        responseData = Converter.convertFromTemperature((String) o);
                        break;
                    case TYPE.GET.ZWAVE_DEVICES:
                        responseData = Converter.convertFromDeviceInfo((String) o);
                        break;
                    case TYPE.SET.SCHEME_STATE:
                        responseData = Converter.convertResultData((String) o);
                        break;
                    case TYPE.SET.SET_POINT:
                        responseData = Converter.convertResultData((String) o);
                        break;
                    case TYPE.GET.USAGE_INFO:
                        responseData = Converter.convertCurrentUsageData((String) o);
                        break;
                }

                if (responseData != null && responseHandler != null) {
                    responseHandler.onResponse(responseData);
                } else if (responseHandler != null)
                    responseHandler.onResponseError(new NullPointerException("response data or responseHandler is null"));
            } else if (o instanceof Response) {

                Response response = (Response) o;
                switch (response.code()){
                    case 401:
                        if (responseHandler != null) responseHandler.onResponseError(new ToonException(ToonException.FORBIDDEN));
                        break;

                    case 403:
                        if (responseHandler != null) responseHandler.onResponseError(new ToonException(ToonException.UNAUTHORIZED));
                        break;

                    case 404:
                        if (responseHandler != null) responseHandler.onResponseError(new ToonException(ToonException.NOT_FOUND));
                        break;

                    default:
                        if (responseHandler != null) responseHandler.onResponseError(new ToonException(ToonException.UNHANDLED));
                }

            } else if (o instanceof Exception) {
                if (responseHandler != null) responseHandler.onResponseError((Exception)o);
            }
        }
    }
}