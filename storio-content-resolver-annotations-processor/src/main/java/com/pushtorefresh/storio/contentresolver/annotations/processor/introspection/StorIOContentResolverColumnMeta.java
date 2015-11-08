package com.pushtorefresh.storio.contentresolver.annotations.processor.introspection;

import com.pushtorefresh.storio.contentresolver.annotations.StorIOContentResolverColumn;

import org.jetbrains.annotations.NotNull;

import javax.lang.model.element.Element;

public class StorIOContentResolverColumnMeta {

    @NotNull
    public final Element enclosingElement;

    @NotNull
    public final Element element;

    @NotNull
    public final String fieldName;

    @NotNull
    public final JavaType javaType;

    @NotNull
    public final StorIOContentResolverColumn storIOContentResolverColumn;

    public StorIOContentResolverColumnMeta(
            @NotNull Element enclosingElement,
            @NotNull Element element,
            @NotNull String fieldName,
            @NotNull JavaType javaType,
            @NotNull StorIOContentResolverColumn storIOContentResolverColumn) {
        this.enclosingElement = enclosingElement;
        this.element = element;
        this.fieldName = fieldName;
        this.javaType = javaType;
        this.storIOContentResolverColumn = storIOContentResolverColumn;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        StorIOContentResolverColumnMeta that = (StorIOContentResolverColumnMeta) o;

        if (!enclosingElement.equals(that.enclosingElement)) return false;
        if (!element.equals(that.element)) return false;
        if (!fieldName.equals(that.fieldName)) return false;
        if (javaType != that.javaType) return false;
        return storIOContentResolverColumn.equals(that.storIOContentResolverColumn);

    }

    @Override
    public int hashCode() {
        int result = enclosingElement.hashCode();
        result = 31 * result + element.hashCode();
        result = 31 * result + fieldName.hashCode();
        result = 31 * result + javaType.hashCode();
        result = 31 * result + storIOContentResolverColumn.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "StorIOContentResolverColumnMeta{" +
                "enclosingElement=" + enclosingElement +
                ", element=" + element +
                ", fieldName='" + fieldName + '\'' +
                ", javaType=" + javaType +
                ", storIOContentResolverColumn=" + storIOContentResolverColumn +
                '}';
    }
}
