package com.pushtorefresh.storio.contentresolver.impl;

import android.net.Uri;
import android.support.annotation.NonNull;

import com.pushtorefresh.storio.contentresolver.Changes;

import java.util.Set;

import rx.Observable;
import rx.functions.Func1;
import rx.subjects.PublishSubject;

/**
 * Hiding RxJava from ClassLoader via separate class
 */
final class ChangesFilter implements Func1<Changes, Boolean> {

    @NonNull
    private final Set<Uri> uris;

    private ChangesFilter(@NonNull Set<Uri> uris) {
        this.uris = uris;
    }

    @NonNull
    static Observable<Changes> apply(@NonNull PublishSubject<Changes> publishSubject, @NonNull Set<Uri> uris) {
        return publishSubject.filter(new ChangesFilter(uris));
    }

    @Override
    public Boolean call(Changes changes) {
        // if one of changed uri found in uris for subscription -> notify observer
        for (Uri uri : uris) {
            if (changes.affectedUris().contains(uri)) {
                return true;
            }
        }

        return false;
    }
}
