package com.pushtorefresh.storio.common.annotations.processor.generate;

import com.pushtorefresh.storio.common.annotations.processor.introspection.StorIOTypeMeta;
import com.squareup.javapoet.JavaFile;

import org.jetbrains.annotations.NotNull;

public interface Generator<TypeMeta extends StorIOTypeMeta> {

    JavaFile generateJavaFile(@NotNull TypeMeta typeMeta);
}
