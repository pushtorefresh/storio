package com.pushtorefresh.storio.common.annotations.processor.introspection;

import org.jetbrains.annotations.NotNull;

import java.lang.annotation.Annotation;

import javax.lang.model.element.Element;

public class StorIOColumnMeta <ColumnAnnotation extends Annotation> {

    @NotNull
    public final Element enclosingElement;

    @NotNull
    public final Element element;

    @NotNull
    public final String fieldName;

    @NotNull
    public final JavaType javaType;

    @NotNull
    public final ColumnAnnotation storIOColumn;

    public StorIOColumnMeta(
            @NotNull Element enclosingElement,
            @NotNull Element element,
            @NotNull String fieldName,
            @NotNull JavaType javaType,
            @NotNull ColumnAnnotation storIOColumn) {
        this.enclosingElement = enclosingElement;
        this.element = element;
        this.fieldName = fieldName;
        this.javaType = javaType;
        this.storIOColumn = storIOColumn;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        StorIOColumnMeta<?> that = (StorIOColumnMeta<?>) o;

        if (!enclosingElement.equals(that.enclosingElement)) return false;
        if (!element.equals(that.element)) return false;
        if (!fieldName.equals(that.fieldName)) return false;
        if (javaType != that.javaType) return false;
        return storIOColumn.equals(that.storIOColumn);

    }

    @Override
    public int hashCode() {
        int result = enclosingElement.hashCode();
        result = 31 * result + element.hashCode();
        result = 31 * result + fieldName.hashCode();
        result = 31 * result + javaType.hashCode();
        result = 31 * result + storIOColumn.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "StorIOColumnMeta{" +
                "enclosingElement=" + enclosingElement +
                ", element=" + element +
                ", fieldName='" + fieldName + '\'' +
                ", javaType=" + javaType +
                ", storIOColumn=" + storIOColumn +
                '}';
    }
}
