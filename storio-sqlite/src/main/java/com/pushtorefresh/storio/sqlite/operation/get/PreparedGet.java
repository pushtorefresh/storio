package com.pushtorefresh.storio.sqlite.operation.get;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.pushtorefresh.storio.operation.PreparedOperationWithReactiveStream;
import com.pushtorefresh.storio.sqlite.StorIOSQLite;
import com.pushtorefresh.storio.sqlite.query.Query;
import com.pushtorefresh.storio.sqlite.query.RawQuery;

public abstract class PreparedGet<T> implements PreparedOperationWithReactiveStream<T> {

    @NonNull
    protected final StorIOSQLite storIOSQLite;

    @Nullable
    protected final Query query;

    @Nullable
    protected final RawQuery rawQuery;

    PreparedGet(@NonNull StorIOSQLite storIOSQLite, @NonNull Query query) {
        this.storIOSQLite = storIOSQLite;
        this.query = query;
        this.rawQuery = null;
    }

    PreparedGet(@NonNull StorIOSQLite storIOSQLite, @NonNull RawQuery rawQuery) {
        this.storIOSQLite = storIOSQLite;
        this.rawQuery = rawQuery;
        query = null;
    }

    public static final class Builder {

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
