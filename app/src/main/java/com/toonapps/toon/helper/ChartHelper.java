package com.toonapps.toon.helper;

import android.content.Context;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.drawable.Drawable;
import android.view.View;

import androidx.core.content.ContextCompat;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineDataSet;
import com.toonapps.toon.R;

import java.util.ArrayList;
import java.util.Calendar;

public class ChartHelper {

    private static final int startNormalTariff = 7;
    private static final int endNormalTariff = 23;

    public static LineChart getDefaultUsageChart(View view, int chartId) {
        LineChart chart = view.findViewById(chartId);
        chart.setDrawBorders(true);
        // no description text
        chart.getDescription().setEnabled(false);
        // if disabled, scaling can be done on x- and y-axis separately
        chart.setPinchZoom(false);

        // Disable chart legend
        Legend l = chart.getLegend();
        l.setEnabled(false);

        // Enable x axis labels
        XAxis xAxis = chart.getXAxis();
        xAxis.setEnabled(true);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setLabelRotationAngle(300);
        xAxis.setGranularity(1f);

        // Disable numbers on the right axes
        chart.getAxisRight().setEnabled(false);
        return chart;
    }

    public static LineDataSet getSingleTariffDataSet(Context context, ArrayList<Entry> values, String graphTitle) {
        LineDataSet set;

        set = new LineDataSet(values, graphTitle);

        set.setDrawIcons(false);
        set.enableDashedLine(10f, 5f, 0f);
        set.enableDashedHighlightLine(10f, 5f, 0f);
        set.setHighlightEnabled(true);
        set.setColor(Color.DKGRAY);
        set.setCircleColor(Color.DKGRAY);
        set.setLineWidth(1f);
        set.setCircleRadius(3f);
        set.setDrawCircleHole(false);
        set.setValueTextSize(9f);
        set.setDrawFilled(true);
        set.setFormLineWidth(1f);
        set.setFormLineDashEffect(new DashPathEffect(new float[]{10f, 5f}, 0f));
        set.setFormSize(15.f);
        if (android.os.Build.VERSION.SDK_INT >= 18) {
            Drawable drawable = ContextCompat.getDrawable(context, R.drawable.fade_red);
            set.setFillDrawable(drawable);
        } else {
            set.setFillColor(Color.RED);
        }

        return set;
    }

    public static LineDataSet getLowTariffDataSet(Context context, ArrayList<Entry> values, String graphTitle) {
        LineDataSet set;

        set = new LineDataSet(values, graphTitle);

        set.setDrawIcons(false);
        set.enableDashedLine(10f, 5f, 0f);
        set.enableDashedHighlightLine(10f, 5f, 0f);
        set.setHighlightEnabled(true);
        set.setColor(Color.DKGRAY);
        set.setCircleColor(Color.DKGRAY);
        set.setLineWidth(1f);
        set.setCircleRadius(3f);
        set.setDrawCircleHole(false);
        set.setValueTextSize(9f);
        set.setDrawFilled(true);
        set.setFormLineWidth(1f);
        set.setFormLineDashEffect(new DashPathEffect(new float[]{10f, 5f}, 0f));
        set.setFormSize(15.f);
        if (android.os.Build.VERSION.SDK_INT >= 18) {
            Drawable drawable = ContextCompat.getDrawable(context, R.drawable.fade_blue);
            set.setFillDrawable(drawable);
        } else {
            set.setFillColor(Color.BLUE);
        }

        return set;
    }

    public static ArrayList[] getDoubleTariffValues(ArrayList<Entry> values) {
        ArrayList<Entry> normalTariffValues = new ArrayList<>();
        ArrayList<Entry> lowTariffValues = new ArrayList<>();

        ArrayList[] valuesArray = new ArrayList[2];

        Calendar timePoint = Calendar.getInstance();

        for (Entry entry: values ) {
            float time = entry.getX();
            timePoint.setTimeInMillis((long) entry.getX() * 1000);
            int hour = timePoint.get(Calendar.HOUR_OF_DAY);

            if (
                timePoint.get(Calendar.HOUR_OF_DAY) >= startNormalTariff &&
                timePoint.get(Calendar.HOUR_OF_DAY) < endNormalTariff){
                    normalTariffValues.add(entry);
            } else {
                lowTariffValues.add(entry);
            }
        }

        //normalTariffValues = values;

        valuesArray[0] = normalTariffValues;
        valuesArray[1] = lowTariffValues;

        return valuesArray;
    }
}