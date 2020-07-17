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
import android.util.DisplayMetrics;
import android.widget.Toast;

public class ScreenHelper {

    public interface DISPLAY_DENSITY {
        int LOW = 0;
        int NORMAL = 1;
        int HIGH = 2;
    }

    public static int getDisplayDensityDpi(Context mContext, boolean testing) {

        if (mContext.getResources().getDisplayMetrics().densityDpi >= DisplayMetrics.DENSITY_XXHIGH) {
            if (testing) Toast.makeText(mContext,
                    "Screen density high: " +
                            mContext.getResources().getDisplayMetrics().densityDpi +
                            "dpi",
                    Toast.LENGTH_SHORT)
                    .show();
            return DISPLAY_DENSITY.HIGH;
        } else if (mContext.getResources().getDisplayMetrics().densityDpi >= DisplayMetrics.DENSITY_HIGH) {
            if (testing) Toast.makeText(mContext,
                    "Screen density normal: " +
                            mContext.getResources().getDisplayMetrics().densityDpi +
                            "dpi",
                    Toast.LENGTH_SHORT)
                    .show();
            return DISPLAY_DENSITY.NORMAL;
        } else {
            if (testing) Toast.makeText(
                    mContext, "Screen density low: " +
                            mContext.getResources().getDisplayMetrics().densityDpi +
                            "dpi",
                    Toast.LENGTH_SHORT)
                    .show();
            return DISPLAY_DENSITY.LOW;
        }
    }

    public static int convertDpsToPixels(Context mContext, int dps) {
        final float scale = mContext.getResources().getDisplayMetrics().density;
        return (int) (dps * scale + 0.5f);
    }
}