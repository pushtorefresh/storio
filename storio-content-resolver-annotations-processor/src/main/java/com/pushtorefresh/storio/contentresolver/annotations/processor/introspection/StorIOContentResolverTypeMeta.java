package com.pushtorefresh.storio.contentresolver.annotations.processor.introspection;

import com.pushtorefresh.storio.contentresolver.annotations.StorIOContentResolverType;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class StorIOContentResolverTypeMeta {

    @NotNull
    public final String simpleName;

    @NotNull
    public final String packageName;

    @NotNull
    public final StorIOContentResolverType storIOContentResolverType;

    /**
     * Yep, this is MODIFIABLE Map, please use it carefully
     */
    @NotNull
    public final Map<String, StorIOContentResolverColumnMeta> columns = new HashMap<String, StorIOContentResolverColumnMeta>();

    public StorIOContentResolverTypeMeta(
            @NotNull String simpleName,
            @NotNull String packageName,
            @NotNull StorIOContentResolverType storIOContentResolverType) {
        this.simpleName = simpleName;
        this.packageName = packageName;
        this.storIOContentResolverType = storIOContentResolverType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        StorIOContentResolverTypeMeta that = (StorIOContentResolverTypeMeta) o;

        if (!simpleName.equals(that.simpleName)) return false;
        if (!packageName.equals(that.packageName)) return false;
        if (!storIOContentResolverType.equals(that.storIOContentResolverType)) return false;
        return columns.equals(that.columns);

    }

    @Override
    public int hashCode() {
        int result = simpleName.hashCode();
        result = 31 * result + packageName.hashCode();
        result = 31 * result + storIOContentResolverType.hashCode();
        result = 31 * result + columns.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "StorIOContentResolverTypeMeta{" +
                "simpleName='" + simpleName + '\'' +
                ", packageName='" + packageName + '\'' +
                ", storIOContentResolverType=" + storIOContentResolverType +
                ", columns=" + columns +
                '}';
    }
}
