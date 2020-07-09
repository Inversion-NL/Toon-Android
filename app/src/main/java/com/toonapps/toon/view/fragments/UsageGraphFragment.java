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

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatToggleButton;
import androidx.fragment.app.Fragment;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.toonapps.toon.R;
import com.toonapps.toon.controller.GasAndElecFlowController;
import com.toonapps.toon.controller.IGasAndElecFlowListener;
import com.toonapps.toon.entity.UsageInfo;
import com.toonapps.toon.helper.AppSettings;
import com.toonapps.toon.helper.ChartHelper;
import com.toonapps.toon.helper.TimeHelper;

import org.json.JSONException;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class UsageGraphFragment extends Fragment implements IGasAndElecFlowListener, CompoundButton.OnCheckedChangeListener {

    public interface TYPE {
        int ELEC = 0;
        int GAS = 1;
    }

    @SuppressWarnings({"FieldCanBeLocal", "HardCodedStringLiteral"})
    private final String REQUEST_TYPE = "type";

    @SuppressWarnings("HardCodedStringLiteral")
    private final String STATE_ACTIVE_BUTTON = "activeButton";
    private LineChart chart;
    private Context mContext;
    private TextView highlightValue;
    private View view;
    private AppCompatToggleButton firstToggle;
    private AppCompatToggleButton secondToggle;
    private AppCompatToggleButton thirdToggle;
    private int activeButton;
    private int type;

    public UsageGraphFragment() {
        // Required empty public constructor
    }

    // TODO Graph Normal Tariff and Low Tariff

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_usage_graph, container, false);

        setChart();
        setResources();

        Bundle arguments = getArguments();
        if (arguments != null) {
            type = arguments.getInt(REQUEST_TYPE, TYPE.ELEC);
            checkSavedInstanceState(savedInstanceState);
            setAnalyticsType();
        }

        return view;
    }

    private void setAnalyticsType() {

        if (getActivity() != null) {

            FirebaseAnalytics mFirebaseAnalytics = FirebaseAnalytics.getInstance(getActivity());

            switch (type) {
                case TYPE.ELEC:
                    //noinspection HardCodedStringLiteral
                    mFirebaseAnalytics.setCurrentScreen(getActivity(), "Graph fragment - Elec",null);

                case TYPE.GAS:
                    //noinspection HardCodedStringLiteral
                    mFirebaseAnalytics.setCurrentScreen(getActivity(), "Graph fragment - Gas",null);
            }
        }
    }

    private void checkSavedInstanceState(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            activeButton = savedInstanceState.getInt(STATE_ACTIVE_BUTTON);
        } else {
            activeButton = R.id.firstToggle;
            firstToggle.setChecked(true);   // This will cause Android to think the button has been set
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        GasAndElecFlowController.getInstance().subscribe(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        GasAndElecFlowController.getInstance().unsubscribe(this);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(STATE_ACTIVE_BUTTON, activeButton);
    }

    private void setResources() {

        highlightValue = view.findViewById(R.id.tv_highlightValue);

        firstToggle = view.findViewById(R.id.firstToggle);
        secondToggle = view.findViewById(R.id.secondToggle);
        thirdToggle = view.findViewById(R.id.thirdToggle);

        firstToggle.setOnCheckedChangeListener(this);
        secondToggle.setOnCheckedChangeListener(this);
        thirdToggle.setOnCheckedChangeListener(this);
    }

    private void setChart() {
        chart = ChartHelper.getDefaultUsageChart(view, R.id.chart);
        chart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {

                float timeInMillis = e.getX();
                long time = (long) timeInMillis;
                Date date = new Date();
                date.setTime(time*1000);
                String dateAndTime = DateFormat.getDateTimeInstance().format(date);

                highlightValue.setText(getOnValueSelectedText(dateAndTime, e.getY()));
            }

            @Override
            public void onNothingSelected() {

            }
        });
    }

    private String getOnValueSelectedText(String dateAndTime, float value) {
        switch (type) {
            case TYPE.ELEC:
                return String.format(Locale.getDefault(), getString(R.string.graph_elec_usage_used), dateAndTime, value);

            case TYPE.GAS:
                return String.format(Locale.getDefault(), getString(R.string.graph_gas_usage_used), dateAndTime, value);
        }
        return "" + value;
    }

    /**
     * Method which can be used by the underlying activity to update the data
     */
    public void updateGraphData(){
        Toast.makeText(mContext, getString(R.string.graph_message_updatingData), Toast.LENGTH_SHORT).show();
        updateData();
    }

    @Override
    public void onUsageChanged(UsageInfo usageInfo) {
        try {
            setChartData(usageInfo.getChartDataPoints());
        } catch (JSONException e) {
            onUsageError(e);
        }
    }

    private void setChartData(ArrayList<Entry> values) {

        boolean doubleTariff = AppSettings.getInstance().isMeterDoubleTariff();
        LineDataSet normalTariff, lowTariff = null;
        ArrayList<Entry> normalTariffValues, lowTariffValues = null;

        if (doubleTariff && type != TYPE.GAS) {
            ArrayList[] valuesArray = ChartHelper.getDoubleTariffValues(values);
            normalTariffValues = valuesArray[0];
            lowTariffValues = valuesArray[1];
        } else normalTariffValues = values;

        if (normalTariffValues.size() < 1) return;

        if (chart.getData() != null &&
                chart.getData().getDataSetCount() > 0) {

            normalTariff = (LineDataSet) chart.getData().getDataSetByIndex(0);
            lowTariff = (LineDataSet) chart.getData().getDataSetByIndex(1);

            if (lowTariff != null) {
                lowTariff.clear();
                lowTariff.setValues(lowTariffValues);
            }

            normalTariff.clear();
            normalTariff.setValues(normalTariffValues);
            chart.getData().notifyDataChanged();
            chart.notifyDataSetChanged();

        } else {
            normalTariff = ChartHelper
                    .getSingleTariffDataSet(
                            mContext,
                            normalTariffValues,
                            getString(R.string.graph_label_electricity)
                    );

            if (lowTariffValues != null) lowTariff = ChartHelper
                    .getLowTariffDataSet(
                            mContext,
                            lowTariffValues,
                            getString(R.string.graph_label_electricity)
                    );
        }

        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
        dataSets.add(normalTariff);
        if (lowTariff != null) dataSets.add(lowTariff);

        LineData data = new LineData(dataSets);
        data.setDrawValues(false);

        chart.setData(data);

        ValueFormatter formatter = new ValueFormatter() {
            @Override
            public String getAxisLabel(float value, AxisBase axis) {
                return TimeHelper.getHumanReadableTime(Locale.getDefault(), (long) value);
            }
        };

        XAxis xAxis = chart.getXAxis();
        xAxis.setValueFormatter(formatter);

        chart.invalidate();

    }

    @Override
    public void onUsageError(Exception e) {
        Toast.makeText(mContext, getString(R.string.graph_message_error_updatingGraphData_txt) + e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.mContext = context;
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        activeButton = buttonView.getId();
        updateData();
    }

    private void updateData(){

        Calendar calendar = Calendar.getInstance();
        long startTime;
        long endTime = calendar.getTimeInMillis() / 1000;

        switch (activeButton){
            case R.id.firstToggle:
                firstToggle.setOnCheckedChangeListener(null);
                secondToggle.setOnCheckedChangeListener(null);
                thirdToggle.setOnCheckedChangeListener(null);
                firstToggle.setChecked(true);
                secondToggle.setChecked(false);
                thirdToggle.setChecked(false);
                firstToggle.setOnCheckedChangeListener(this);
                secondToggle.setOnCheckedChangeListener(this);
                thirdToggle.setOnCheckedChangeListener(this);

                calendar.add(Calendar.HOUR_OF_DAY, -6);
                startTime = calendar.getTimeInMillis() / 1000;

                getData(startTime, endTime);
                break;

            case R.id.secondToggle:
                firstToggle.setOnCheckedChangeListener(null);
                secondToggle.setOnCheckedChangeListener(null);
                thirdToggle.setOnCheckedChangeListener(null);
                firstToggle.setChecked(false);
                secondToggle.setChecked(true);
                thirdToggle.setChecked(false);
                firstToggle.setOnCheckedChangeListener(this);
                secondToggle.setOnCheckedChangeListener(this);
                thirdToggle.setOnCheckedChangeListener(this);

                calendar.add(Calendar.HOUR_OF_DAY, -12);
                startTime = calendar.getTimeInMillis() / 1000;

                getData(startTime, endTime);
                break;

            case R.id.thirdToggle:
                firstToggle.setOnCheckedChangeListener(null);
                secondToggle.setOnCheckedChangeListener(null);
                thirdToggle.setOnCheckedChangeListener(null);
                firstToggle.setChecked(false);
                secondToggle.setChecked(false);
                thirdToggle.setChecked(true);
                firstToggle.setOnCheckedChangeListener(this);
                secondToggle.setOnCheckedChangeListener(this);
                thirdToggle.setOnCheckedChangeListener(this);

                calendar.add(Calendar.DAY_OF_YEAR, -1);
                startTime = calendar.getTimeInMillis() / 1000;

                getData(startTime, endTime);
                break;
        }
    }

    private void getData(long startTime, long endTime){
        switch (type){
            case TYPE.ELEC:
                GasAndElecFlowController.getInstance().updateElecFlow(startTime, endTime);
                break;

            case TYPE.GAS:
                GasAndElecFlowController.getInstance().updateGasFlow(startTime, endTime);
                break;
        }
    }
}