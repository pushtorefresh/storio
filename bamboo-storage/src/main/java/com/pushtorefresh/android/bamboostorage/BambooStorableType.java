package com.pushtorefresh.android.bamboostorage;

import android.support.annotation.Nullable;

public interface BambooStorableType {
    @Nullable Long getStorableId();
    void setStorableId(@Nullable Long storableId);
}
