package com.pushtorefresh.storio.contentresolver.impl;

import android.content.ContentResolver;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.support.annotation.NonNull;

import com.pushtorefresh.storio.contentresolver.Changes;

import java.util.Set;

import rx.Observable;
import rx.Subscriber;
import rx.functions.Action0;
import rx.subscriptions.Subscriptions;

/**
 * Hides RxJava from ClassLoader via separate class.
 *
 * FOR INTERNAL USAGE ONLY.
 */
final class RxChangesObserver {

    private RxChangesObserver() {
        throw new IllegalStateException("No instances please.");
    }

    @NonNull
    static Observable<Changes> observeChanges(@NonNull final ContentResolver contentResolver, @NonNull final Set<Uri> uris, @NonNull final Handler handler) {
        return Observable.create(new Observable.OnSubscribe<Changes>() {
            @Override
            public void call(final Subscriber<? super Changes> subscriber) {
                final ContentObserver contentObserver = new ContentObserver(handler) {
                    @Override
                    public boolean deliverSelfNotifications() {
                        return false;
                    }

                    @Override
                    public void onChange(boolean selfChange, Uri uri) {
                        subscriber.onNext(Changes.newInstance(uri));
                    }
                };

                for (Uri uri : uris) {
                    contentResolver.registerContentObserver(
                            uri,
                            true,
                            contentObserver
                    );
                }

                subscriber.add(Subscriptions.create(new Action0() {
                    @Override
                    public void call() {
                        // Preventing memory leak on unsubscribe from Observable
                        contentResolver.unregisterContentObserver(contentObserver);
                    }
                }));
            }
        });
    }
}
