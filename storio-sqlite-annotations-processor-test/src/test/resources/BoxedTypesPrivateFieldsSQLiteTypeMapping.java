package com.pushtorefresh.storio2.sqlite.annotations;

import com.pushtorefresh.storio2.sqlite.SQLiteTypeMapping;

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