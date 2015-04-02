package com.pushtorefresh.storio.exception;

import android.support.annotation.NonNull;

public class GetException extends RuntimeException {

    private static final long serialVersionUID = 1;

    public GetException(@NonNull String detailMessage) {
        super(detailMessage);
    }
}
