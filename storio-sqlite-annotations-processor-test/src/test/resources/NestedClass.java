package com.pushtorefresh.storio.sqlite.annotations;

public class NestedClass {

    @StorIOSQLiteType(table = "table")
    public static class ActualClass {

        @StorIOSQLiteColumn(name = "id", key = true)
        long id;

        @StorIOSQLiteColumn(name = "author")
        String author;

        @StorIOSQLiteColumn(name = "content")
        String content;
    }
}