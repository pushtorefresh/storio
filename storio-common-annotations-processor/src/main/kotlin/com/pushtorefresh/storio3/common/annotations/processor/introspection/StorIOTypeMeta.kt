package com.pushtorefresh.storio3.common.annotations.processor.introspection

import com.squareup.javapoet.ClassName
import javax.lang.model.element.ExecutableElement

abstract class StorIOTypeMeta<out TypeAnnotation : Annotation, ColumnMeta : StorIOColumnMeta<*>>
@JvmOverloads constructor(
        val simpleName: String,
        val packageName: String,
        val storIOType: TypeAnnotation,
        var needsCreator: Boolean = false,
        val nonNullAnnotationClass: ClassName
) {
    var creator: ExecutableElement? = null

    // Yep, this is MODIFIABLE Map, please use it carefully.
    val columns: MutableMap<String, ColumnMeta> = mutableMapOf()

    val orderedColumns: Collection<ColumnMeta>
        get() = when {
            needsCreator -> {
                val params = mutableListOf<String>()
                creator?.let {
                    it.parameters.mapTo(params) { it.simpleName.toString() }
                }
                val orderedColumns = mutableListOf<ColumnMeta?>().apply {
                    (0 until columns.size).forEach { add(null) }
                }
                columns.values.forEach { orderedColumns[params.indexOf(it.realElementName)] = it }
                orderedColumns.map { it as ColumnMeta }
            }
            else -> columns.values
        }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other?.javaClass != javaClass) return false

        other as StorIOTypeMeta<*, *>

        if (simpleName != other.simpleName) return false
        if (packageName != other.packageName) return false
        if (storIOType != other.storIOType) return false
        if (columns != other.columns) return false

        return true
    }

    abstract val generateTableClass: Boolean

    override fun hashCode(): Int {
        var result = simpleName.hashCode()
        result = 31 * result + packageName.hashCode()
        result = 31 * result + storIOType.hashCode()
        result = 31 * result + columns.hashCode()
        return result
    }

    override fun toString() = "StorIOTypeMeta(simpleName='$simpleName', packageName='$packageName', storIOType=$storIOType, needsCreator=$needsCreator, creator=$creator, columns=$columns)"
}