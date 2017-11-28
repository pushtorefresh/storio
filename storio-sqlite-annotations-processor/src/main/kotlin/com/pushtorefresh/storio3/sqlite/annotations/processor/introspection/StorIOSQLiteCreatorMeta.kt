package com.pushtorefresh.storio3.sqlite.annotations.processor.introspection

import com.pushtorefresh.storio3.common.annotations.processor.introspection.StorIOCreatorMeta
import com.pushtorefresh.storio3.sqlite.annotations.StorIOSQLiteCreator
import javax.lang.model.element.Element
import javax.lang.model.element.ExecutableElement

class StorIOSQLiteCreatorMeta(enclosingElement: Element,
                              element: ExecutableElement,
                              storIOSQLiteCreator: StorIOSQLiteCreator)
    : StorIOCreatorMeta<StorIOSQLiteCreator>(
        enclosingElement,
        element,
        storIOSQLiteCreator)