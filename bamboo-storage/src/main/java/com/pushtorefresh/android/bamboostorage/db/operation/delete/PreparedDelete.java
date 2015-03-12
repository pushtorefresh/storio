package com.pushtorefresh.android.bamboostorage.db.operation.delete;

import android.support.annotation.NonNull;

import com.pushtorefresh.android.bamboostorage.db.BambooStorage;
import com.pushtorefresh.android.bamboostorage.db.operation.PreparedOperation;
import com.pushtorefresh.android.bamboostorage.db.query.DeleteQuery;

import java.util.Collection;

public abstract class PreparedDelete<T> implements PreparedOperation<T>{

    @NonNull protected BambooStorage bambooStorage;

    protected PreparedDelete(@NonNull BambooStorage bambooStorage) {
        this.bambooStorage = bambooStorage;
    }

    public static class Builder {

        @NonNull private final BambooStorage bambooStorage;

        public Builder(@NonNull BambooStorage bambooStorage) {
            this.bambooStorage = bambooStorage;
        }

        @NonNull public PreparedDeleteByQuery.Builder byQuery(@NonNull DeleteQuery deleteQuery) {
            return new PreparedDeleteByQuery.Builder(bambooStorage, deleteQuery);
        }

        @NonNull public <T> PreparedDeleteObject.Builder<T> object(@NonNull T object) {
            return new PreparedDeleteObject.Builder<>(bambooStorage, object);
        }

        @NonNull public <T> PreparedDeleteCollectionOfObjects.Builder<T> objects(@NonNull Collection<T> objects) {
            return new PreparedDeleteCollectionOfObjects.Builder<>(bambooStorage, objects);
        }
    }
}
