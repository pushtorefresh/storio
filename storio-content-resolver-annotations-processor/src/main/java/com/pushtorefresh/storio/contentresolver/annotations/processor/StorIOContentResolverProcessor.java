package com.pushtorefresh.storio.contentresolver.annotations.processor;

import com.google.auto.service.AutoService;
import com.pushtorefresh.storio.common.annotations.processor.ProcessingException;
import com.pushtorefresh.storio.common.annotations.processor.StorIOAnnotationsProcessor;
import com.pushtorefresh.storio.common.annotations.processor.generate.ResolverGenerator;
import com.pushtorefresh.storio.common.annotations.processor.introspection.JavaType;
import com.pushtorefresh.storio.contentresolver.annotations.StorIOContentResolverColumn;
import com.pushtorefresh.storio.contentresolver.annotations.StorIOContentResolverType;
import com.pushtorefresh.storio.contentresolver.annotations.processor.generate.DeleteResolverGenerator;
import com.pushtorefresh.storio.contentresolver.annotations.processor.generate.GetResolverGenerator;
import com.pushtorefresh.storio.contentresolver.annotations.processor.generate.PutResolverGenerator;
import com.pushtorefresh.storio.contentresolver.annotations.processor.introspection.StorIOContentResolverColumnMeta;
import com.pushtorefresh.storio.contentresolver.annotations.processor.introspection.StorIOContentResolverTypeMeta;

import org.jetbrains.annotations.NotNull;

import java.lang.annotation.Annotation;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;

import static javax.lang.model.element.ElementKind.CLASS;
import static javax.lang.model.element.Modifier.FINAL;
import static javax.lang.model.element.Modifier.PRIVATE;

/**
 * Annotation processor for StorIOContentResolver
 * <p>
 * It'll process annotations to generate StorIOContentResolver Object-Mapping
 * <p>
 * Addition: Annotation Processor should work fast and be optimized because it's part of compilation
 * We don't want to annoy developers, who use StorIO
 */
// Generate file with annotation processor declaration via another Annotation Processor!
@AutoService(Processor.class)
public class StorIOContentResolverProcessor extends StorIOAnnotationsProcessor<StorIOContentResolverTypeMeta, StorIOContentResolverColumnMeta> {

    @NotNull
    @Override
    public Set<String> getSupportedAnnotationTypes() {
        final Set<String> supportedAnnotations = new HashSet<String>(2);

        supportedAnnotations.add(StorIOContentResolverType.class.getCanonicalName());
        supportedAnnotations.add(StorIOContentResolverColumn.class.getCanonicalName());

        return supportedAnnotations;
    }

    /**
     * Processes annotated class
     *
     * @param classElement type element annotated with {@link StorIOContentResolverType}
     * @param elementUtils utils for working with elementUtils
     * @return result of processing as {@link StorIOContentResolverTypeMeta}
     */
    @NotNull
    @Override
    protected StorIOContentResolverTypeMeta processAnnotatedClass(@NotNull TypeElement classElement, @NotNull Elements elementUtils) {
        final StorIOContentResolverType storIOContentResolverType = classElement.getAnnotation(StorIOContentResolverType.class);

        final String uri = storIOContentResolverType.uri();

        if (uri == null || uri.length() == 0) {
            throw new ProcessingException(
                    classElement,
                    "Uri of " + classElement.getQualifiedName() + " annotated with " + StorIOContentResolverType.class.getSimpleName() + " is null or empty"
            );
        }

        final String simpleName = classElement.getSimpleName().toString();
        final String packageName = elementUtils.getPackageOf(classElement).getQualifiedName().toString();

        return new StorIOContentResolverTypeMeta(simpleName, packageName, storIOContentResolverType);
    }

    /**
     * Processes fields annotated with {@link StorIOContentResolverColumn}
     *
     * @param roundEnvironment current processing environment
     * @param annotatedClasses map of classes annotated with {@link StorIOContentResolverType}
     */
    @Override
    protected void processAnnotatedFields(@NotNull final RoundEnvironment roundEnvironment, @NotNull final Map<TypeElement, StorIOContentResolverTypeMeta> annotatedClasses) {
        final Set<? extends Element> elementsAnnotatedWithStorIOContentResolverColumn
                = roundEnvironment.getElementsAnnotatedWith(StorIOContentResolverColumn.class);

        for (final Element annotatedFieldElement : elementsAnnotatedWithStorIOContentResolverColumn) {
            validateAnnotatedField(annotatedFieldElement);

            final StorIOContentResolverColumnMeta storIOContentResolverColumnMeta = processAnnotatedField(annotatedFieldElement);
            final StorIOContentResolverTypeMeta storIOContentResolverTypeMeta = annotatedClasses.get(storIOContentResolverColumnMeta.enclosingElement);

            if (storIOContentResolverTypeMeta == null) {
                throw new ProcessingException(
                        annotatedFieldElement, "Field marked with "
                        + StorIOContentResolverColumn.class.getSimpleName()
                        + " annotation should be placed in class marked by "
                        + StorIOContentResolverType.class.getSimpleName()
                        + " annotation"
                );
            }

            // Put meta column info
            // If class already contains column with same name -> throw exception
            if (storIOContentResolverTypeMeta.columns.put(storIOContentResolverColumnMeta.fieldName, storIOContentResolverColumnMeta) != null) {
                throw new ProcessingException(annotatedFieldElement, "Column name already used in this class");
            }
        }
    }

    /**
     * Checks that element annotated with {@link StorIOContentResolverColumn} satisfies all required conditions
     *
     * @param annotatedField element annotated with {@link StorIOContentResolverColumn}
     */
    @Override
    protected void validateAnnotatedField(@NotNull final Element annotatedField) {
        // we expect here that annotatedElement is Field, annotation requires that via @Target

        final Element enclosingElement = annotatedField.getEnclosingElement();

        if (!enclosingElement.getKind().equals(CLASS)) {
            throw new ProcessingException(
                annotatedField,
                    "Please apply " + StorIOContentResolverType.class.getSimpleName() + " to fields of class: " + annotatedField.getSimpleName()
            );
        }

        if (enclosingElement.getAnnotation(StorIOContentResolverType.class) == null) {
            throw new ProcessingException(
                    annotatedField,
                    "Please annotate class " + enclosingElement.getSimpleName() + " with " + StorIOContentResolverType.class.getSimpleName()
            );
        }

        if (annotatedField.getModifiers().contains(PRIVATE)) {
            throw new ProcessingException(
              annotatedField,
                    StorIOContentResolverColumn.class.getSimpleName() + " can not be applied to private field: " + annotatedField.getSimpleName()
            );
        }

        if (annotatedField.getModifiers().contains(FINAL)) {
            throw new ProcessingException(
                    annotatedField,
                    StorIOContentResolverColumn.class.getSimpleName() + " can not be applied to final field: " + annotatedField.getSimpleName()
            );
        }
    }

    /**
     * Processes annotated field and returns result of processing or throws exception
     *
     * @param annotatedField field that was annotated with {@link StorIOContentResolverColumn}
     * @return non-null {@link StorIOContentResolverColumnMeta} with meta information about field
     */
    @NotNull
    @Override
    protected StorIOContentResolverColumnMeta processAnnotatedField(@NotNull final Element annotatedField) {
        final JavaType javaType;

        try {
            javaType = JavaType.from(annotatedField.asType());
        } catch (Exception e) {
            throw new ProcessingException(
                    annotatedField, "Unsupported type of field for "
                    + StorIOContentResolverColumn.class.getSimpleName()
                    + " annotation, if you need to serialize/deserialize field of that type "
                    + "-> please write your own resolver: "
                    + e.getMessage()
            );
        }

        final StorIOContentResolverColumn storIOContentResolverColumn = annotatedField.getAnnotation(StorIOContentResolverColumn.class);

        final String columnName = storIOContentResolverColumn.name();

        if (columnName == null || columnName.length() == 0) {
            throw new ProcessingException(annotatedField, "Column name is null or empty");
        }

        return new StorIOContentResolverColumnMeta(
                annotatedField.getEnclosingElement(),
                annotatedField,
                annotatedField.getSimpleName().toString(),
                javaType,
                storIOContentResolverColumn
        );
    }

    @Override
    protected void validateAnnotatedClassesAndColumns(@NotNull final Map<TypeElement, StorIOContentResolverTypeMeta> annotatedClasses) {
        // check that each annotated class has columns with at least one key column
        for (Map.Entry<TypeElement, StorIOContentResolverTypeMeta> annotatedClass : annotatedClasses.entrySet()) {
            if (annotatedClass.getValue().columns.isEmpty()) {
                throw new ProcessingException(annotatedClass.getKey(),
                        "Class marked with "
                                + StorIOContentResolverType.class.getSimpleName()
                                + " annotation should have at least one field marked with "
                                + StorIOContentResolverColumn.class.getSimpleName()
                                + " annotation");
            }

            boolean hasAtLeastOneKeyColumn = false;

            for (final StorIOContentResolverColumnMeta columnMeta : annotatedClass.getValue().columns.values()) {
                if (columnMeta.storIOColumn.key()) {
                    hasAtLeastOneKeyColumn = true;
                    break;
                }
            }

            if (!hasAtLeastOneKeyColumn) {
                throw new ProcessingException(annotatedClass.getKey(),
                        "Class marked with "
                                + StorIOContentResolverType.class.getSimpleName()
                                + " annotation should have at least one KEY field marked with "
                                + StorIOContentResolverColumn.class.getSimpleName() + " annotation");
            }
        }
    }

    @NotNull
    @Override
    protected Class<? extends Annotation> getTypeAnnotationClass() {
        return StorIOContentResolverType.class;
    }

    @NotNull
    @Override
    protected Class<? extends Annotation> getColumnAnnotationClass() {
        return StorIOContentResolverColumn.class;
    }

    @NotNull
    @Override
    protected ResolverGenerator<StorIOContentResolverTypeMeta> createPutResolver() {
        return new PutResolverGenerator();
    }

    @NotNull
    @Override
    protected ResolverGenerator<StorIOContentResolverTypeMeta> createGetResolver() {
        return new GetResolverGenerator();
    }

    @NotNull
    @Override
    protected ResolverGenerator<StorIOContentResolverTypeMeta> createDeleteResolver() {
        return new DeleteResolverGenerator();
    }
}
