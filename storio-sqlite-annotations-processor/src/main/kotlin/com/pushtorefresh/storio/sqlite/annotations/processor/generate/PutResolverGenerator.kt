package com.pushtorefresh.storio.sqlite.annotations.processor.generate

import com.pushtorefresh.storio.common.annotations.processor.generate.Common.ANDROID_NON_NULL_ANNOTATION_CLASS_NAME
import com.pushtorefresh.storio.common.annotations.processor.generate.Common.INDENT
import com.pushtorefresh.storio.common.annotations.processor.generate.Generator
import com.pushtorefresh.storio.sqlite.annotations.processor.introspection.StorIOSQLiteTypeMeta
import com.squareup.javapoet.*
import javax.lang.model.element.Modifier.PUBLIC

private const val SUFFIX = "StorIOSQLitePutResolver"

object PutResolverGenerator : Generator<StorIOSQLiteTypeMeta> {

    override fun generateJavaFile(typeMeta: StorIOSQLiteTypeMeta): JavaFile {
        val className = ClassName.get(typeMeta.packageName, typeMeta.simpleName)

        val putResolver = TypeSpec.classBuilder(generateName(typeMeta))
                .addJavadoc("Generated resolver for Put Operation.\n")
                .addModifiers(PUBLIC)
                .superclass(ParameterizedTypeName.get(ClassName.get("com.pushtorefresh.storio.sqlite.operations.put", "DefaultPutResolver"), className))
                .addMethod(createMapToInsertQueryMethodSpec(typeMeta, className))
                .addMethod(createMapToUpdateQueryMethodSpec(typeMeta, className))
                .addMethod(createMapToContentValuesMethodSpec(typeMeta, className))
                .build()

        return JavaFile
                .builder(typeMeta.packageName, putResolver)
                .indent(INDENT)
                .build()
    }

    private fun createMapToInsertQueryMethodSpec(typeMeta: StorIOSQLiteTypeMeta, className: ClassName): MethodSpec {
        return MethodSpec.methodBuilder("mapToInsertQuery")
                .addJavadoc("{@inheritDoc}\n")
                .addAnnotation(Override::class.java)
                .addAnnotation(ANDROID_NON_NULL_ANNOTATION_CLASS_NAME)
                .addModifiers(PUBLIC)
                .returns(ClassName.get("com.pushtorefresh.storio.sqlite.queries", "InsertQuery"))
                .addParameter(ParameterSpec.builder(className, "object")
                        .addAnnotation(ANDROID_NON_NULL_ANNOTATION_CLASS_NAME)
                        .build())
                .addCode("""return InsertQuery.builder()
                            $INDENT.table(${"$"}S)
                            $INDENT.build();
                         """.trimIndent(),
                        typeMeta.storIOType.table)
                .build()
    }

    private fun createMapToUpdateQueryMethodSpec(typeMeta: StorIOSQLiteTypeMeta, className: ClassName): MethodSpec {
        val where = QueryGenerator.createWhere(typeMeta, "object")

        return MethodSpec.methodBuilder("mapToUpdateQuery")
                .addJavadoc("{@inheritDoc}\n")
                .addAnnotation(Override::class.java)
                .addAnnotation(ANDROID_NON_NULL_ANNOTATION_CLASS_NAME)
                .addModifiers(PUBLIC)
                .returns(ClassName.get("com.pushtorefresh.storio.sqlite.queries", "UpdateQuery"))
                .addParameter(ParameterSpec.builder(className, "object")
                        .addAnnotation(ANDROID_NON_NULL_ANNOTATION_CLASS_NAME)
                        .build())
                .addCode("""return UpdateQuery.builder()
                            $INDENT.table(${"$"}S)
                            $INDENT.where(${"$"}S)
                            $INDENT.whereArgs(${"$"}L)
                            $INDENT.build();
                         """.trimIndent(),
                        typeMeta.storIOType.table,
                        where[QueryGenerator.WHERE_CLAUSE],
                        where[QueryGenerator.WHERE_ARGS])
                .build()
    }

    private fun createMapToContentValuesMethodSpec(typeMeta: StorIOSQLiteTypeMeta, className: ClassName): MethodSpec {
        val builder = MethodSpec.methodBuilder("mapToContentValues")
                .addJavadoc("{@inheritDoc}\n")
                .addAnnotation(Override::class.java)
                .addAnnotation(ANDROID_NON_NULL_ANNOTATION_CLASS_NAME)
                .addModifiers(PUBLIC)
                .returns(ClassName.get("android.content", "ContentValues"))
                .addParameter(ParameterSpec.builder(className, "object")
                        .addAnnotation(ANDROID_NON_NULL_ANNOTATION_CLASS_NAME)
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

    fun generateName(typeMeta: StorIOSQLiteTypeMeta) = "${typeMeta.simpleName}$SUFFIX"
}
