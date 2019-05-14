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
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.toonapps.toon.R;

import java.util.ArrayList;

public class ChartHelper {

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

        XAxis xAxis = chart.getXAxis();
        xAxis.setEnabled(false);

        // Disable numbers on the right
        chart.getAxisRight().setEnabled(false);
        return chart;
    }

    public static LineDataSet getDefaultLineDataSet(Context context, ArrayList<Entry> values1, String graphTitle) {
        LineDataSet set1;

        set1 = new LineDataSet(values1, graphTitle);

        set1.setDrawIcons(false);
        set1.enableDashedLine(10f, 5f, 0f);
        set1.enableDashedHighlightLine(10f, 5f, 0f);
        set1.setHighlightEnabled(true);
        set1.setColor(Color.DKGRAY);
        set1.setCircleColor(Color.DKGRAY);
        set1.setLineWidth(1f);
        set1.setCircleRadius(3f);
        set1.setDrawCircleHole(false);
        set1.setValueTextSize(9f);
        set1.setDrawFilled(true);
        set1.setFormLineWidth(1f);
        set1.setFormLineDashEffect(new DashPathEffect(new float[]{10f, 5f}, 0f));
        set1.setFormSize(15.f);
        if (android.os.Build.VERSION.SDK_INT >= 18) {
            Drawable drawable = ContextCompat.getDrawable(context, R.drawable.fade_blue);
            set1.setFillDrawable(drawable);
        } else {
            set1.setFillColor(Color.DKGRAY);
        }

        return set1;
    }

    public static ArrayList<ILineDataSet> getDataSets(Context context, ArrayList<Entry> values1, String graphTitle) {
        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
        dataSets.add(getDefaultLineDataSet(context, values1, graphTitle));

        return dataSets;
    }
}