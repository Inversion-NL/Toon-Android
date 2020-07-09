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

import android.content.Context;

import com.toonapps.toon.R;

import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

public class ErrorMessage {

    private final Context context;

    public ErrorMessage(Context mContext) {
        this.context = mContext;
    }

    public String getHumanReadableErrorMessage(Exception exception) {
        if (exception instanceof UnknownHostException) return context.getString(R.string.exception_message_hostUnknown);
        else if (exception instanceof SocketTimeoutException) return context.getString(R.string.exception_message_timeout);
        else if (exception instanceof IllegalArgumentException) return context.getString(R.string.exception_message_incorrectHostname);
        else if (exception instanceof ConnectException) return context.getString(R.string.exception_message_noNetwork);
        else if (exception instanceof ToonException) {
            ToonException toonException = (ToonException) exception;
            switch (toonException.getType()) {

                case ToonException.FORBIDDEN:
                    return context.getString(R.string.exception_message_connectionRefused);

                case ToonException.NOT_FOUND:
                    return context.getString(R.string.exception_message_pageNotFound);

                case ToonException.UNAUTHORIZED:
                    return context.getString(R.string.exception_message_notAuthorized);

                case ToonException.UNSUPPORTED:
                    return context.getString(R.string.exception_message_unsupported);

                case ToonException.GETDEVICESERROR:
                    return context.getString(R.string.exception_message_getDevicesError) + " " + exception.getMessage();

                case ToonException.UNHANDLED:
                default:
                    return context.getString(R.string.exception_message_unhandled);
            }
        }
        else return exception.getMessage();
    }
}