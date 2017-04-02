package com.pushtorefresh.storio.contentresolver.annotations.processor.introspection

import com.pushtorefresh.storio.common.annotations.processor.introspection.StorIOCreatorMeta
import com.pushtorefresh.storio.contentresolver.annotations.StorIOContentResolverCreator

import javax.lang.model.element.Element
import javax.lang.model.element.ExecutableElement

class StorIOContentResolverCreatorMeta(enclosingElement: Element,
                                       element: ExecutableElement,
                                       storIOContentResolverCreator: StorIOContentResolverCreator)
    : StorIOCreatorMeta<StorIOContentResolverCreator>(
        enclosingElement,
        element,
        storIOContentResolverCreator)