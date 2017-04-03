package com.pushtorefresh.storio.sqlite.annotations;

import com.pushtorefresh.storio.sqlite.SQLiteTypeMapping;

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