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
     * Optional: Specifies uri for all operations
     * Should be set as default if there is no uri for some particular operation
     *
     * @return uri
     * @see #insertUri()
     * @see #updateUri()
     * @see #deleteUri()
     */
    String uri() default "";

    /**
     * Optional: Specifies uri for insert operation
     *
     * @return uri
     */
    String insertUri() default "";

    /**
     * Optional: Specifies uri for update operation
     *
     * @return uri
     */
    String updateUri() default "";

    /**
     * Optional: Specifies uri for delete operation
     *
     * @return uri
     */
    String deleteUri() default "";
}
