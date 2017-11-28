package com.pushtorefresh.storio3.sqlite.annotations;

public interface AnnotatedFieldNotInsideClass {

    @StorIOSQLiteColumn(name = "id", key = true)
    long id();
}