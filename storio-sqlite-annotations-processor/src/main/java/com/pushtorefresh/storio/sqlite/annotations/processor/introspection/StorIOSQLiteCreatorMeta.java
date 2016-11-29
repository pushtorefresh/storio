package com.pushtorefresh.storio.sqlite.annotations.processor.introspection;

import com.pushtorefresh.storio.common.annotations.processor.introspection.StorIOCreatorMeta;
import com.pushtorefresh.storio.sqlite.annotations.StorIOSQLiteCreator;

import org.jetbrains.annotations.NotNull;

import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;

public class StorIOSQLiteCreatorMeta extends StorIOCreatorMeta<StorIOSQLiteCreator> {

    public StorIOSQLiteCreatorMeta(@NotNull Element enclosingElement,
                                   @NotNull ExecutableElement element,
                                   @NotNull StorIOSQLiteCreator storIOSQLiteCreator) {
        super(enclosingElement, element, storIOSQLiteCreator);
    }
}
