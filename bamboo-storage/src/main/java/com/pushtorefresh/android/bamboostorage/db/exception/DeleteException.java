package com.pushtorefresh.android.bamboostorage.db.exception;

import android.support.annotation.NonNull;

public class DeleteException extends RuntimeException {
    public DeleteException(@NonNull String detailMessage) {
        super(detailMessage);
    }
}
