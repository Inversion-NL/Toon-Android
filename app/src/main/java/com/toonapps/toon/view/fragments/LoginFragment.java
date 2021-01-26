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

package com.toonapps.toon.view.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.heinrichreimersoftware.materialintro.app.SlideFragment;
import com.toonapps.toon.R;
import com.toonapps.toon.controller.DeviceController;
import com.toonapps.toon.controller.IDeviceListener;
import com.toonapps.toon.entity.DeviceInfo;
import com.toonapps.toon.helper.AppSettings;
import com.toonapps.toon.helper.ErrorMessage;
import com.toonapps.toon.helper.FirebaseHelper;
import com.toonapps.toon.helper.ScreenHelper;

public class LoginFragment extends SlideFragment {

    private boolean loggedIn = false;
    private Context context;
    private EditText et_toonAddress;
    private EditText et_toonPort;
    private ProgressDialog progressDialog;
    private TextView txt_errorMessage;
    private AppCompatButton btn_login;
    private RadioButton rb_protocol_https;
    private AppCompatCheckBox cb_advancedSettings;
    private ConstraintLayout cl_advancedSettings;
    private TextInputEditText et_httpHeaderValue;
    private TextInputEditText et_httpHeaderKey;

    private final boolean testing = false;
    private boolean usingAdvancedSettings = false;

    private interface errorCheckType {
        int TYPE_STRING = 0;
        int TYPE_INT = 1;
    }

    public LoginFragment() {
        // Required empty public constructor
    }

    public static LoginFragment newInstance() {
        return new LoginFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_connect_login, container, false);

        if (getActivity() != null) //noinspection HardCodedStringLiteral
            FirebaseAnalytics.getInstance(context)
                .setCurrentScreen(getActivity(), "Login fragment",null);

        // Setting to prevent keyboard from changing the layout
        if (getActivity() != null) getActivity()
                .getWindow()
                .setSoftInputMode(
                        WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN |
                        WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        setAndInitWidgets(view);
        return view;
    }

    private void setAndInitWidgets(View view) {
        if (ScreenHelper.getDisplayDensityDpi(context, false) == ScreenHelper.DISPLAY_DENSITY.LOW) {
            // Hide image for small devices
            AppCompatImageView img_login = view.findViewById(R.id.img_login);
            img_login.setVisibility(View.GONE);
        }

        if (AppSettings.getInstance() != null) {
            // Sometimes either AppSettings or sharedPref in Appsettings is null

            et_toonAddress = view.findViewById(R.id.et_toonAddress);
            et_toonPort = view.findViewById(R.id.et_toonPort);
            RadioButton rb_protocol_http = view.findViewById(R.id.rb_protocol_http);
            rb_protocol_https = view.findViewById(R.id.rb_protocol_https);
            txt_errorMessage = view.findViewById(R.id.txt_errorMessage);
            RadioGroup rg = view.findViewById(R.id.rg_protocol);
            rg.setEnabled(false);

            et_toonAddress.setText(AppSettings.getInstance().getAddress());

            int portInt = AppSettings.getInstance().getPort();
            if (portInt == 0) et_toonPort.setText("");
            else et_toonPort.setText(String.valueOf(portInt));

            //noinspection HardCodedStringLiteral
            if (AppSettings.getInstance().getProtocol().equals("https")) {
                rb_protocol_https.setChecked(true);
            } else rb_protocol_http.setChecked(true);

            btn_login = view.findViewById(R.id.btn_login);
            btn_login.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (checkFields()) updateData();
                    else advancedFieldCheck();
                }
            });

            cl_advancedSettings = view.findViewById(R.id.cl_advancedSettings);

            et_httpHeaderKey = view.findViewById(R.id.et_httpHeaderKey);
            String httpHeaderKey = AppSettings.getInstance().getHttpHeaderKey();
            et_httpHeaderKey.setText(httpHeaderKey);

            String httpHeaderValue = AppSettings.getInstance().getHttpHeaderValue();
            et_httpHeaderValue = view.findViewById(R.id.et_httpHeaderValue);
            et_httpHeaderValue.setText(httpHeaderValue);

            cb_advancedSettings = view.findViewById(R.id.cb_advancedSettings);
            if (AppSettings.getInstance().useHttpHeader()) {
                cb_advancedSettings.setChecked(true);
                cl_advancedSettings.setVisibility(View.VISIBLE);
            } else {
                cl_advancedSettings.setVisibility(View.GONE);
            }
            cb_advancedSettings.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) cl_advancedSettings.setVisibility(View.VISIBLE);
                    else cl_advancedSettings.setVisibility(View.GONE);
                }
            });
        }
    }

    private void updateData() {
        showProgressDialog();

        AppSettings.getInstance().setPort(Integer.parseInt(et_toonPort.getText().toString()));
        AppSettings.getInstance().setAddress(et_toonAddress.getText().toString());
        AppSettings.getInstance().setProtocol(getProtocolFromRadioButtons());
        AppSettings.getInstance().setUseHttpHeader(usingAdvancedSettings);

        if (usingAdvancedSettings) {
            AppSettings.getInstance().setHttpHeaderKey(et_httpHeaderKey.getText().toString());
            AppSettings.getInstance().setHttpHeaderValue(et_httpHeaderValue.getText().toString());
        }

        DeviceController.getInstance().subscribe(new IDeviceListener() {
            @Override
            public void onDeviceInfoChanged(DeviceInfo aDevicesInfo) {
                loggedIn = true;
                dismissProgressDialog();
                if (isAdded()) {
                    txt_errorMessage.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
                    txt_errorMessage.setText(R.string.connectionWizard_login_msg_loginSuccessful);
                }
            }

            @Override
            public void onDeviceError(Exception e) {
                try {
                    // OkHTTPClient makes several requests.
                    // If the second request get's here, there might not be an activity
                    // attached anymore resulting in a crash of the app
                    dismissProgressDialog();

                    if (testing && isAdded()) {
                        txt_errorMessage.setTextColor(getResources().getColor(android.R.color.black));
                        txt_errorMessage.setText(R.string.testing);
                        loggedIn = true;

                    } else {

                        btn_login.setText(R.string.connectionWizard_login_buttonLogin_retryText);

                        if (e instanceof IllegalArgumentException && isAdded())
                            // If the error has to to with the host name, set error on address widget
                            // rather than setting a generic error
                            et_toonAddress.setError(getString(R.string.exception_message_incorrectHostname));
                        else if (isAdded()){
                            ErrorMessage errorMessage = new ErrorMessage(context);
                            String message = errorMessage.getHumanReadableErrorMessage(e);
                            txt_errorMessage.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
                            txt_errorMessage.setText(message);
                        }

                        loggedIn = false;
                    }
                } catch (IllegalStateException illegalState) {
                    //noinspection HardCodedStringLiteral
                    FirebaseHelper.getInstance().recordExceptionAndLog(illegalState, "Illegal state in LoginFragment onDeviceError");
                    illegalState.printStackTrace();
                }
            }
        });
        DeviceController.getInstance().updateZWaveDevices();
    }

    private String getProtocolFromRadioButtons() {
        if (rb_protocol_https.isChecked()) return context.getString(R.string.connectionWizard_login_protocol_https);
        else return context.getString(R.string.connectionWizard_login_protocol_http);
    }

    private void showProgressDialog() {
        progressDialog = new ProgressDialog(context);
        progressDialog.setTitle(getString(R.string.connectionWizard_login_msg_loading_title));
        progressDialog.setMessage(getString(R.string.connectionWizard_login_msg_loading_text));
        progressDialog.setCancelable(true);
        progressDialog.show();
        progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                cancelAllRequests();
            }
        });
    }

    private void dismissProgressDialog() {
        if (progressDialog != null) {
            if (progressDialog.isShowing()) progressDialog.dismiss();
        }
    }

    private boolean checkFields() {
        final boolean textFields = !TextUtils.isEmpty(et_toonAddress.getText().toString()) && !TextUtils.isEmpty(et_toonPort.getText().toString());
        boolean advancedFields = true;

        usingAdvancedSettings = cb_advancedSettings.isChecked();

        if (usingAdvancedSettings) {
            advancedFields = !TextUtils.isEmpty(et_httpHeaderValue.getText().toString()) && !TextUtils.isEmpty(et_httpHeaderKey.getText().toString());
        }
        return textFields && advancedFields;
    }

    private void advancedFieldCheck() {
        setErrorField(et_toonAddress, errorCheckType.TYPE_STRING);
        setErrorField(et_toonPort, errorCheckType.TYPE_INT);
        if (usingAdvancedSettings) {
            setErrorField(et_httpHeaderKey, errorCheckType.TYPE_STRING);
            setErrorField(et_httpHeaderValue, errorCheckType.TYPE_STRING);
        }
    }

    private void setErrorField(EditText editText, int type) {
        switch (type){
            case errorCheckType.TYPE_STRING:
                String addressText = editText.getText().toString();
                if(TextUtils.isEmpty(addressText))
                    editText.setError(getString(R.string.connectionWizard_login_error_pleaseEnterAddress));
                else if (addressText.length() < 2)
                    editText.setError(getString(R.string.connectionWizard_login_error_noValidInput));
                break;

            case errorCheckType.TYPE_INT:
                String portText = editText.getText().toString();
                if (TextUtils.isEmpty(portText))
                    editText.setError(getString(R.string.connectionWizard_login_error_pleaseEnterNumber));
                try {
                    int portNumber = Integer.parseInt(portText);
                    if (portNumber < 1 || portNumber > 65535) editText.setError(getString(R.string.connectionWizard_login_error_enterValidNumber));
                } catch (NumberFormatException e) {
                    editText.setError(getString(R.string.connectionWizard_login_error_pleaseOnlyUseNumbers));
                }
                break;
        }

    }

    private void cancelAllRequests() {
        DeviceController.getInstance().cancelAllRequests();
    }

    @Override
    public boolean canGoForward() {
        return loggedIn;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        this.context = context;
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        // Cancel all pending requests
        cancelAllRequests();

        // On detaching the fragment, dismiss the progress dialog
        dismissProgressDialog();
        super.onDetach();
    }
}