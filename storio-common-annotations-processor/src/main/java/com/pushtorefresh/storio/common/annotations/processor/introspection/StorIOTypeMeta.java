package com.pushtorefresh.storio.common.annotations.processor.introspection;

import org.jetbrains.annotations.NotNull;

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.Map;

public class StorIOTypeMeta <TypeAnnotation extends Annotation, ColumnMeta extends StorIOColumnMeta> {

    @NotNull
    public final String simpleName;

    @NotNull
    public final String packageName;

    @NotNull
    public final TypeAnnotation storIOType;

    /**
     * Yep, this is MODIFIABLE Map, please use it carefully
     */
    @NotNull
    public final Map<String, ColumnMeta> columns = new HashMap<String, ColumnMeta>();

    public StorIOTypeMeta(
            @NotNull String simpleName,
            @NotNull String packageName,
            @NotNull TypeAnnotation storIOType) {
        this.simpleName = simpleName;
        this.packageName = packageName;
        this.storIOType = storIOType;
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
                ", columns=" + columns +
                '}';
    }
}
