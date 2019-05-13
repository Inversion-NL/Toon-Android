package com.toonapps.toon.helper;

import android.app.Activity;

import java.util.Timer;
import java.util.TimerTask;

public class TimerHelper {

    private final TimerInterface listener;
    private final long delay;
    private final Activity mActivity;
    private Timer timer;

    public TimerHelper(Activity mActivity, long delay, TimerInterface listener) {
        this.mActivity = mActivity;
        this.delay = delay;
        this.listener = listener;
    }

    private TimerTask timerTask = new TimerTask() {
        @Override
        public void run() {
            mActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    listener.onTime();
                }
            });
        }
    };

    public void start() {
        if(timer != null) {
            return;
        }
        timer = new Timer();
        timer.scheduleAtFixedRate(timerTask, delay, delay);
    }

    public void stop() {
        if (timer != null) timer.cancel();
        timer = null;
    }

    public interface TimerInterface {
        void onTime();
    }
}