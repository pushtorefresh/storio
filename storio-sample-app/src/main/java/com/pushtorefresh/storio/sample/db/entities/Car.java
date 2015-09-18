package com.pushtorefresh.storio.sample.db.entities;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.UUID;

public final class Car {

//    @Nullable
//    private final Long id;

    @NonNull
    private final String uuid;

//    private final long personId;
    private final String personUuid;

    @NonNull
    private final String model;

//    public Car(@Nullable Long id, long personId, @NonNull String model) {
//        this.id = id;
//        this.personId = personId;
//        this.model = model;
//    }

    private Car(Builder builder) {
        uuid        = builder.uuid;
//        id          = builder.id;
        personUuid  = builder.personUuid;
        model       = builder.model;
    }

//    @Nullable
//    public Long id() {
//        return id;
//    }

    @Nullable
    public String uuid() {
        return uuid;
    }

//    public long personId() {
//        return personId;
//    }

    public String personUuid() {
        return personUuid;
    }

    @NonNull
    public String model() {
        return model;
    }

    @Override
    public String toString() {
        if (true) {
            return model  + " (uuid=" + uuid + ")";
        }
        else {
            return "Car{" +
//                    "id=" + id +
                    "uuid=" + uuid +
//                    ", personId=" + personId +
                    ", personUuid=" + personUuid +
                    ", model='" + model + '\'' +
                    '}';
        }
    }

//////////////////////////////////////////////////////////////////
// builder
//////////////////////////////////////////////////////////////////
    public static class Builder {

        @NonNull
        private /*final*/ String uuid;

//        @Nullable
//        private Long id = null;

        private String personUuid = "";

        @NonNull
        private String model = "";

        public Builder(@NonNull String model) {
            uuid = UUID.randomUUID().toString();

            this.model = model;
        }

//        public Builder id(@Nullable Long value) {
//            id = value;
//            return this;
//        }

        public Builder uuid(@NonNull String value) {
            uuid = value;
            return this;
        }

        public Builder personUuid(String value) {
            personUuid = value;
            return this;
        }

//        public Builder model(@NonNull String value) {
//            model = value;
//            return this;
//        }

        public Car build() {
            return new Car(this);
        }
    }
}