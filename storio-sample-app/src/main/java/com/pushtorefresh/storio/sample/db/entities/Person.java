package com.pushtorefresh.storio.sample.db.entities;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

// Consider using immutable entities:
// 1 Thread safety
// 2 No problems with inconsistency
// 3 No state, mutability == state, more states -> more bugs!
public final class Person {

//    @Nullable
//    private final Long id;

    @NonNull
    private final String uuid;

    @NonNull
    private final String name;

    @NonNull
    private final List<Car> cars;

//    public Person(@Nullable Long id, @NonNull String name, @NonNull List<Car> cars) {
//        this.id = id;
//        this.name = name;
//        this.cars = Collections.unmodifiableList(cars);
//    }

    private Person(Builder builder) {
        uuid    = builder.uuid;
//        id    = builder.id;
        name    = builder.name;
        cars    = builder.cars;
    }

//    @Nullable
//    public Long id() {
//        return id;
//    }

    @NonNull
    public String uuid() {
        return uuid;
    }

    @NonNull
    public String name() {
        return name;
    }

    @NonNull
    public List<Car> cars() {
        return cars;
    }

//////////////////////////////////////////////////////////////////
// builder
//////////////////////////////////////////////////////////////////
    public static class Builder {

        @NonNull
        private /*final*/ String uuid;

//        @Nullable
//        private Long id = null;

        @NonNull
        private String name;

        @NonNull
        private /*final*/ List<Car> cars = Collections.emptyList();

        public Builder(@NonNull String name) {
            uuid = UUID.randomUUID().toString();

            this.name = name;
        }

//        public Builder id(@Nullable Long value) {
//            id = value;
//            return this;
//        }

        public Builder uuid(@NonNull String value) {
            uuid = value;
            return this;
        }

        public Builder name(@NonNull String value) {
            name = value;
            return this;
        }

        public Builder cars(@NonNull List<Car> value) {
            cars = value;
            return this;
        }

        public Person build() {
            return new Person(this);
        }
    }
}