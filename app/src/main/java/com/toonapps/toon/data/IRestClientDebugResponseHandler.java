package com.toonapps.toon.data;

public interface IRestClientDebugResponseHandler {

    void onResponse(String response);
    void onResponseError(Exception e);
}