package com.pushtorefresh.android.bamboostorage.operation;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public interface PutResolver<T> {
    @NonNull String getTableName(@NonNull T object);

    @NonNull String getInternalIdColumnName(@NonNull T object);

    @Nullable Long getInternalIdValue(@NonNull T object);

    void setInternalId(@NonNull T object, long id);
}
