package com.pushtorefresh.storio.exception;

import android.support.annotation.NonNull;

public class PutException extends RuntimeException {

    private static final long serialVersionUID = 1;

    public PutException(@NonNull String detailMessage) {
        super(detailMessage);
    }
}
