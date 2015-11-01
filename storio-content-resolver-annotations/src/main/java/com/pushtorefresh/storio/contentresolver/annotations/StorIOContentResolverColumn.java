package com.pushtorefresh.storio.contentresolver.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target(FIELD)
@Retention(RUNTIME)
public @interface StorIOContentResolverColumn {

    /**
     * Required: specifies column name
     *
     * @return non-null column name
     */
    String name();

    /**
     * Optional: marks column as key, so it will be used to identify rows for Put and Delete Operations
     *
     * @return true if column is key, false otherwise
     */
    boolean key() default false;
}
