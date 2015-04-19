package com.pushtorefresh.storio.sqlite.processor;

import java.util.HashMap;
import java.util.Map;

public class StorIOSQLiteTypeMeta {

    public final String simpleName;
    public final String packageName;
    public final String tableName;

    /**
     * Yep, this is MODIFIABLE Map, please use it carefully
     */
    public final Map<String, StorIOSQLiteTypeMeta> columns = new HashMap<String, StorIOSQLiteTypeMeta>();

    public StorIOSQLiteTypeMeta(String simpleName, String packageName, String tableName) {
        this.simpleName = simpleName;
        this.packageName = packageName;
        this.tableName = tableName;
    }
}
