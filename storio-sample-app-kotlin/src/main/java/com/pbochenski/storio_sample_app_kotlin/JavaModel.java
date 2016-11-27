package com.pbochenski.storio_sample_app_kotlin;

import com.pushtorefresh.storio.sqlite.annotations.StorIOSQLiteColumn;
import com.pushtorefresh.storio.sqlite.annotations.StorIOSQLiteType;

/**
 * Created by Pawel Bochenski on 27.11.2016.
 */

@StorIOSQLiteType(table = "javaModel")
public class JavaModel {

    @StorIOSQLiteColumn(name = "id", key = true)
    int id;

    // annotated fields should have package-level visibility
    @StorIOSQLiteColumn(name = "author")
    String author;

    @StorIOSQLiteColumn(name = "content")
    String content;

    JavaModel() {
    }
}
