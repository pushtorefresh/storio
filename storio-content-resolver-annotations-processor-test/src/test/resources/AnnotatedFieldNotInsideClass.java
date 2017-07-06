package com.pushtorefresh.storio.contentresolver.annotations;

public interface AnnotatedFieldNotInsideClass {

    @StorIOContentResolverColumn(name = "id", key = true)
    long id();
}