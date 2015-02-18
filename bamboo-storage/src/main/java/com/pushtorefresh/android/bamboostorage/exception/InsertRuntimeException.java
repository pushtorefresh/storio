package com.pushtorefresh.android.bamboostorage.exception;

import android.support.annotation.NonNull;

public class InsertRuntimeException extends RuntimeException {

    public InsertRuntimeException(@NonNull String message) {
        super(message);
    }
}
