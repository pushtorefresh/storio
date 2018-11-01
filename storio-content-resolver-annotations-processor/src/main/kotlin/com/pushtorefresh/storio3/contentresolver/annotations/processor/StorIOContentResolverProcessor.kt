package com.pushtorefresh.storio3.contentresolver.annotations.processor

import com.pushtorefresh.storio3.common.annotations.processor.ProcessingException
import com.pushtorefresh.storio3.common.annotations.processor.SkipNotAnnotatedClassWithAnnotatedParentException
import com.pushtorefresh.storio3.common.annotations.processor.StorIOAnnotationsProcessor
import com.pushtorefresh.storio3.common.annotations.processor.generate.Generator
import com.pushtorefresh.storio3.common.annotations.processor.introspection.JavaType
import com.pushtorefresh.storio3.contentresolver.annotations.StorIOContentResolverColumn
import com.pushtorefresh.storio3.contentresolver.annotations.StorIOContentResolverCreator
import com.pushtorefresh.storio3.contentresolver.annotations.StorIOContentResolverType
import com.pushtorefresh.storio3.contentresolver.annotations.processor.generate.DeleteResolverGenerator
import com.pushtorefresh.storio3.contentresolver.annotations.processor.generate.GetResolverGenerator
import com.pushtorefresh.storio3.contentresolver.annotations.processor.generate.MappingGenerator
import com.pushtorefresh.storio3.contentresolver.annotations.processor.generate.PutResolverGenerator
import com.pushtorefresh.storio3.contentresolver.annotations.processor.introspection.StorIOContentResolverColumnMeta
import com.pushtorefresh.storio3.contentresolver.annotations.processor.introspection.StorIOContentResolverCreatorMeta
import com.pushtorefresh.storio3.contentresolver.annotations.processor.introspection.StorIOContentResolverTypeMeta
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.element.Element
import javax.lang.model.element.ElementKind
import javax.lang.model.element.ExecutableElement
import javax.lang.model.element.Modifier.ABSTRACT
import javax.lang.model.element.TypeElement
import javax.lang.model.util.Elements
import javax.tools.Diagnostic.Kind.WARNING

/**
 * Annotation processor for StorIOContentResolver
 *
 * It'll process annotations to generate StorIOContentResolver Object-Mapping
 *
 * Addition: Annotation Processor should work fast and be optimized because it's part of compilation
 * We don't want to annoy developers, who use StorIO
 */
open class StorIOContentResolverProcessor : StorIOAnnotationsProcessor<StorIOContentResolverTypeMeta, StorIOContentResolverColumnMeta>() {

    override fun getSupportedAnnotationTypes() = setOf(
            StorIOContentResolverType::class.java.canonicalName,
            StorIOContentResolverColumn::class.java.canonicalName,
            StorIOContentResolverCreator::class.java.canonicalName
    )

    /**
     * Processes annotated class
     *
     * @param classElement type element annotated with [StorIOContentResolverType]
     *
     * @param elementUtils utils for working with elementUtils
     *
     * @return result of processing as [StorIOContentResolverTypeMeta]
     */
    override fun processAnnotatedClass(classElement: TypeElement, elementUtils: Elements): StorIOContentResolverTypeMeta {
        val storIOContentResolverType = classElement.getAnnotation(StorIOContentResolverType::class.java)

        val commonUri = storIOContentResolverType.uri

        val urisForOperations = mapOf(
                "insert" to storIOContentResolverType.insertUri,
                "update" to storIOContentResolverType.updateUri,
                "delete" to storIOContentResolverType.deleteUri
        )

        validateUris(classElement, commonUri, urisForOperations)

        val simpleName = classElement.simpleName.toString()
        val packageName = elementUtils.getPackageOf(classElement).qualifiedName.toString()

        return StorIOContentResolverTypeMeta(simpleName, packageName, storIOContentResolverType, ABSTRACT in classElement.modifiers, nonNullAnnotationClassName)
    }

    /**
     * Verifies that uris are valid.
     *
     * @param classElement type element
     *
     * @param commonUri nullable default uri for all operations
     *
     * @param operationUriMap non-null map where key - operation name, value - specific uri for this
     * * operation
     */
    private fun validateUris(classElement: TypeElement, commonUri: String, operationUriMap: Map<String, String>) {

        if (!validateUri(commonUri)) {
            val operationsWithInvalidUris = mutableListOf<String>()
            operationUriMap.forEach { (key, value) ->
                if (!validateUri(value)) operationsWithInvalidUris.add(key)
            }
            if (operationsWithInvalidUris.isNotEmpty()) {
                var message = "Uri of ${classElement.simpleName} annotated with ${typeAnnotationClass.simpleName} is empty"

                if (operationsWithInvalidUris.size < operationUriMap.size) {
                    message += " for operation " + operationsWithInvalidUris[0]
                }
                // Else (there is no any uris) - do not specify operation, because commonUri is default and straightforward way.
                throw ProcessingException(classElement, message)
            }
            // It will be okay if uris for all operations were specified separately.
        }
    }

    private fun validateUri(uri: String) = uri.isNotEmpty()

    /**
     * Processes fields annotated with [StorIOContentResolverColumn]
     *
     * @param roundEnvironment current processing environment
     *
     * @param annotatedClasses map of classes annotated with [StorIOContentResolverType]
     */
    override fun processAnnotatedFieldsOrMethods(roundEnvironment: RoundEnvironment, annotatedClasses: Map<TypeElement, StorIOContentResolverTypeMeta>) {
        val elementsAnnotatedWithStorIOContentResolverColumn = roundEnvironment.getElementsAnnotatedWith(StorIOContentResolverColumn::class.java)

        elementsAnnotatedWithStorIOContentResolverColumn.forEach { element ->
            try {
                validateAnnotatedFieldOrMethod(element)

                val columnMeta = processAnnotatedFieldOrMethod(element)

                annotatedClasses[columnMeta.enclosingElement]?.let { typeMeta ->
                    // If class already contains column with same name - throw an exception.
                    if (columnMeta.storIOColumn.name in typeMeta.columns.keys) {
                        throw ProcessingException(element, "Column name already used in this class: ${columnMeta.storIOColumn.name}")
                    }

                    // If field annotation applied to both fields and methods in a same class.
                    if (typeMeta.needsCreator && !columnMeta.needsCreator || !typeMeta.needsCreator && columnMeta.needsCreator && typeMeta.columns.isNotEmpty()) {
                        throw ProcessingException(element, "Can't apply ${StorIOContentResolverColumn::class.java.simpleName} annotation to both" +
                                " fields and methods in a same class: ${typeMeta.simpleName}"
                        )
                    }

                    // If column needs creator then enclosing class needs it as well.
                    if (!typeMeta.needsCreator && columnMeta.needsCreator) typeMeta.needsCreator = true

                    // Put meta column info.
                    typeMeta.columns += columnMeta.storIOColumn.name to columnMeta
                }
            } catch (e: SkipNotAnnotatedClassWithAnnotatedParentException) {
                messager.printMessage(WARNING, e.message)
            }
        }
    }

    /**
     * Processes annotated field and returns result of processing or throws exception
     *
     * @param annotatedField field that was annotated with [StorIOContentResolverColumn]
     *
     * @return non-null [StorIOContentResolverColumnMeta] with meta information about field
     */
    override fun processAnnotatedFieldOrMethod(annotatedField: Element): StorIOContentResolverColumnMeta {
        val javaType: JavaType

        try {
            javaType = JavaType.from(
                    if (annotatedField.kind == ElementKind.FIELD)
                        annotatedField.asType()
                    else
                        (annotatedField as ExecutableElement).returnType)
        } catch (e: Exception) {
            throw ProcessingException(annotatedField, "Unsupported type of field or method for ${StorIOContentResolverColumn::class.java.simpleName} annotation," +
                    " if you need to serialize/deserialize field of that type -> please write your own resolver: ${e.message}"
            )
        }

        val column = annotatedField.getAnnotation(StorIOContentResolverColumn::class.java)

        if (column.ignoreNull && annotatedField.asType().kind.isPrimitive) {
            throw ProcessingException(annotatedField, "ignoreNull should not be used for primitive type: ${annotatedField.simpleName}")
        }

        if (column.name.isEmpty()) {
            throw ProcessingException(annotatedField, "Column name is empty: ${annotatedField.simpleName}")
        }

        val getter = getters[annotatedField]

        return StorIOContentResolverColumnMeta(annotatedField.enclosingElement, annotatedField, annotatedField.simpleName.toString(), javaType, column, getter)
    }

    /**
     * Processes factory methods or constructors annotated with [StorIOContentResolverCreator].
     *
     * @param roundEnvironment current processing environment
     *
     * @param annotatedClasses map of classes annotated with [StorIOContentResolverType]
     */
    override fun processAnnotatedExecutables(roundEnvironment: RoundEnvironment, annotatedClasses: Map<TypeElement, StorIOContentResolverTypeMeta>) {
        val elementsAnnotatedWithStorIOContentResolverCreator = roundEnvironment.getElementsAnnotatedWith(StorIOContentResolverCreator::class.java)

        elementsAnnotatedWithStorIOContentResolverCreator.forEach { element ->
            val executableElement = element as ExecutableElement
            validateAnnotatedExecutable(executableElement)
            val creatorMeta = StorIOContentResolverCreatorMeta(executableElement.enclosingElement, executableElement,
                    executableElement.getAnnotation(StorIOContentResolverCreator::class.java))

            annotatedClasses[creatorMeta.enclosingElement]?.let { typeMeta ->
                // Put meta creator info.
                // If class already contains another creator -> throw exception.
                if (typeMeta.creator == null) {
                    typeMeta.creator = executableElement
                } else {
                    throw ProcessingException(executableElement, "Only one creator method or constructor is allowed: ${executableElement.enclosingElement.simpleName}")
                }
            }
        }
    }

    override fun validateAnnotatedClassesAndColumns(annotatedClasses: Map<TypeElement, StorIOContentResolverTypeMeta>) {
        // check that each annotated class has columns with at least one key column
        annotatedClasses.forEach { (key, typeMeta) ->
            if (typeMeta.columns.isEmpty()) {
                throw ProcessingException(key, "Class marked with ${StorIOContentResolverType::class.java.simpleName} annotation should have at least one field or method" +
                        " marked with ${StorIOContentResolverColumn::class.java.simpleName} annotation: ${typeMeta.simpleName}")
            }

            val hasAtLeastOneKeyColumn = typeMeta.columns.values.any { it.storIOColumn.key }

            if (!hasAtLeastOneKeyColumn) {
                throw ProcessingException(key, "Class marked with ${StorIOContentResolverType::class.java.simpleName} annotation should have at least one KEY field or method" +
                        " marked with ${StorIOContentResolverColumn::class.java.simpleName} annotation: ${typeMeta.simpleName}")
            }

            if (typeMeta.needsCreator && typeMeta.creator == null) {
                throw ProcessingException(key, "Class marked with ${StorIOContentResolverType::class.java.simpleName} annotation needs factory method or constructor" +
                        " marked with ${StorIOContentResolverCreator::class.java.simpleName} annotation: ${typeMeta.simpleName}")
            }

            if (typeMeta.needsCreator) {
                if (typeMeta.creator == null) {
                    throw ProcessingException(key, "Class marked with ${StorIOContentResolverType::class.java.simpleName} annotation needs factory method or constructor marked with" +
                            " ${StorIOContentResolverCreator::class.java.simpleName} annotation: ${typeMeta.simpleName}")
                }

                val params = typeMeta.creator!!.parameters.map { it.simpleName.toString() }
                val resolvesParams = typeMeta.columns.values.all { it.realElementName in params }

                if (params.size != typeMeta.columns.size || !resolvesParams) {
                    throw ProcessingException(key, "Class marked with ${StorIOContentResolverType::class.java.simpleName} annotation needs factory method or constructor marked with" +
                            " ${StorIOContentResolverCreator::class.java.simpleName} annotation with parameters matching ${typeMeta.simpleName} columns")
                }
            }
        }
    }

    override val typeAnnotationClass: Class<out Annotation>
        get() = StorIOContentResolverType::class.java

    override val columnAnnotationClass: Class<out Annotation>
        get() = StorIOContentResolverColumn::class.java

    override val creatorAnnotationClass: Class<out Annotation>
        get() = StorIOContentResolverCreator::class.java

    override fun createPutResolver(): Generator<StorIOContentResolverTypeMeta> = PutResolverGenerator

    override fun createGetResolver(): Generator<StorIOContentResolverTypeMeta> = GetResolverGenerator

    override fun createDeleteResolver(): Generator<StorIOContentResolverTypeMeta> = DeleteResolverGenerator

    override fun createMapping(): Generator<StorIOContentResolverTypeMeta> = MappingGenerator

    override fun createTableGenerator(): Generator<StorIOContentResolverTypeMeta>? = null
}