package com.pushtorefresh.storio3.contentresolver.annotations.processor.generate

import com.pushtorefresh.storio3.common.annotations.processor.generate.Common.INDENT
import com.pushtorefresh.storio3.common.annotations.processor.generate.Generator
import com.pushtorefresh.storio3.contentresolver.annotations.processor.introspection.StorIOContentResolverTypeMeta
import com.squareup.javapoet.*
import javax.lang.model.element.Modifier.PUBLIC

private const val SUFFIX = "StorIOContentResolverPutResolver"

object PutResolverGenerator : Generator<StorIOContentResolverTypeMeta> {

    override fun generateJavaFile(typeMeta: StorIOContentResolverTypeMeta): JavaFile {
        val className = ClassName.get(typeMeta.packageName, typeMeta.simpleName)

        val putResolver = TypeSpec.classBuilder(generateName(typeMeta))
                .addJavadoc("Generated resolver for Put Operation\n")
                .addModifiers(PUBLIC)
                .superclass(ParameterizedTypeName.get(ClassName.get("com.pushtorefresh.storio3.contentresolver.operations.put", "DefaultPutResolver"), className))
                .addMethod(createMapToInsertQueryMethodSpec(typeMeta, className))
                .addMethod(createMapToUpdateQueryMethodSpec(typeMeta, className))
                .addMethod(createMapToContentValuesMethodSpec(typeMeta, className))
                .build()

        return JavaFile
                .builder(typeMeta.packageName, putResolver)
                .indent(INDENT)
                .build()

    }

    private fun createMapToInsertQueryMethodSpec(typeMeta: StorIOContentResolverTypeMeta, storIOContentResolverClassName: ClassName): MethodSpec {
        var insertUri = typeMeta.storIOType.insertUri
        if (insertUri.isEmpty()) insertUri = typeMeta.storIOType.uri

        return MethodSpec.methodBuilder("mapToInsertQuery")
                .addJavadoc("{@inheritDoc}\n")
                .addAnnotation(Override::class.java)
                .addAnnotation(typeMeta.nonNullAnnotationClass)
                .addModifiers(PUBLIC)
                .returns(ClassName.get("com.pushtorefresh.storio3.contentresolver.queries", "InsertQuery"))
                .addParameter(ParameterSpec.builder(storIOContentResolverClassName, "object")
                        .addAnnotation(typeMeta.nonNullAnnotationClass)
                        .build())
                .addCode("""return InsertQuery.builder()
                            $INDENT.uri(${"$"}S)
                            $INDENT.build();
                         """.trimIndent(),
                        insertUri)
                .build()
    }

    private fun createMapToUpdateQueryMethodSpec(typeMeta: StorIOContentResolverTypeMeta, className: ClassName): MethodSpec {
        val where = QueryGenerator.createWhere(typeMeta, "object")

        var updateUri = typeMeta.storIOType.updateUri
        if (updateUri.isEmpty()) updateUri = typeMeta.storIOType.uri

        return MethodSpec.methodBuilder("mapToUpdateQuery")
                .addJavadoc("{@inheritDoc}\n")
                .addAnnotation(Override::class.java)
                .addAnnotation(typeMeta.nonNullAnnotationClass)
                .addModifiers(PUBLIC)
                .returns(ClassName.get("com.pushtorefresh.storio3.contentresolver.queries", "UpdateQuery"))
                .addParameter(ParameterSpec.builder(className, "object")
                        .addAnnotation(typeMeta.nonNullAnnotationClass)
                        .build())
                .addCode("""return UpdateQuery.builder()
                            $INDENT.uri(${"$"}S)
                            $INDENT.where(${"$"}S)
                            $INDENT.whereArgs(${"$"}L)
                            $INDENT.build();
                         """.trimIndent(),
                        updateUri,
                        where[QueryGenerator.WHERE_CLAUSE],
                        where[QueryGenerator.WHERE_ARGS])
                .build()
    }

    private fun createMapToContentValuesMethodSpec(typeMeta: StorIOContentResolverTypeMeta, className: ClassName): MethodSpec {
        val builder = MethodSpec.methodBuilder("mapToContentValues")
                .addJavadoc("{@inheritDoc}\n")
                .addAnnotation(Override::class.java)
                .addAnnotation(typeMeta.nonNullAnnotationClass)
                .addModifiers(PUBLIC)
                .returns(ClassName.get("android.content", "ContentValues"))
                .addParameter(ParameterSpec.builder(className, "object")
                        .addAnnotation(typeMeta.nonNullAnnotationClass)
                        .build())
                .addStatement("ContentValues contentValues = new ContentValues(\$L)", typeMeta.columns.size)
                .addCode("\n")

        typeMeta.columns.values.forEach { columnMeta ->
            val ignoreNull = columnMeta.storIOColumn.ignoreNull
            if (ignoreNull) {
                builder.beginControlFlow("if (object.\$L != null)", columnMeta.contextAwareName)
            }
            builder.addStatement("contentValues.put(\$S, object.\$L)", columnMeta.storIOColumn.name, columnMeta.contextAwareName)
            if (ignoreNull) builder.endControlFlow()
        }

        return builder
                .addCode("\n")
                .addStatement("return contentValues")
                .build()
    }

    fun generateName(typeMeta: StorIOContentResolverTypeMeta) = "${typeMeta.simpleName}$SUFFIX"
}