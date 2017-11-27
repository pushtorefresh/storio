package com.pushtorefresh.storio3.contentresolver.annotations;

public interface AnnotatedFieldNotInsideClass {

    @StorIOContentResolverColumn(name = "id", key = true)
    long id();
}