package com.pushtorefresh.storio.sqlite.annotations;

import com.pushtorefresh.storio.sqlite.SQLiteTypeMapping;

/**
 * Generated mapping with collection of resolvers.
 */
public class BoxedTypesPrivateFieldsSQLiteTypeMapping extends SQLiteTypeMapping<BoxedTypesPrivateFields> {
    public BoxedTypesPrivateFieldsSQLiteTypeMapping() {
        super(new BoxedTypesPrivateFieldsStorIOSQLitePutResolver(),
                new BoxedTypesPrivateFieldsStorIOSQLiteGetResolver(),
                new BoxedTypesPrivateFieldsStorIOSQLiteDeleteResolver());
    }
}