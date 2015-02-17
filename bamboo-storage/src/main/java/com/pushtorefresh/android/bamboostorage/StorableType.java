package com.pushtorefresh.android.bamboostorage;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface StorableType {

    @NonNull String idFieldName();

    @Nullable String tableName();
}
