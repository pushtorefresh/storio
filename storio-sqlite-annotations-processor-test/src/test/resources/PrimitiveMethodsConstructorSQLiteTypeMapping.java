package com.pushtorefresh.storio2.sqlite.annotations;

import com.pushtorefresh.storio2.sqlite.SQLiteTypeMapping;

/**
 * Generated mapping with collection of resolvers.
 */
public class PrimitiveMethodsConstructorSQLiteTypeMapping extends SQLiteTypeMapping<PrimitiveMethodsConstructor> {
    public PrimitiveMethodsConstructorSQLiteTypeMapping() {
        super(new PrimitiveMethodsConstructorStorIOSQLitePutResolver(),
                new PrimitiveMethodsConstructorStorIOSQLiteGetResolver(),
                new PrimitiveMethodsConstructorStorIOSQLiteDeleteResolver());
    }
}