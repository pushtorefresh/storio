package com.pushtorefresh.storio.sample.db.entities;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public final class Car {

    @Nullable
    private final Long id;

    private final long personId;

    @NonNull
    private final String model;

    public Car(@Nullable Long id, long personId, @NonNull String model) {
        this.id = id;
        this.personId = personId;
        this.model = model;
    }

    @Nullable
    public Long id() {
        return id;
    }

    public long personId() {
        return personId;
    }

    @NonNull
    public String model() {
        return model;
    }
}
