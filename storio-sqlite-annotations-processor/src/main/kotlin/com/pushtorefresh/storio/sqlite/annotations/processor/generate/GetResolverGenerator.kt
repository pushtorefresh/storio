package com.pushtorefresh.storio.sqlite.annotations.processor.generate

import com.pushtorefresh.storio.common.annotations.processor.generate.Common.ANDROID_NON_NULL_ANNOTATION_CLASS_NAME
import com.pushtorefresh.storio.common.annotations.processor.generate.Common.INDENT
import com.pushtorefresh.storio.common.annotations.processor.generate.Common.getFromCursorString
import com.pushtorefresh.storio.common.annotations.processor.generate.Generator
import com.pushtorefresh.storio.sqlite.annotations.processor.introspection.StorIOSQLiteTypeMeta
import com.squareup.javapoet.*
import javax.lang.model.element.ElementKind
import javax.lang.model.element.ExecutableElement
import javax.lang.model.element.Modifier.PUBLIC

private const val SUFFIX = "StorIOSQLiteGetResolver"

object GetResolverGenerator : Generator<StorIOSQLiteTypeMeta> {

    override fun generateJavaFile(typeMeta: StorIOSQLiteTypeMeta): JavaFile {
        val className = ClassName.get(typeMeta.packageName, typeMeta.simpleName)

        val getResolver = TypeSpec.classBuilder(generateName(typeMeta))
                .addJavadoc("Generated resolver for Get Operation.\n")
                .addModifiers(PUBLIC)
                .superclass(ParameterizedTypeName.get(ClassName.get("com.pushtorefresh.storio.sqlite.operations.get", "DefaultGetResolver"), className))
                .addMethod(
                        if (typeMeta.needCreator)
                            createMapFromCursorWithCreatorMethodSpec(typeMeta, className)
                        else
                            createMapFromCursorMethodSpec(typeMeta, className))
                .build()

        return JavaFile
                .builder(typeMeta.packageName, getResolver)
                .indent(INDENT)
                .build()
    }

    private fun createMapFromCursorMethodSpec(typeMeta: StorIOSQLiteTypeMeta, className: ClassName): MethodSpec {
        val builder = MethodSpec.methodBuilder("mapFromCursor")
                .addJavadoc("{@inheritDoc}\n")
                .addAnnotation(Override::class.java)
                .addAnnotation(ANDROID_NON_NULL_ANNOTATION_CLASS_NAME)
                .addModifiers(PUBLIC)
                .returns(className)
                .addParameter(ParameterSpec.builder(ClassName.get("android.database", "Cursor"), "cursor")
                        .addAnnotation(ANDROID_NON_NULL_ANNOTATION_CLASS_NAME)
                        .build())
                .addStatement("\$T object = new \$T()", className, className)
                .addCode("\n")

        typeMeta.columns.values.forEach { columnMeta ->
            val columnIndex = "cursor.getColumnIndex(\"${columnMeta.storIOColumn.name}\")"

            val javaType = columnMeta.javaType

            val getFromCursor = getFromCursorString(columnMeta, javaType, columnIndex)

            val isBoxed = javaType.isBoxedType
            // otherwise -> if primitive and value from cursor null -> fail early
            if (isBoxed) builder.beginControlFlow("if (!cursor.isNull(\$L))", columnIndex)

            if (columnMeta.needAccessors) {
                builder.addStatement("object.\$L(cursor.\$L)", columnMeta.setter, getFromCursor)
            } else {
                builder.addStatement("object.\$L = cursor.\$L", columnMeta.elementName, getFromCursor)
            }

            if (isBoxed) builder.endControlFlow()
        }

        return builder
                .addCode("\n")
                .addStatement("return object")
                .build()
    }

    private fun createMapFromCursorWithCreatorMethodSpec(typeMeta: StorIOSQLiteTypeMeta, className: ClassName): MethodSpec {
        val builder = MethodSpec.methodBuilder("mapFromCursor")
                .addJavadoc("{@inheritDoc}\n")
                .addAnnotation(Override::class.java)
                .addAnnotation(ANDROID_NON_NULL_ANNOTATION_CLASS_NAME)
                .addModifiers(PUBLIC)
                .returns(className)
                .addParameter(ParameterSpec.builder(ClassName.get("android.database", "Cursor"), "cursor")
                        .addAnnotation(ANDROID_NON_NULL_ANNOTATION_CLASS_NAME)
                        .build())
                .addCode("\n")

        val paramsBuilder = StringBuilder().apply { append("(") }
        var first = true
        typeMeta.orderedColumns.forEach { columnMeta ->
            val columnIndex = "cursor.getColumnIndex(\"${columnMeta.storIOColumn.name}\")"

            val javaType = columnMeta.javaType

            val getFromCursor = getFromCursorString(columnMeta, javaType, columnIndex)

            val name = TypeName.get((columnMeta.element as ExecutableElement).returnType)

            val isBoxed = javaType.isBoxedType
            if (isBoxed) { // otherwise -> if primitive and value from cursor null -> fail early
                builder.addStatement("\$T \$L = null", name, columnMeta.realElementName)
                builder.beginControlFlow("if (!cursor.isNull(\$L))", columnIndex)
                builder.addStatement("\$L = cursor.\$L", columnMeta.realElementName, getFromCursor)
                builder.endControlFlow()
            } else {
                builder.addStatement("\$T \$L = cursor.\$L", name, columnMeta.realElementName, getFromCursor)
            }

            if (!first) paramsBuilder.append(", ")
            first = false
            paramsBuilder.append(columnMeta.realElementName)
        }
        paramsBuilder.append(")")
        builder.addCode("\n")

        // creator can't be null here
        if (typeMeta.creator!!.kind == ElementKind.CONSTRUCTOR) {
            builder.addStatement("\$T object = new \$T$paramsBuilder", className, className)
        } else {
            builder.addStatement("\$T object = \$T.\$L", className, className, "${typeMeta.creator!!.simpleName}$paramsBuilder")
        }

        return builder
                .addCode("\n")
                .addStatement("return object")
                .build()
    }

    fun generateName(typeMeta: StorIOSQLiteTypeMeta) = "${typeMeta.simpleName}$SUFFIX"
}
