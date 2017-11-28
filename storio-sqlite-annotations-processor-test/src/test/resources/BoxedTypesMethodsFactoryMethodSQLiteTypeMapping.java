package com.pushtorefresh.storio3.sqlite.annotations;

import com.pushtorefresh.storio3.sqlite.SQLiteTypeMapping;

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