package com.pushtorefresh.storio.sqlite.processor.generate;

import com.squareup.javapoet.TypeSpec;

import javax.lang.model.element.Modifier;

public class DefaultPutResolverSourceGenerator {

    public String generateSources(String storIOSQLiteTypeSimpleName) {
        final TypeSpec putResolver = TypeSpec.classBuilder("StorIOSQLite" + storIOSQLiteTypeSimpleName)
                .addModifiers(Modifier.PUBLIC)
                .addJavadoc("Generated class for Default")
                .build();

        return null;
    }
}
