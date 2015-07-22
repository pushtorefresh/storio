package com.pushtorefresh.storio;

import android.support.annotation.NonNull;

/**
 * Common {@link RuntimeException} for all errors and exceptions occurred during StorIO operations.
 */
public class StorIOException extends RuntimeException {

    /**
     * {@inheritDoc}
     */
    public StorIOException(@NonNull final String detailMessage) {
        super(detailMessage);
    }

    /**
     * {@inheritDoc}
     */
    public StorIOException(@NonNull final String detailMessage, @NonNull final Throwable throwable) {
        super(detailMessage, throwable);
    }

    /**
     * {@inheritDoc}
     */
    public StorIOException(@NonNull final Throwable throwable) {
        super(throwable);
    }
}
