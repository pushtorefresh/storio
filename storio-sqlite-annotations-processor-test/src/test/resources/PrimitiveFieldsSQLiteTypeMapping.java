package com.pushtorefresh.storio3.sqlite.annotations;

import com.pushtorefresh.storio3.sqlite.SQLiteTypeMapping;

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