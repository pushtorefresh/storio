package com.pushtorefresh.storio.sqlite.processor;

import com.google.auto.service.AutoService;
import com.pushtorefresh.storio.sqlite.processor.annotation.StorIOSQLiteColumn;
import com.pushtorefresh.storio.sqlite.processor.annotation.StorIOSQLiteType;

import java.util.HashSet;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.TypeElement;

/**
 * Annotation processor for StorIOSQLite
 * <p>
 * It'll process annotations to generate StorIOSQLite Default Operation Resolvers implementations
 */
@AutoService(Processor.class)
public class StorIOSQLiteProcessor extends AbstractProcessor {

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        final Set<String> supportedAnnotations = new HashSet<String>(2);

        supportedAnnotations.add(StorIOSQLiteType.class.getCanonicalName());
        supportedAnnotations.add(StorIOSQLiteColumn.class.getCanonicalName());

        return supportedAnnotations;
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {


        return true;
    }
}
