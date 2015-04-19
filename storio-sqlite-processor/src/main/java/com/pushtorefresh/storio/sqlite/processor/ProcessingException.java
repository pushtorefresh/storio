package com.pushtorefresh.storio.sqlite.processor;

import javax.lang.model.element.Element;

/**
 * Useful for logging errors from AnnotationProcessor,
 * stores reference to {@link Element} that caused exception so IDE will show developer where is the problem
 */
public class ProcessingException extends RuntimeException {

    /**
     * Element that caused exception
     */
    private final Element element;

    public ProcessingException(Element element, String message) {
        super(message);
        this.element = element;
    }

    /**
     * @return non-null element that caused exception
     */
    public Element element() {
        return element;
    }
}
