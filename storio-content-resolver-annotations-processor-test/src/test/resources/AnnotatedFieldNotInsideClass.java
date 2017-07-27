package com.pushtorefresh.storio2.contentresolver.annotations;

public interface AnnotatedFieldNotInsideClass {

    @StorIOContentResolverColumn(name = "id", key = true)
    long id();
}