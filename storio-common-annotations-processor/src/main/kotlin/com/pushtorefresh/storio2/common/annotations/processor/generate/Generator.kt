package com.pushtorefresh.storio2.common.annotations.processor.generate

import com.pushtorefresh.storio2.common.annotations.processor.introspection.StorIOTypeMeta
import com.squareup.javapoet.JavaFile

interface Generator<in TypeMeta : StorIOTypeMeta<*, *>> {

    fun generateJavaFile(typeMeta: TypeMeta): JavaFile
}