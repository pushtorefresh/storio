package com.pushtorefresh.storio.sqlite.processor.generate;

import com.pushtorefresh.storio.sqlite.processor.ProcessingException;
import com.pushtorefresh.storio.sqlite.processor.introspection.JavaType;
import com.pushtorefresh.storio.sqlite.processor.introspection.StorIOSQLiteColumnMeta;
import com.pushtorefresh.storio.sqlite.processor.introspection.StorIOSQLiteTypeMeta;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeSpec;

import org.jetbrains.annotations.NotNull;

import static com.pushtorefresh.storio.sqlite.processor.generate.Common.ANDROID_NON_NULL_ANNOTATION_CLASS_NAME;
import static com.pushtorefresh.storio.sqlite.processor.generate.Common.INDENT;
import static com.pushtorefresh.storio.sqlite.processor.introspection.JavaType.BOOLEAN;
import static com.pushtorefresh.storio.sqlite.processor.introspection.JavaType.BOOLEAN_OBJECT;
import static com.pushtorefresh.storio.sqlite.processor.introspection.JavaType.DOUBLE;
import static com.pushtorefresh.storio.sqlite.processor.introspection.JavaType.DOUBLE_OBJECT;
import static com.pushtorefresh.storio.sqlite.processor.introspection.JavaType.FLOAT;
import static com.pushtorefresh.storio.sqlite.processor.introspection.JavaType.FLOAT_OBJECT;
import static com.pushtorefresh.storio.sqlite.processor.introspection.JavaType.INTEGER;
import static com.pushtorefresh.storio.sqlite.processor.introspection.JavaType.INTEGER_OBJECT;
import static com.pushtorefresh.storio.sqlite.processor.introspection.JavaType.LONG;
import static com.pushtorefresh.storio.sqlite.processor.introspection.JavaType.LONG_OBJECT;
import static com.pushtorefresh.storio.sqlite.processor.introspection.JavaType.SHORT;
import static com.pushtorefresh.storio.sqlite.processor.introspection.JavaType.SHORT_OBJECT;
import static com.pushtorefresh.storio.sqlite.processor.introspection.JavaType.STRING;
import static javax.lang.model.element.Modifier.PUBLIC;

public class GetResolverGenerator {

    @NotNull
    public JavaFile generateJavaFile(@NotNull StorIOSQLiteTypeMeta storIOSQLiteTypeMeta) {
        final ClassName storIOSQLiteTypeClassName = ClassName.get(storIOSQLiteTypeMeta.packageName, storIOSQLiteTypeMeta.simpleName);

        final TypeSpec getResolver = TypeSpec.classBuilder(storIOSQLiteTypeMeta.simpleName + "StorIOSQLiteGetResolver")
                .addJavadoc("Generated resolver for Get Operation\n")
                .addModifiers(PUBLIC)
                .superclass(ParameterizedTypeName.get(ClassName.get("com.pushtorefresh.storio.sqlite.operation.get", "DefaultGetResolver"), storIOSQLiteTypeClassName))
                .addMethod(createMapFromCursorMethodSpec(storIOSQLiteTypeMeta, storIOSQLiteTypeClassName))
                .build();

        return JavaFile
                .builder(storIOSQLiteTypeMeta.packageName, getResolver)
                .indent(INDENT)
                .build();
    }

    @NotNull
    MethodSpec createMapFromCursorMethodSpec(@NotNull StorIOSQLiteTypeMeta storIOSQLiteTypeMeta, @NotNull ClassName storIOSQLiteTypeClassName) {
        final MethodSpec.Builder builder = MethodSpec.methodBuilder("mapFromCursor")
                .addJavadoc("{@inheritDoc}\n")
                .addAnnotation(Override.class)
                .addAnnotation(ANDROID_NON_NULL_ANNOTATION_CLASS_NAME)
                .addModifiers(PUBLIC)
                .returns(storIOSQLiteTypeClassName)
                .addParameter(ParameterSpec.builder(ClassName.get("android.database", "Cursor"), "cursor")
                        .addAnnotation(ANDROID_NON_NULL_ANNOTATION_CLASS_NAME)
                        .build())
                .addStatement("$T object = new $T()", storIOSQLiteTypeClassName, storIOSQLiteTypeClassName)
                .addCode("\n");

        for (final StorIOSQLiteColumnMeta columnMeta : storIOSQLiteTypeMeta.columns.values()) {
            final String columnIndex = "cursor.getColumnIndex(\"" + columnMeta.storIOSQLiteColumn.name() + "\")";

            final String getFromCursor;

            final JavaType javaType = columnMeta.javaType;

            if (javaType == BOOLEAN || javaType == BOOLEAN_OBJECT) {
                getFromCursor = "getInt(" + columnIndex + ") == 1";
            } else if (javaType == SHORT || javaType == SHORT_OBJECT) {
                getFromCursor = "getShort(" + columnIndex + ")";
            } else if (javaType == INTEGER || javaType == INTEGER_OBJECT) {
                getFromCursor = "getInt(" + columnIndex + ")";
            } else if (javaType == LONG || javaType == LONG_OBJECT) {
                getFromCursor = "getLong(" + columnIndex + ")";
            } else if (javaType == FLOAT || javaType == FLOAT_OBJECT) {
                getFromCursor = "getFloat(" + columnIndex + ")";
            } else if (javaType == DOUBLE || javaType == DOUBLE_OBJECT) {
                getFromCursor = "getDouble(" + columnIndex + ")";
            } else if (javaType == STRING) {
                getFromCursor = "getString(" + columnIndex + ")";
            } else {
                throw new ProcessingException(columnMeta.element, "Can not generate GetResolver for field");
            }

            builder
                    .addStatement("object.$L = cursor.$L", columnMeta.fieldName, getFromCursor);
        }

        return builder
                .addCode("\n")
                .addStatement("return object")
                .build();
    }
}
