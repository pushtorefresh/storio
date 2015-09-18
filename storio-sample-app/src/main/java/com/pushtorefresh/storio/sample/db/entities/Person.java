package com.pushtorefresh.storio.sample.db.entities;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.Collections;
import java.util.List;

// Consider using immutable entities:
// 1 Thread safety
// 2 No problems with inconsistency
// 3 No state, mutability == state, more states -> more bugs!
public final class Person {

    @Nullable
    private final Long id;

    @NonNull
    private final String name;

    @NonNull
    private final List<Car> cars;

    public Person(@Nullable Long id, @NonNull String name, @NonNull List<Car> cars) {
        this.id = id;
        this.name = name;
        this.cars = Collections.unmodifiableList(cars);
    }

    @Nullable
    public Long id() {
        return id;
    }

    @NonNull
    public String name() {
        return name;
    }

    @NonNull
    public List<Car> cars() {
        return cars;
    }
}