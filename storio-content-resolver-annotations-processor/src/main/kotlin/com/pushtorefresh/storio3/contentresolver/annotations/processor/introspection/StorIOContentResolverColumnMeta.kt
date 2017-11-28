package com.pushtorefresh.storio3.contentresolver.annotations.processor.introspection

import com.pushtorefresh.storio3.common.annotations.processor.introspection.JavaType
import com.pushtorefresh.storio3.common.annotations.processor.introspection.StorIOColumnMeta
import com.pushtorefresh.storio3.contentresolver.annotations.StorIOContentResolverColumn

import javax.lang.model.element.Element

class StorIOContentResolverColumnMeta(
        enclosingElement: Element,
        element: Element,
        fieldName: String,
        javaType: JavaType,
        storIOColumn: StorIOContentResolverColumn,
        getter: String? = null)
    : StorIOColumnMeta<StorIOContentResolverColumn>(
        enclosingElement,
        element,
        fieldName,
        javaType,
        storIOColumn,
        getter)