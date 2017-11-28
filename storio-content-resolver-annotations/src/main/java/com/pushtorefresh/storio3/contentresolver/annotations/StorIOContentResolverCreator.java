package com.pushtorefresh.storio3.contentresolver.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.CONSTRUCTOR;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Annotation for marking constructor or factory method.
 */
@Target({CONSTRUCTOR, METHOD})
@Retention(RUNTIME) // We allow users to write reflection based code to work with annotation.
public @interface StorIOContentResolverCreator {
}
