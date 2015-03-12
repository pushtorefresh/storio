package com.pushtorefresh.android.bamboostorage.db.exception;

import android.support.annotation.NonNull;

public class GetException extends RuntimeException {
    public GetException(@NonNull String detailMessage) {
        super(detailMessage);
    }
}
