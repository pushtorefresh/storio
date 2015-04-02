package com.pushtorefresh.storio.exception;

import android.support.annotation.NonNull;

public class DeleteException extends RuntimeException {

    private static final long serialVersionUID = 1;

    public DeleteException(@NonNull String detailMessage) {
        super(detailMessage);
    }
}
