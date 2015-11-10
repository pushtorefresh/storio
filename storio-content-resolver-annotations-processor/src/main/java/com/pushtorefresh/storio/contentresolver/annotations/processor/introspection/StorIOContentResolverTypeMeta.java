package com.pushtorefresh.storio.contentresolver.annotations.processor.introspection;

import com.pushtorefresh.storio.common.annotations.processor.introspection.StorIOTypeMeta;
import com.pushtorefresh.storio.contentresolver.annotations.StorIOContentResolverType;

import org.jetbrains.annotations.NotNull;

public class StorIOContentResolverTypeMeta extends StorIOTypeMeta<StorIOContentResolverType, StorIOContentResolverColumnMeta> {

    public StorIOContentResolverTypeMeta(
            @NotNull String simpleName,
            @NotNull String packageName,
            @NotNull StorIOContentResolverType storIOType) {
        super(simpleName, packageName, storIOType);
    }
}
