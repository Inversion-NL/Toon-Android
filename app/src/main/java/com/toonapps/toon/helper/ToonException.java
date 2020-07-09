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

package com.toonapps.toon.helper;

public class ToonException extends Exception{

    private final int exceptionType;
    private final Exception exception;

    public static final int FORBIDDEN = 0;
    public static final int NOT_FOUND = 1;
    public static final int UNAUTHORIZED = 2;
    public static final int UNSUPPORTED = 3;
    public static final int GETDEVICESERROR = 4;
    public static final int UNHANDLED = 99;

    public ToonException(int type, Exception e) {
        exceptionType = type;
        exception = e;
    }

    int getType() {
        return exceptionType;
    }

    public Exception getException() {
        return exception;
    }
}