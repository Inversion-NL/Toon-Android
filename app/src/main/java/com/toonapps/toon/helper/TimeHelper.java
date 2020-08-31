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

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class TimeHelper {

    public static long getLastPeriod(int amount){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.MINUTE, -amount);

        return calendar.getTimeInMillis();
    }

    public static Calendar getMidnight() {
        Calendar midnight = Calendar.getInstance();
        midnight.set(Calendar.HOUR_OF_DAY, 0);
        midnight.set(Calendar.MINUTE, 59);
        midnight.add(Calendar.HOUR_OF_DAY, -2);
        return midnight;
    }

    public static String getHumanReadableTime(Locale locale, long timeInSeconds) {
        Calendar dateTime = getNow();
        dateTime.setTimeInMillis(timeInSeconds * 1000);

        return SimpleDateFormat
                .getTimeInstance(
                        SimpleDateFormat.SHORT,
                        locale)
                .format(dateTime.getTime());
    }

    public static Calendar getNow() {
        return Calendar.getInstance();
    }

    public static boolean isMoreThanNumberOfDaysAgoFromNow(long time, int days) {
        Calendar today = getNow();
        long todayMillis = today.getTimeInMillis();

        long diff = todayMillis - time;
        int numOfDays = (int) (diff / (1000 * 60 * 60 * 24));
        int hours = (int) (diff / (1000 * 60 * 60));
        int minutes = (int) (diff / (1000 * 60));
        int seconds = (int) (diff / (1000));

        return numOfDays > days;
    }
}