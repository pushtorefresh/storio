package com.pushtorefresh.storio2.sqlite.annotations.processor.introspection

import com.pushtorefresh.storio2.common.annotations.processor.introspection.StorIOTypeMeta
import com.pushtorefresh.storio2.sqlite.annotations.StorIOSQLiteType

class StorIOSQLiteTypeMeta(
        simpleName: String,
        packageName: String,
        storIOType: StorIOSQLiteType,
        needsCreator: Boolean)
    : StorIOTypeMeta<StorIOSQLiteType, StorIOSQLiteColumnMeta>(
        simpleName,
        packageName,
        storIOType,
        needsCreator)