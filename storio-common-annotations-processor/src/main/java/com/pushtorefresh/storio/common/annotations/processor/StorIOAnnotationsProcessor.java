package com.pushtorefresh.storio.common.annotations.processor;

import com.pushtorefresh.storio.common.annotations.processor.generate.Generator;
import com.pushtorefresh.storio.common.annotations.processor.introspection.StorIOColumnMeta;
import com.pushtorefresh.storio.common.annotations.processor.introspection.StorIOCreatorMeta;
import com.pushtorefresh.storio.common.annotations.processor.introspection.StorIOTypeMeta;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

import static javax.lang.model.element.ElementKind.CLASS;
import static javax.lang.model.element.ElementKind.FIELD;
import static javax.lang.model.element.ElementKind.METHOD;
import static javax.lang.model.element.Modifier.FINAL;
import static javax.lang.model.element.Modifier.PRIVATE;
import static javax.lang.model.element.Modifier.STATIC;
import static javax.tools.Diagnostic.Kind.ERROR;

/**
 * Base annotation processor for StorIO.
 * <p>
 * It'll process annotations to generate StorIO Object-Mapping.
 * <p>
 * Addition: Annotation Processor should work fast and be optimized because it's part of compilation.
 * We don't want to annoy developers, who use StorIO.
 */
// Generate file with annotation processor declaration via another Annotation Processor!
public abstract class StorIOAnnotationsProcessor
        <TypeMeta extends StorIOTypeMeta, ColumnMeta extends StorIOColumnMeta>
        extends AbstractProcessor {

    private Filer filer;
    private Elements elementUtils;
    private Types typeUtils;
    protected Messager messager;

    /**
     * Processes class annotations.
     *
     * @param roundEnvironment environment
     * @return non-null unmodifiable map(element, typeMeta)
     */
    @NotNull
    private Map<TypeElement, TypeMeta> processAnnotatedClasses(@NotNull final RoundEnvironment roundEnvironment, @NotNull final Elements elementUtils) {
        final Set<? extends Element> elementsAnnotatedWithStorIOType
                = roundEnvironment.getElementsAnnotatedWith(getTypeAnnotationClass());

        final Map<TypeElement, TypeMeta> results
                = new HashMap<TypeElement, TypeMeta>(elementsAnnotatedWithStorIOType.size());

        for (final Element annotatedElement : elementsAnnotatedWithStorIOType) {
            final TypeElement classElement = validateAnnotatedClass(annotatedElement);
            final TypeMeta typeMeta = processAnnotatedClass(classElement, elementUtils);
            results.put(classElement, typeMeta);
        }

        return Collections.unmodifiableMap(results);
    }

    /**
     * Checks that annotated element satisfies all required conditions.
     *
     * @param annotatedElement an annotated type
     * @return {@link TypeElement} object
     */
    @NotNull
    private TypeElement validateAnnotatedClass(@NotNull final Element annotatedElement) {
        // We expect here that annotatedElement is Class, annotation requires that via @Target.
        final TypeElement annotatedTypeElement = (TypeElement) annotatedElement;

        if (annotatedTypeElement.getModifiers().contains(PRIVATE)) {
            throw new ProcessingException(
                    annotatedElement,
                    getTypeAnnotationClass().getSimpleName() + " can not be applied to private class: " + annotatedTypeElement.getQualifiedName()
            );
        }

        return annotatedTypeElement;
    }

    /**
     * Checks that element annotated with {@link StorIOColumnMeta} satisfies all required conditions.
     *
     * @param annotatedElement an annotated field
     * @throws SkipNotAnnotatedClassWithAnnotatedParentException
     */
    protected void validateAnnotatedFieldOrMethod(@NotNull final Element annotatedElement) throws SkipNotAnnotatedClassWithAnnotatedParentException {
        // We expect here that annotatedElement is Field or Method, annotation requires that via @Target.

        final Element enclosingElement = annotatedElement.getEnclosingElement();

        if (enclosingElement.getKind() != CLASS) {
            throw new ProcessingException(
                    annotatedElement,
                    "Please apply " + getColumnAnnotationClass().getSimpleName() + " to fields or methods of class: " + annotatedElement.getSimpleName()
            );
        }

        if (enclosingElement.getAnnotation(getTypeAnnotationClass()) == null) {
            Element superClass = typeUtils.asElement(((TypeElement) enclosingElement).getSuperclass());
            if (superClass.getAnnotation(getTypeAnnotationClass()) != null) {
                throw new SkipNotAnnotatedClassWithAnnotatedParentException("Fields of classes not annotated with" + getTypeAnnotationClass().getSimpleName() +
                "which have parents annotated with" + getTypeAnnotationClass().getSimpleName() + "will be skipped (e.g. AutoValue case)");
            } else {
                throw new ProcessingException(
                        annotatedElement,
                        "Please annotate class " + enclosingElement.getSimpleName() + " with " + getTypeAnnotationClass().getSimpleName()
                );
            }
        }

        if (annotatedElement.getModifiers().contains(PRIVATE)) {
            throw new ProcessingException(
                    annotatedElement,
                    getColumnAnnotationClass().getSimpleName() + " can not be applied to private field or method: " + annotatedElement.getSimpleName()
            );
        }

        if (annotatedElement.getKind() == FIELD && annotatedElement.getModifiers().contains(FINAL)) {
            throw new ProcessingException(
                    annotatedElement,
                    getColumnAnnotationClass().getSimpleName() + " can not be applied to final field: " + annotatedElement.getSimpleName()
            );
        }

        if (annotatedElement.getKind() == METHOD && !((ExecutableElement) annotatedElement).getParameters().isEmpty()) {
            throw new ProcessingException(
                    annotatedElement,
                    getColumnAnnotationClass().getSimpleName() + " can not be applied to method with parameters: " + annotatedElement.getSimpleName()
            );
        }
    }

    /**
     * Checks that element annotated with {@link StorIOCreatorMeta} satisfies all required conditions.
     *
     * @param annotatedElement an annotated factory method or constructor
     */
    protected void validateAnnotatedExecutable(@NotNull final ExecutableElement annotatedElement) {
        // We expect here that annotatedElement is Method or Constructor, annotation requires that via @Target.

        final Element enclosingElement = annotatedElement.getEnclosingElement();

        if (enclosingElement.getKind() != CLASS) {
            throw new ProcessingException(
                    annotatedElement,
                    "Please apply " + getCreatorAnnotationClass().getSimpleName() + " to constructor or factory method of class: " + enclosingElement.getSimpleName()
            );
        }

        if (enclosingElement.getAnnotation(getTypeAnnotationClass()) == null) {
            throw new ProcessingException(
                    annotatedElement,
                    "Please annotate class " + enclosingElement.getSimpleName() + " with " + getTypeAnnotationClass().getSimpleName()
            );
        }

        if (annotatedElement.getModifiers().contains(PRIVATE)) {
            throw new ProcessingException(
                    annotatedElement,
                    getCreatorAnnotationClass().getSimpleName() + " can not be applied to private methods or constructors: " + annotatedElement.getSimpleName()
            );
        }

        if (annotatedElement.getKind() == METHOD && !annotatedElement.getModifiers().contains(STATIC)) {
            throw new ProcessingException(
                    annotatedElement,
                    getCreatorAnnotationClass().getSimpleName() + " can not be applied to non-static methods: " + annotatedElement.getSimpleName()
            );
        }

        if (annotatedElement.getKind() == METHOD && !annotatedElement.getReturnType().equals(enclosingElement.asType())) {
            throw new ProcessingException(
                    annotatedElement,
                    getCreatorAnnotationClass().getSimpleName() + " can not be applied to method with different return type from: " + enclosingElement.getSimpleName()
            );
        }
    }

    @Override
    public synchronized void init(@NotNull final ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        filer = processingEnv.getFiler();
        elementUtils = processingEnv.getElementUtils(); // why class name is "Elements" but method "getElementUtils()", OKAY..
        typeUtils = processingEnv.getTypeUtils();
        messager = processingEnv.getMessager();
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    //endregion

    /**
     * For those who don't familiar with Annotation Processing API — this is the main method of Annotation Processor lifecycle.
     * <p>
     * It will be called after Java Compiler will find lang elements annotated with annotations from {@link #getSupportedAnnotationTypes()}.
     *
     * @param annotations set of annotations
     * @param roundEnv    environment of current processing round
     * @return true if annotation processor should not be invoked in next rounds of annotation processing, false otherwise
     */
    @Override
    public boolean process(@Nullable final Set<? extends TypeElement> annotations, @NotNull final RoundEnvironment roundEnv) {
        try {
            final Map<TypeElement, TypeMeta> annotatedClasses = processAnnotatedClasses(roundEnv, elementUtils);

            processAnnotatedFieldsOrMethods(roundEnv, annotatedClasses);

            processAnnotatedExecutables(roundEnv, annotatedClasses);

            validateAnnotatedClassesAndColumns(annotatedClasses);

            final Generator<TypeMeta> putResolverGenerator = createPutResolver();
            final Generator<TypeMeta> getResolverGenerator = createGetResolver();
            final Generator<TypeMeta> deleteResolverGenerator = createDeleteResolver();
            final Generator<TypeMeta> mappingGenerator = createMapping();

            for (TypeMeta typeMeta : annotatedClasses.values()) {
                putResolverGenerator.generateJavaFile(typeMeta).writeTo(filer);
                getResolverGenerator.generateJavaFile(typeMeta).writeTo(filer);
                deleteResolverGenerator.generateJavaFile(typeMeta).writeTo(filer);
                mappingGenerator.generateJavaFile(typeMeta).writeTo(filer);
            }
        } catch (ProcessingException e) {
            messager.printMessage(ERROR, e.getMessage(), e.element());
        } catch (Exception e) {
            messager.printMessage(ERROR, "Problem occurred with StorIOProcessor: " + e.getMessage());
        }

        return true;
    }

    /**
     * Processes annotated class.
     *
     * @param classElement type element
     * @param elementUtils utils for working with elementUtils
     * @return result of processing as {@link TypeMeta}
     */
    @NotNull
    protected abstract TypeMeta processAnnotatedClass(@NotNull TypeElement classElement, @NotNull Elements elementUtils);

    /**
     * Processes fields.
     *
     * @param roundEnvironment current processing environment
     * @param annotatedClasses map of annotated classes
     */
    protected abstract void processAnnotatedFieldsOrMethods(@NotNull final RoundEnvironment roundEnvironment, @NotNull Map<TypeElement, TypeMeta> annotatedClasses);

    /**
     * Processes annotated field and returns result of processing or throws exception.
     *
     * @param annotatedField field that was annotated as column
     * @return non-null {@link StorIOColumnMeta} with meta information about field
     */
    @NotNull
    protected abstract ColumnMeta processAnnotatedFieldOrMethod(@NotNull final Element annotatedField);

    /**
     * Processes methods and constructors.
     *
     * @param roundEnvironment current processing environment
     * @param annotatedClasses map of annotated classes
     */
    protected abstract void processAnnotatedExecutables(@NotNull final RoundEnvironment roundEnvironment, @NotNull Map<TypeElement, TypeMeta> annotatedClasses);

    protected abstract void validateAnnotatedClassesAndColumns(@NotNull Map<TypeElement, TypeMeta> annotatedClasses);

    @NotNull
    protected abstract Class<? extends Annotation> getTypeAnnotationClass();

    @NotNull
    protected abstract Class<? extends Annotation> getColumnAnnotationClass();

    @NotNull
    protected abstract Class<? extends Annotation> getCreatorAnnotationClass();

    @NotNull
    protected abstract Generator<TypeMeta> createPutResolver();

    @NotNull
    protected abstract Generator<TypeMeta> createGetResolver();

    @NotNull
    protected abstract Generator<TypeMeta> createDeleteResolver();

    @NotNull
    protected abstract Generator<TypeMeta> createMapping();
}
