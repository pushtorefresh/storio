package com.pushtorefresh.storio3.common.annotations.processor

import com.pushtorefresh.storio3.common.annotations.processor.generate.Generator
import com.pushtorefresh.storio3.common.annotations.processor.introspection.StorIOColumnMeta
import com.pushtorefresh.storio3.common.annotations.processor.introspection.StorIOCreatorMeta
import com.pushtorefresh.storio3.common.annotations.processor.introspection.StorIOTypeMeta
import com.squareup.javapoet.ClassName
import javax.annotation.processing.*
import javax.lang.model.SourceVersion
import javax.lang.model.element.Element
import javax.lang.model.element.ElementKind
import javax.lang.model.element.ElementKind.*
import javax.lang.model.element.ExecutableElement
import javax.lang.model.element.Modifier.*
import javax.lang.model.element.TypeElement
import javax.lang.model.util.Elements
import javax.lang.model.util.Types
import javax.tools.Diagnostic.Kind.ERROR

/**
 * Base annotation processor for StorIO.
 *
 * It'll process annotations to generate StorIO Object-Mapping.
 *
 * Addition: Annotation Processor should work fast and be optimized because it's part of compilation.
 * We don't want to annoy developers, who use StorIO.
 */
abstract class StorIOAnnotationsProcessor<TypeMeta : StorIOTypeMeta<*, *>, out ColumnMeta : StorIOColumnMeta<*>> : AbstractProcessor() {

    private lateinit var filer: Filer
    private lateinit var elementUtils: Elements
    private lateinit var typeUtils: Types
    protected lateinit var messager: Messager
    protected lateinit var nonNullAnnotationClassName: ClassName

    // cashing getters for private fields to avoid second pass since we already have result after the validation step
    protected val getters = mutableMapOf<Element, String>()

    /**
     * Processes class annotations.
     *
     * @param roundEnvironment environment
     *
     * @return non-null unmodifiable map(element, typeMeta)
     */
    private fun processAnnotatedClasses(roundEnvironment: RoundEnvironment, elementUtils: Elements): Map<TypeElement, TypeMeta> {
        nonNullAnnotationClassName = if (AndroidXUtils.hasAndroidX(elementUtils)) {
            ClassName.get("androidx.annotation", "NonNull")
        } else {
            ClassName.get("android.support.annotation", "NonNull")
        }
        val elementsAnnotatedWithStorIOType = roundEnvironment.getElementsAnnotatedWith(typeAnnotationClass)

        val results = mutableMapOf<TypeElement, TypeMeta>()

        elementsAnnotatedWithStorIOType.forEach {
            val classElement = validateAnnotatedClass(it)
            val typeMeta = processAnnotatedClass(classElement, elementUtils)
            results += classElement to typeMeta
        }

        return results.toMap()
    }

    /**
     * Checks that annotated element satisfies all required conditions.
     *
     * @param annotatedElement an annotated type
     *
     * @return [TypeElement] object
     */
    private fun validateAnnotatedClass(annotatedElement: Element): TypeElement {
        // We expect here that annotatedElement is Class, annotation requires that via @Target.
        val typeElement = annotatedElement as TypeElement

        if (typeElement.kind != CLASS) {
            throw ProcessingException(annotatedElement, "${typeAnnotationClass.simpleName} can be applied only to classes not to ${typeElement.simpleName}")
        }

        if (typeElement.enclosingElement.kind != PACKAGE) {
            throw ProcessingException(annotatedElement, "${typeAnnotationClass.simpleName} can't be applied to nested or inner classes: ${typeElement.simpleName}")
        }

        return typeElement
    }

    /**
     * Checks that element annotated with [StorIOColumnMeta] satisfies all required conditions.
     *
     * @param annotatedElement an annotated field or method
     */
    @Throws(SkipNotAnnotatedClassWithAnnotatedParentException::class)
    protected fun validateAnnotatedFieldOrMethod(annotatedElement: Element) {
        // We expect here that annotatedElement is Field or Method, annotation requires that via @Target.
        val enclosingElement = annotatedElement.enclosingElement

        if (enclosingElement.kind != CLASS) {
            throw ProcessingException(annotatedElement, "Please apply ${columnAnnotationClass.simpleName} only to members of class (fields or methods)" +
                    " - not to members of ${enclosingElement.simpleName}")
        }

        if (enclosingElement.getAnnotation(typeAnnotationClass) == null) {
            val superClass = typeUtils.asElement((enclosingElement as TypeElement).superclass)
            if (superClass.getAnnotation(typeAnnotationClass) != null) {
                throw SkipNotAnnotatedClassWithAnnotatedParentException("Fields of classes not annotated with ${typeAnnotationClass.simpleName}" +
                        " which have parents annotated with ${typeAnnotationClass.simpleName} will be skipped (e.g. AutoValue case)")
            } else {
                throw ProcessingException(annotatedElement, "Please annotate class ${enclosingElement.getSimpleName()} with ${typeAnnotationClass.simpleName}")
            }
        }

        if (PRIVATE in annotatedElement.modifiers) {
            if (annotatedElement.kind == FIELD) {
                if (!findGetterForPrivateField(annotatedElement)) {
                    throw ProcessingException(annotatedElement, "${columnAnnotationClass.simpleName} can not be applied to private field without corresponding getter: ${annotatedElement.simpleName}")
                }
            } else {
                throw ProcessingException(annotatedElement, "${columnAnnotationClass.simpleName} can not be applied to private method: ${annotatedElement.simpleName}")
            }
        }

        if (annotatedElement.kind == FIELD && FINAL in annotatedElement.modifiers && annotatedElement !in getters) {
            throw ProcessingException(annotatedElement, "${columnAnnotationClass.simpleName} can not be applied to final field: ${annotatedElement.simpleName}")
        }

        if (annotatedElement.kind == METHOD && (annotatedElement as ExecutableElement).parameters.isNotEmpty()) {
            throw ProcessingException(annotatedElement, "${columnAnnotationClass.simpleName} can not be applied to method with parameters: ${annotatedElement.getSimpleName()}")
        }
    }

    /**
     * Checks that element annotated with [StorIOCreatorMeta] satisfies all required conditions.
     *
     * @param annotatedElement an annotated factory method or constructor
     */
    protected fun validateAnnotatedExecutable(annotatedElement: ExecutableElement) {
        // We expect here that annotatedElement is Method or Constructor, annotation requires that via @Target.
        val enclosingElement = annotatedElement.enclosingElement

        if (enclosingElement.kind != CLASS) {
            throw ProcessingException(annotatedElement, "Please apply ${creatorAnnotationClass.simpleName} to constructor or factory method of class - not to ${enclosingElement.simpleName}")
        }

        if (enclosingElement.getAnnotation(typeAnnotationClass) == null) {
            throw ProcessingException(annotatedElement, "Please annotate class ${enclosingElement.simpleName} with ${typeAnnotationClass.simpleName}")
        }

        if (PRIVATE in annotatedElement.modifiers) {
            throw ProcessingException(annotatedElement, "${creatorAnnotationClass.simpleName} can not be applied to private methods or constructors: ${annotatedElement.simpleName}")
        }

        if (annotatedElement.kind == METHOD && STATIC !in annotatedElement.modifiers) {
            throw ProcessingException(annotatedElement, "${creatorAnnotationClass.simpleName} can not be applied to non-static methods: ${annotatedElement.simpleName}")
        }

        if (annotatedElement.kind == METHOD && annotatedElement.returnType != enclosingElement.asType()) {
            throw ProcessingException(annotatedElement, "${creatorAnnotationClass.simpleName} can not be applied to method with return type different from ${enclosingElement.simpleName}")
        }
    }

    /**
     * Checks that field is accessible via corresponding getter.
     * Cashes names of elements getters into [getters].
     *
     * @param annotatedElement an annotated field
     */
    protected fun findGetterForPrivateField(annotatedElement: Element): Boolean {
        val name = annotatedElement.simpleName.toString()
        var getter: String? = null
        annotatedElement.enclosingElement.enclosedElements.forEach { element ->
            if (element.kind == ElementKind.METHOD) {
                val method = element as ExecutableElement
                val methodName = method.simpleName.toString()
                // check if it is a valid getter
                if ((methodName == String.format("get%s", name.capitalize())
                        || methodName == String.format("is%s", name.capitalize())
                        // Special case for properties which name starts with is.
                        // Kotlin will generate getter with the same name instead of isIsProperty.
                        || methodName == name && name.startsWithIs())
                        && !method.modifiers.contains(PRIVATE)
                        && !method.modifiers.contains(STATIC)
                        && method.parameters.isEmpty()
                        && method.returnType == annotatedElement.asType()) {
                    getter = methodName
                }
            }
        }
        if (getter == null) {
            return false
        } else {
            getters += annotatedElement to getter!!
            return true
        }
    }

    @Synchronized override fun init(processingEnv: ProcessingEnvironment) {
        super.init(processingEnv)
        filer = processingEnv.filer
        elementUtils = processingEnv.elementUtils
        typeUtils = processingEnv.typeUtils
        messager = processingEnv.messager
    }

    override fun getSupportedSourceVersion(): SourceVersion = SourceVersion.latestSupported()

    /**
     * For those who don't familiar with Annotation Processing API — this is the main method of Annotation Processor lifecycle.
     *
     * It will be called after Java Compiler will find lang elements annotated with annotations from [getSupportedAnnotationTypes].
     *
     * @param annotations set of annotations
     *
     * @param roundEnv environment of current processing round
     *
     * @return true if annotation processor should not be invoked in next rounds of annotation processing, false otherwise
     */
    override fun process(annotations: Set<TypeElement>?, roundEnv: RoundEnvironment): Boolean {
        try {
            val annotatedClasses = processAnnotatedClasses(roundEnv, elementUtils)

            processAnnotatedFieldsOrMethods(roundEnv, annotatedClasses)

            processAnnotatedExecutables(roundEnv, annotatedClasses)

            validateAnnotatedClassesAndColumns(annotatedClasses)

            val putResolverGenerator = createPutResolver()
            val getResolverGenerator = createGetResolver()
            val deleteResolverGenerator = createDeleteResolver()
            val mappingGenerator = createMapping()
            val tableGenerator = createTableGenerator()

            annotatedClasses.values.forEach {
                putResolverGenerator.generateJavaFile(it).writeTo(filer)
                getResolverGenerator.generateJavaFile(it).writeTo(filer)
                deleteResolverGenerator.generateJavaFile(it).writeTo(filer)
                mappingGenerator.generateJavaFile(it).writeTo(filer)
                if (it.generateTableClass) tableGenerator?.generateJavaFile(it)?.writeTo(filer)
            }
        } catch (e: ProcessingException) {
            messager.printMessage(ERROR, e.message, e.element)
        } catch (e: Exception) {
            messager.printMessage(ERROR, "Problem occurred with StorIOProcessor: ${e.message}")
        }

        return true
    }

    /**
     * Processes annotated class.
     *
     * @param classElement type element
     *
     * @param elementUtils utils for working with elementUtils
     *
     * @return result of processing as [TypeMeta]
     */
    protected abstract fun processAnnotatedClass(classElement: TypeElement, elementUtils: Elements): TypeMeta

    /**
     * Processes fields.
     *
     * @param roundEnvironment current processing environment
     *
     * @param annotatedClasses map of annotated classes
     */
    protected abstract fun processAnnotatedFieldsOrMethods(roundEnvironment: RoundEnvironment, annotatedClasses: Map<TypeElement, TypeMeta>)

    /**
     * Processes annotated field and returns result of processing or throws exception.
     *
     * @param annotatedField field that was annotated as column
     *
     * @return non-null [StorIOColumnMeta] with meta information about field
     */
    protected abstract fun processAnnotatedFieldOrMethod(annotatedField: Element): ColumnMeta

    /**
     * Processes methods and constructors.
     *
     * @param roundEnvironment current processing environment
     *
     * @param annotatedClasses map of annotated classes
     */
    protected abstract fun processAnnotatedExecutables(roundEnvironment: RoundEnvironment, annotatedClasses: Map<TypeElement, TypeMeta>)

    protected abstract fun validateAnnotatedClassesAndColumns(annotatedClasses: Map<TypeElement, TypeMeta>)

    protected abstract val typeAnnotationClass: Class<out Annotation>

    protected abstract val columnAnnotationClass: Class<out Annotation>

    protected abstract val creatorAnnotationClass: Class<out Annotation>

    protected abstract fun createPutResolver(): Generator<TypeMeta>

    protected abstract fun createGetResolver(): Generator<TypeMeta>

    protected abstract fun createDeleteResolver(): Generator<TypeMeta>

    protected abstract fun createMapping(): Generator<TypeMeta>

    protected abstract fun createTableGenerator(): Generator<TypeMeta>?
}
