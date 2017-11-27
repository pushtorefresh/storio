package com.pushtorefresh.storio3.common.annotations.processor.introspection

import javax.lang.model.element.Element
import javax.lang.model.element.ExecutableElement

open class StorIOCreatorMeta<out CreatorAnnotation : Annotation>(
        val enclosingElement: Element,
        val element: ExecutableElement,
        val creatorAnnotation: CreatorAnnotation) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other?.javaClass != javaClass) return false

        other as StorIOCreatorMeta<*>

        if (enclosingElement != other.enclosingElement) return false
        if (element != other.element) return false
        if (creatorAnnotation != other.creatorAnnotation) return false

        return true
    }

    override fun hashCode(): Int {
        var result = enclosingElement.hashCode()
        result = 31 * result + element.hashCode()
        result = 31 * result + creatorAnnotation.hashCode()
        return result
    }

    override fun toString(): String = "StorIOCreatorMeta(enclosingElement=$enclosingElement, element=$element, creatorAnnotation=$creatorAnnotation)"

}