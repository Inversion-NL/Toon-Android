package com.toonapps.toon.view.fragments;

import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;

import com.toonapps.toon.R;
import com.toonapps.toon.data.IRestClientDebugResponseHandler;
import com.toonapps.toon.data.RestClient;

import org.json.JSONException;
import org.json.JSONObject;

import static androidx.core.content.ContextCompat.getSystemService;

public class Troubleshooting extends Fragment implements IRestClientDebugResponseHandler {

    private RestClient restClient;
    private ProgressDialog progressDialog;
    private View view;
    private int currentAction = -1;
    private TextView debugTV;

    private interface action {
        int ZWAVE_DEVICES = 0;
        int THERMOSTAT_INFO = 1;
        int CURRENT_USAGE = 2;
    }

    public Troubleshooting() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_troubleshooting, container, false);
        restClient = new RestClient(this);

        initProgressDialog();
        setButtonListeners();
        initTextView();

        return view;
    }

    private void initTextView() {
        debugTV = view.findViewById(R.id.debugTV);
        debugTV.setMovementMethod(new ScrollingMovementMethod());

        String message1 = getString(R.string.debug_explanation_message1);
        String message2 = getString(R.string.debug_explanation_message2);
        String message3 = getString(R.string.debug_explanation_message3);
        TextView explanation = view.findViewById(R.id.tv_explanation);
        explanation.setText(message1.concat("\n").concat(message2).concat("\n").concat(message3));
    }

    private void setButtonListeners() {
        Button getZwaveDebugInfo = view.findViewById(R.id.btn_getZwaveDebugInfo);
        getZwaveDebugInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getZwaveDevices();
            }
        });

        final Button getDebugThermostatInfo = view.findViewById(R.id.btn_getDebugThermostatInfo);
        getDebugThermostatInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getThermostatInfo();
            }
        });

        Button getCurrentUsageDebugInfo = view.findViewById(R.id.btn_getCurrentUsageDebugInfo);
        getCurrentUsageDebugInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getCurrentUsage();
            }
        });
    }

    private void initProgressDialog() {
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setMessage(getString(R.string.debug_progressDialog_message));
    }

    @Override
    public void onResponse(String response) {

        SwitchCompat sw_format = view.findViewById(R.id.sw_format);

        if (sw_format.isChecked()) {
            try {

                JSONObject jsonObject = new JSONObject(response);
                String formatted = jsonObject.toString(4);
                debugTV.setText(formatted);

                hideProgressDialog();
                copyToClipboard(formatted);

            } catch (JSONException ignored) {
                /*
                Something went wrong while formatting the received JSON text.
                Let's try again without formatting it.
                 */
                sw_format.setChecked(false);

                switch (currentAction) {
                    case action.CURRENT_USAGE:
                        getCurrentUsage();
                        break;

                    case action.THERMOSTAT_INFO:
                        getThermostatInfo();
                        break;

                    case action.ZWAVE_DEVICES:
                        getZwaveDevices();
                        break;

                    default:
                        break;
                }
            }
        } else {
            debugTV.setText(response);
            sw_format.setChecked(true);
            hideProgressDialog();
            copyToClipboard(response);
        }
    }

    @Override
    public void onResponseError(Exception e) {
        hideProgressDialog();
        String message = getString(R.string.debug_response_error_message).concat(":\n\n").concat(e.toString());
        debugTV.setText(message);
    }

    private void getZwaveDevices() {
        currentAction = action.ZWAVE_DEVICES;
        restClient.getDebugZwaveDevices();
        if (!progressDialog.isShowing()) progressDialog.show();
    }

    private void getCurrentUsage() {
        currentAction = action.CURRENT_USAGE;
        restClient.getDebugCurrentUsage();
        if (!progressDialog.isShowing()) progressDialog.show();
    }

    private void getThermostatInfo() {
        currentAction = action.THERMOSTAT_INFO;
        restClient.getDebugThermostatInfo();
        if (!progressDialog.isShowing()) progressDialog.show();
    }

    private void hideProgressDialog() {
        if (progressDialog != null)
            if (progressDialog.isShowing()) progressDialog.dismiss();
    }

    private void copyToClipboard(String text) {
        if (getContext() != null) {
            String label = getString(R.string.debug_clipboard_label);
            ClipboardManager clipboard = getSystemService(getContext(), ClipboardManager.class);
            ClipData clip = ClipData.newPlainText(label, text);
            if (clipboard != null) {
                clipboard.setPrimaryClip(clip);
                Toast.makeText(getContext(), R.string.debug_toast_rawDataCopiedToClipboard, Toast.LENGTH_LONG).show();
            }
        }
    }
}