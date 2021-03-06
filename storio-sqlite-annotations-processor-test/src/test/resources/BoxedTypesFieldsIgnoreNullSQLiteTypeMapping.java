package com.pushtorefresh.storio3.sqlite.annotations;

import com.pushtorefresh.storio3.sqlite.SQLiteTypeMapping;

/**
 * Generated mapping with collection of resolvers.
 */
public class BoxedTypesFieldsIgnoreNullSQLiteTypeMapping extends SQLiteTypeMapping<BoxedTypesFieldsIgnoreNull> {
    public BoxedTypesFieldsIgnoreNullSQLiteTypeMapping() {
        super(new BoxedTypesFieldsIgnoreNullStorIOSQLitePutResolver(),
                new BoxedTypesFieldsIgnoreNullStorIOSQLiteGetResolver(),
                new BoxedTypesFieldsIgnoreNullStorIOSQLiteDeleteResolver());
    }
}