package com.pushtorefresh.bamboostorage;

import android.support.annotation.NonNull;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation for marking IBambooStorableItem subclass
 *
 * @author Artem Zinnatullin [artem.zinnatullin@gmail.com]
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface BambooStorableTypeMeta {

    String DEFAULT_INTERNAL_ID_FIELD_NAME = "_id";

    /**
     * Returns path to the content for ContentResolver
     * @return path to the content, for SQLiteDatabase under ContentProvider it should be table name
     */
    @NonNull String contentPath();

    /**
     * Returns internal id field name for your storable type
     * @return internal id field name
     */
    @NonNull String internalIdFieldName() default DEFAULT_INTERNAL_ID_FIELD_NAME;
}
