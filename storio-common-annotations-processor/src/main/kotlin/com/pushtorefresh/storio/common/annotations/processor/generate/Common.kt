package com.pushtorefresh.storio.common.annotations.processor.generate

import com.pushtorefresh.storio.common.annotations.processor.ProcessingException
import com.pushtorefresh.storio.common.annotations.processor.introspection.JavaType
import com.pushtorefresh.storio.common.annotations.processor.introspection.StorIOColumnMeta
import com.squareup.javapoet.ClassName

import com.pushtorefresh.storio.common.annotations.processor.introspection.JavaType.BOOLEAN
import com.pushtorefresh.storio.common.annotations.processor.introspection.JavaType.BOOLEAN_OBJECT
import com.pushtorefresh.storio.common.annotations.processor.introspection.JavaType.BYTE_ARRAY
import com.pushtorefresh.storio.common.annotations.processor.introspection.JavaType.DOUBLE
import com.pushtorefresh.storio.common.annotations.processor.introspection.JavaType.DOUBLE_OBJECT
import com.pushtorefresh.storio.common.annotations.processor.introspection.JavaType.FLOAT
import com.pushtorefresh.storio.common.annotations.processor.introspection.JavaType.FLOAT_OBJECT
import com.pushtorefresh.storio.common.annotations.processor.introspection.JavaType.INTEGER
import com.pushtorefresh.storio.common.annotations.processor.introspection.JavaType.INTEGER_OBJECT
import com.pushtorefresh.storio.common.annotations.processor.introspection.JavaType.LONG
import com.pushtorefresh.storio.common.annotations.processor.introspection.JavaType.LONG_OBJECT
import com.pushtorefresh.storio.common.annotations.processor.introspection.JavaType.SHORT
import com.pushtorefresh.storio.common.annotations.processor.introspection.JavaType.SHORT_OBJECT
import com.pushtorefresh.storio.common.annotations.processor.introspection.JavaType.STRING

object Common {

    val ANDROID_NON_NULL_ANNOTATION_CLASS_NAME: ClassName = ClassName.get(
            "android.support.annotation", "NonNull")

    val INDENT = "    " // 4 spaces

    fun getFromCursorString(columnMeta: StorIOColumnMeta<*>, javaType: JavaType,
                            columnIndex: String): String {
        when {
            javaType == BOOLEAN || javaType == BOOLEAN_OBJECT ->
                return "getInt($columnIndex) == 1"
            javaType == SHORT || javaType == SHORT_OBJECT -> return "getShort($columnIndex)"
            javaType == INTEGER || javaType == INTEGER_OBJECT -> return "getInt($columnIndex)"
            javaType == LONG || javaType == LONG_OBJECT -> return "getLong($columnIndex)"
            javaType == FLOAT || javaType == FLOAT_OBJECT -> return "getFloat($columnIndex)"
            javaType == DOUBLE || javaType == DOUBLE_OBJECT -> return "getDouble($columnIndex)"
            javaType == STRING -> return "getString($columnIndex)"
            javaType == BYTE_ARRAY -> return "getBlob($columnIndex)"
            else -> throw ProcessingException(columnMeta.element,
                    "Can not generate GetResolver for field")
        }
    }

}
