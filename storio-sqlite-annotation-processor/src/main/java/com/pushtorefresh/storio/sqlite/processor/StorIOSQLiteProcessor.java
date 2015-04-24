package com.pushtorefresh.storio.sqlite.processor;

import com.google.auto.service.AutoService;
import com.pushtorefresh.storio.sqlite.annotation.StorIOSQLiteColumn;
import com.pushtorefresh.storio.sqlite.annotation.StorIOSQLiteType;
import com.pushtorefresh.storio.sqlite.processor.generate.DeleteResolverGenerator;
import com.pushtorefresh.storio.sqlite.processor.generate.GetResolverGenerator;
import com.pushtorefresh.storio.sqlite.processor.generate.PutResolverGenerator;
import com.pushtorefresh.storio.sqlite.processor.introspection.JavaType;
import com.pushtorefresh.storio.sqlite.processor.introspection.StorIOSQLiteColumnMeta;
import com.pushtorefresh.storio.sqlite.processor.introspection.StorIOSQLiteTypeMeta;

import org.jetbrains.annotations.NotNull;

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
import static javax.lang.model.element.Modifier.FINAL;
import static javax.lang.model.element.Modifier.PRIVATE;
import static javax.tools.Diagnostic.Kind.ERROR;

/**
 * Annotation processor for StorIOSQLite
 * <p/>
 * It'll process annotations to generate StorIOSQLite Object-Mapping
 * <p/>
 * Addition: Annotation Processor should work fast and be optimized because it's part of compilation
 * We don't want to annoy developers, who use StorIO
 */
// Generate file with annotation processor declaration via another Annotation Processor!
@AutoService(Processor.class)
public class StorIOSQLiteProcessor extends AbstractProcessor {

    private Filer filer;
    private Elements elementUtils;
    private Messager messager;

    /**
     * Processes class annotations
     *
     * @param roundEnvironment environment
     * @return non-null unmodifiable map(element, typeMeta)
     */
    @NotNull
    private static Map<TypeElement, StorIOSQLiteTypeMeta> processAnnotatedClasses(@NotNull final RoundEnvironment roundEnvironment, @NotNull final Elements elementUtils) {
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
    @NotNull
    private static TypeElement validateAnnotatedClass(@NotNull final Element annotatedElement) {
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
    @NotNull
    private static StorIOSQLiteTypeMeta processAnnotatedClass(@NotNull TypeElement classElement, @NotNull Elements elementUtils) {
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

        return new StorIOSQLiteTypeMeta(simpleName, packageName, storIOSQLiteType);
    }

    /**
     * Processes fields annotated with {@link StorIOSQLiteColumn}
     *
     * @param roundEnvironment current processing environment
     * @param annotatedClasses map of classes annotated with {@link StorIOSQLiteType}
     */
    private static void processAnnotatedFields(@NotNull final RoundEnvironment roundEnvironment, @NotNull Map<TypeElement, StorIOSQLiteTypeMeta> annotatedClasses) {
        final Set<? extends Element> elementsAnnotatedWithStorIOSQLiteColumn
                = roundEnvironment.getElementsAnnotatedWith(StorIOSQLiteColumn.class);

        for (final Element annotatedFieldElement : elementsAnnotatedWithStorIOSQLiteColumn) {
            validateAnnotatedField(annotatedFieldElement);
            final StorIOSQLiteColumnMeta storIOSQLiteColumnMeta = processAnnotatedField(annotatedFieldElement);

            final StorIOSQLiteTypeMeta storIOSQLiteTypeMeta = annotatedClasses.get(storIOSQLiteColumnMeta.enclosingElement);

            if (storIOSQLiteTypeMeta == null) {
                throw new ProcessingException(annotatedFieldElement, "Field marked with "
                        + StorIOSQLiteColumn.class.getSimpleName()
                        + " annotation should be placed in class marked by "
                        + StorIOSQLiteType.class.getSimpleName()
                        + " annotation"
                );
            }

            // Put meta column info
            // If class already contains column with same name -> throw exception
            if (storIOSQLiteTypeMeta.columns.put(storIOSQLiteColumnMeta.storIOSQLiteColumn.name(), storIOSQLiteColumnMeta) != null) {
                throw new ProcessingException(annotatedFieldElement, "Column name already used in this class");
            }
        }
    }

    //region Processing of annotated classes

    /**
     * Checks that element annotated with {@link StorIOSQLiteColumn} satisfies all required conditions
     *
     * @param annotatedField element annotated with {@link StorIOSQLiteColumn}
     */
    private static void validateAnnotatedField(@NotNull final Element annotatedField) {
        // we expect here that annotatedElement is Field, annotation requires that via @Target

        final Element enclosingElement = annotatedField.getEnclosingElement();

        if (!enclosingElement.getKind().equals(CLASS)) {
            throw new ProcessingException(
                    annotatedField,
                    "Please apply " + StorIOSQLiteType.class.getSimpleName() + " to fields of class: " + annotatedField.getSimpleName()
            );
        }

        if (enclosingElement.getAnnotation(StorIOSQLiteType.class) == null) {
            throw new ProcessingException(
                    annotatedField,
                    "Please annotate class " + enclosingElement.getSimpleName() + " with " + StorIOSQLiteType.class.getSimpleName()
            );
        }

        if (annotatedField.getModifiers().contains(PRIVATE)) {
            throw new ProcessingException(
                    annotatedField,
                    StorIOSQLiteColumn.class.getSimpleName() + " can not be applied to private field: " + annotatedField.getSimpleName()
            );
        }

        if (annotatedField.getModifiers().contains(FINAL)) {
            throw new ProcessingException(
                    annotatedField,
                    StorIOSQLiteColumn.class.getSimpleName() + " can not be applied to final field: " + annotatedField.getSimpleName()
            );
        }
    }

    /**
     * Processes annotated field and returns result of processing or throws exception
     *
     * @param annotatedField field that was annotated with {@link StorIOSQLiteColumn}
     * @return non-null {@link StorIOSQLiteColumnMeta} with meta information about field
     */
    @NotNull
    private static StorIOSQLiteColumnMeta processAnnotatedField(@NotNull final Element annotatedField) {
        final JavaType javaType;

        try {
            javaType = JavaType.from(annotatedField.asType());
        } catch (Exception e) {
            throw new ProcessingException(annotatedField, "Unsupported type of field for "
                    + StorIOSQLiteColumn.class.getSimpleName()
                    + " annotation, if you need to serialize/deserialize field of that type "
                    + "-> please write your own resolver: "
                    + e.getMessage()
            );
        }

        final StorIOSQLiteColumn storIOSQLiteColumn = annotatedField.getAnnotation(StorIOSQLiteColumn.class);

        final String columnName = storIOSQLiteColumn.name();

        if (columnName == null || columnName.length() == 0) {
            throw new ProcessingException(annotatedField, "Column name is null or empty");
        }

        return new StorIOSQLiteColumnMeta(
                annotatedField.getEnclosingElement(),
                annotatedField,
                annotatedField.getSimpleName().toString(),
                javaType,
                storIOSQLiteColumn
        );
    }

    private static void validateAnnotatedClassesAndColumns(@NotNull Map<TypeElement, StorIOSQLiteTypeMeta> annotatedClasses) {
        // check that each annotated class has columns with at least one key column
        for (Map.Entry<TypeElement, StorIOSQLiteTypeMeta> annotatedClass : annotatedClasses.entrySet()) {
            if (annotatedClass.getValue().columns.size() == 0) {
                throw new ProcessingException(annotatedClass.getKey(),
                        "Class marked with "
                                + StorIOSQLiteType.class.getSimpleName()
                                + " annotation should have at least one field marked with "
                                + StorIOSQLiteColumn.class.getSimpleName()
                                + " annotation");
            }

            boolean hasAtLeastOneKeyColumn = false;

            for (StorIOSQLiteColumnMeta columnMeta : annotatedClass.getValue().columns.values()) {
                if (columnMeta.storIOSQLiteColumn.key()) {
                    hasAtLeastOneKeyColumn = true;
                    break;
                }
            }

            if (!hasAtLeastOneKeyColumn) {
                throw new ProcessingException(annotatedClass.getKey(),
                        "Class marked with "
                                + StorIOSQLiteType.class.getSimpleName()
                                + " annotation should have at least one KEY field marked with "
                                + StorIOSQLiteColumn.class.getSimpleName() + " annotation");
            }
        }
    }

    //endregion

    //region Processing of annotated fields

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

    //endregion

    /**
     * For those who don't familiar with Annotation Processing API — this is the main method of Annotation Processor lifecycle
     * <p/>
     * It will be after Java Compiler will find lang elements annotated with annotations from {@link #getSupportedAnnotationTypes()}
     *
     * @param annotations set of annotations
     * @param roundEnv    environment of current processing round
     * @return true if annotation processor should not be invoked in next rounds of annotation processing, false otherwise
     */
    @Override
    public boolean process(final Set<? extends TypeElement> annotations, final RoundEnvironment roundEnv) {
        try {
            final Map<TypeElement, StorIOSQLiteTypeMeta> annotatedClasses
                    = processAnnotatedClasses(roundEnv, elementUtils);

            processAnnotatedFields(roundEnv, annotatedClasses);

            validateAnnotatedClassesAndColumns(annotatedClasses);

            final PutResolverGenerator putResolverGenerator = new PutResolverGenerator();
            final GetResolverGenerator getResolverGenerator = new GetResolverGenerator();
            final DeleteResolverGenerator deleteResolverGenerator = new DeleteResolverGenerator();

            for (StorIOSQLiteTypeMeta storIOSQLiteTypeMeta : annotatedClasses.values()) {
                putResolverGenerator.generateJavaFile(storIOSQLiteTypeMeta).writeTo(filer);
                getResolverGenerator.generateJavaFile(storIOSQLiteTypeMeta).writeTo(filer);
                deleteResolverGenerator.generateJavaFile(storIOSQLiteTypeMeta).writeTo(filer);
            }
        } catch (ProcessingException e) {
            messager.printMessage(ERROR, e.getMessage(), e.element());
        } catch (Exception e) {
            messager.printMessage(ERROR, "Problem occurred with StorIOSQLiteProcessor: " + e.getMessage());
        }

        return true;
    }
}
