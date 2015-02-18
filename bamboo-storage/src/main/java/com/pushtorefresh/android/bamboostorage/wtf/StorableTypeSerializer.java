package com.pushtorefresh.android.bamboostorage.wtf;

import android.content.ContentValues;
import android.support.annotation.NonNull;

public interface StorableTypeSerializer<T> {

    @NonNull public ContentValues toContentValues(@NonNull T object);
}
