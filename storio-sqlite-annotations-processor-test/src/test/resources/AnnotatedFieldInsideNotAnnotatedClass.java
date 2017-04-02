package com.pushtorefresh.storio.sqlite.annotations;

public class AnnotatedFieldInsideNotAnnotatedClass {

    @StorIOSQLiteColumn(name = "id", key = true)
    long id;
}