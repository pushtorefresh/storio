package com.pushtorefresh.storio.sqlite.annotations.processor.introspection;

import com.pushtorefresh.storio.common.annotations.processor.introspection.StorIOTypeMeta;
import com.pushtorefresh.storio.sqlite.annotations.StorIOSQLiteType;

import org.jetbrains.annotations.NotNull;

public class StorIOSQLiteTypeMeta extends StorIOTypeMeta<StorIOSQLiteType, StorIOSQLiteColumnMeta> {

    public StorIOSQLiteTypeMeta(
            @NotNull String simpleName,
            @NotNull String packageName,
            @NotNull StorIOSQLiteType storIOType,
            boolean needCreator) {
        super(simpleName, packageName, storIOType, needCreator);
    }
}