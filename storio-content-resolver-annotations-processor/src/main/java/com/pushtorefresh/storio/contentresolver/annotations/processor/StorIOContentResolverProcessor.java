package com.pushtorefresh.storio.contentresolver.annotations.processor;

import com.google.auto.service.AutoService;
import com.pushtorefresh.storio.common.annotations.processor.ProcessingException;
import com.pushtorefresh.storio.common.annotations.processor.SkipNotAnnotatedClassWithAnnotatedParentException;
import com.pushtorefresh.storio.common.annotations.processor.StorIOAnnotationsProcessor;
import com.pushtorefresh.storio.common.annotations.processor.generate.Generator;
import com.pushtorefresh.storio.common.annotations.processor.introspection.JavaType;
import com.pushtorefresh.storio.contentresolver.annotations.StorIOContentResolverColumn;
import com.pushtorefresh.storio.contentresolver.annotations.StorIOContentResolverCreator;
import com.pushtorefresh.storio.contentresolver.annotations.StorIOContentResolverType;
import com.pushtorefresh.storio.contentresolver.annotations.processor.generate.DeleteResolverGenerator;
import com.pushtorefresh.storio.contentresolver.annotations.processor.generate.GetResolverGenerator;
import com.pushtorefresh.storio.contentresolver.annotations.processor.generate.MappingGenerator;
import com.pushtorefresh.storio.contentresolver.annotations.processor.generate.PutResolverGenerator;
import com.pushtorefresh.storio.contentresolver.annotations.processor.introspection.StorIOContentResolverColumnMeta;
import com.pushtorefresh.storio.contentresolver.annotations.processor.introspection.StorIOContentResolverCreatorMeta;
import com.pushtorefresh.storio.contentresolver.annotations.processor.introspection.StorIOContentResolverTypeMeta;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;

import static javax.tools.Diagnostic.Kind.WARNING;

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
        final Set<String> supportedAnnotations = new HashSet<String>(3);

        supportedAnnotations.add(StorIOContentResolverType.class.getCanonicalName());
        supportedAnnotations.add(StorIOContentResolverColumn.class.getCanonicalName());
        supportedAnnotations.add(StorIOContentResolverCreator.class.getCanonicalName());

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

        final String commonUri = storIOContentResolverType.uri();

        final Map<String, String> urisForOperations = new HashMap<String, String>(3);
        urisForOperations.put("insert", storIOContentResolverType.insertUri());
        urisForOperations.put("update", storIOContentResolverType.updateUri());
        urisForOperations.put("delete", storIOContentResolverType.deleteUri());

        validateUris(classElement, commonUri, urisForOperations);

        final String simpleName = classElement.getSimpleName().toString();
        final String packageName = elementUtils.getPackageOf(classElement).getQualifiedName().toString();

        return new StorIOContentResolverTypeMeta(simpleName, packageName, storIOContentResolverType);
    }


    /**
     * Verifies that uris are valid.
     *
     * @param classElement    type element
     * @param commonUri       nullable default uri for all operations
     * @param operationUriMap non-null map where
     *                        key - operation name,
     *                        value - specific uri for this operation
     */
    protected void validateUris(
            @NotNull TypeElement classElement,
            @Nullable String commonUri,
            @NotNull Map<String, String> operationUriMap) {

        if (!validateUri(commonUri)) {
            final List<String> operationsWithInvalidUris = new ArrayList<String>(operationUriMap.size());
            for (Map.Entry<String, String> entry : operationUriMap.entrySet()) {
                if (!validateUri(entry.getValue())) {
                    operationsWithInvalidUris.add(entry.getKey());
                }
            }
            if (!operationsWithInvalidUris.isEmpty()) {
                String message = "Uri of " + classElement.getQualifiedName()
                        + " annotated with " + getTypeAnnotationClass().getSimpleName() + " is null or empty";

                if (operationsWithInvalidUris.size() < operationUriMap.size()) {
                    message += " for operation " + operationsWithInvalidUris.get(0);
                }
                // Else (there is no any uris) - do not specify operation,
                // because commonUri is default and straightforward way.

                throw new ProcessingException(classElement, message);
            }

            // It will be okay if uris for all operations were specified separately.
        }
    }

    private boolean validateUri(@Nullable String uri) {
        return uri != null && uri.length() > 0;
    }

    /**
     * Processes fields annotated with {@link StorIOContentResolverColumn}
     *
     * @param roundEnvironment current processing environment
     * @param annotatedClasses map of classes annotated with {@link StorIOContentResolverType}
     */
    @Override
    protected void processAnnotatedFieldsOrMethods(@NotNull final RoundEnvironment roundEnvironment, @NotNull final Map<TypeElement, StorIOContentResolverTypeMeta> annotatedClasses) {
        final Set<? extends Element> elementsAnnotatedWithStorIOContentResolverColumn
                = roundEnvironment.getElementsAnnotatedWith(StorIOContentResolverColumn.class);

        for (final Element annotatedFieldElement : elementsAnnotatedWithStorIOContentResolverColumn) {
            try {
                validateAnnotatedFieldOrMethod(annotatedFieldElement);

                final StorIOContentResolverColumnMeta storIOContentResolverColumnMeta = processAnnotatedFieldOrMethod(annotatedFieldElement);

                final StorIOContentResolverTypeMeta storIOContentResolverTypeMeta = annotatedClasses.get(storIOContentResolverColumnMeta.enclosingElement);

                if (storIOContentResolverTypeMeta == null) {
                    throw new ProcessingException(annotatedFieldElement, "Field marked with "
                            + StorIOContentResolverColumn.class.getSimpleName()
                            + " annotation should be placed in class marked by "
                            + StorIOContentResolverType.class.getSimpleName()
                            + " annotation"
                    );
                }

                // If class already contains column with same name -> throw an exception.
                if (storIOContentResolverTypeMeta.columns.containsKey(storIOContentResolverColumnMeta.storIOColumn.name())) {
                    throw new ProcessingException(annotatedFieldElement, "Column name already used in this class");
                }

                // If field annotation applied to both fields and methods in a same class.
                if ((storIOContentResolverTypeMeta.needCreator && !storIOContentResolverColumnMeta.isMethod()) ||
                        (!storIOContentResolverTypeMeta.needCreator && storIOContentResolverColumnMeta.isMethod() && !storIOContentResolverTypeMeta.columns.isEmpty())) {
                    throw new ProcessingException(annotatedFieldElement, "Can't apply"
                            + StorIOContentResolverColumn.class.getSimpleName()
                            + " annotation to both fields and methods in a same class"
                    );
                }

                // If column needs creator then enclosing class needs it as well.
                if (!storIOContentResolverTypeMeta.needCreator && storIOContentResolverColumnMeta.isMethod()) {
                    storIOContentResolverTypeMeta.needCreator = true;
                }

                // Put meta column info.
                storIOContentResolverTypeMeta.columns.put(storIOContentResolverColumnMeta.storIOColumn.name(), storIOContentResolverColumnMeta);
            } catch (SkipNotAnnotatedClassWithAnnotatedParentException e) {
                messager.printMessage(WARNING, e.getMessage());
            }
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
    protected StorIOContentResolverColumnMeta processAnnotatedFieldOrMethod(@NotNull final Element annotatedField) {
        final JavaType javaType;

        try {
            javaType = JavaType.from(annotatedField.getKind() == ElementKind.FIELD ? annotatedField.asType() : ((ExecutableElement) annotatedField).getReturnType());
        } catch (Exception e) {
            throw new ProcessingException(annotatedField, "Unsupported type of field for "
                    + StorIOContentResolverColumn.class.getSimpleName()
                    + " annotation, if you need to serialize/deserialize field of that type "
                    + "-> please write your own resolver: "
                    + e.getMessage()
            );
        }

        final StorIOContentResolverColumn storIOContentResolverColumn = annotatedField.getAnnotation(StorIOContentResolverColumn.class);

        if (storIOContentResolverColumn.ignoreNull() && annotatedField.asType().getKind().isPrimitive()) {
            throw new ProcessingException(
                    annotatedField,
                    "ignoreNull should not be used for primitive type: " + annotatedField.asType());
        }

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

    /**
     * Processes factory methods or constructors annotated with {@link StorIOContentResolverCreator}.
     *
     * @param roundEnvironment current processing environment
     * @param annotatedClasses map of classes annotated with {@link StorIOContentResolverType}
     */
    @Override
    protected void processAnnotatedExecutables(@NotNull RoundEnvironment roundEnvironment, @NotNull Map<TypeElement, StorIOContentResolverTypeMeta> annotatedClasses) {
        final Set<? extends Element> elementsAnnotatedWithStorIOContentResolverCreator
                = roundEnvironment.getElementsAnnotatedWith(StorIOContentResolverCreator.class);

        for (final Element annotatedElement : elementsAnnotatedWithStorIOContentResolverCreator) {
            final ExecutableElement annotatedExecutableElement = (ExecutableElement) annotatedElement;
            validateAnnotatedExecutable(annotatedExecutableElement);
            final StorIOContentResolverCreatorMeta storIOContentResolverCreatorMeta = new StorIOContentResolverCreatorMeta(
                    annotatedExecutableElement.getEnclosingElement(),
                    annotatedExecutableElement,
                    annotatedExecutableElement.getAnnotation(StorIOContentResolverCreator.class));

            final StorIOContentResolverTypeMeta storIOContentResolverTypeMeta = annotatedClasses.get(storIOContentResolverCreatorMeta.enclosingElement);

            if (storIOContentResolverTypeMeta == null) {
                throw new ProcessingException(annotatedElement, "Method or constructor marked with "
                        + StorIOContentResolverCreator.class.getSimpleName()
                        + " annotation should be placed in class marked by "
                        + StorIOContentResolverType.class.getSimpleName()
                        + " annotation"
                );
            }

            // Put meta creator info.
            // If class already contains another creator -> throw exception.
            if (storIOContentResolverTypeMeta.creator == null) {
                storIOContentResolverTypeMeta.creator = annotatedExecutableElement;
            } else {
                throw new ProcessingException(annotatedExecutableElement, "Only one creator method or constructor is allowed");
            }
        }
    }

    @Override
    protected void validateAnnotatedClassesAndColumns(@NotNull final Map<TypeElement, StorIOContentResolverTypeMeta> annotatedClasses) {
        // check that each annotated class has columns with at least one key column
        for (Map.Entry<TypeElement, StorIOContentResolverTypeMeta> annotatedType : annotatedClasses.entrySet()) {
            final StorIOContentResolverTypeMeta storIOContentResolverTypeMeta = annotatedType.getValue();

            if (storIOContentResolverTypeMeta.columns.isEmpty()) {
                throw new ProcessingException(annotatedType.getKey(),
                        "Class marked with "
                                + StorIOContentResolverType.class.getSimpleName()
                                + " annotation should have at least one field marked with "
                                + StorIOContentResolverColumn.class.getSimpleName()
                                + " annotation");
            }

            boolean hasAtLeastOneKeyColumn = false;

            for (final StorIOContentResolverColumnMeta columnMeta : annotatedType.getValue().columns.values()) {
                if (columnMeta.storIOColumn.key()) {
                    hasAtLeastOneKeyColumn = true;
                    break;
                }
            }

            if (!hasAtLeastOneKeyColumn) {
                throw new ProcessingException(annotatedType.getKey(),
                        "Class marked with "
                                + StorIOContentResolverType.class.getSimpleName()
                                + " annotation should have at least one KEY field marked with "
                                + StorIOContentResolverColumn.class.getSimpleName() + " annotation");
            }

            if (storIOContentResolverTypeMeta.needCreator && storIOContentResolverTypeMeta.creator == null) {
                throw new ProcessingException(annotatedType.getKey(),
                        "Class marked with "
                                + StorIOContentResolverType.class.getSimpleName()
                                + " annotation needs factory method or constructor marked with "
                                + StorIOContentResolverColumn.class.getSimpleName() + " annotation");
            }

            if (storIOContentResolverTypeMeta.needCreator && storIOContentResolverTypeMeta.creator.getParameters().size() != storIOContentResolverTypeMeta.columns.size()) {
                throw new ProcessingException(annotatedType.getKey(),
                        "Class marked with "
                                + StorIOContentResolverType.class.getSimpleName()
                                + " annotation needs factory method or constructor marked with "
                                + StorIOContentResolverColumn.class.getSimpleName() + " annotation with the same amount of parameters as the number of columns");
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
    protected Class<? extends Annotation> getCreatorAnnotationClass() {
        return StorIOContentResolverCreator.class;
    }

    @NotNull
    @Override
    protected Generator<StorIOContentResolverTypeMeta> createPutResolver() {
        return new PutResolverGenerator();
    }

    @NotNull
    @Override
    protected Generator<StorIOContentResolverTypeMeta> createGetResolver() {
        return new GetResolverGenerator();
    }

    @NotNull
    @Override
    protected Generator<StorIOContentResolverTypeMeta> createDeleteResolver() {
        return new DeleteResolverGenerator();
    }

    @NotNull
    @Override
    protected Generator<StorIOContentResolverTypeMeta> createMapping() {
        return new MappingGenerator();
    }
}
