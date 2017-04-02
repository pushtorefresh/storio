package com.pushtorefresh.storio.contentresolver.annotations.processor.introspection

import com.pushtorefresh.storio.common.annotations.processor.introspection.StorIOTypeMeta
import com.pushtorefresh.storio.contentresolver.annotations.StorIOContentResolverType

class StorIOContentResolverTypeMeta(
        simpleName: String,
        packageName: String,
        storIOType: StorIOContentResolverType)
    : StorIOTypeMeta<StorIOContentResolverType, StorIOContentResolverColumnMeta>(
        simpleName,
        packageName,
        storIOType)