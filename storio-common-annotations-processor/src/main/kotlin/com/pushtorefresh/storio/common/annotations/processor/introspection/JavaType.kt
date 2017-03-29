package com.pushtorefresh.storio.common.annotations.processor.introspection

import javax.lang.model.type.TypeKind
import javax.lang.model.type.TypeMirror

enum class JavaType {

    BOOLEAN,
    BOOLEAN_OBJECT,
    SHORT,
    SHORT_OBJECT,
    INTEGER,
    INTEGER_OBJECT,
    LONG,
    LONG_OBJECT,
    FLOAT,
    FLOAT_OBJECT,
    DOUBLE,
    DOUBLE_OBJECT,
    STRING,
    BYTE_ARRAY;

    val isBoxedType: Boolean
        get() =
        when (this) {
            BOOLEAN_OBJECT, SHORT_OBJECT, INTEGER_OBJECT, LONG_OBJECT, FLOAT_OBJECT,
            DOUBLE_OBJECT -> true
            else -> false
        }

    companion object {

        fun from(typeMirror: TypeMirror): JavaType {
            val typeKind = typeMirror.kind
            val typeName = typeMirror.toString() // fqn of type, for example java.lang.String

            when {
                typeKind == TypeKind.BOOLEAN -> return BOOLEAN
                typeName == Boolean::class.javaObjectType.canonicalName -> return BOOLEAN_OBJECT
                typeKind == TypeKind.SHORT -> return SHORT
                typeName == Short::class.javaObjectType.canonicalName -> return SHORT_OBJECT
                typeKind == TypeKind.INT -> return INTEGER
                typeName == Integer::class.javaObjectType.canonicalName -> return INTEGER_OBJECT
                typeKind == TypeKind.LONG -> return LONG
                typeName == Long::class.javaObjectType.canonicalName -> return LONG_OBJECT
                typeKind == TypeKind.FLOAT -> return FLOAT
                typeName == Float::class.javaObjectType.canonicalName -> return FLOAT_OBJECT
                typeKind == TypeKind.DOUBLE -> return DOUBLE
                typeName == Double::class.javaObjectType.canonicalName -> return DOUBLE_OBJECT
                typeName == String::class.javaObjectType.canonicalName -> return STRING
                typeName == ByteArray::class.java.canonicalName -> return BYTE_ARRAY
                else -> throw IllegalArgumentException("Unsupported type: $typeMirror")
            }
        }
    }
}
