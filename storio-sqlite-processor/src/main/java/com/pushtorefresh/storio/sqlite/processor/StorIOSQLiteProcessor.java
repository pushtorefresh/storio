package com.pushtorefresh.storio.sqlite.processor;

import com.google.auto.service.AutoService;
import com.pushtorefresh.storio.sqlite.processor.annotation.StorIOSQLiteColumn;
import com.pushtorefresh.storio.sqlite.processor.annotation.StorIOSQLiteType;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;

import static javax.lang.model.element.ElementKind.CLASS;
import static javax.lang.model.element.Modifier.PRIVATE;
import static javax.tools.Diagnostic.Kind.ERROR;

/**
 * Annotation processor for StorIOSQLite
 * <p>
 * It'll process annotations to generate StorIOSQLite Object-Mapping
 * <p>
 * Addition: Annotation Processor should work fast and be optimized because it's part of compilation
 * We don't want to annoy developers, who use StorIO
 */
@AutoService(Processor.class)
// Generate file with annotation processor declaration via another Annotation Processor!
public class StorIOSQLiteProcessor extends AbstractProcessor {

    private Filer filer;
    private Elements elementUtils;
    private Messager messager;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        filer = processingEnv.getFiler();
        elementUtils = processingEnv.getElementUtils(); // why class name is "Elements" but method "getElementUtils()", OKAY..
        messager = processingEnv.getMessager();
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        final Set<String> supportedAnnotations = new HashSet<String>(2);

        supportedAnnotations.add(StorIOSQLiteType.class.getCanonicalName());
        supportedAnnotations.add(StorIOSQLiteColumn.class.getCanonicalName());

        return supportedAnnotations;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    /**
     * For those who don't familiar with Annotation Processing API — this is the main method
     * <p>
     * It will be after Java Compiler will find lang elements annotated with annotations from {@link #getSupportedAnnotationTypes()}
     *
     * @param annotations set of annotations
     * @param roundEnv    environment of current processing round
     * @return true if annotation processor should not be invoked in next rounds of annotation processing, false otherwise
     */
    @Override
    public boolean process(final Set<? extends TypeElement> annotations, final RoundEnvironment roundEnv) {
        try {
            final Map<TypeElement, StorIOSQLiteTypeMeta> annotatedClasses = processAnnotatedClasses(roundEnv, elementUtils);
        } catch (ProcessingException e) {
            messager.printMessage(ERROR, e.getMessage(), e.element());
        } catch (Exception e) {
            messager.printMessage(ERROR, "Problem occurred with StorIOSQLiteProcessor: " + e.getMessage());
        }

        return true;
    }

    //region Processing of annotated classes

    /**
     * Processes class annotations
     *
     * @param roundEnvironment environment
     * @return non-null unmodifiable map(element, typeMeta)
     */
    private static Map<TypeElement, StorIOSQLiteTypeMeta> processAnnotatedClasses(final RoundEnvironment roundEnvironment, final Elements elementUtils) {
        final Set<? extends Element> elementsAnnotatedWithStorIOSQLiteType
                = roundEnvironment.getElementsAnnotatedWith(StorIOSQLiteType.class);

        final Map<TypeElement, StorIOSQLiteTypeMeta> results
                = new HashMap<TypeElement, StorIOSQLiteTypeMeta>(elementsAnnotatedWithStorIOSQLiteType.size());

        for (final Element annotatedElement : elementsAnnotatedWithStorIOSQLiteType) {
            final TypeElement classElement = validateAnnotatedClass(annotatedElement);
            final StorIOSQLiteTypeMeta storIOSQLiteTypeMeta = processAnnotatedClass(classElement, elementUtils);
            results.put(classElement, storIOSQLiteTypeMeta);
        }

        return Collections.unmodifiableMap(results);
    }

    /**
     * Checks that element annotated with {@link StorIOSQLiteType} satisfies all required conditions
     *
     * @param annotatedElement element annotated with {@link StorIOSQLiteType}
     * @return {@link TypeElement} object
     */
    private static TypeElement validateAnnotatedClass(final Element annotatedElement) {
        // we expect here that annotatedElement is Class, annotation requires that via @Target
        final TypeElement annotatedTypeElement = (TypeElement) annotatedElement;

        if (annotatedTypeElement.getModifiers().contains(PRIVATE)) {
            throw new ProcessingException(
                    annotatedElement,
                    StorIOSQLiteType.class.getSimpleName() + " can not be applied to private class: " + annotatedTypeElement.getQualifiedName()
            );
        }

        return annotatedTypeElement;
    }

    /**
     * Processes annotated class
     *
     * @param classElement type element annotated with {@link StorIOSQLiteType}
     * @param elementUtils utils for working with elementUtils
     * @return result of processing as {@link StorIOSQLiteTypeMeta}
     */
    private static StorIOSQLiteTypeMeta processAnnotatedClass(TypeElement classElement, Elements elementUtils) {
        final StorIOSQLiteType storIOSQLiteType = classElement.getAnnotation(StorIOSQLiteType.class);

        final String tableName = storIOSQLiteType.table();

        if (tableName == null || tableName.length() == 0) {
            throw new ProcessingException(
                    classElement,
                    "Table name of " + classElement.getQualifiedName() + " annotated with " + StorIOSQLiteType.class.getSimpleName() + " is null or empty"
            );
        }

        final String simpleName = classElement.getSimpleName().toString();
        final String packageName = elementUtils.getPackageOf(classElement).getQualifiedName().toString();

        return new StorIOSQLiteTypeMeta(simpleName, packageName, tableName);
    }

    //endregion

    //region Processing of annotated fields

    private static void processAnnotatedFields(final RoundEnvironment roundEnvironment) {
        final Set<? extends Element> elementsAnnotatedWithStorIOSQLiteColumn
                = roundEnvironment.getElementsAnnotatedWith(StorIOSQLiteColumn.class);

        for (final Element element : elementsAnnotatedWithStorIOSQLiteColumn) {
            validateAnnotatedField(element);
            processAnnotatedField(element);
        }
    }

    /**
     * Checks that element annotated with {@link StorIOSQLiteColumn} satisfies all required conditions
     *
     * @param annotatedElement element annotated with {@link StorIOSQLiteColumn}
     */
    private static void validateAnnotatedField(final Element annotatedElement) {
        // we expect here that annotatedElement is Field, annotation requires that via @Target

        final Element enclosingElement = annotatedElement.getEnclosingElement();

        if (!enclosingElement.getKind().equals(CLASS)) {
            throw new ProcessingException(
                    annotatedElement,
                    "Please apply " + StorIOSQLiteType.class.getSimpleName() + " to fields of class: " + annotatedElement.getSimpleName()
            );
        }

        if (enclosingElement.getAnnotation(StorIOSQLiteType.class) == null) {
            throw new ProcessingException(
                    annotatedElement,
                    "Please annotate class " + enclosingElement.getSimpleName() + " with " + StorIOSQLiteType.class.getSimpleName()
            );
        }

        if (annotatedElement.getModifiers().contains(PRIVATE)) {
            throw new ProcessingException(
                    annotatedElement,
                    StorIOSQLiteColumn.class.getSimpleName() + " can not be applied to private field: " + annotatedElement.getSimpleName()
            );
        }
    }

    private static void processAnnotatedField(final Element annotatedField) {

    }

    //endregion
}
