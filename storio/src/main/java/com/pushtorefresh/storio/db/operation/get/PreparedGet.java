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
    @NonNull  protected final GetResolver getResolver;

    PreparedGet(@NonNull StorIODb storIODb, @NonNull Query query, @NonNull GetResolver getResolver) {
        this.storIODb = storIODb;
        this.query = query;
        this.getResolver = getResolver;
        this.rawQuery = null;
    }

    PreparedGet(@NonNull StorIODb storIODb, @NonNull RawQuery rawQuery, @NonNull GetResolver getResolver) {
        this.storIODb = storIODb;
        this.rawQuery = rawQuery;
        this.getResolver = getResolver;
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
