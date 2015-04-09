package com.pushtorefresh.storio.sqlite.impl;

import android.support.annotation.NonNull;

import com.pushtorefresh.storio.sqlite.Changes;

import java.util.Set;

import rx.Observable;
import rx.functions.Func1;
import rx.subjects.PublishSubject;

/**
 * Hiding RxJava from ClassLoader via separate class
 */
class ChangesFilter implements Func1<Changes, Boolean> {

    @NonNull
    private final Set<String> tables;

    private ChangesFilter(@NonNull Set<String> tables) {
        this.tables = tables;
    }

    @NonNull
    static Observable<Changes> apply(@NonNull PublishSubject<Changes> publishSubject, @NonNull Set<String> tables) {
        return publishSubject.filter(new ChangesFilter(tables));
    }

    @Override
    public Boolean call(Changes changes) {
        // if one of changed tables found in tables for subscription -> notify observer
        for (String affectedTable : changes.affectedTables()) {
            if (tables.contains(affectedTable)) {
                return true;
            }
        }

        return false;
    }
}
