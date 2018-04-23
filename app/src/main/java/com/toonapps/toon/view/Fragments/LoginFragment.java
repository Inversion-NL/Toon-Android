package com.toonapps.toon.view.Fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import com.heinrichreimersoftware.materialintro.app.SlideFragment;
import com.toonapps.toon.R;
import com.toonapps.toon.controller.DeviceController;
import com.toonapps.toon.controller.IDeviceListener;
import com.toonapps.toon.entity.DeviceInfo;
import com.toonapps.toon.helper.AppSettings;
import com.toonapps.toon.helper.ErrorMessage;

public class LoginFragment extends SlideFragment {

    private boolean loggedIn = false;
    private Context context;
    private EditText address;
    private EditText port;
    private ProgressDialog progressDialog;
    private TextView txt_errorMessage;
    private AppCompatButton btn_login;

    private final boolean testing = true;

    public LoginFragment() {
        // Required empty public constructor
    }

    public static LoginFragment newInstance() {
        return new LoginFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_connect_login, container, false);

        // Setting to prevent keyboard from changing the layout
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN |
                WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        address = view.findViewById(R.id.et_toonAddress);
        port = view.findViewById(R.id.et_toonPort);
        txt_errorMessage = view.findViewById(R.id.txt_errorMessage);

        address.setText(AppSettings.getInstance().getAddress());

        int portInt = AppSettings.getInstance().getPort();
        if (portInt == 0) port.setText("");
        else port.setText(String.valueOf(portInt));

        btn_login = view.findViewById(R.id.btn_login);
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkFields()) updateData();
                else advancedFieldCheck();
            }
        });

        return view;
    }

    private void updateData() {
        showProgressDialog();

        String url = "http://" + address.getText().toString() + ":" + port.getText().toString();
        AppSettings.getInstance().setUrl(url);

        DeviceController.getInstance().subscribe(new IDeviceListener() {
            @Override
            public void onDeviceInfoChanged(DeviceInfo aDevicesInfo) {
                loggedIn = true;
                dismissProgressDialog();
                txt_errorMessage.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
                txt_errorMessage.setText(R.string.connectionWizard_login_msg_loginSuccessful);
            }

            @Override
            public void onDeviceError(Exception e) {
                try {
                    // OkHTTPClient makes several requests.
                    // If the second request get's here, there might not be an activity
                    // attached anymore resulting in a crash of the app
                    dismissProgressDialog();

                    //noinspection ConstantConditions
                    if (testing) {
                        txt_errorMessage.setTextColor(getResources().getColor(android.R.color.black));
                        txt_errorMessage.setText(R.string.connectionWizard_login_msg_loginSuccessful);
                        loggedIn = true;

                    } else {

                        btn_login.setText(R.string.connectionWizard_login_buttonLogin_retryText);

                        if (e instanceof IllegalArgumentException)
                            // If the error has to to with the host name, set error on addres widget
                            // rather than setting a generic error
                            address.setError(getString(R.string.exception_message_incorrectHostname));
                        else {
                            String message = ErrorMessage.getInstance(context).getHumanReadableErrorMessage(e);
                            txt_errorMessage.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
                            txt_errorMessage.setText(message);
                        }

                        loggedIn = false;
                    }
                } catch (IllegalStateException illegalState) {
                    illegalState.printStackTrace();
                }
            }
        });
        DeviceController.getInstance().updateDeviceInfo();
    }

    private void showProgressDialog() {
        progressDialog = new ProgressDialog(context);
        progressDialog.setTitle(getString(R.string.connectionWizard_login_msg_loading_title));
        progressDialog.setMessage(getString(R.string.connectionWizard_login_msg_loading_text));
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    private void dismissProgressDialog() {
        progressDialog.dismiss();
    }

    private boolean checkFields() {
        return !TextUtils.isEmpty(address.getText().toString()) && !TextUtils.isEmpty(port.getText().toString());
    }

    private void advancedFieldCheck() {
        // Address text field checks
        String addressText = address.getText().toString();
        if(TextUtils.isEmpty(addressText))
            address.setError("Please enter address");
        else if (addressText.length() < 2)
            address.setError("Not a valid input");

        // Port text field checks
        String portText = port.getText().toString();
        if (TextUtils.isEmpty(portText))
            port.setError("Please enter a port number");
        try {
            int portNumber = Integer.valueOf(portText);
            if (portNumber < 1 || portNumber > 65535) port.setError("Enter a valid port number");
        } catch (NumberFormatException e) {
            port.setError("Please only use numbers");
        }

    }

    @Override
    public boolean canGoForward() {
        return loggedIn;
    }

    @Override
    public void onAttach(Context context) {
        this.context = context;
        super.onAttach(context);
    }
}