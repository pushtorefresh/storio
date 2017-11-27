package com.pushtorefresh.storio3.contentresolver.annotations.processor.introspection

import com.pushtorefresh.storio3.common.annotations.processor.introspection.StorIOCreatorMeta
import com.pushtorefresh.storio3.contentresolver.annotations.StorIOContentResolverCreator

import javax.lang.model.element.Element
import javax.lang.model.element.ExecutableElement

class StorIOContentResolverCreatorMeta(enclosingElement: Element,
                                       element: ExecutableElement,
                                       storIOContentResolverCreator: StorIOContentResolverCreator)
    : StorIOCreatorMeta<StorIOContentResolverCreator>(
        enclosingElement,
        element,
        storIOContentResolverCreator)