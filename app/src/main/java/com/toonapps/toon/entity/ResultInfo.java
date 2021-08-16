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

import com.google.gson.annotations.SerializedName;

@SuppressWarnings({"unused", "HardCodedStringLiteral"})
public class ResultInfo {

    private int code;

    @SuppressWarnings("HardCodedStringLiteral")
    @SerializedName(value="result")
    private String result;

    public boolean isSuccess() {
        //noinspection HardCodedStringLiteral
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