package com.pushtorefresh.android.bamboostorage.wtf;

import android.database.Cursor;
import android.support.annotation.NonNull;

public interface StorableTypeParser<T> {

    T parseFromCursor(@NonNull Cursor cursor);
}
