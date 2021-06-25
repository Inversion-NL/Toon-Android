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
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.AudioAttributes;
import android.net.Uri;
import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.firebase.messaging.RemoteMessage;
import com.toonapps.toon.R;
import com.toonapps.toon.view.FullscreenAlarmActivity;
import com.toonapps.toon.view.MainActivity;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class NotificationHelper {

    @SuppressWarnings("FieldCanBeLocal")
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

    @SuppressWarnings("HardCodedStringLiteral")
    public interface FCM_NOTIFICATION {
        interface TYPE {
            String ALARM = "alarm";
            String NOTIFICATION = "notification";
            String UNKNOWN = "unknown";
        }
        interface SUBTYPE {
            interface ALARM {
                String SMOKE = "smoke";
                String HEAT = "heat";
                String UNKNOWN = "unknown";
            }
            interface NOTIFICATION {
                String DATA = "data";
            }
        }
        interface SOURCE {
            String SENSOR = "sensor";
        }
        interface LOCATION {
            String ROOM = "room";
        }
    }

    public static HashMap<String, String> checkNotificationDataForRoomAndSensor(HashMap<String, String> hashMap) {
        if (!hashMap.isEmpty()) {
            hashMap.size();
            String room = hashMap.get(FCM_NOTIFICATION.LOCATION.ROOM);
            String sensor = hashMap.get(FCM_NOTIFICATION.SOURCE.SENSOR);

            if (sensor != null && room != null && !room.isEmpty() && !sensor.isEmpty()) {
                return hashMap;
            } else return new HashMap<>();
        }
        return new HashMap<>();
    }

    public static String getNotificationType(RemoteMessage remoteMessage) {

        HashMap<String, String> hashMap = convertToHashMap(remoteMessage.getData());

        if (hashMap.containsKey("type")){

            String type = hashMap.get("type");

            if (type != null) {
                if (type.equals(FCM_NOTIFICATION.TYPE.ALARM))
                    return FCM_NOTIFICATION.TYPE.ALARM;
                else if (type.equals(FCM_NOTIFICATION.TYPE.NOTIFICATION))
                    return FCM_NOTIFICATION.TYPE.NOTIFICATION;
            }
        }
        return FCM_NOTIFICATION.TYPE.UNKNOWN;
    }

    @SuppressWarnings("HardCodedStringLiteral")
    public static String getAlarmType(RemoteMessage remoteMessage) {
        HashMap<String, String> data = convertToHashMap(remoteMessage.getData());

        if (data.containsKey("subtype")) {
            if (data.get("subtype").equals(FCM_NOTIFICATION.SUBTYPE.ALARM.HEAT))
                return FCM_NOTIFICATION.SUBTYPE.ALARM.HEAT;

            else if (data.get("subtype").equals(FCM_NOTIFICATION.SUBTYPE.ALARM.SMOKE))
                return FCM_NOTIFICATION.SUBTYPE.ALARM.SMOKE;
        }

        return FCM_NOTIFICATION.SUBTYPE.ALARM.UNKNOWN;
    }

    public static String getSensorName(Context context, RemoteMessage remoteMessage) {
        String sensor = remoteMessage.getData().get(NotificationHelper.FCM_NOTIFICATION.SOURCE.SENSOR);
        if (sensor == null || sensor.isEmpty()) sensor = context.getString(R.string.fcm_notification_unknownSmokeSensor);
        return sensor;
    }

    public static String getRoomName(Context context, RemoteMessage remoteMessage) {
        String room = remoteMessage.getData().get(NotificationHelper.FCM_NOTIFICATION.LOCATION.ROOM);
        if (room == null || room.isEmpty()) room = context.getString(R.string.fcm_notification_unknownRoom);
        return room;
    }

    @SuppressWarnings("HardCodedStringLiteral")
    public static String getNormalNotificationTitle(RemoteMessage remoteMessage) {
        HashMap<String, String> hashMap = NotificationHelper.convertToHashMap(remoteMessage.getData());

        if (hashMap.containsKey("title")) {
            return hashMap.get("title");
        }
        return "";
    }

    @SuppressWarnings("HardCodedStringLiteral")
    public static String getNormalNotificationMessage(RemoteMessage remoteMessage) {
        HashMap<String, String> hashMap = NotificationHelper.convertToHashMap(remoteMessage.getData());

        if (hashMap.containsKey("message")) {
            return hashMap.get("message");
        }
        return "";
    }

    public static void createSimpleNotification(
            Context context,
            String title,
            String text,
            RemoteMessage remoteMessage) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            CreateChannel(context, CHANNEL.DEFAULT, PRIORITY.IMPORTANCE_DEFAULT);

        String channelIdStr = getChannelIdStringFromId(CHANNEL.DEFAULT, context);

        Map<String, String> map = remoteMessage.getData();
        Intent notificationIntent = new Intent(context, MainActivity.class);
        //noinspection HardCodedStringLiteral
        notificationIntent.putExtra("notificationData", convertToHashMap(map));
        PendingIntent contentIntent = PendingIntent.getActivity(context, 0, notificationIntent, 0);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channelIdStr)
                .setAutoCancel(true)
                .setContentIntent(contentIntent)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
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
        Notification notification = builder.build();
        notification.defaults |= Notification.DEFAULT_SOUND;
        notificationManager.notify(new Random().nextInt(), notification);
    }

    /**
     * Creates an alarm notification and sends the data in the remote message along with the
     * pending intent
     * @param context Context
     * @param title Title of the notification
     * @param text Text of the notification
     * @param remoteMessage Remote message to go along the pending intent
     */
    public static void createAlarmNotification(
            Context context,
            String title,
            String text,
            RemoteMessage remoteMessage) {

        Intent fullScreenIntent = new Intent(context.getApplicationContext(), FullscreenAlarmActivity.class);
        FULLSCREEN_INTENT_REQUEST_CODE = 0;

        Map<String, String> map = remoteMessage.getData();
        //noinspection HardCodedStringLiteral
        fullScreenIntent.putExtra("data", convertToHashMap(map));
        PendingIntent fullScreenPendingIntent = PendingIntent.getActivity(
                context,
                FULLSCREEN_INTENT_REQUEST_CODE,
                fullScreenIntent,
                PendingIntent.FLAG_UPDATE_CURRENT
        );

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            CreateChannel(context, CHANNEL.ALARM, PRIORITY.IMPORTANCE_MAX);

        String channelIdStr = getChannelIdStringFromId(CHANNEL.ALARM, context);

        Uri alarm_smokeDetector = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://"+ context.getPackageName() + "/" + R.raw.alarm_smokedetector);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channelIdStr)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle(title)
                .setContentText(text)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setSound(alarm_smokeDetector)
                .setAutoCancel(true)
                .setFullScreenIntent(fullScreenPendingIntent, true);

        if (AppSettings.getInstance().getNotificationShouldVibrate()) {
            if (AppSettings.getInstance().getSmokeSensorNotificationShouldVibrate()) {
                // Extra strong vibration
                long[] pattern = {500,500,500,500,500,500,500,500,500,500,500};
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

    public static HashMap<String, String> convertToHashMap(Map<String, String> map) {
        if (map instanceof HashMap) //noinspection unchecked
            return (HashMap) map;
        else return new HashMap<>(map);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private static void CreateChannel(Context context, int channelId, int priority) {

        String channelName = getChannelNameFromId(channelId, context);
        String channelIdStr = getChannelIdStringFromId(channelId, context);

        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationChannel channel =
                new NotificationChannel(
                        channelIdStr,
                        channelName,
                        priority
                );

        channel.setShowBadge(true);
        channel.enableLights(true);
        channel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
        channel.setLightColor(Color.RED);

        if (priority != PRIORITY.IMPORTANCE_HIGH && priority != PRIORITY.IMPORTANCE_MAX) {

            channel.enableVibration(true);

        } else if (priority == PRIORITY.IMPORTANCE_HIGH || priority == PRIORITY.IMPORTANCE_MAX) {
            // High priority channel has different settings
            channel.enableVibration(true);
            long[] pattern = {500,500,500,500,500,500,500,500,500,500,500};
            channel.setVibrationPattern(pattern);

            AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                    .build();

            Uri alarm_smokeDetector = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://"+ context.getPackageName() + "/" + R.raw.alarm_smokedetector);
            channel.setSound(alarm_smokeDetector, audioAttributes);
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

    private static String getChannelIdStringFromId(int id, Context context) {
        switch (id) {
            default:
            case CHANNEL.DEFAULT:
                return context.getString(R.string.notification_channelName_default);

            case CHANNEL.ALARM:
                return context.getString(R.string.notification_channelName_alarm);
        }
    }
}
