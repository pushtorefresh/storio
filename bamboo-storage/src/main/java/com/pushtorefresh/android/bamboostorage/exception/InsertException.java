package com.pushtorefresh.android.bamboostorage.exception;

import android.support.annotation.NonNull;

public class InsertException extends RuntimeException {
    public InsertException(@NonNull String detailMessage) {
        super(detailMessage);
    }
}
