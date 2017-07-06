package com.pushtorefresh.storio.sqlite.annotations;

import com.pushtorefresh.storio.sqlite.SQLiteTypeMapping;

/**
 * Generated mapping with collection of resolvers.
 */
public class PrimitiveFieldsSQLiteTypeMapping extends SQLiteTypeMapping<PrimitiveFields> {
    public PrimitiveFieldsSQLiteTypeMapping() {
        super(new PrimitiveFieldsStorIOSQLitePutResolver(),
                new PrimitiveFieldsStorIOSQLiteGetResolver(),
                new PrimitiveFieldsStorIOSQLiteDeleteResolver());
    }
}