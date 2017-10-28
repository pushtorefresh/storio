package com.pushtorefresh.storio2.contentresolver.annotations.processor.introspection

import com.pushtorefresh.storio2.common.annotations.processor.introspection.StorIOTypeMeta
import com.pushtorefresh.storio2.contentresolver.annotations.StorIOContentResolverType

class StorIOContentResolverTypeMeta(
        simpleName: String,
        packageName: String,
        storIOType: StorIOContentResolverType,
        needsCreator: Boolean)
    : StorIOTypeMeta<StorIOContentResolverType, StorIOContentResolverColumnMeta>(
        simpleName,
        packageName,
        storIOType,
        needsCreator) {

    override val generateTableClass: Boolean
        get() = false
}