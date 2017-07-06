package com.pushtorefresh.storio.contentresolver.annotations.processor.generate

import com.pushtorefresh.storio.common.annotations.processor.generate.Common.ANDROID_NON_NULL_ANNOTATION_CLASS_NAME
import com.pushtorefresh.storio.common.annotations.processor.generate.Common.INDENT
import com.pushtorefresh.storio.common.annotations.processor.generate.Generator
import com.pushtorefresh.storio.contentresolver.annotations.processor.introspection.StorIOContentResolverTypeMeta
import com.squareup.javapoet.*
import javax.lang.model.element.Modifier.PUBLIC

private const val SUFFIX = "StorIOContentResolverDeleteResolver"

object DeleteResolverGenerator : Generator<StorIOContentResolverTypeMeta> {

    override fun generateJavaFile(typeMeta: StorIOContentResolverTypeMeta): JavaFile {
        val className = ClassName.get(typeMeta.packageName, typeMeta.simpleName)

        val deleteResolver = TypeSpec.classBuilder(generateName(typeMeta))
                .addJavadoc("Generated resolver for Delete Operation\n")
                .addModifiers(PUBLIC)
                .superclass(ParameterizedTypeName.get(ClassName.get("com.pushtorefresh.storio.contentresolver.operations.delete", "DefaultDeleteResolver"), className))
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
                .addAnnotation(ANDROID_NON_NULL_ANNOTATION_CLASS_NAME)
                .addModifiers(PUBLIC)
                .returns(ClassName.get("com.pushtorefresh.storio.contentresolver.queries", "DeleteQuery"))
                .addParameter(ParameterSpec.builder(className, "object")
                        .addAnnotation(ANDROID_NON_NULL_ANNOTATION_CLASS_NAME)
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