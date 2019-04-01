package com.toonapps.toon.data;

import android.os.AsyncTask;

import com.toonapps.toon.helper.AppSettings;
import com.toonapps.toon.helper.ToonException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class RestClient {

    private String url;
    private String token;
    private String seperator;
    private IRestClientResponseHandler responseHandler;

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
                .addHeader("Api-Key",  token)
                .build();

        new RestClientExecutor(request, "setschemestate").execute();
    }

    public void setSchemeState(boolean anIsProgramOn){
        getDataFromSharedPreferences();
        int isProgramOn = (anIsProgramOn) ? 1 : 0;

        try {
        Request request = new Request.Builder()
                .url(url + seperator + "happ_thermstat?action=changeSchemeState&state=" + isProgramOn)
                .addHeader("Api-Key", token)
                .build();

        new RestClientExecutor(request, "setschemestate").execute();
        } catch (Exception e) {
            responseHandler.onResponseError(e);
        }
    }

    public void setSetpoint(int aTemperature){
        getDataFromSharedPreferences();
        Request request = new Request.Builder()
                .url(url + seperator + "happ_thermstat?action=setSetpoint&Setpoint=" + aTemperature)
                .addHeader("Api-Key", token)
                .build();

        new RestClientExecutor(request, "setsetpoint").execute();
    }

    public void getThermostatInfo(){
        getDataFromSharedPreferences();
        Request request = new Request.Builder()
                .url(url + seperator + "happ_thermstat?action=getThermostatInfo")
                .addHeader("Api-Key", token)
                .build();

        new RestClientExecutor(request, "getthermostatinfo").execute();
    }

    public void getZWaveDevices() throws IllegalArgumentException {
        getDataFromSharedPreferences();
        Request request = new Request.Builder()
                .url(url + seperator + "hdrv_zwave?action=getDevices.json")
                .addHeader("Api-Key", token)
                .build();

        new RestClientExecutor(request, "getzwavedevices").execute();
    }

    public class RestClientExecutor extends AsyncTask{

        private OkHttpClient client;
        private Request request;
        private String method;

        public RestClientExecutor(Request aRequest, String aMethod){
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
                    case "getthermostatinfo":
                        responseData = Converter.convertFromTemperature((String) o);
                        break;
                    case "getzwavedevices":
                        responseData = Converter.convertFromDeviceInfo((String) o);
                        break;
                    case "setschemestate":
                        break;
                    case "setsetpoint":
                        break;
                }

                if (responseData != null && responseHandler != null) {
                    responseHandler.onResponse(responseData);
                } else
                    responseHandler.onResponseError(new NullPointerException("response data or responseHandler is null"));
            } else if (o instanceof Response) {

                Response response = (Response) o;
                switch (response.code()){
                    case 401:
                        responseHandler.onResponseError(new ToonException(ToonException.FORBIDDEN));
                        break;

                    case 403:
                        responseHandler.onResponseError(new ToonException(ToonException.UNAUTHORIZED));
                        break;

                    case 404:
                        responseHandler.onResponseError(new ToonException(ToonException.NOT_FOUND));
                        break;

                    default:
                        responseHandler.onResponseError(new ToonException(ToonException.UNHANDLED));
                }

            } else if (o instanceof Exception) {
                responseHandler.onResponseError((Exception)o);
            }
        }
    }
}