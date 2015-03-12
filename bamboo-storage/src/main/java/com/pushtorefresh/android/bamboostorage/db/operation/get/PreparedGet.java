package com.pushtorefresh.android.bamboostorage.db.operation.get;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.pushtorefresh.android.bamboostorage.db.BambooStorageDb;
import com.pushtorefresh.android.bamboostorage.db.operation.PreparedOperationWithReactiveStream;
import com.pushtorefresh.android.bamboostorage.db.query.Query;
import com.pushtorefresh.android.bamboostorage.db.query.RawQuery;

public abstract class PreparedGet<T> implements PreparedOperationWithReactiveStream<T> {

    @NonNull  protected final BambooStorageDb bambooStorageDb;
    @Nullable protected final Query query;
    @Nullable protected final RawQuery rawQuery;

    PreparedGet(@NonNull BambooStorageDb bambooStorageDb, @NonNull Query query) {
        this.bambooStorageDb = bambooStorageDb;
        this.query = query;
        this.rawQuery = null;
    }

    PreparedGet(@NonNull BambooStorageDb bambooStorageDb, @NonNull RawQuery rawQuery) {
        this.bambooStorageDb = bambooStorageDb;
        this.rawQuery = rawQuery;
        query = null;
    }

    public static class Builder {

        @NonNull private final BambooStorageDb bambooStorageDb;

        public Builder(@NonNull BambooStorageDb bambooStorageDb) {
            this.bambooStorageDb = bambooStorageDb;
        }

        @NonNull public PreparedGetCursor.Builder cursor() {
            return new PreparedGetCursor.Builder(bambooStorageDb);
        }

        @NonNull public <T> PreparedGetListOfObjects.Builder<T> listOfObjects(@NonNull Class<T> type) {
            return new PreparedGetListOfObjects.Builder<>(bambooStorageDb, type);
        }
    }

}
