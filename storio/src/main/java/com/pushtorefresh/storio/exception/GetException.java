package com.pushtorefresh.storio.exception;

import android.support.annotation.NonNull;

public class GetException extends RuntimeException {
    public GetException(@NonNull String detailMessage) {
        super(detailMessage);
    }
}
