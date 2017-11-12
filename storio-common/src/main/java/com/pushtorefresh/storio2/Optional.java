package com.pushtorefresh.storio2;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public final class Optional<T> {

    @NonNull
    private transient static final Optional<?> EMPTY = new Optional<Object>(null);

    @Nullable
    private final T value;

    private Optional(@Nullable T value) {
        this.value = value;
    }

    @NonNull
    public static <T> Optional<T> empty() {
        //noinspection unchecked
        return (Optional<T>) EMPTY;
    }

    @NonNull
    public static <T> Optional<T> of(@Nullable T value) {
        //noinspection unchecked
        return value == null ? (Optional<T>) EMPTY : new Optional<T>(value);
    }

    @NonNull
    public T get() {
        if (value == null)
            throw new NullPointerException();
        return value;
    }

    public boolean isPresent() {
        return value != null;
    }

    @NonNull
    public T or(@NonNull T defaultValue) {
        return value != null ? value : defaultValue;
    }

    @Nullable
    public T orNull() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Optional<?> optional = (Optional<?>) o;

        return value != null ? value.equals(optional.value) : optional.value == null;
    }

    @Override
    public int hashCode() {
        return value != null ? value.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "Optional{" +
                "value=" + value +
                '}';
    }
}
