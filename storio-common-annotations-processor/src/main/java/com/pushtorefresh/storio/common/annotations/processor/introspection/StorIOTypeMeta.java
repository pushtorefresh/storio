package com.pushtorefresh.storio.common.annotations.processor.introspection;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.annotation.Annotation;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.lang.model.element.ExecutableElement;

public class StorIOTypeMeta <TypeAnnotation extends Annotation, ColumnMeta extends StorIOColumnMeta> {

    @NotNull
    public final String simpleName;

    @NotNull
    public final String packageName;

    @NotNull
    public final TypeAnnotation storIOType;

    public boolean needCreator;

    @Nullable
    public ExecutableElement creator;

    /**
     * Yep, this is MODIFIABLE Map, please use it carefully.
     * {@link LinkedHashMap} is used intentionally for building parameter list for
     * constructor or factory method depending on ordering of methods annotated with
     * column annotations.
     */
    @NotNull
    public final Map<String, ColumnMeta> columns = new LinkedHashMap<String, ColumnMeta>();

    public StorIOTypeMeta(
            @NotNull String simpleName,
            @NotNull String packageName,
            @NotNull TypeAnnotation storIOType) {
        this(simpleName, packageName, storIOType, false);
    }

    public StorIOTypeMeta(
            @NotNull String simpleName,
            @NotNull String packageName,
            @NotNull TypeAnnotation storIOType,
            boolean needCreator) {
        this.simpleName = simpleName;
        this.packageName = packageName;
        this.storIOType = storIOType;
        this.needCreator = needCreator;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        StorIOTypeMeta<?, ?> that = (StorIOTypeMeta<?, ?>) o;

        if (!simpleName.equals(that.simpleName)) return false;
        if (!packageName.equals(that.packageName)) return false;
        if (!storIOType.equals(that.storIOType)) return false;
        return columns.equals(that.columns);

    }

    @Override
    public int hashCode() {
        int result = simpleName.hashCode();
        result = 31 * result + packageName.hashCode();
        result = 31 * result + storIOType.hashCode();
        result = 31 * result + columns.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "StorIOTypeMeta{" +
                "simpleName='" + simpleName + '\'' +
                ", packageName='" + packageName + '\'' +
                ", storIOType=" + storIOType +
                ", needCreator=" + needCreator +
                ", creator=" + creator +
                ", columns=" + columns +
                '}';
    }
}
