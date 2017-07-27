package com.pushtorefresh.storio2.sqlite.annotations;

public class AnnotatedFieldInsideNotAnnotatedClass {

    @StorIOSQLiteColumn(name = "id", key = true)
    long id;
}