package com.pushtorefresh.storio.contentprovider.operation.put;

import android.support.annotation.NonNull;

import com.pushtorefresh.storio.contentprovider.StorIOContentProvider;

public class PreparedPut {

    public static class Builder {

        @NonNull private final StorIOContentProvider storIOContentProvider;

        public Builder(@NonNull StorIOContentProvider storIOContentProvider) {
            this.storIOContentProvider = storIOContentProvider;
        }
    }
}
