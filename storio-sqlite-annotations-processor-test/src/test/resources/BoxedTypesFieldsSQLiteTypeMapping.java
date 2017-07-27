package com.pushtorefresh.storio2.sqlite.annotations;

import com.pushtorefresh.storio2.sqlite.SQLiteTypeMapping;

/**
 * Generated mapping with collection of resolvers.
 */
public class BoxedTypesFieldsSQLiteTypeMapping extends SQLiteTypeMapping<BoxedTypesFields> {
    public BoxedTypesFieldsSQLiteTypeMapping() {
        super(new BoxedTypesFieldsStorIOSQLitePutResolver(),
                new BoxedTypesFieldsStorIOSQLiteGetResolver(),
                new BoxedTypesFieldsStorIOSQLiteDeleteResolver());
    }
}