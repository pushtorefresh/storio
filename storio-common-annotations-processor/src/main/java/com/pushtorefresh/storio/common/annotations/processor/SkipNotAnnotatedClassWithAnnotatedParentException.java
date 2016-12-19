package com.pushtorefresh.storio.common.annotations.processor;

public class SkipNotAnnotatedClassWithAnnotatedParentException extends Exception {

    public SkipNotAnnotatedClassWithAnnotatedParentException(String message) {
        super(message);
    }
}
