package com.pushtorefresh.storio.db.operation.delete;

import android.support.annotation.NonNull;

import com.pushtorefresh.storio.db.StorIODb;
import com.pushtorefresh.storio.db.operation.PreparedOperation;
import com.pushtorefresh.storio.db.query.DeleteQuery;

import java.util.Collection;

public abstract class PreparedDelete<T> implements PreparedOperation<T>{

    @NonNull protected StorIODb storIODb;

    protected PreparedDelete(@NonNull StorIODb storIODb) {
        this.storIODb = storIODb;
    }

    public static class Builder {

        @NonNull private final StorIODb storIODb;

        public Builder(@NonNull StorIODb storIODb) {
            this.storIODb = storIODb;
        }

        @NonNull public PreparedDeleteByQuery.Builder byQuery(@NonNull DeleteQuery deleteQuery) {
            return new PreparedDeleteByQuery.Builder(storIODb, deleteQuery);
        }

        @NonNull public <T> PreparedDeleteObject.Builder<T> object(@NonNull T object) {
            return new PreparedDeleteObject.Builder<>(storIODb, object);
        }

        @NonNull public <T> PreparedDeleteCollectionOfObjects.Builder<T> objects(@NonNull Collection<T> objects) {
            return new PreparedDeleteCollectionOfObjects.Builder<>(storIODb, objects);
        }
    }
}
