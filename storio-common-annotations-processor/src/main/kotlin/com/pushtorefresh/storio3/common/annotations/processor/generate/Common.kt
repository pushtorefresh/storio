package com.pushtorefresh.storio3.common.annotations.processor.generate

import com.pushtorefresh.storio3.common.annotations.processor.introspection.JavaType
import com.pushtorefresh.storio3.common.annotations.processor.introspection.JavaType.*

object Common {

    val INDENT = "    " // 4 spaces

    fun getFromCursorString(javaType: JavaType, columnIndex: String) = when (javaType) {
        BOOLEAN, BOOLEAN_OBJECT -> "getInt($columnIndex) == 1"
        SHORT, SHORT_OBJECT -> "getShort($columnIndex)"
        INTEGER, INTEGER_OBJECT -> "getInt($columnIndex)"
        LONG, LONG_OBJECT -> "getLong($columnIndex)"
        FLOAT, FLOAT_OBJECT -> "getFloat($columnIndex)"
        DOUBLE, DOUBLE_OBJECT -> "getDouble($columnIndex)"
        STRING -> "getString($columnIndex)"
        BYTE_ARRAY -> "getBlob($columnIndex)"
    }
}