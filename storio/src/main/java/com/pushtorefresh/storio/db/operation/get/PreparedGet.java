package com.pushtorefresh.storio.db.operation.get;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.pushtorefresh.storio.db.StorIODb;
import com.pushtorefresh.storio.db.operation.PreparedOperationWithReactiveStream;
import com.pushtorefresh.storio.db.query.Query;
import com.pushtorefresh.storio.db.query.RawQuery;

public abstract class PreparedGet<T> implements PreparedOperationWithReactiveStream<T> {

    @NonNull  protected final StorIODb storIODb;
    @Nullable protected final Query query;
    @Nullable protected final RawQuery rawQuery;

    PreparedGet(@NonNull StorIODb storIODb, @NonNull Query query) {
        this.storIODb = storIODb;
        this.query = query;
        this.rawQuery = null;
    }

    PreparedGet(@NonNull StorIODb storIODb, @NonNull RawQuery rawQuery) {
        this.storIODb = storIODb;
        this.rawQuery = rawQuery;
        query = null;
    }

    public static class Builder {

        @NonNull private final StorIODb storIODb;

        public Builder(@NonNull StorIODb storIODb) {
            this.storIODb = storIODb;
        }

        @NonNull public PreparedGetCursor.Builder cursor() {
            return new PreparedGetCursor.Builder(storIODb);
        }

        @NonNull public <T> PreparedGetListOfObjects.Builder<T> listOfObjects(@NonNull Class<T> type) {
            return new PreparedGetListOfObjects.Builder<>(storIODb, type);
        }
    }

}
