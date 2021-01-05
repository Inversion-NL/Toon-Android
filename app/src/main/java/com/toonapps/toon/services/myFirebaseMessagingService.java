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

package com.toonapps.toon.services;

import androidx.annotation.NonNull;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.toonapps.toon.R;
import com.toonapps.toon.helper.AppSettings;
import com.toonapps.toon.helper.NotificationHelper;

import java.util.Map;

import timber.log.Timber;

public class myFirebaseMessagingService extends FirebaseMessagingService {

    @SuppressWarnings("HardCodedStringLiteral")
    interface FCM_NOTIFICATION {
        interface SOURCE {
            String TYPE_SENSOR = "sensor";
        }
        interface TYPE {
            String SMOKE_ALARM = "smoke_alarm";
        }
    }

    interface ALARM {
        int TYPE_UNKNOWN = 0;
        int TYPE_SMOKE = 1;
    }

    @Override
    public void onNewToken(@NonNull String s) {
        //Todo Talk to MainActivity to tell the user to update their FCM token in Toon

        AppSettings.getInstance().setFirebaseInstanceId(s);
        Timber.d("New Firebase Messaging token: %s", s);

        super.onNewToken(s);
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {

        Timber.d("New Firebase Message received");

        RemoteMessage.Notification notification = remoteMessage.getNotification();
        if (notification != null) {

            // When there is both a notification and data in the FCM message
            // These type of messages are only received when the app is active
            // See here for more info: https://firebase.google.com/docs/cloud-messaging/android/receive

            String title = remoteMessage.getNotification().getTitle();
            String body = remoteMessage.getNotification().getBody();

            Timber.d("Notification message received from Firebase Cloud Messaging, ignoring data in notification");
            Timber.d("Notification message: %s %s", title, body);
            NotificationHelper
                    .createAlarmNotification(
                            getApplicationContext(),
                            title,
                            body
                    );

        } else {
            // When there is only data in the FCM message and no notification
            // When there is also notification data in the message, the data isn't received here
            // when the app isn't active.
            // When the user clicks the notification, the data is in a extra bundle
            Map<String, String> data = remoteMessage.getData();

            if (data.size() > 0) {
                Timber.d(
                        "Data message received from Firebase Cloud Messaging: %s ",
                        data.toString()
                );

                for (Map.Entry pair : data.entrySet()) {
                    Timber.d("%s: %s", pair.getKey(), pair.getValue());
                }

                String title = getString(R.string.fcm_notification_alarmSmokeSensors);

                switch (getAlarmType(remoteMessage)){

                    case ALARM.TYPE_SMOKE:

                        Timber.d("Creating alarm notification with priority high!");

                        String sensor = remoteMessage.getData().get(FCM_NOTIFICATION.SOURCE.TYPE_SENSOR);
                        if (sensor == null || sensor.isEmpty()) sensor = getString(R.string.fcm_notification_unknownSmokeSensor);

                        String text = String.format(getString(R.string.fcm_notification_reportedSmokeAlarm), sensor);

                        NotificationHelper
                                .createAlarmNotification(
                                        getApplicationContext(),
                                        title,
                                        text);
                        break;

                    case ALARM.TYPE_UNKNOWN:
                    default:
                        Timber.d("Creating alarm notification with normal priority");
                        NotificationHelper
                                .createSimpleNotification(
                                        getApplicationContext(),
                                        title,
                                        getString(R.string.fcm_notification_unknownAlarm)
                                );
                        break;
                }

            } else {
                Timber.d("Data message received from Firebase Cloud Messaging but both notification and data fields were empty!");
            }
        }

        super.onMessageReceived(remoteMessage);
    }

    private int getAlarmType(RemoteMessage remoteMessage) {

        if (remoteMessage.getData().size() > 0) {

            String alarmValue = remoteMessage.getData().get(FCM_NOTIFICATION.TYPE.SMOKE_ALARM);
            if (alarmValue != null && !alarmValue.isEmpty() && Boolean.parseBoolean(alarmValue))
                return ALARM.TYPE_SMOKE;

        } else return ALARM.TYPE_UNKNOWN;

        return ALARM.TYPE_UNKNOWN;
    }
}