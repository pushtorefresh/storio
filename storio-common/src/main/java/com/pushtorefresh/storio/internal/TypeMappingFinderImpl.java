package com.pushtorefresh.storio.internal;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.pushtorefresh.storio.TypeMappingFinder;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Common class for search and cache indirect type mapping.
 */
public class TypeMappingFinderImpl implements TypeMappingFinder {

    @Nullable
    private Map<Class<?>, ? extends TypeMapping<?>> directTypeMapping;

    @NonNull
    private final Map<Class<?>, TypeMapping<?>> indirectTypesMappingCache
            = new ConcurrentHashMap<Class<?>, TypeMapping<?>>();

    @SuppressWarnings("unchecked")
    @Nullable
    @Override
    public <T> TypeMapping<T> findTypeMapping(@NonNull final Class<T> type) {
        if (directTypeMapping == null) {
            // No known type mappings, looks like user forgot to add them.
            return null;
        }

        // Search algorithm:
        // Check current class for direct or indirect type mapping from cache.
        // Walk through all parent classes and interfaces.
        // If parent type has direct mapping -> we found indirect type mapping!
        // Walk through all parent classes and interfaces recursively.
        // If current parent type == Object.class and doesn't implement another interfaces ->
        // there is no indirect type mapping.
        // Complexity:
        // O(n) where n is number of parent types of passed type (pretty fast).

        final TypeMapping<T> directOrCachedMapping = getDirectOrCachedTypeMapping(type, directTypeMapping);
        if (directOrCachedMapping != null) {
            return directOrCachedMapping;
        }

        // Okay, we don't have direct type mapping.
        // And we don't have cache for indirect type mapping.

        // Check parent class for direct type mapping.
        final Class<?> parentType = type.getSuperclass();
        if (parentType != null && parentType != Object.class) {
            final TypeMapping<T> mappingOfParent = (TypeMapping<T>) directTypeMapping.get(parentType);
            if (mappingOfParent != null) {
                indirectTypesMappingCache.put(type, mappingOfParent);
                return mappingOfParent;
            }
        }

        // Check direct type mapping in own interfaces.
        for (Class<?> ownInterface : type.getInterfaces()) {
            final TypeMapping<T> mappingOfInterface = (TypeMapping<T>) directTypeMapping.get(ownInterface);
            if (mappingOfInterface != null) {
                indirectTypesMappingCache.put(type, mappingOfInterface);
                return mappingOfInterface;
            }
        }

        // Let's try to find indirect type mapping of parent class.
        if (parentType != null && parentType != Object.class) {
            final TypeMapping<T> indirectMappingOfParent = (TypeMapping<T>) findTypeMapping(parentType);
            if (indirectMappingOfParent != null) {
                indirectTypesMappingCache.put(type, indirectMappingOfParent);
                return indirectMappingOfParent;
            }
        }

        // Try to find indirect type mapping in own interfaces.
        for (Class<?> ownInterface : type.getInterfaces()) {
            final TypeMapping<T> mappingOfInterfaces = (TypeMapping<T>) findTypeMapping(ownInterface);
            if (mappingOfInterfaces != null) {
                indirectTypesMappingCache.put(type, mappingOfInterfaces);
                return mappingOfInterfaces;
            }
        }

        // No type mapping found.
        return null;
    }

    @SuppressWarnings("unchecked")
    @Nullable
    private <T> TypeMapping<T> getDirectOrCachedTypeMapping(
            @NonNull final Class<T> type,
            @NonNull final Map<Class<?>, ? extends TypeMapping<?>> directMappingMap) {

        final TypeMapping<T> directMapping = (TypeMapping<T>) directMappingMap.get(type);
        if (directMapping != null) {
            return directMapping;
        }

        // May be value already in cache.
        final TypeMapping<T> cachedMapping = (TypeMapping<T>) indirectTypesMappingCache.get(type);
        if (cachedMapping != null) {
            // fffast! O(1)
            return cachedMapping;
        }

        return null;
    }

    @Override
    public void directTypeMapping(@Nullable Map<Class<?>, ? extends TypeMapping<?>> directTypeMapping) {
        this.directTypeMapping = directTypeMapping;
    }

    @Nullable
    @Override
    public Map<Class<?>, ? extends TypeMapping<?>> directTypeMapping() {
        return directTypeMapping;
    }
}
