package com.pushtorefresh.storio;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.pushtorefresh.storio.internal.TypeMapping;

import java.util.Map;

/**
 * Interface for search type mapping.
 */
public interface TypeMappingFinder {
    @Nullable
    <T> TypeMapping<T> findTypeMapping(@NonNull final Class<T> type);

    void directTypeMapping(@Nullable Map<Class<?>, ? extends TypeMapping<?>> directTypeMapping);

    @Nullable
    Map<Class<?>, ? extends TypeMapping<?>> directTypeMapping();
}
