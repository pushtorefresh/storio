package com.pushtorefresh.storio.sqlite.operation.get;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.pushtorefresh.storio.sqlite.StorIOSQLiteDb;
import com.pushtorefresh.storio.operation.PreparedOperationWithReactiveStream;
import com.pushtorefresh.storio.sqlite.query.Query;
import com.pushtorefresh.storio.sqlite.query.RawQuery;

public abstract class PreparedGet<T> implements PreparedOperationWithReactiveStream<T> {

    @NonNull
    protected final StorIOSQLiteDb storIOSQLiteDb;

    @Nullable
    protected final Query query;

    @Nullable
    protected final RawQuery rawQuery;

    @NonNull
    protected final GetResolver getResolver;

    PreparedGet(@NonNull StorIOSQLiteDb storIOSQLiteDb, @NonNull Query query, @NonNull GetResolver getResolver) {
        this.storIOSQLiteDb = storIOSQLiteDb;
        this.query = query;
        this.getResolver = getResolver;
        this.rawQuery = null;
    }

    PreparedGet(@NonNull StorIOSQLiteDb storIOSQLiteDb, @NonNull RawQuery rawQuery, @NonNull GetResolver getResolver) {
        this.storIOSQLiteDb = storIOSQLiteDb;
        this.rawQuery = rawQuery;
        this.getResolver = getResolver;
        query = null;
    }

    public static class Builder {

        @NonNull
        private final StorIOSQLiteDb storIOSQLiteDb;

        public Builder(@NonNull StorIOSQLiteDb storIOSQLiteDb) {
            this.storIOSQLiteDb = storIOSQLiteDb;
        }

        @NonNull
        public PreparedGetCursor.Builder cursor() {
            return new PreparedGetCursor.Builder(storIOSQLiteDb);
        }

        @NonNull
        public <T> PreparedGetListOfObjects.Builder<T> listOfObjects(@NonNull Class<T> type) {
            return new PreparedGetListOfObjects.Builder<T>(storIOSQLiteDb, type);
        }
    }

}
