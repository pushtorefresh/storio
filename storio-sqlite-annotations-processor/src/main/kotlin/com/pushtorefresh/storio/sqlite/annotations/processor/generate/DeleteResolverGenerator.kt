package com.pushtorefresh.storio.sqlite.annotations.processor.generate

import com.pushtorefresh.storio.common.annotations.processor.generate.Common.ANDROID_NON_NULL_ANNOTATION_CLASS_NAME
import com.pushtorefresh.storio.common.annotations.processor.generate.Common.INDENT
import com.pushtorefresh.storio.common.annotations.processor.generate.Generator
import com.pushtorefresh.storio.sqlite.annotations.processor.introspection.StorIOSQLiteTypeMeta
import com.squareup.javapoet.*
import javax.lang.model.element.Modifier.PUBLIC

object DeleteResolverGenerator : Generator<StorIOSQLiteTypeMeta> {

    override fun generateJavaFile(typeMeta: StorIOSQLiteTypeMeta): JavaFile {
        val className = ClassName.get(typeMeta.packageName, typeMeta.simpleName)

        val deleteResolver = TypeSpec.classBuilder(generateName(typeMeta))
                .addJavadoc("Generated resolver for Delete Operation.\n")
                .addModifiers(PUBLIC)
                .superclass(ParameterizedTypeName.get(ClassName.get(
                        "com.pushtorefresh.storio.sqlite.operations.delete",
                        "DefaultDeleteResolver"),
                        className))
                .addMethod(createMapToDeleteQueryMethodSpec(typeMeta, className))
                .build()

        return JavaFile
                .builder(typeMeta.packageName, deleteResolver)
                .indent(INDENT)
                .build()
    }

    private fun createMapToDeleteQueryMethodSpec(storIOSQLiteTypeMeta: StorIOSQLiteTypeMeta,
                                                 storIOSQLiteTypeClassName: ClassName): MethodSpec {
        val where = QueryGenerator.createWhere(storIOSQLiteTypeMeta, "object")

        return MethodSpec.methodBuilder("mapToDeleteQuery")
                .addJavadoc("{@inheritDoc}\n")
                .addAnnotation(Override::class.java)
                .addAnnotation(ANDROID_NON_NULL_ANNOTATION_CLASS_NAME)
                .addModifiers(PUBLIC)
                .returns(ClassName.get("com.pushtorefresh.storio.sqlite.queries", "DeleteQuery"))
                .addParameter(ParameterSpec.builder(storIOSQLiteTypeClassName, "object")
                        .addAnnotation(ANDROID_NON_NULL_ANNOTATION_CLASS_NAME)
                        .build())
                .addCode("""return DeleteQuery.builder()
$INDENT.table(${"$"}S)
$INDENT.where(${"$"}S)
$INDENT.whereArgs(${"$"}L)
$INDENT.build();
""",
                        storIOSQLiteTypeMeta.storIOType.table,
                        where[QueryGenerator.WHERE_CLAUSE],
                        where[QueryGenerator.WHERE_ARGS])
                .build()
    }

    private val SUFFIX = "StorIOSQLiteDeleteResolver"

    fun generateName(storIOSQLiteTypeMeta: StorIOSQLiteTypeMeta) =
            "${storIOSQLiteTypeMeta.simpleName}$SUFFIX"
}
