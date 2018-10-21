package com.pushtorefresh.storio3.contentresolver.annotations.processor.generate

import com.pushtorefresh.storio3.common.annotations.processor.generate.Common.INDENT
import com.pushtorefresh.storio3.common.annotations.processor.generate.Generator
import com.pushtorefresh.storio3.contentresolver.annotations.processor.introspection.StorIOContentResolverTypeMeta
import com.squareup.javapoet.*
import javax.lang.model.element.Modifier.PUBLIC

private const val SUFFIX = "StorIOContentResolverDeleteResolver"

object DeleteResolverGenerator : Generator<StorIOContentResolverTypeMeta> {

    override fun generateJavaFile(typeMeta: StorIOContentResolverTypeMeta): JavaFile {
        val className = ClassName.get(typeMeta.packageName, typeMeta.simpleName)

        val deleteResolver = TypeSpec.classBuilder(generateName(typeMeta))
                .addJavadoc("Generated resolver for Delete Operation\n")
                .addModifiers(PUBLIC)
                .superclass(ParameterizedTypeName.get(ClassName.get("com.pushtorefresh.storio3.contentresolver.operations.delete", "DefaultDeleteResolver"), className))
                .addMethod(createMapToDeleteQueryMethodSpec(typeMeta, className))
                .build()

        return JavaFile
                .builder(typeMeta.packageName, deleteResolver)
                .indent(INDENT)
                .build()
    }

    private fun createMapToDeleteQueryMethodSpec(typeMeta: StorIOContentResolverTypeMeta, className: ClassName): MethodSpec {
        val where = QueryGenerator.createWhere(typeMeta, "object")

        var deleteUri = typeMeta.storIOType.deleteUri
        if (deleteUri.isEmpty()) deleteUri = typeMeta.storIOType.uri

        return MethodSpec.methodBuilder("mapToDeleteQuery")
                .addJavadoc("{@inheritDoc}\n")
                .addAnnotation(Override::class.java)
                .addAnnotation(typeMeta.nonNullAnnotationClass)
                .addModifiers(PUBLIC)
                .returns(ClassName.get("com.pushtorefresh.storio3.contentresolver.queries", "DeleteQuery"))
                .addParameter(ParameterSpec.builder(className, "object")
                        .addAnnotation(typeMeta.nonNullAnnotationClass)
                        .build())
                .addCode("""return DeleteQuery.builder()
                            $INDENT.uri(${"$"}S)
                            $INDENT.where(${"$"}S)
                            $INDENT.whereArgs(${"$"}L)
                            $INDENT.build();
                         """.trimIndent(),
                        deleteUri,
                        where[QueryGenerator.WHERE_CLAUSE],
                        where[QueryGenerator.WHERE_ARGS])
                .build()
    }

    fun generateName(typeMeta: StorIOContentResolverTypeMeta) = "${typeMeta.simpleName}$SUFFIX"
}