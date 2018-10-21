package com.pushtorefresh.storio3.sqlite.annotations.processor.generate

import com.pushtorefresh.storio3.common.annotations.processor.generate.Common.INDENT
import com.pushtorefresh.storio3.common.annotations.processor.generate.Generator
import com.pushtorefresh.storio3.sqlite.annotations.processor.introspection.StorIOSQLiteTypeMeta
import com.squareup.javapoet.*
import javax.lang.model.element.Modifier.PUBLIC

private const val SUFFIX = "StorIOSQLiteDeleteResolver"

object DeleteResolverGenerator : Generator<StorIOSQLiteTypeMeta> {

    override fun generateJavaFile(typeMeta: StorIOSQLiteTypeMeta): JavaFile {
        val className = ClassName.get(typeMeta.packageName, typeMeta.simpleName)

        val deleteResolver = TypeSpec.classBuilder(generateName(typeMeta))
                .addJavadoc("Generated resolver for Delete Operation.\n")
                .addModifiers(PUBLIC)
                .superclass(ParameterizedTypeName.get(ClassName.get("com.pushtorefresh.storio3.sqlite.operations.delete", "DefaultDeleteResolver"), className))
                .addMethod(createMapToDeleteQueryMethodSpec(typeMeta, className))
                .build()

        return JavaFile
                .builder(typeMeta.packageName, deleteResolver)
                .indent(INDENT)
                .build()
    }

    private fun createMapToDeleteQueryMethodSpec(typeMeta: StorIOSQLiteTypeMeta, className: ClassName): MethodSpec {
        val where = QueryGenerator.createWhere(typeMeta, "object")

        return MethodSpec.methodBuilder("mapToDeleteQuery")
                .addJavadoc("{@inheritDoc}\n")
                .addAnnotation(Override::class.java)
                .addAnnotation(typeMeta.nonNullAnnotationClass)
                .addModifiers(PUBLIC)
                .returns(ClassName.get("com.pushtorefresh.storio3.sqlite.queries", "DeleteQuery"))
                .addParameter(ParameterSpec.builder(className, "object")
                        .addAnnotation(typeMeta.nonNullAnnotationClass)
                        .build())
                .addCode("""return DeleteQuery.builder()
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

    fun generateName(typeMeta: StorIOSQLiteTypeMeta) = "${typeMeta.simpleName}$SUFFIX"
}