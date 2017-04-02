package com.pushtorefresh.storio.common.annotations.processor.generate

import com.pushtorefresh.storio.common.annotations.processor.ProcessingException
import com.pushtorefresh.storio.common.annotations.processor.introspection.JavaType
import com.pushtorefresh.storio.common.annotations.processor.introspection.JavaType.*
import com.pushtorefresh.storio.common.annotations.processor.introspection.StorIOColumnMeta
import com.squareup.javapoet.ClassName

object Common {

    val ANDROID_NON_NULL_ANNOTATION_CLASS_NAME: ClassName = ClassName.get("android.support.annotation", "NonNull")

    val INDENT = "    " // 4 spaces

    fun getFromCursorString(columnMeta: StorIOColumnMeta<*>, javaType: JavaType, columnIndex: String) = when (javaType) {
        BOOLEAN, BOOLEAN_OBJECT -> "getInt($columnIndex) == 1"
        SHORT, SHORT_OBJECT -> "getShort($columnIndex)"
        INTEGER, INTEGER_OBJECT -> "getInt($columnIndex)"
        LONG, LONG_OBJECT -> "getLong($columnIndex)"
        FLOAT, FLOAT_OBJECT -> "getFloat($columnIndex)"
        DOUBLE, DOUBLE_OBJECT -> "getDouble($columnIndex)"
        STRING -> "getString($columnIndex)"
        BYTE_ARRAY -> "getBlob($columnIndex)"
        else -> throw ProcessingException(columnMeta.element, "Can not generate GetResolver for field")
    }
}