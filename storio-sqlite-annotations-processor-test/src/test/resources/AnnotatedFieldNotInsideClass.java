package com.pushtorefresh.storio2.sqlite.annotations;

public interface AnnotatedFieldNotInsideClass {

    @StorIOSQLiteColumn(name = "id", key = true)
    long id();
}