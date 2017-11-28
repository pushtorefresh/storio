package com.pushtorefresh.storio3.contentresolver.annotations;

public class AnnotatedFieldInsideNotAnnotatedClass {

    @StorIOContentResolverColumn(name = "id", key = true)
    long id;
}