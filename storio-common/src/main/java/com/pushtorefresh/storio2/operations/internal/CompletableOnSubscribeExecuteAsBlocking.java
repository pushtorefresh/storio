package com.pushtorefresh.storio2.operations.internal;

import android.support.annotation.NonNull;

import com.pushtorefresh.storio2.operations.PreparedOperation;

import io.reactivex.CompletableEmitter;
import io.reactivex.CompletableOnSubscribe;

/**
 * Required to avoid problems with ClassLoader when RxJava is not in ClassPath
 * We can not use anonymous classes from RxJava directly in StorIO, ClassLoader won't be happy :(
 * <p>
 * For internal usage only!
 */
public final class CompletableOnSubscribeExecuteAsBlocking implements CompletableOnSubscribe {

    @NonNull
    private final PreparedOperation preparedOperation;

    public CompletableOnSubscribeExecuteAsBlocking(@NonNull PreparedOperation preparedOperation) {
        this.preparedOperation = preparedOperation;
    }

    @Override
    public void subscribe(@io.reactivex.annotations.NonNull CompletableEmitter emitter) throws Exception {
        try {
            preparedOperation.executeAsBlocking();
            emitter.onComplete();
        } catch (Exception e) {
            emitter.onError(e);
        }
    }
}
