package com.pushtorefresh.storio;

import android.support.annotation.NonNull;

/**
 * Use it instead of {@link LogListener}
 * if you don't wont to override log methods for all levels.
 */
public class LogListenerAdapter implements LogListener {

    @Override public void v(@NonNull String message) {
    }

    @Override public void d(@NonNull String message) {
    }

    @Override public void i(@NonNull String message) {
    }

    @Override public void w(@NonNull String message) {
    }

    @Override public void e(@NonNull String message) {
    }
}
