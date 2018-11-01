package com.pushtorefresh.storio3.contentresolver.annotations.processor.introspection

import com.pushtorefresh.storio3.common.annotations.processor.introspection.StorIOTypeMeta
import com.pushtorefresh.storio3.contentresolver.annotations.StorIOContentResolverType
import com.squareup.javapoet.ClassName

class StorIOContentResolverTypeMeta(
        simpleName: String,
        packageName: String,
        storIOType: StorIOContentResolverType,
        needsCreator: Boolean,
        nonNullAnnotationClass: ClassName
) : StorIOTypeMeta<StorIOContentResolverType, StorIOContentResolverColumnMeta>(
        simpleName,
        packageName,
        storIOType,
        needsCreator,
        nonNullAnnotationClass
) {
    override val generateTableClass: Boolean
        get() = false
}