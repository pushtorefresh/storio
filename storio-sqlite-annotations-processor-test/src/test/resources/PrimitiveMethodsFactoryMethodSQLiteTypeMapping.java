package com.pushtorefresh.storio.sqlite.annotations;

import com.pushtorefresh.storio.sqlite.SQLiteTypeMapping;

/**
 * Generated mapping with collection of resolvers.
 */
public class PrimitiveMethodsFactoryMethodSQLiteTypeMapping extends SQLiteTypeMapping<PrimitiveMethodsFactoryMethod> {
    public PrimitiveMethodsFactoryMethodSQLiteTypeMapping() {
        super(new PrimitiveMethodsFactoryMethodStorIOSQLitePutResolver(),
                new PrimitiveMethodsFactoryMethodStorIOSQLiteGetResolver(),
                new PrimitiveMethodsFactoryMethodStorIOSQLiteDeleteResolver());
    }
}