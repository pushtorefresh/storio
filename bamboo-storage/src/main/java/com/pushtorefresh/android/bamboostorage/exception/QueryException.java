package com.pushtorefresh.android.bamboostorage.exception;

import android.support.annotation.NonNull;

public class QueryException extends RuntimeException {
    public QueryException(@NonNull String detailMessage) {
        super(detailMessage);
    }
}
