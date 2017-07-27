package com.pushtorefresh.storio2.contentresolver.annotations;

public class AnnotatedFieldInsideNotAnnotatedClass {

    @StorIOContentResolverColumn(name = "id", key = true)
    long id;
}