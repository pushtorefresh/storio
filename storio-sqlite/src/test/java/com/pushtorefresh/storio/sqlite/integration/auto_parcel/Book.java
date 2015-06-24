package com.pushtorefresh.storio.sqlite.integration.auto_parcel;

import android.support.annotation.NonNull;

import auto.parcel.AutoParcel;

@AutoParcel
abstract class Book {

    abstract int id();

    @NonNull
    abstract String title();

    @NonNull
    abstract String author();

    @NonNull
    static Builder builder() {
        return new AutoParcel_Book.Builder();
    }

    @AutoParcel.Builder
    abstract static class Builder {

        @NonNull
        abstract Builder id(int id);

        @NonNull
        abstract Builder title(@NonNull String title);

        @NonNull
        abstract Builder author(@NonNull String author);

        @NonNull
        abstract Book build();
    }
}
