package com.pushtorefresh.storio.sqlite.processor.introspection;

import org.jetbrains.annotations.NotNull;

import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;

public enum JavaType {

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
    STRING;

    @NotNull
    public static JavaType from(@NotNull TypeMirror typeMirror) {
        final TypeKind typeKind = typeMirror.getKind();
        final String typeName = typeMirror.toString(); // fqn of type, for example java.lang.String

        if (typeKind == TypeKind.BOOLEAN) {
            return BOOLEAN;
        } else if ("java.lang.Boolean".equals(typeName)) {
            return BOOLEAN_OBJECT;
        } else if (typeKind == TypeKind.SHORT) {
            return SHORT;
        } else if ("java.lang.Short".equals(typeName)) {
            return SHORT_OBJECT;
        } else if (typeKind == TypeKind.INT) {
            return INTEGER;
        } else if ("java.lang.Integer".equals(typeName)) {
            return INTEGER_OBJECT;
        } else if (typeKind == TypeKind.LONG) {
            return LONG;
        } else if ("java.lang.Long".equals(typeName)) {
            return LONG_OBJECT;
        } else if (typeKind == TypeKind.FLOAT) {
            return FLOAT;
        } else if ("java.lang.Float".equals(typeName)) {
            return FLOAT_OBJECT;
        } else if (typeKind == TypeKind.DOUBLE) {
            return DOUBLE;
        } else if ("java.lang.Double".equals(typeName)) {
            return DOUBLE_OBJECT;
        } else if ("java.lang.String".equals(typeName)) {
            return STRING;
        } else {
            throw new IllegalArgumentException("Unsupported type: " + typeMirror);
        }
    }
}
