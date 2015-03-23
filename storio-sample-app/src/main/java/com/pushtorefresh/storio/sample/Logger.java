package com.pushtorefresh.storio.sample;

import android.support.annotation.NonNull;
import android.util.Log;

import com.pushtorefresh.storio.LogListenerAdapter;

public class Logger extends LogListenerAdapter {

    private static final String TAG = "StorIO-Sample";

    private volatile boolean isEnabled = BuildConfig.DEBUG;

    @Override public void d(@NonNull String message) {
        if (isEnabled) {
            Log.d(TAG, message);
        }
    }

    @Override public void i(@NonNull String message) {
        if (isEnabled) {
            Log.i(TAG, message);
        }
    }

    @Override public void w(@NonNull String message) {
        if (isEnabled) {
            Log.w(TAG, message);
        }
    }

    @Override public void e(@NonNull String message) {
        if (isEnabled) {
            Log.e(TAG, message);
        }
    }

    public void setEnabled(boolean isEnabled) {
        this.isEnabled = isEnabled;
    }
}
