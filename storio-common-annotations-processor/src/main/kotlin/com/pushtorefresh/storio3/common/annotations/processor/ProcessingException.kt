package com.pushtorefresh.storio3.common.annotations.processor

import javax.lang.model.element.Element

/**
 * Useful for logging errors from AnnotationProcessor, stores reference to [Element] that caused
 * exception so IDE will show developer where is the problem
 */
class ProcessingException(val element: Element, message: String) : RuntimeException(message)