package com.pushtorefresh.storio.db.operation.delete;

import android.support.annotation.NonNull;

import com.pushtorefresh.storio.db.BambooStorageDb;
import com.pushtorefresh.storio.db.operation.PreparedOperation;
import com.pushtorefresh.storio.db.query.DeleteQuery;

import java.util.Collection;

public abstract class PreparedDelete<T> implements PreparedOperation<T>{

    @NonNull protected BambooStorageDb bambooStorageDb;

    protected PreparedDelete(@NonNull BambooStorageDb bambooStorageDb) {
        this.bambooStorageDb = bambooStorageDb;
    }

    public static class Builder {

        @NonNull private final BambooStorageDb bambooStorageDb;

        public Builder(@NonNull BambooStorageDb bambooStorageDb) {
            this.bambooStorageDb = bambooStorageDb;
        }

        @NonNull public PreparedDeleteByQuery.Builder byQuery(@NonNull DeleteQuery deleteQuery) {
            return new PreparedDeleteByQuery.Builder(bambooStorageDb, deleteQuery);
        }

        @NonNull public <T> PreparedDeleteObject.Builder<T> object(@NonNull T object) {
            return new PreparedDeleteObject.Builder<>(bambooStorageDb, object);
        }

        @NonNull public <T> PreparedDeleteCollectionOfObjects.Builder<T> objects(@NonNull Collection<T> objects) {
            return new PreparedDeleteCollectionOfObjects.Builder<>(bambooStorageDb, objects);
        }
    }
}
