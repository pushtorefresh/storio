package com.pushtorefresh.storio2.sqlite.annotations.processor.introspection

import com.pushtorefresh.storio2.common.annotations.processor.introspection.JavaType
import com.pushtorefresh.storio2.common.annotations.processor.introspection.StorIOColumnMeta
import com.pushtorefresh.storio2.sqlite.annotations.StorIOSQLiteColumn
import javax.lang.model.element.Element

class StorIOSQLiteColumnMeta(
        enclosingElement: Element,
        element: Element,
        fieldName: String,
        javaType: JavaType,
        storIOColumn: StorIOSQLiteColumn,
        getter: String? = null)
    : StorIOColumnMeta<StorIOSQLiteColumn>(
        enclosingElement,
        element,
        fieldName,
        javaType,
        storIOColumn,
        getter)
