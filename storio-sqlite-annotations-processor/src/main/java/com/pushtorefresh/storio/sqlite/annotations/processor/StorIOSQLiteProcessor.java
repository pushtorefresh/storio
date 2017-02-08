package com.pushtorefresh.storio.sqlite.annotations.processor;

import com.google.auto.service.AutoService;
import com.pushtorefresh.storio.common.annotations.processor.ProcessingException;
import com.pushtorefresh.storio.common.annotations.processor.SkipNotAnnotatedClassWithAnnotatedParentException;
import com.pushtorefresh.storio.common.annotations.processor.StorIOAnnotationsProcessor;
import com.pushtorefresh.storio.common.annotations.processor.generate.Generator;
import com.pushtorefresh.storio.common.annotations.processor.introspection.JavaType;
import com.pushtorefresh.storio.sqlite.annotations.StorIOSQLiteColumn;
import com.pushtorefresh.storio.sqlite.annotations.StorIOSQLiteCreator;
import com.pushtorefresh.storio.sqlite.annotations.StorIOSQLiteType;
import com.pushtorefresh.storio.sqlite.annotations.processor.generate.DeleteResolverGenerator;
import com.pushtorefresh.storio.sqlite.annotations.processor.generate.GetResolverGenerator;
import com.pushtorefresh.storio.sqlite.annotations.processor.generate.MappingGenerator;
import com.pushtorefresh.storio.sqlite.annotations.processor.generate.PutResolverGenerator;
import com.pushtorefresh.storio.sqlite.annotations.processor.introspection.StorIOSQLiteColumnMeta;
import com.pushtorefresh.storio.sqlite.annotations.processor.introspection.StorIOSQLiteCreatorMeta;
import com.pushtorefresh.storio.sqlite.annotations.processor.introspection.StorIOSQLiteTypeMeta;

import org.jetbrains.annotations.NotNull;

import java.lang.annotation.Annotation;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;

import static javax.tools.Diagnostic.Kind.WARNING;

/**
 * Annotation processor for StorIOSQLite.
 * <p>
 * It'll process annotations to generate StorIOSQLite Object-Mapping.
 * <p>
 * Addition: Annotation Processor should work fast and be optimized because it's part of compilation.
 * We don't want to annoy developers, who use StorIO.
 */
// Generate file with annotation processor declaration via another Annotation Processor!
@AutoService(Processor.class)
public class StorIOSQLiteProcessor extends StorIOAnnotationsProcessor<StorIOSQLiteTypeMeta, StorIOSQLiteColumnMeta> {

    @NotNull
    @Override
    public Set<String> getSupportedAnnotationTypes() {
        final Set<String> supportedAnnotations = new HashSet<String>(3);

        supportedAnnotations.add(StorIOSQLiteType.class.getCanonicalName());
        supportedAnnotations.add(StorIOSQLiteColumn.class.getCanonicalName());
        supportedAnnotations.add(StorIOSQLiteCreator.class.getCanonicalName());

        return supportedAnnotations;
    }

    /**
     * Processes annotated class.
     *
     * @param classElement type element annotated with {@link StorIOSQLiteType}
     * @param elementUtils utils for working with elementUtils
     * @return result of processing as {@link StorIOSQLiteTypeMeta}
     */
    @NotNull
    @Override
    protected StorIOSQLiteTypeMeta processAnnotatedClass(@NotNull TypeElement classElement, @NotNull Elements elementUtils) {
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

        return new StorIOSQLiteTypeMeta(simpleName, packageName, storIOSQLiteType, classElement.getModifiers().contains(Modifier.ABSTRACT));
    }

    /**
     * Processes fields annotated with {@link StorIOSQLiteColumn}.
     *
     * @param roundEnvironment current processing environment
     * @param annotatedClasses map of classes annotated with {@link StorIOSQLiteType}
     */
    @Override
    protected void processAnnotatedFieldsOrMethods(@NotNull final RoundEnvironment roundEnvironment, @NotNull Map<TypeElement, StorIOSQLiteTypeMeta> annotatedClasses) {
        final Set<? extends Element> elementsAnnotatedWithStorIOSQLiteColumn
                = roundEnvironment.getElementsAnnotatedWith(StorIOSQLiteColumn.class);

        for (final Element annotatedFieldElement : elementsAnnotatedWithStorIOSQLiteColumn) {
            try {
                validateAnnotatedFieldOrMethod(annotatedFieldElement);

                final StorIOSQLiteColumnMeta storIOSQLiteColumnMeta = processAnnotatedFieldOrMethod(annotatedFieldElement);

                final StorIOSQLiteTypeMeta storIOSQLiteTypeMeta = annotatedClasses.get(storIOSQLiteColumnMeta.enclosingElement);

                if (storIOSQLiteTypeMeta == null) {
                    throw new ProcessingException(annotatedFieldElement, "Field marked with "
                            + StorIOSQLiteColumn.class.getSimpleName()
                            + " annotation should be placed in class marked by "
                            + StorIOSQLiteType.class.getSimpleName()
                            + " annotation"
                    );
                }

                // If class already contains column with same name -> throw an exception.
                if (storIOSQLiteTypeMeta.columns.containsKey(storIOSQLiteColumnMeta.storIOColumn.name())) {
                    throw new ProcessingException(annotatedFieldElement, "Column name already used in this class");
                }

                // If field annotation applied to both fields and methods in a same class.
                if ((storIOSQLiteTypeMeta.needCreator && !storIOSQLiteColumnMeta.isMethod()) ||
                        (!storIOSQLiteTypeMeta.needCreator && storIOSQLiteColumnMeta.isMethod() && !storIOSQLiteTypeMeta.columns.isEmpty())) {
                    throw new ProcessingException(annotatedFieldElement, "Can't apply"
                            + StorIOSQLiteColumn.class.getSimpleName()
                            + " annotation to both fields and methods in a same class"
                    );
                }

                // If column needs creator then enclosing class needs it as well.
                if (!storIOSQLiteTypeMeta.needCreator && storIOSQLiteColumnMeta.isMethod()) {
                    storIOSQLiteTypeMeta.needCreator = true;
                }

                // Put meta column info.
                storIOSQLiteTypeMeta.columns.put(storIOSQLiteColumnMeta.storIOColumn.name(), storIOSQLiteColumnMeta);
            } catch (SkipNotAnnotatedClassWithAnnotatedParentException e) {
                messager.printMessage(WARNING, e.getMessage());
            }
        }
    }

    /**
     * Processes annotated field and returns result of processing or throws exception.
     *
     * @param annotatedField field that was annotated with {@link StorIOSQLiteColumn}
     * @return non-null {@link StorIOSQLiteColumnMeta} with meta information about field
     */
    @NotNull
    @Override
    protected StorIOSQLiteColumnMeta processAnnotatedFieldOrMethod(@NotNull final Element annotatedField) {
        final JavaType javaType;

        try {
            javaType = JavaType.from(annotatedField.getKind() == ElementKind.FIELD ? annotatedField.asType() : ((ExecutableElement) annotatedField).getReturnType());
        } catch (Exception e) {
            throw new ProcessingException(annotatedField, "Unsupported type of field for "
                    + StorIOSQLiteColumn.class.getSimpleName()
                    + " annotation, if you need to serialize/deserialize field of that type "
                    + "-> please write your own resolver: "
                    + e.getMessage()
            );
        }

        final StorIOSQLiteColumn storIOSQLiteColumn = annotatedField.getAnnotation(StorIOSQLiteColumn.class);

        if (storIOSQLiteColumn.ignoreNull() && annotatedField.asType().getKind().isPrimitive()) {
            throw new ProcessingException(
                    annotatedField,
                    "ignoreNull should not be used for primitive type: " + annotatedField.asType());
        }

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

    /**
     * Processes factory methods or constructors annotated with {@link StorIOSQLiteCreator}.
     *
     * @param roundEnvironment current processing environment
     * @param annotatedClasses map of classes annotated with {@link StorIOSQLiteType}
     */
    @Override
    protected void processAnnotatedExecutables(@NotNull RoundEnvironment roundEnvironment, @NotNull Map<TypeElement, StorIOSQLiteTypeMeta> annotatedClasses) {
        final Set<? extends Element> elementsAnnotatedWithStorIOSQLiteCreator
                = roundEnvironment.getElementsAnnotatedWith(StorIOSQLiteCreator.class);

        for (final Element annotatedElement : elementsAnnotatedWithStorIOSQLiteCreator) {
            final ExecutableElement annotatedExecutableElement = (ExecutableElement) annotatedElement;
            validateAnnotatedExecutable(annotatedExecutableElement);
            final StorIOSQLiteCreatorMeta storIOSQLiteCreatorMeta = new StorIOSQLiteCreatorMeta(
                    annotatedExecutableElement.getEnclosingElement(),
                    annotatedExecutableElement,
                    annotatedExecutableElement.getAnnotation(StorIOSQLiteCreator.class));

            final StorIOSQLiteTypeMeta storIOSQLiteTypeMeta = annotatedClasses.get(storIOSQLiteCreatorMeta.enclosingElement);

            if (storIOSQLiteTypeMeta == null) {
                throw new ProcessingException(annotatedElement, "Method or constructor marked with "
                        + StorIOSQLiteCreator.class.getSimpleName()
                        + " annotation should be placed in class marked by "
                        + StorIOSQLiteType.class.getSimpleName()
                        + " annotation"
                );
            }

            // Put meta creator info.
            // If class already contains another creator -> throw exception.
            if (storIOSQLiteTypeMeta.creator == null) {
                storIOSQLiteTypeMeta.creator = annotatedExecutableElement;
            } else {
                throw new ProcessingException(annotatedExecutableElement, "Only one creator method or constructor is allowed");
            }
        }
    }

    @Override
    protected void validateAnnotatedClassesAndColumns(@NotNull Map<TypeElement, StorIOSQLiteTypeMeta> annotatedClasses) {
        // Check that each annotated class has columns with at least one key column.
        for (final Map.Entry<TypeElement, StorIOSQLiteTypeMeta> annotatedType : annotatedClasses.entrySet()) {
            final StorIOSQLiteTypeMeta storIOSQLiteTypeMeta = annotatedType.getValue();

            if (storIOSQLiteTypeMeta.columns.size() == 0) {
                throw new ProcessingException(annotatedType.getKey(),
                        "Class marked with "
                                + StorIOSQLiteType.class.getSimpleName()
                                + " annotation should have at least one field marked with "
                                + StorIOSQLiteColumn.class.getSimpleName()
                                + " annotation");
            }

            boolean hasAtLeastOneKeyColumn = false;

            for (final StorIOSQLiteColumnMeta columnMeta : storIOSQLiteTypeMeta.columns.values()) {
                if (columnMeta.storIOColumn.key()) {
                    hasAtLeastOneKeyColumn = true;
                    break;
                }
            }

            if (!hasAtLeastOneKeyColumn) {
                throw new ProcessingException(annotatedType.getKey(),
                        "Class marked with "
                                + StorIOSQLiteType.class.getSimpleName()
                                + " annotation should have at least one KEY field marked with "
                                + StorIOSQLiteColumn.class.getSimpleName() + " annotation");
            }

            if (storIOSQLiteTypeMeta.needCreator && storIOSQLiteTypeMeta.creator == null) {
                throw new ProcessingException(annotatedType.getKey(),
                        "Class marked with "
                                + StorIOSQLiteType.class.getSimpleName()
                                + " annotation needs factory method or constructor marked with "
                                + StorIOSQLiteCreator.class.getSimpleName() + " annotation");
            }

            if (storIOSQLiteTypeMeta.needCreator && storIOSQLiteTypeMeta.creator.getParameters().size() != storIOSQLiteTypeMeta.columns.size()) {
                throw new ProcessingException(annotatedType.getKey(),
                        "Class marked with "
                                + StorIOSQLiteType.class.getSimpleName()
                                + " annotation needs factory method or constructor marked with "
                                + StorIOSQLiteCreator.class.getSimpleName() + " annotation with the same amount of parameters as the number of columns");
            }
        }
    }

    @NotNull
    @Override
    protected Class<? extends Annotation> getTypeAnnotationClass() {
        return StorIOSQLiteType.class;
    }

    @NotNull
    @Override
    protected Class<? extends Annotation> getColumnAnnotationClass() {
        return StorIOSQLiteColumn.class;
    }

    @NotNull
    @Override
    protected Class<? extends Annotation> getCreatorAnnotationClass() {
        return StorIOSQLiteCreator.class;
    }

    @NotNull
    @Override
    protected Generator<StorIOSQLiteTypeMeta> createPutResolver() {
        return new PutResolverGenerator();
    }

    @NotNull
    @Override
    protected Generator<StorIOSQLiteTypeMeta> createGetResolver() {
        return new GetResolverGenerator();
    }

    @NotNull
    @Override
    protected Generator<StorIOSQLiteTypeMeta> createDeleteResolver() {
        return new DeleteResolverGenerator();
    }

    @NotNull
    @Override
    protected Generator<StorIOSQLiteTypeMeta> createMapping() {
        return new MappingGenerator();
    }
}
