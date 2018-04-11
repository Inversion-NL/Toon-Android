package com.toonapps.toon.data;

import android.os.AsyncTask;
import android.util.Log;

import com.toonapps.toon.helper.AppSettings;

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

    public RestClient(String anUrl, IRestClientResponseHandler aResponseHandler){
        url = anUrl;
        responseHandler = aResponseHandler;
    }

    public void setSchemeTemperatureState(int aMode){
        Request request = new Request.Builder()
                .url(url + seperator + "happ_thermstat?action=changeSchemeState&state=2&temperatureState=" + aMode)
                .addHeader("Api-Key",  token)
                .build();

        new RestClientExecutor(request, "setschemestate").execute();
    }

    public void setSchemeState(boolean anIsProgramOn){

        int isProgramOn = (anIsProgramOn) ? 1 : 0;

        try {
        Request request = new Request.Builder()
                .url(url + seperator + "happ_thermstat?action=changeSchemeState&state=" + isProgramOn)
                .addHeader("Api-Key", token)
                .build();

        new RestClientExecutor(request, "setschemestate").execute();
        } catch (Exception e) {
            //TODO Notify users something went wrong
            e.printStackTrace();
        }
    }

    public void setSetpoint(int aTemperature){
        Request request = new Request.Builder()
                .url(url + seperator + "happ_thermstat?action=setSetpoint&Setpoint=" + aTemperature)
                .addHeader("Api-Key", token)
                .build();

        new RestClientExecutor(request, "setsetpoint").execute();
    }

    public void getThermostatInfo(){
        Request request = new Request.Builder()
                .url(url + seperator + "happ_thermstat?action=getThermostatInfo")
                .addHeader("Api-Key", token)
                .build();

        new RestClientExecutor(request, "getthermostatinfo").execute();
    }

    public void getZWaveDevices(){
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
            client = new OkHttpClient();
            request = aRequest;
            method = aMethod;
        }

        @Override
        protected Object doInBackground(Object[] params) {
            try {
                Response response = client.newCall(request).execute();
                return response.body().string();
            }
            catch(Exception e){
                Log.d("Exception occured", e.getMessage());
            }

            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            if(o instanceof String) {
                ResponseData responseData = null;

                switch(method){
                    case "getthermostatinfo":
                        responseData = Converter.convertFromTemperature((String)o);
                        break;
                    case "getzwavedevices":
                        responseData = Converter.convertFromDeviceInfo((String)o);
                        break;
                    case "setschemestate":
                        break;
                    case "setsetpoint":
                        break;
                }

                if(responseData != null && responseHandler != null) {
                    responseHandler.onResponse(responseData);
                }
            }
        }
    }
}
