package com.pushtorefresh.storio.contentresolver.annotations;

public class NestedClass {

    @StorIOContentResolverType(uri = "content://uri")
    public static class ActualClass {

        @StorIOContentResolverColumn(name = "id", key = true)
        long id;

        @StorIOContentResolverColumn(name = "author")
        String author;

        @StorIOContentResolverColumn(name = "content")
        String content;
    }
}