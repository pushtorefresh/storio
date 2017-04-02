package com.pushtorefresh.storio.sqlite.annotations.processor.generate

import com.pushtorefresh.storio.common.annotations.processor.generate.Common.INDENT
import com.pushtorefresh.storio.common.annotations.processor.generate.Generator
import com.pushtorefresh.storio.sqlite.annotations.processor.introspection.StorIOSQLiteTypeMeta
import com.squareup.javapoet.*
import javax.lang.model.element.Modifier.PUBLIC

private const val SUFFIX = "SQLiteTypeMapping"

object MappingGenerator : Generator<StorIOSQLiteTypeMeta> {

    override fun generateJavaFile(typeMeta: StorIOSQLiteTypeMeta): JavaFile {
        val typeClassName = ClassName.get(typeMeta.packageName, typeMeta.simpleName)

        val superclass = ClassName.get("com.pushtorefresh.storio.sqlite", SUFFIX)
        val superclassParametrized = ParameterizedTypeName.get(superclass, typeClassName)

        val mapping = TypeSpec.classBuilder(typeMeta.simpleName + SUFFIX)
                .addJavadoc("Generated mapping with collection of resolvers.\n")
                .addModifiers(PUBLIC)
                .superclass(superclassParametrized)
                .addMethod(createConstructor(typeMeta))
                .build()

        return JavaFile
                .builder(typeMeta.packageName, mapping)
                .indent(INDENT)
                .build()
    }

    private fun createConstructor(typeMeta: StorIOSQLiteTypeMeta): MethodSpec {
        val putResolver = ClassName.get(typeMeta.packageName, PutResolverGenerator.generateName(typeMeta))
        val getResolver = ClassName.get(typeMeta.packageName, GetResolverGenerator.generateName(typeMeta))
        val deleteResolver = ClassName.get(typeMeta.packageName, DeleteResolverGenerator.generateName(typeMeta))

        return MethodSpec.constructorBuilder()
                .addModifiers(PUBLIC)
                .addStatement("super(new \$T(),\nnew \$T(),\nnew \$T())", putResolver, getResolver, deleteResolver)
                .build()
    }
}