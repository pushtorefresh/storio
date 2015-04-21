package com.pushtorefresh.storio.sqlite.processor.generate;

import com.pushtorefresh.storio.sqlite.processor.introspection.StorIOSQLiteTypeMeta;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.TypeSpec;

import org.jetbrains.annotations.NotNull;

public class PutResolverSourceGenerator {

    @NotNull
    public JavaFile generateJavaFile(@NotNull StorIOSQLiteTypeMeta storIOSQLiteTypeMeta) {
        final TypeSpec putResolver = TypeSpec.classBuilder("StorIOSQLite" + storIOSQLiteTypeMeta.simpleName)
                .addJavadoc("Generated resolver for Put Operation")
                .build();

        return JavaFile
                .builder(storIOSQLiteTypeMeta.packageName, putResolver)
                .build();
    }
}
