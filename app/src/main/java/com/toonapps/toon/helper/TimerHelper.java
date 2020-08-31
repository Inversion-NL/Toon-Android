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

import android.app.Activity;

import java.util.Timer;
import java.util.TimerTask;

public class TimerHelper {

    private final TimerInterface listener;
    private final long delay;
    private final Activity mActivity;
    private Timer timer;

    public TimerHelper(Activity mActivity, long delay, TimerInterface listener) {
        this.mActivity = mActivity;
        this.delay = delay;
        this.listener = listener;
    }

    private final TimerTask timerTask = new TimerTask() {
        @Override
        public void run() {
            mActivity.runOnUiThread(listener::onTime);
        }
    };

    public void startWithDelay() {
        if(timer != null) {
            return;
        }
        timer = new Timer();
        timer.scheduleAtFixedRate(timerTask, delay, delay);
    }

    public void startImmediatelyAndOnlyOnce() {
        if(timer != null) {
            return;
        }
        timer = new Timer();
        timer.schedule(timerTask, 0, delay);
    }

    public void stop() {
        if (timer != null) timer.cancel();
        timer = null;
    }

    public interface TimerInterface {
        void onTime();
    }
}