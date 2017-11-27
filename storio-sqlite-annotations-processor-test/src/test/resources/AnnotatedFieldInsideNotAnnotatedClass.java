package com.pushtorefresh.storio3.sqlite.annotations;

public class AnnotatedFieldInsideNotAnnotatedClass {

    @StorIOSQLiteColumn(name = "id", key = true)
    long id;
}