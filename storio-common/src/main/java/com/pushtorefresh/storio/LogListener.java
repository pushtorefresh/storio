package com.pushtorefresh.storio;

import android.support.annotation.NonNull;

/**
 * Interface to redirect log output to client.
 */
public interface LogListener {

    void v(@NonNull String message);

    void d(@NonNull String message);

    void i(@NonNull String message);

    void w(@NonNull String message);

    void e(@NonNull String message);
}