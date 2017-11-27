package com.pushtorefresh.storio3.sqlite.annotations;

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

  /**
   * Flag to opt out from generating table class based on field annotations.
   *
   * @return flag that shows if table class should be generated or not.
   */
  boolean generateTableClass() default true;
}
