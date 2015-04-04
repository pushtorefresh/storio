package com.pushtorefresh.storio.contentresolver.operation.delete;

import android.support.annotation.NonNull;

import com.pushtorefresh.storio.contentresolver.StorIOContentResolver;
import com.pushtorefresh.storio.operation.PreparedOperation;

public abstract class PreparedDelete<T> implements PreparedOperation<T> {

    /**
     * Builder for {@link PreparedDelete}
     */
    public static class Builder {
        @NonNull
        private final StorIOContentResolver storIOContentResolver;

        public Builder(@NonNull StorIOContentResolver storIOContentResolver) {
            this.storIOContentResolver = storIOContentResolver;
        }
    }
}
