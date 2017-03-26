package com.pushtorefresh.storio.sqlite.impl;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.pushtorefresh.storio.operations.PreparedOperation;
import com.pushtorefresh.storio.sqlite.Interceptor;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

/**
 * A concrete interceptor chain that carries the entire interceptor chain:
 * all user interceptors and finally the database caller.
 */
public class ChainImpl implements Interceptor.Chain {

    @NonNull
    public static Interceptor.Chain buildChain(
            @NonNull List<Interceptor> registeredInterceptors,
            @NonNull Interceptor realInterceptor
    ) {
        final List<Interceptor> interceptors = new ArrayList<Interceptor>(registeredInterceptors.size() + 1);
        interceptors.addAll(registeredInterceptors);
        interceptors.add(realInterceptor);

        return new ChainImpl(interceptors.listIterator());
    }

    @NonNull
    private final ListIterator<Interceptor> interceptors;

    private int calls;

    public ChainImpl(@NonNull ListIterator<Interceptor> interceptors) {
        this.interceptors = interceptors;
    }

    @Nullable // can be null on PreparedGetObject
    @Override
    public <Result> Result proceed(@NonNull PreparedOperation<Result> operation) {
        if (!interceptors.hasNext()) {
            throw new IllegalStateException("proceed was called on empty iterator");
        }

        calls++;

        // Confirm that this is the only call to chain.proceed().
        if (calls > 1) {
            throw new IllegalStateException("nextInterceptor " + interceptors.previous()
                    + " must call proceed() exactly once");
        }

        // Call the nextChain nextInterceptor in the chain.
        final Interceptor nextInterceptor = interceptors.next();
        final ChainImpl nextChain = new ChainImpl(interceptors);
        final Result result = nextInterceptor.intercept(operation, nextChain);

        return result;
    }
}
