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

import java.util.HashMap;

import timber.log.Timber;

public class myFirebaseMessagingService extends FirebaseMessagingService {

    @Override
    public void onNewToken(@NonNull String s) {
        //Todo Talk to MainActivity to tell the user to update their FCM token in Toon

        AppSettings mAppSettings = AppSettings.getInstance();
        mAppSettings.initialize(this);

        mAppSettings.setFirebaseInstanceId(s);
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

            NotificationHelper
                    .createSimpleNotification(
                            getApplicationContext(),
                            title,
                            body,
                            remoteMessage
                    );

        } else {

            // When there is only data in the FCM message and no notification
            // When there is also notification data in the message, the data isn't received here
            // when the app isn't active.
            // When the user clicks the notification, the data is in a extra bundle

            HashMap<String, String> hashMap =
                    NotificationHelper.convertToHashMap(remoteMessage.getData());
            if (hashMap.size() > 0) {
                switch (NotificationHelper.getNotificationType(remoteMessage)){

                    case NotificationHelper.FCM_NOTIFICATION.TYPE.ALARM:
                        createAlarmNotification(remoteMessage);
                        break;

                    case NotificationHelper.FCM_NOTIFICATION.TYPE.NOTIFICATION:
                        createNormalNotification(remoteMessage);
                        break;
                }
            } else {
                Timber.d("Data message received from Firebase Cloud Messaging but both notification and data fields were empty!");
            }
        }

        super.onMessageReceived(remoteMessage);
    }

    private void createAlarmNotification(RemoteMessage remoteMessage) {

        String title;

        switch (NotificationHelper.getAlarmType(remoteMessage)){

            case NotificationHelper.FCM_NOTIFICATION.SUBTYPE.ALARM.SMOKE:

                title = getString(R.string.fcm_notification_alarmSmokeSensors);

                String sensor = NotificationHelper.getSensorName(this, remoteMessage);
                String room = NotificationHelper.getRoomName(this, remoteMessage);
                String text =
                    String.format(
                        getString(R.string.fcm_notification_reportedSmokeAlarm),
                        sensor,
                        room
                );

                NotificationHelper
                        .createAlarmNotification(
                                getApplicationContext(),
                                title,
                                text,
                                remoteMessage);
                break;

            case NotificationHelper.FCM_NOTIFICATION.SUBTYPE.ALARM.UNKNOWN:

                title = getString(R.string.fcm_notification_alarmSmokeSensors);

                NotificationHelper
                        .createAlarmNotification(
                                getApplicationContext(),
                                title,
                                getString(R.string.fcm_notification_unknownAlarm),
                                remoteMessage
                        );
                break;
        }
    }

    private void createNormalNotification(RemoteMessage remoteMessage) {

        String title = NotificationHelper.getNormalNotificationTitle(remoteMessage);
        String message = NotificationHelper.getNormalNotificationMessage(remoteMessage);

        if (!title.isEmpty() || !title.equals("")) {

            NotificationHelper
                    .createSimpleNotification(
                            getApplicationContext(),
                            title,
                            message,
                            remoteMessage
                    );
        }
    }
}