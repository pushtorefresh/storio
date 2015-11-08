package com.pushtorefresh.storio.sqlite.annotations.processor;

import org.jetbrains.annotations.NotNull;

import javax.lang.model.element.Element;

/**
 * Useful for logging errors from AnnotationProcessor,
 * stores reference to {@link Element} that caused exception so IDE will show developer where is the problem
 */
public class ProcessingException extends RuntimeException {

    /**
     * Element that caused exception
     */
    @NotNull
    private final Element element;

    public ProcessingException(@NotNull Element element, @NotNull String message) {
        super(message);
        this.element = element;
    }

    /**
     * @return not-null element that caused exception
     */
    @NotNull
    public Element element() {
        return element;
    }
}
