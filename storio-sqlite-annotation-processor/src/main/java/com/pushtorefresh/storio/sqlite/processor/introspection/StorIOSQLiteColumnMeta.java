package com.pushtorefresh.storio.sqlite.processor.introspection;

import com.pushtorefresh.storio.sqlite.annotation.StorIOSQLiteColumn;

import org.jetbrains.annotations.NotNull;

import javax.lang.model.element.Element;

public class StorIOSQLiteColumnMeta {

    @NotNull
    public final Element enclosingElement;

    @NotNull
    public final String fieldName;

    @NotNull
    public final JavaType javaType;

    @NotNull
    public final StorIOSQLiteColumn storIOSQLiteColumn;

    public StorIOSQLiteColumnMeta(@NotNull Element enclosingElement, @NotNull String fieldName, @NotNull JavaType javaType, @NotNull StorIOSQLiteColumn storIOSQLiteColumn) {
        this.enclosingElement = enclosingElement;
        this.fieldName = fieldName;
        this.javaType = javaType;
        this.storIOSQLiteColumn = storIOSQLiteColumn;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        StorIOSQLiteColumnMeta that = (StorIOSQLiteColumnMeta) o;

        if (!enclosingElement.equals(that.enclosingElement)) return false;
        if (!fieldName.equals(that.fieldName)) return false;
        if (javaType != that.javaType) return false;
        return storIOSQLiteColumn.equals(that.storIOSQLiteColumn);
    }

    @Override
    public int hashCode() {
        int result = enclosingElement.hashCode();
        result = 31 * result + fieldName.hashCode();
        result = 31 * result + javaType.hashCode();
        result = 31 * result + storIOSQLiteColumn.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "StorIOSQLiteColumnMeta{" +
                "enclosingElement=" + enclosingElement +
                ", fieldName='" + fieldName + '\'' +
                ", javaType=" + javaType +
                ", storIOSQLiteColumn=" + storIOSQLiteColumn +
                '}';
    }
}
