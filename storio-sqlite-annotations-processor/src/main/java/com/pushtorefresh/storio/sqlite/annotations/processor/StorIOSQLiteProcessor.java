package com.pushtorefresh.storio.sqlite.annotations.processor;

import com.google.auto.service.AutoService;
import com.pushtorefresh.storio.common.annotations.processor.ProcessingException;
import com.pushtorefresh.storio.common.annotations.processor.StorIOAnnotationsProcessor;
import com.pushtorefresh.storio.common.annotations.processor.generate.Generator;
import com.pushtorefresh.storio.common.annotations.processor.introspection.JavaType;
import com.pushtorefresh.storio.sqlite.annotations.StorIOSQLiteColumn;
import com.pushtorefresh.storio.sqlite.annotations.StorIOSQLiteType;
import com.pushtorefresh.storio.sqlite.annotations.processor.generate.DeleteResolverGenerator;
import com.pushtorefresh.storio.sqlite.annotations.processor.generate.GetResolverGenerator;
import com.pushtorefresh.storio.sqlite.annotations.processor.generate.MappingGenerator;
import com.pushtorefresh.storio.sqlite.annotations.processor.generate.PutResolverGenerator;
import com.pushtorefresh.storio.sqlite.annotations.processor.introspection.StorIOSQLiteColumnMeta;
import com.pushtorefresh.storio.sqlite.annotations.processor.introspection.StorIOSQLiteTypeMeta;

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

import static javax.lang.model.util.ElementFilter.methodsIn;

/**
 * Annotation processor for StorIOSQLite
 * <p>
 * It'll process annotations to generate StorIOSQLite Object-Mapping
 * <p>
 * Addition: Annotation Processor should work fast and be optimized because it's part of compilation
 * We don't want to annoy developers, who use StorIO
 */
// Generate file with annotation processor declaration via another Annotation Processor!
@AutoService(Processor.class)
public class StorIOSQLiteProcessor extends StorIOAnnotationsProcessor<StorIOSQLiteTypeMeta, StorIOSQLiteColumnMeta> {

    @NotNull
    @Override
    public Set<String> getSupportedAnnotationTypes() {
        final Set<String> supportedAnnotations = new HashSet<String>(2);

        supportedAnnotations.add(StorIOSQLiteType.class.getCanonicalName());
        supportedAnnotations.add(StorIOSQLiteColumn.class.getCanonicalName());

        return supportedAnnotations;
    }

    /**
     * Processes annotated class
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

        return new StorIOSQLiteTypeMeta(simpleName, packageName, storIOSQLiteType);
    }

    /**
     * Processes fields annotated with {@link StorIOSQLiteColumn}
     *
     * @param roundEnvironment current processing environment
     * @param annotatedClasses map of classes annotated with {@link StorIOSQLiteType}
     */
    @Override
    protected void processAnnotatedFields(@NotNull final RoundEnvironment roundEnvironment, @NotNull Map<TypeElement, StorIOSQLiteTypeMeta> annotatedClasses) {
        final Set<? extends Element> elementsAnnotatedWithStorIOSQLiteColumn
                = roundEnvironment.getElementsAnnotatedWith(StorIOSQLiteColumn.class);

        for (final Element annotatedFieldElement : elementsAnnotatedWithStorIOSQLiteColumn) {
            final StorIOSQLiteColumnMeta storIOSQLiteColumnMeta = processAnnotatedField(annotatedFieldElement);
            final StorIOSQLiteTypeMeta storIOSQLiteTypeMeta = annotatedClasses.get(storIOSQLiteColumnMeta.enclosingElement);
            validateAnnotatedField(annotatedFieldElement, storIOSQLiteTypeMeta.storIOType.hasConstructor());

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
            if (storIOSQLiteTypeMeta.columns.put(storIOSQLiteColumnMeta.storIOColumn.name(), storIOSQLiteColumnMeta) != null) {
                throw new ProcessingException(annotatedFieldElement, "Column name already used in this class");
            }
        }
    }

    /**
     * Processes annotated field and returns result of processing or throws exception
     *
     * @param annotatedField field that was annotated with {@link StorIOSQLiteColumn}
     * @return non-null {@link StorIOSQLiteColumnMeta} with meta information about field
     */
    @NotNull
    @Override
    protected StorIOSQLiteColumnMeta processAnnotatedField(@NotNull final Element annotatedField) {
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

        if (storIOSQLiteColumn.ignoreNull() && annotatedField.asType().getKind().isPrimitive()) {
            throw new ProcessingException(
                    annotatedField,
                    "ignoreNull should not be used for primitive type: " + annotatedField.asType());
        }

        final String columnName = storIOSQLiteColumn.name();

        if (columnName == null || columnName.length() == 0) {
            throw new ProcessingException(annotatedField, "Column name is null or empty");
        }
        String fieldName = annotatedField.getSimpleName().toString();
        String getterName = findAccessorName(annotatedField, "get");

        return new StorIOSQLiteColumnMeta(
                annotatedField.getEnclosingElement(),
                annotatedField,
                fieldName,
                getterName,
                javaType,
                storIOSQLiteColumn
        );
    }

    private String findAccessorName(Element annotatedField, String prefix) {
        Element enclosingElement = annotatedField.getEnclosingElement();
        String s = annotatedField.getSimpleName().toString();
        String compare = prefix + s.substring(0, 1).toUpperCase() + s.substring(1) + "()";
        for (Element e : methodsIn(enclosingElement.getEnclosedElements())) {
            if ((e.getSimpleName().toString() + "()").equals(compare)) {
                return compare;
            }
        }
        return null;
    }

    @Override
    protected void validateAnnotatedClassesAndColumns(@NotNull Map<TypeElement, StorIOSQLiteTypeMeta> annotatedClasses) {
        // check that each annotated class has columns with at least one key column
        for (final Map.Entry<TypeElement, StorIOSQLiteTypeMeta> annotatedClass : annotatedClasses.entrySet()) {
            if (annotatedClass.getValue().columns.size() == 0) {
                throw new ProcessingException(annotatedClass.getKey(),
                        "Class marked with "
                                + StorIOSQLiteType.class.getSimpleName()
                                + " annotation should have at least one field marked with "
                                + StorIOSQLiteColumn.class.getSimpleName()
                                + " annotation");
            }

            boolean hasAtLeastOneKeyColumn = false;

            for (final StorIOSQLiteColumnMeta columnMeta : annotatedClass.getValue().columns.values()) {
                if (columnMeta.storIOColumn.key()) {
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
