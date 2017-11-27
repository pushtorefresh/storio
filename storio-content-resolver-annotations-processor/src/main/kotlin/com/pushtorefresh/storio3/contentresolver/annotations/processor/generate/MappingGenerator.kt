package com.pushtorefresh.storio3.contentresolver.annotations.processor.generate

import com.pushtorefresh.storio3.common.annotations.processor.generate.Common
import com.pushtorefresh.storio3.common.annotations.processor.generate.Generator
import com.pushtorefresh.storio3.contentresolver.annotations.processor.introspection.StorIOContentResolverTypeMeta
import com.squareup.javapoet.*
import javax.lang.model.element.Modifier.PUBLIC

private const val SUFFIX = "ContentResolverTypeMapping"

object MappingGenerator : Generator<StorIOContentResolverTypeMeta> {

    override fun generateJavaFile(typeMeta: StorIOContentResolverTypeMeta): JavaFile {
        val storIOSQLiteTypeClassName = ClassName.get(typeMeta.packageName, typeMeta.simpleName)

        val superclass = ClassName.get("com.pushtorefresh.storio3.contentresolver", SUFFIX)
        val superclassParametrized = ParameterizedTypeName.get(superclass, storIOSQLiteTypeClassName)

        val mapping = TypeSpec.classBuilder(typeMeta.simpleName + SUFFIX)
                .addJavadoc("Generated mapping with collection of resolvers\n")
                .addModifiers(PUBLIC)
                .superclass(superclassParametrized)
                .addMethod(createConstructor(typeMeta))
                .build()

        return JavaFile
                .builder(typeMeta.packageName, mapping)
                .indent(Common.INDENT)
                .build()
    }

    private fun createConstructor(typeMeta: StorIOContentResolverTypeMeta): MethodSpec {
        val putResolver = ClassName.get(typeMeta.packageName, PutResolverGenerator.generateName(typeMeta))
        val getResolver = ClassName.get(typeMeta.packageName, GetResolverGenerator.generateName(typeMeta))
        val deleteResolver = ClassName.get(typeMeta.packageName, DeleteResolverGenerator.generateName(typeMeta))

        return MethodSpec.constructorBuilder()
                .addModifiers(PUBLIC)
                .addStatement("super(new \$T(),\nnew \$T(),\nnew \$T())",
                        putResolver, getResolver, deleteResolver)
                .build()
    }
}