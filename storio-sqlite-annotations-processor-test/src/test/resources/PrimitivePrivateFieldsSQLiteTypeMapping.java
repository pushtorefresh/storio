package com.pushtorefresh.storio.sqlite.annotations;

import com.pushtorefresh.storio.sqlite.SQLiteTypeMapping;

/**
 * Generated mapping with collection of resolvers.
 */
public class PrimitivePrivateFieldsSQLiteTypeMapping extends SQLiteTypeMapping<PrimitivePrivateFields> {
    public PrimitivePrivateFieldsSQLiteTypeMapping() {
        super(new PrimitivePrivateFieldsStorIOSQLitePutResolver(),
                new PrimitivePrivateFieldsStorIOSQLiteGetResolver(),
                new PrimitivePrivateFieldsStorIOSQLiteDeleteResolver());
    }
}