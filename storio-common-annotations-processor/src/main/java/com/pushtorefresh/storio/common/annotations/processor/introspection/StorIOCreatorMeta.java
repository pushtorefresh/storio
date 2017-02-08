package com.pushtorefresh.storio.common.annotations.processor.introspection;

import org.jetbrains.annotations.NotNull;

import java.lang.annotation.Annotation;

import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;

public class StorIOCreatorMeta<CreatorAnnotation extends Annotation> {

    @NotNull
    public final Element enclosingElement;

    @NotNull
    public final ExecutableElement element;

    @NotNull
    public final CreatorAnnotation creatorAnnotation;

    public StorIOCreatorMeta(@NotNull Element enclosingElement,
                             @NotNull ExecutableElement element,
                             @NotNull CreatorAnnotation creatorAnnotation) {
        this.enclosingElement = enclosingElement;
        this.element = element;
        this.creatorAnnotation = creatorAnnotation;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        StorIOCreatorMeta<?> that = (StorIOCreatorMeta<?>) o;

        if (!enclosingElement.equals(that.enclosingElement)) return false;
        if (!element.equals(that.element)) return false;
        return creatorAnnotation.equals(that.creatorAnnotation);

    }

    @Override
    public int hashCode() {
        int result = enclosingElement.hashCode();
        result = 31 * result + element.hashCode();
        result = 31 * result + creatorAnnotation.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "StorIOCreatorMeta{" +
                "enclosingElement=" + enclosingElement +
                ", element=" + element +
                ", creatorAnnotation=" + creatorAnnotation +
                '}';
    }
}
