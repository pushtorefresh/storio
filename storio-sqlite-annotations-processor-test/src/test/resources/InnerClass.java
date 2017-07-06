package com.pushtorefresh.storio.sqlite.annotations;

public class InnerClass {

    @StorIOSQLiteType(table = "table")
    public class ActualClass {

        @StorIOSQLiteColumn(name = "id", key = true)
        long id;

        @StorIOSQLiteColumn(name = "author")
        String author;

        @StorIOSQLiteColumn(name = "content")
        String content;
    }
}