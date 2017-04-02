package com.pushtorefresh.storio.sqlite.annotations;

import com.pushtorefresh.storio.sqlite.SQLiteTypeMapping;

/**
 * Generated mapping with collection of resolvers.
 */
public class BoxedTypesMethodsFactoryMethodSQLiteTypeMapping extends SQLiteTypeMapping<BoxedTypesMethodsFactoryMethod> {
    public BoxedTypesMethodsFactoryMethodSQLiteTypeMapping() {
        super(new BoxedTypesMethodsFactoryMethodStorIOSQLitePutResolver(),
                new BoxedTypesMethodsFactoryMethodStorIOSQLiteGetResolver(),
                new BoxedTypesMethodsFactoryMethodStorIOSQLiteDeleteResolver());
    }
}