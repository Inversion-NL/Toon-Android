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
import android.content.Context;
import android.content.IntentSender;

import androidx.appcompat.app.AlertDialog;

import com.google.android.play.core.appupdate.AppUpdateInfo;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.install.model.UpdateAvailability;
import com.google.android.play.core.tasks.Task;
import com.toonapps.toon.R;

import static com.google.android.play.core.install.model.AppUpdateType.IMMEDIATE;


public class AppUpdateHelper {

    private static final int DAYS_FOR_FLEXIBLE_UPDATE = 31;
    private static Context context;

    @SuppressWarnings("ConstantConditions")
    public static void checkForAppUpdate(
            Activity context,
            int REQUEST_CODE_APP_UPDATE) {

        AppUpdateHelper.context = context;
        AppSettings mAppSettings = AppSettings.getInstance();
        mAppSettings.initialize(context);

        final AppUpdateManager appUpdateManager = AppUpdateManagerFactory.create(context);
        Task<AppUpdateInfo> appUpdateInfoTask = appUpdateManager.getAppUpdateInfo();

        appUpdateInfoTask.addOnSuccessListener(appUpdateInfo -> {
            try {
                if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                        && appUpdateInfo.isUpdateTypeAllowed(IMMEDIATE)
                        && appUpdateInfo.clientVersionStalenessDays() != null
                        && appUpdateInfo.clientVersionStalenessDays() >= DAYS_FOR_FLEXIBLE_UPDATE) {

                    long previousUpdateTime = mAppSettings.getAppUpdateDialogShownDate();
                    if (TimeHelper.isMoreThanNumberOfDaysAgoFromNow(previousUpdateTime, 7)) {
                        showAppUpdateAvailableDialog(context, REQUEST_CODE_APP_UPDATE, appUpdateManager, appUpdateInfo);
                    }

                }
            } catch (Exception ignore) {}
        });
    }

    private static void showAppUpdateAvailableDialog(
            Activity context,
            int REQUEST_CODE_APP_UPDATE,
            AppUpdateManager appUpdateManager,
            AppUpdateInfo appUpdateInfo) {

        AlertDialog.Builder powerProductionDialog = new AlertDialog.Builder(context)
                .setTitle(R.string.dialog_appUpdate_available_title)
                .setMessage(R.string.dialog_appUpdate_available_msg)
                .setCancelable(true)
                .setIcon(android.R.drawable.ic_dialog_alert);
        powerProductionDialog.setPositiveButton(R.string.dialog_button_yes, (dialog, which) -> updateApp(context, REQUEST_CODE_APP_UPDATE, appUpdateManager, appUpdateInfo));

        powerProductionDialog.setNegativeButton(R.string.dialog_button_notNow, (dialog, which) -> {

            FirebaseHelper.getInstance(context).recordLog(FirebaseHelper.EVENT.APP_UPDATE.UPDATE_DIALOG_DISMISSED);
            AppSettings.getInstance().setAppUpdateDialogShownDate(TimeHelper.getNow().getTimeInMillis());
        });

        powerProductionDialog.show();
    }

    private static void updateApp(
            Activity context,
            int REQUEST_CODE_APP_UPDATE,
            AppUpdateManager appUpdateManager,
            AppUpdateInfo appUpdateInfo) {
        try {
            appUpdateManager.startUpdateFlowForResult(
                    appUpdateInfo,
                    IMMEDIATE,
                    context,
                    REQUEST_CODE_APP_UPDATE
            );
        } catch (IntentSender.SendIntentException e) {
            //noinspection HardCodedStringLiteral
            FirebaseHelper
                    .getInstance(context)
                    .recordExceptionAndLog(
                            e,
                            "Exception while requesting app update (to first start the app update) : "
                                    + e.getMessage()
                    );
        }
    }

    public static void checkIfAppUpdatedSuccessfully(
            Activity context,
            int REQUEST_CODE_APP_UPDATE) {
        final AppUpdateManager appUpdateManager = AppUpdateManagerFactory.create(context);

        appUpdateManager
                .getAppUpdateInfo()
                .addOnSuccessListener(
                        appUpdateInfo -> {
                            if (appUpdateInfo.updateAvailability()
                                    == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS) {
                                // If an in-app update is already running, resume the update.
                                try {
                                    appUpdateManager.startUpdateFlowForResult(
                                            appUpdateInfo,
                                            IMMEDIATE,
                                            context,
                                            REQUEST_CODE_APP_UPDATE);
                                } catch (IntentSender.SendIntentException e) {
                                    e.printStackTrace();
                                    //noinspection HardCodedStringLiteral
                                    FirebaseHelper
                                            .getInstance(context)
                                            .recordExceptionAndLog(
                                                    e,
                                                    "Exception while requesting app update (DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS) : "
                                                            + e.getMessage()
                                            );
                                }
                            }
                        });
    }
}