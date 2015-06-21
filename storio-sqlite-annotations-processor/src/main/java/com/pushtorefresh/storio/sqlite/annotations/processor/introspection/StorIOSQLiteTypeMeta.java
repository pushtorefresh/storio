package com.pushtorefresh.storio.sqlite.annotations.processor.introspection;

import com.pushtorefresh.storio.sqlite.annotations.StorIOSQLiteType;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class StorIOSQLiteTypeMeta {

    @NotNull
    public final String simpleName;

    @NotNull
    public final String packageName;

    @NotNull
    public final StorIOSQLiteType storIOSQLiteType;

    /**
     * Yep, this is MODIFIABLE Map, please use it carefully
     */
    @NotNull
    public final Map<String, StorIOSQLiteColumnMeta> columns = new HashMap<String, StorIOSQLiteColumnMeta>();

    public StorIOSQLiteTypeMeta(@NotNull String simpleName, @NotNull String packageName, @NotNull StorIOSQLiteType storIOSQLiteType) {
        this.simpleName = simpleName;
        this.packageName = packageName;
        this.storIOSQLiteType = storIOSQLiteType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        StorIOSQLiteTypeMeta that = (StorIOSQLiteTypeMeta) o;

        if (!simpleName.equals(that.simpleName)) return false;
        if (!packageName.equals(that.packageName)) return false;
        if (!storIOSQLiteType.equals(that.storIOSQLiteType)) return false;
        return columns.equals(that.columns);
    }

    @Override
    public int hashCode() {
        int result = simpleName.hashCode();
        result = 31 * result + packageName.hashCode();
        result = 31 * result + storIOSQLiteType.hashCode();
        result = 31 * result + columns.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "StorIOSQLiteTypeMeta{" +
                "simpleName='" + simpleName + '\'' +
                ", packageName='" + packageName + '\'' +
                ", storIOSQLiteType=" + storIOSQLiteType +
                ", columns=" + columns +
                '}';
    }
}
