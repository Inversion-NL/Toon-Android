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

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.toonapps.toon.R;
import com.toonapps.toon.view.FullscreenAlarmActivity;

import java.util.Random;

public class NotificationHelper {

    private static int FULLSCREEN_INTENT_REQUEST_CODE = 91239123;

    @SuppressWarnings("unused")
    @RequiresApi(api = Build.VERSION_CODES.N)
    public interface PRIORITY {
        int IMPORTANCE_MAX = NotificationManager.IMPORTANCE_MAX;
        int IMPORTANCE_HIGH = NotificationManager.IMPORTANCE_HIGH;
        int IMPORTANCE_DEFAULT = NotificationManager.IMPORTANCE_DEFAULT;
        int IMPORTANCE_LOW = NotificationManager.IMPORTANCE_LOW;
        int IMPORTANCE_MIN = NotificationManager.IMPORTANCE_MIN;
    }

    public interface CHANNEL {
        int DEFAULT = 0;
        int ALARM = 1;
    }

    public static void createSimpleNotification(Context context, String title, String text) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            CreateChannel(context, CHANNEL.DEFAULT, PRIORITY.IMPORTANCE_DEFAULT, false);

        String channelIdStr = getChannelIdStringFromId(CHANNEL.DEFAULT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channelIdStr)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle(title)
                .setContentText(text)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        if (AppSettings.getInstance().getNotificationShouldVibrate()) {
            // Default vibration settings
            builder.setDefaults(Notification.DEFAULT_VIBRATE);
        } else {
            // Disable vibration
            builder.setVibrate(null);
        }

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);

        // notificationId is a unique int for each notification that you must define
        notificationManager.notify(new Random().nextInt(), builder.build());
    }

    public static void createAlarmNotification(Context context, String title, String text) {

        Intent fullScreenIntent = new Intent(context.getApplicationContext(), FullscreenAlarmActivity.class);
        FULLSCREEN_INTENT_REQUEST_CODE = 0;
        PendingIntent fullScreenPendingIntent = PendingIntent.getActivity(
                context,
                FULLSCREEN_INTENT_REQUEST_CODE,
                fullScreenIntent,
                PendingIntent.FLAG_UPDATE_CURRENT
        );

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            CreateChannel(context, CHANNEL.ALARM, PRIORITY.IMPORTANCE_MAX, false);

        String channelIdStr = getChannelIdStringFromId(CHANNEL.ALARM);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channelIdStr)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle(title)
                .setContentText(text)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .setAutoCancel(true)
                .setFullScreenIntent(fullScreenPendingIntent, true);

        if (AppSettings.getInstance().getNotificationShouldVibrate()) {
            if (AppSettings.getInstance().getSmokeSensorNotificationShouldVibrate()) {
                // Extra strong vibration
                long[] pattern = {500,500,500,500,500,500,500,500,500};
                builder.setVibrate(pattern);
            } else {
                // Default vibration settings
                builder.setDefaults(Notification.DEFAULT_VIBRATE);
            }
        } else builder.setVibrate(null);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);

        // notificationId is a unique int for each notification that you must define
        notificationManager.notify(new Random().nextInt(), builder.build());
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private static void CreateChannel(Context context, int channelId, int priority, boolean backgroundProcess) {

        String channelName = getChannelNameFromId(channelId, context);
        String channelIdStr = getChannelIdStringFromId(channelId);

        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationChannel channel =
                new NotificationChannel(
                        channelIdStr,
                        channelName,
                        priority
                );

        if (!backgroundProcess) {
            channel.setShowBadge(true);
            channel.enableLights(true);
            channel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
            channel.setLightColor(Color.BLUE);
        }
        if (!backgroundProcess && (priority != PRIORITY.IMPORTANCE_HIGH || priority != PRIORITY.IMPORTANCE_MAX) && AppSettings.getInstance().getNotificationShouldVibrate()) {
            channel.enableVibration(true);
        } else if (!backgroundProcess && (priority == PRIORITY.IMPORTANCE_HIGH || priority == PRIORITY.IMPORTANCE_MAX) && AppSettings.getInstance().getSmokeSensorNotificationShouldVibrate()) {
            // High priority channel has different settings
            channel.enableVibration(true);
            long[] pattern = {500,500,500,500,500,500,500,500,500};
            channel.setVibrationPattern(pattern);
        }
        if (mNotificationManager != null)
            mNotificationManager.createNotificationChannel(channel);
    }

    private static String getChannelNameFromId(int id, Context context) {
        switch (id) {
            default:
            case CHANNEL.DEFAULT:
                return context.getString(R.string.notification_channelName_default);

            case CHANNEL.ALARM:
                return context.getString(R.string.notification_channelName_alarm);
        }
    }

    @SuppressWarnings("HardCodedStringLiteral")
    private static String getChannelIdStringFromId(int id) {
        switch (id) {
            default:
            case CHANNEL.DEFAULT:
                return "Default";

            case CHANNEL.ALARM:
                return "Alarm";
        }
    }
}
