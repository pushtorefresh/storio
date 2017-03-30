package com.pushtorefresh.storio.contentresolver.annotations.processor.generate

import com.pushtorefresh.storio.common.annotations.processor.generate.Common.ANDROID_NON_NULL_ANNOTATION_CLASS_NAME
import com.pushtorefresh.storio.common.annotations.processor.generate.Common.INDENT
import com.pushtorefresh.storio.common.annotations.processor.generate.Common.getFromCursorString
import com.pushtorefresh.storio.common.annotations.processor.generate.Generator
import com.pushtorefresh.storio.contentresolver.annotations.processor.introspection.StorIOContentResolverTypeMeta
import com.squareup.javapoet.*
import javax.lang.model.element.ElementKind
import javax.lang.model.element.ExecutableElement
import javax.lang.model.element.Modifier.PUBLIC

private const val SUFFIX = "StorIOContentResolverGetResolver"

object GetResolverGenerator : Generator<StorIOContentResolverTypeMeta> {

    override fun generateJavaFile(typeMeta: StorIOContentResolverTypeMeta): JavaFile {
        val className = ClassName.get(typeMeta.packageName, typeMeta.simpleName)

        val getResolver = TypeSpec.classBuilder(generateName(typeMeta))
                .addJavadoc("Generated resolver for Get Operation\n")
                .addModifiers(PUBLIC)
                .superclass(ParameterizedTypeName.get(ClassName.get("com.pushtorefresh.storio.contentresolver.operations.get", "DefaultGetResolver"), className))
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

    private fun createMapFromCursorMethodSpec(typeMeta: StorIOContentResolverTypeMeta, className: ClassName): MethodSpec {
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

        typeMeta.columns.values.forEach {
            val columnIndex = "cursor.getColumnIndex(\"${it.storIOColumn.name}\")"

            val javaType = it.javaType

            val getFromCursor = getFromCursorString(it, javaType, columnIndex)

            val isBoxed = javaType.isBoxedType
            // otherwise -> if primitive and value from cursor null -> fail early
            if (isBoxed) builder.beginControlFlow("if (!cursor.isNull(\$L))", columnIndex)

            builder.addStatement("object.\$L = cursor.\$L", it.elementName, getFromCursor)

            if (isBoxed) builder.endControlFlow()
        }

        return builder
                .addCode("\n")
                .addStatement("return object")
                .build()
    }

    private fun createMapFromCursorWithCreatorMethodSpec(typeMeta: StorIOContentResolverTypeMeta, className: ClassName): MethodSpec {
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
        typeMeta.orderedColumns.forEach {
            val columnIndex = "cursor.getColumnIndex(\"${it.storIOColumn.name}\")"

            val javaType = it.javaType

            val getFromCursor = getFromCursorString(it, javaType, columnIndex)

            val name = TypeName.get((it.element as ExecutableElement).returnType)

            val isBoxed = javaType.isBoxedType
            if (isBoxed) { // otherwise -> if primitive and value from cursor null -> fail early
                builder.addStatement("\$T \$L = null", name, it.realElementName)
                builder.beginControlFlow("if (!cursor.isNull(\$L))", columnIndex)
                builder.addStatement("\$L = cursor.\$L", it.realElementName, getFromCursor)
                builder.endControlFlow()
            } else {
                builder.addStatement("\$T \$L = cursor.\$L", name, it.realElementName, getFromCursor)
            }

            if (!first) paramsBuilder.append(", ")
            first = false
            paramsBuilder.append(it.realElementName)
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

    fun generateName(storIOSQLiteTypeMeta: StorIOContentResolverTypeMeta) = "${storIOSQLiteTypeMeta.simpleName}$SUFFIX"
}
