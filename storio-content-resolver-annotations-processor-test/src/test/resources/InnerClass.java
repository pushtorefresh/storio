package com.pushtorefresh.storio.contentresolver.annotations;

public class InnerClass {

    @StorIOContentResolverType(uri = "content://uri")
    public class ActualClass {

        @StorIOContentResolverColumn(name = "id", key = true)
        long id;

        @StorIOContentResolverColumn(name = "author")
        String author;

        @StorIOContentResolverColumn(name = "content")
        String content;
    }
}