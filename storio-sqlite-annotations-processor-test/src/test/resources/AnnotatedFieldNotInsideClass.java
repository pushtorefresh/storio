package com.pushtorefresh.storio.sqlite.annotations;

public interface AnnotatedFieldNotInsideClass {

    @StorIOSQLiteColumn(name = "id", key = true)
    long id();
}