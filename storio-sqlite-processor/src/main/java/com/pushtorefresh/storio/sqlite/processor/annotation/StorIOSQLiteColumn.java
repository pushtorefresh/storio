package com.pushtorefresh.storio.sqlite.processor.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Annotation for marking field of some class as SQLite column
 */
@Target(FIELD)
@Retention(RUNTIME) // we allow users to write reflection based code to work with annotation
public @interface StorIOSQLiteColumn {

    /**
     * Required: specifies column name
     *
     * @return non-null column name
     */
    String name();
}
