package com.pushtorefresh.storio.common.annotations.processor.generate

import com.pushtorefresh.storio.common.annotations.processor.introspection.StorIOTypeMeta
import com.squareup.javapoet.JavaFile

interface Generator<in TypeMeta : StorIOTypeMeta<*, *>> {

    fun generateJavaFile(typeMeta: TypeMeta): JavaFile
}