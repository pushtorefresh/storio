package com.pushtorefresh.android.bamboostorage.wtf;

import android.database.Cursor;
import android.support.annotation.NonNull;

public interface StorableTypeParser<T> {

    @NonNull T parseFromCursor(@NonNull Cursor cursor);
}
