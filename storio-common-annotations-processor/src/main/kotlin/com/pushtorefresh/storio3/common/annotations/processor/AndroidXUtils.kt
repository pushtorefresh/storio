package com.pushtorefresh.storio3.common.annotations.processor

import javax.lang.model.util.Elements

object AndroidXUtils {

    @JvmStatic
    fun hasAndroidX(elementUtils: Elements): Boolean {
        val annotationsPresent = elementUtils.getTypeElement("androidx.annotation.NonNull") != null
        val corePresent = elementUtils.getTypeElement("androidx.core.content.ContextCompat") != null
        return annotationsPresent && corePresent
    }
}
