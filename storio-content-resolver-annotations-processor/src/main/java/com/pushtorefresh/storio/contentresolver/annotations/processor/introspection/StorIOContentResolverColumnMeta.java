package com.pushtorefresh.storio.contentresolver.annotations.processor.introspection;

import com.pushtorefresh.storio.common.annotations.processor.introspection.JavaType;
import com.pushtorefresh.storio.common.annotations.processor.introspection.StorIOColumnMeta;
import com.pushtorefresh.storio.contentresolver.annotations.StorIOContentResolverColumn;

import org.jetbrains.annotations.NotNull;

import javax.lang.model.element.Element;

public class StorIOContentResolverColumnMeta extends StorIOColumnMeta<StorIOContentResolverColumn> {

    public StorIOContentResolverColumnMeta(
            @NotNull Element enclosingElement,
            @NotNull Element element,
            @NotNull String fieldName,
            @NotNull JavaType javaType,
            @NotNull StorIOContentResolverColumn storIOColumn) {
        super(enclosingElement, element, fieldName, null, javaType, storIOColumn);
    }
}
