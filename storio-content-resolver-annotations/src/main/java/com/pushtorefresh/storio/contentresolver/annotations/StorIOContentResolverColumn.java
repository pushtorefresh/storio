package com.pushtorefresh.storio.contentresolver.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({FIELD, METHOD})
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

    /**
     * Optional: indicates that field should not be serialized when its value is {@code null}
     * Should be used for not primitive types only
     *
     * @return true if column with {@code null} value should be ignored, false otherwise
     */
    boolean ignoreNull() default false;
}
