package com.toonapps.toon.data;

public interface IRestClientResponseHandler {

    void onResponse(ResponseData aResponse);
    void onResponseError(Exception e);
}