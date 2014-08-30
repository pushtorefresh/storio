package com.pushtorefresh.bamboostorage;

import android.support.annotation.NonNull;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation for marking StorableItem subclass
 *
 * @author Artem Zinnatullin [artem.zinnatullin@gmail.com]
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface ContentPathForContentResolver {

    /**
     * Returns path to the content for ContentResolver
     * @return path to the content, for SQLiteDatabase under ContentProvider it should be table name
     */
    @NonNull
    String value();
}
