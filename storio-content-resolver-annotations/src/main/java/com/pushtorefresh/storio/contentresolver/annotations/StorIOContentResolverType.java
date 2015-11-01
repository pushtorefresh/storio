package com.pushtorefresh.storio.contentresolver.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Annotation for marking class as StorIOContentResolverType to generate object mapping code
 */
@Target(TYPE)
@Retention(RUNTIME) // we allow users to write reflection based code to work with annotation
public @interface StorIOContentResolverType {

    /**
     * Required: Specifies uri
     *
     * @return uri
     */
    String uri();
}
