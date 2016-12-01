package com.pushtorefresh.storio.sqlite.annotations.processor.introspection;

import com.pushtorefresh.storio.common.annotations.processor.introspection.JavaType;
import com.pushtorefresh.storio.common.annotations.processor.introspection.StorIOColumnMeta;
import com.pushtorefresh.storio.sqlite.annotations.StorIOSQLiteColumn;

import org.jetbrains.annotations.NotNull;

import javax.lang.model.element.Element;

public class StorIOSQLiteColumnMeta extends StorIOColumnMeta<StorIOSQLiteColumn> {

    public StorIOSQLiteColumnMeta(
            @NotNull Element enclosingElement,
            @NotNull Element element,
            @NotNull String fieldName,
            @NotNull JavaType javaType,
            @NotNull StorIOSQLiteColumn storIOColumn) {
        super(enclosingElement, element, fieldName, javaType, storIOColumn);
    }
}
