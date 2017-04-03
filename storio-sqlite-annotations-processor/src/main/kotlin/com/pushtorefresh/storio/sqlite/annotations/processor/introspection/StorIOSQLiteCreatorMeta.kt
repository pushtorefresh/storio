package com.pushtorefresh.storio.sqlite.annotations.processor.introspection

import com.pushtorefresh.storio.common.annotations.processor.introspection.StorIOCreatorMeta
import com.pushtorefresh.storio.sqlite.annotations.StorIOSQLiteCreator
import javax.lang.model.element.Element
import javax.lang.model.element.ExecutableElement

class StorIOSQLiteCreatorMeta(enclosingElement: Element,
                              element: ExecutableElement,
                              storIOSQLiteCreator: StorIOSQLiteCreator)
    : StorIOCreatorMeta<StorIOSQLiteCreator>(
        enclosingElement,
        element,
        storIOSQLiteCreator)