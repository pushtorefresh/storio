package com.pushtorefresh.storio.common.annotations.processor.generate;

import com.pushtorefresh.storio.common.annotations.processor.ProcessingException;
import com.pushtorefresh.storio.common.annotations.processor.introspection.JavaType;
import com.pushtorefresh.storio.common.annotations.processor.introspection.StorIOColumnMeta;
import com.squareup.javapoet.ClassName;

import org.jetbrains.annotations.NotNull;

import static com.pushtorefresh.storio.common.annotations.processor.introspection.JavaType.BOOLEAN;
import static com.pushtorefresh.storio.common.annotations.processor.introspection.JavaType.BOOLEAN_OBJECT;
import static com.pushtorefresh.storio.common.annotations.processor.introspection.JavaType.BYTE_ARRAY;
import static com.pushtorefresh.storio.common.annotations.processor.introspection.JavaType.DOUBLE;
import static com.pushtorefresh.storio.common.annotations.processor.introspection.JavaType.DOUBLE_OBJECT;
import static com.pushtorefresh.storio.common.annotations.processor.introspection.JavaType.FLOAT;
import static com.pushtorefresh.storio.common.annotations.processor.introspection.JavaType.FLOAT_OBJECT;
import static com.pushtorefresh.storio.common.annotations.processor.introspection.JavaType.INTEGER;
import static com.pushtorefresh.storio.common.annotations.processor.introspection.JavaType.INTEGER_OBJECT;
import static com.pushtorefresh.storio.common.annotations.processor.introspection.JavaType.LONG;
import static com.pushtorefresh.storio.common.annotations.processor.introspection.JavaType.LONG_OBJECT;
import static com.pushtorefresh.storio.common.annotations.processor.introspection.JavaType.SHORT;
import static com.pushtorefresh.storio.common.annotations.processor.introspection.JavaType.SHORT_OBJECT;
import static com.pushtorefresh.storio.common.annotations.processor.introspection.JavaType.STRING;

public class Common {

    public static final ClassName ANDROID_NON_NULL_ANNOTATION_CLASS_NAME = ClassName.get("android.support.annotation", "NonNull");

    public static final String INDENT = "    "; // 4 spaces

    @NotNull
    public static String getFromCursorString(@NotNull StorIOColumnMeta columnMeta, @NotNull JavaType javaType, @NotNull String columnIndex) {
        if (javaType == BOOLEAN || javaType == BOOLEAN_OBJECT) {
            return "getInt(" + columnIndex + ") == 1";
        } else if (javaType == SHORT || javaType == SHORT_OBJECT) {
            return "getShort(" + columnIndex + ")";
        } else if (javaType == INTEGER || javaType == INTEGER_OBJECT) {
            return "getInt(" + columnIndex + ")";
        } else if (javaType == LONG || javaType == LONG_OBJECT) {
            return "getLong(" + columnIndex + ")";
        } else if (javaType == FLOAT || javaType == FLOAT_OBJECT) {
            return "getFloat(" + columnIndex + ")";
        } else if (javaType == DOUBLE || javaType == DOUBLE_OBJECT) {
            return "getDouble(" + columnIndex + ")";
        } else if (javaType == STRING) {
            return "getString(" + columnIndex + ")";
        } else if (javaType == BYTE_ARRAY) {
            return "getBlob(" + columnIndex + ")";
        } else {
            throw new ProcessingException(columnMeta.element, "Can not generate GetResolver for field");
        }
    }

}
