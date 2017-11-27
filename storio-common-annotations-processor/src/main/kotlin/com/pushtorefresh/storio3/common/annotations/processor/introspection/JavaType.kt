package com.pushtorefresh.storio3.common.annotations.processor.introspection

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
        get() = when (this) {
            BOOLEAN_OBJECT, SHORT_OBJECT, INTEGER_OBJECT, LONG_OBJECT, FLOAT_OBJECT, DOUBLE_OBJECT -> true
            else -> false
        }

    companion object {

        fun from(typeMirror: TypeMirror): JavaType {
            val typeKind = typeMirror.kind
            val typeName = typeMirror.toString() // fqn of type, for example java.lang.String

            return when {
                typeKind == TypeKind.BOOLEAN -> BOOLEAN
                typeName == Boolean::class.javaObjectType.canonicalName -> BOOLEAN_OBJECT
                typeKind == TypeKind.SHORT -> SHORT
                typeName == Short::class.javaObjectType.canonicalName -> SHORT_OBJECT
                typeKind == TypeKind.INT -> INTEGER
                typeName == Integer::class.javaObjectType.canonicalName -> INTEGER_OBJECT
                typeKind == TypeKind.LONG -> LONG
                typeName == Long::class.javaObjectType.canonicalName -> LONG_OBJECT
                typeKind == TypeKind.FLOAT -> FLOAT
                typeName == Float::class.javaObjectType.canonicalName -> FLOAT_OBJECT
                typeKind == TypeKind.DOUBLE -> DOUBLE
                typeName == Double::class.javaObjectType.canonicalName -> DOUBLE_OBJECT
                typeName == String::class.javaObjectType.canonicalName -> STRING
                typeName == ByteArray::class.java.canonicalName -> BYTE_ARRAY
                else -> throw IllegalArgumentException("Unsupported type: $typeMirror")
            }
        }
    }

    val sqliteType: String
        get() = when (this) {
            BOOLEAN,
            BOOLEAN_OBJECT,
            SHORT,
            SHORT_OBJECT,
            INTEGER,
            INTEGER_OBJECT,
            LONG,
            LONG_OBJECT -> "INTEGER"
            FLOAT,
            FLOAT_OBJECT,
            DOUBLE,
            DOUBLE_OBJECT -> "REAL"
            STRING -> "TEXT"
            BYTE_ARRAY -> "BLOB"
        }
}