package com.pushtorefresh.storio.sqlite.annotations;

import com.pushtorefresh.storio.sqlite.SQLiteTypeMapping;

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