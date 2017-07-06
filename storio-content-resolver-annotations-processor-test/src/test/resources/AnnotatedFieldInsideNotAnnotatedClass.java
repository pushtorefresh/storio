package com.pushtorefresh.storio.contentresolver.annotations;

public class AnnotatedFieldInsideNotAnnotatedClass {

    @StorIOContentResolverColumn(name = "id", key = true)
    long id;
}