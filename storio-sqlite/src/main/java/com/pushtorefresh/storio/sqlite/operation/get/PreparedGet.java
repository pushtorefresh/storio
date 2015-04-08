package com.pushtorefresh.storio.sqlite.operation.get;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.pushtorefresh.storio.sqlite.StorIOSQLite;
import com.pushtorefresh.storio.operation.PreparedOperationWithReactiveStream;
import com.pushtorefresh.storio.sqlite.query.Query;
import com.pushtorefresh.storio.sqlite.query.RawQuery;

public abstract class PreparedGet<T> implements PreparedOperationWithReactiveStream<T> {

    @NonNull
    protected final StorIOSQLite storIOSQLite;

    @Nullable
    protected final Query query;

    @Nullable
    protected final RawQuery rawQuery;

    @NonNull
    protected final GetResolver getResolver;

    PreparedGet(@NonNull StorIOSQLite storIOSQLite, @NonNull Query query, @NonNull GetResolver getResolver) {
        this.storIOSQLite = storIOSQLite;
        this.query = query;
        this.getResolver = getResolver;
        this.rawQuery = null;
    }

    PreparedGet(@NonNull StorIOSQLite storIOSQLite, @NonNull RawQuery rawQuery, @NonNull GetResolver getResolver) {
        this.storIOSQLite = storIOSQLite;
        this.rawQuery = rawQuery;
        this.getResolver = getResolver;
        query = null;
    }

    public static class Builder {

        @NonNull
        private final StorIOSQLite storIOSQLite;

        public Builder(@NonNull StorIOSQLite storIOSQLite) {
            this.storIOSQLite = storIOSQLite;
        }

        @NonNull
        public PreparedGetCursor.Builder cursor() {
            return new PreparedGetCursor.Builder(storIOSQLite);
        }

        @NonNull
        public <T> PreparedGetListOfObjects.Builder<T> listOfObjects(@NonNull Class<T> type) {
            return new PreparedGetListOfObjects.Builder<T>(storIOSQLite, type);
        }
    }

}
