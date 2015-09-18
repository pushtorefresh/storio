package com.pushtorefresh.storio.sample.db.entities;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public final class Car {

    @Nullable
    private final Long id;

    private final long personId;

    @NonNull
    private final String model;

//    public Car(@Nullable Long id, long personId, @NonNull String model) {
//        this.id = id;
//        this.personId = personId;
//        this.model = model;
//    }

    private Car(Builder builder) {
        id          = builder.id;
        personId    = builder.personId;
        model       = builder.model;
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

    @Override
    public String toString() {
        return "Car{" +
                "id=" + id +
                ", personId=" + personId +
                ", model='" + model + '\'' +
                '}';
    }

    public static class Builder {

        @Nullable
        private Long id = null;

        private long personId = 0;

        @NonNull
        private String model = "";

        public Builder id(@Nullable Long value) {
            id = value;
            return this;
        }

        public Builder personId(long value) {
            personId = value;
            return this;
        }

        public Builder model(@NonNull String value) {
            model = value;
            return this;
        }

        public Car build() {
            return new Car(this);
        }
    }
}