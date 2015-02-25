package com.pushtorefresh.android.bamboostorage.exception;

import android.support.annotation.NonNull;

public class UpdateException extends RuntimeException {
    public UpdateException(@NonNull String detailMessage) {
        super(detailMessage);
    }
}
