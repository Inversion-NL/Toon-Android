package com.toonapps.toon.entity;

import com.google.gson.annotations.SerializedName;

public class ResultInfo {

    private int code;

    @SerializedName(value="result")
    private String result;

    public boolean isSuccess() {
        return result.equals("ok");
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }
}