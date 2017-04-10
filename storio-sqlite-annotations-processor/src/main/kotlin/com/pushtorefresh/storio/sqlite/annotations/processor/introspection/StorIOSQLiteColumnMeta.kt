package com.pushtorefresh.storio.sqlite.annotations.processor.introspection

import com.pushtorefresh.storio.common.annotations.processor.introspection.JavaType
import com.pushtorefresh.storio.common.annotations.processor.introspection.StorIOColumnMeta
import com.pushtorefresh.storio.sqlite.annotations.StorIOSQLiteColumn
import javax.lang.model.element.Element

class StorIOSQLiteColumnMeta(
        enclosingElement: Element,
        element: Element,
        fieldName: String,
        javaType: JavaType,
        storIOColumn: StorIOSQLiteColumn,
        getter: String? = null,
        setter: String? = null)
    : StorIOColumnMeta<StorIOSQLiteColumn>(
        enclosingElement,
        element,
        fieldName,
        javaType,
        storIOColumn,
        getter,
        setter)
