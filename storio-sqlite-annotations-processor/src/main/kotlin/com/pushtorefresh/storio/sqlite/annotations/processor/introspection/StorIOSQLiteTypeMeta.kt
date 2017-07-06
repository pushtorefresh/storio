package com.pushtorefresh.storio.sqlite.annotations.processor.introspection

import com.pushtorefresh.storio.common.annotations.processor.introspection.StorIOTypeMeta
import com.pushtorefresh.storio.sqlite.annotations.StorIOSQLiteType

class StorIOSQLiteTypeMeta(
        simpleName: String,
        packageName: String,
        storIOType: StorIOSQLiteType,
        needCreator: Boolean)
    : StorIOTypeMeta<StorIOSQLiteType, StorIOSQLiteColumnMeta>(
        simpleName,
        packageName,
        storIOType,
        needCreator)