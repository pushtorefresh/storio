package com.pushtorefresh.storio.sqlite.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Annotation for marking class as StorIOSQLiteType to generate object mapping code
 */
@Target(TYPE)
@Retention(RUNTIME) // we allow users to write reflection based code to work with annotation
public @interface StorIOSQLiteType {

    /**
     * Required: Specifies table name
     *
     * @return table name
     */
    String table();
}
