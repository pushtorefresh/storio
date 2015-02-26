package com.pushtorefresh.android.bamboostorage.exception;

import android.support.annotation.NonNull;

public class PutException extends RuntimeException {
    public PutException(@NonNull String detailMessage) {
        super(detailMessage);
    }
}
