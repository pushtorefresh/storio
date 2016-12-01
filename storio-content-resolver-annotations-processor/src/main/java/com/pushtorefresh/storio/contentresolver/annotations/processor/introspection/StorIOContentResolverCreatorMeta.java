package com.pushtorefresh.storio.contentresolver.annotations.processor.introspection;

import com.pushtorefresh.storio.common.annotations.processor.introspection.StorIOCreatorMeta;
import com.pushtorefresh.storio.contentresolver.annotations.StorIOContentResolverCreator;

import org.jetbrains.annotations.NotNull;

import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;

public class StorIOContentResolverCreatorMeta extends StorIOCreatorMeta<StorIOContentResolverCreator> {

    public StorIOContentResolverCreatorMeta(@NotNull Element enclosingElement,
                                            @NotNull ExecutableElement element,
                                            @NotNull StorIOContentResolverCreator storIOContentResolverCreator) {
        super(enclosingElement, element, storIOContentResolverCreator);
    }
}
