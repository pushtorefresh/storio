package com.pushtorefresh.storio.common.annotations.processor.introspection;

import org.jetbrains.annotations.NotNull;

import java.lang.annotation.Annotation;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;

public class StorIOColumnMeta <ColumnAnnotation extends Annotation> {

    @NotNull
    public final Element enclosingElement;

    @NotNull
    public final Element element;

    @NotNull
    public final String elementName;

    @NotNull
    public final JavaType javaType;

    @NotNull
    public final ColumnAnnotation storIOColumn;

    public StorIOColumnMeta(
            @NotNull Element enclosingElement,
            @NotNull Element element,
            @NotNull String elementName,
            @NotNull JavaType javaType,
            @NotNull ColumnAnnotation storIOColumn) {
        this.enclosingElement = enclosingElement;
        this.element = element;
        this.elementName = elementName;
        this.javaType = javaType;
        this.storIOColumn = storIOColumn;
    }

    public boolean isMethod() {
        return element.getKind() == ElementKind.METHOD;
    }

    @NotNull
    public String getRealElementName() {
        if (elementName.startsWith("get") && Character.isUpperCase(elementName.charAt(3))) {
            return decapitalize(elementName.substring(3));
        } else if (elementName.startsWith("is") && Character.isUpperCase(elementName.charAt(2))) {
            return decapitalize(elementName.substring(2));
        } else {
            return elementName;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        StorIOColumnMeta<?> that = (StorIOColumnMeta<?>) o;

        if (!enclosingElement.equals(that.enclosingElement)) return false;
        if (!element.equals(that.element)) return false;
        if (!elementName.equals(that.elementName)) return false;
        if (javaType != that.javaType) return false;
        return storIOColumn.equals(that.storIOColumn);

    }

    @Override
    public int hashCode() {
        int result = enclosingElement.hashCode();
        result = 31 * result + element.hashCode();
        result = 31 * result + elementName.hashCode();
        result = 31 * result + javaType.hashCode();
        result = 31 * result + storIOColumn.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "StorIOColumnMeta{" +
                "enclosingElement=" + enclosingElement +
                ", element=" + element +
                ", elementName='" + elementName + '\'' +
                ", javaType=" + javaType +
                ", storIOColumn=" + storIOColumn +
                '}';
    }

    @NotNull
    private static String decapitalize(@NotNull String str) {
        if (str.length() > 1) {
            return Character.toLowerCase(str.charAt(0)) + str.substring(1);
        } else {
            return str.toLowerCase();
        }
    }
}
