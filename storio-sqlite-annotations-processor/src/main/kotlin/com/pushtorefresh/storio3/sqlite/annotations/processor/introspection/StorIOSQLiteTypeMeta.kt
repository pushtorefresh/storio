package com.pushtorefresh.storio3.sqlite.annotations.processor.introspection

import com.pushtorefresh.storio3.common.annotations.processor.introspection.StorIOTypeMeta
import com.pushtorefresh.storio3.sqlite.annotations.StorIOSQLiteType
import com.squareup.javapoet.ClassName

class StorIOSQLiteTypeMeta(
        simpleName: String,
        packageName: String,
        storIOType: StorIOSQLiteType,
        needsCreator: Boolean,
        nonNullAnnotationClass: ClassName
) : StorIOTypeMeta<StorIOSQLiteType, StorIOSQLiteColumnMeta>(
        simpleName,
        packageName,
        storIOType,
        needsCreator,
        nonNullAnnotationClass
) {
    override val generateTableClass: Boolean
        get() = storIOType.generateTableClass
}