package com.pushtorefresh.android.bamboostorage.query;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public class RawQuery {

    @NonNull  public final String query;

    @Nullable public final String[] args;

    public RawQuery(@NonNull String query, @Nullable String[] args) {
        this.query = query;
        this.args = args;
    }
}
