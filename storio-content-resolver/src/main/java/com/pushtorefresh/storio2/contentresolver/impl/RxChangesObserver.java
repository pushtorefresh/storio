package com.pushtorefresh.storio2.contentresolver.impl;

import android.content.ContentResolver;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.NonNull;

import com.pushtorefresh.storio2.contentresolver.Changes;

import java.util.Set;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableEmitter;
import io.reactivex.FlowableOnSubscribe;
import io.reactivex.functions.Cancellable;

/**
 * Hides RxJava2 from ClassLoader via separate class.
 *
 * FOR INTERNAL USE ONLY.
 */
final class RxChangesObserver {

    private RxChangesObserver() {
        throw new IllegalStateException("No instances please.");
    }

    @NonNull
    static Flowable<Changes> observeChanges(@NonNull final ContentResolver contentResolver,
                                            @NonNull final Set<Uri> uris,
                                            @NonNull final Handler handler,
                                            final int sdkVersion,
                                            @NonNull BackpressureStrategy backpressureStrategy) {
        return Flowable.create(new FlowableOnSubscribe<Changes>() {
            @Override
            public void subscribe(@io.reactivex.annotations.NonNull final FlowableEmitter<Changes> emitter) throws Exception {
                // Use one ContentObserver for all passed Uris on API >= 16
                if (sdkVersion >= Build.VERSION_CODES.JELLY_BEAN) {
                    final ContentObserver contentObserver = new ContentObserver(handler) {
                        @Override
                        public boolean deliverSelfNotifications() {
                            return false;
                        }

                        @Override
                        public void onChange(boolean selfChange, Uri uri) {
                            emitter.onNext(Changes.newInstance(uri));
                        }
                    };

                    for (Uri uri : uris) {
                        contentResolver.registerContentObserver(
                                uri,
                                true,
                                contentObserver
                        );
                    }

                    emitter.setCancellable(new Cancellable() {
                        @Override
                        public void cancel() throws Exception {
                            contentResolver.unregisterContentObserver(contentObserver);
                        }
                    });
                } else {
                    // Register separate ContentObserver for each uri on API < 16
                    for (final Uri uri : uris) {
                        final ContentObserver contentObserver = new ContentObserver(handler) {
                            @Override
                            public boolean deliverSelfNotifications() {
                                return false;
                            }

                            @Override
                            public void onChange(boolean selfChange) {
                                emitter.onNext(Changes.newInstance(uri));
                            }
                        };

                        contentResolver.registerContentObserver(uri, true, contentObserver);
                        emitter.setCancellable(new Cancellable() {
                            @Override
                            public void cancel() throws Exception {
                                contentResolver.unregisterContentObserver(contentObserver);
                            }
                        });
                    }
                }
            }
        }, backpressureStrategy);
    }
}