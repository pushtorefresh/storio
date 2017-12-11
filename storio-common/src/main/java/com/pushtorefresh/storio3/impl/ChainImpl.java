package com.pushtorefresh.storio3.impl;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.pushtorefresh.storio3.operations.PreparedOperation;
import com.pushtorefresh.storio3.Interceptor;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

/**
 * A concrete interceptor chain that carries the entire interceptor chain:
 * all user interceptors and finally the database/content resolver caller.
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

        for (Interceptor interceptor : interceptors) {
            if (interceptor == null) {
                throw new IllegalArgumentException("Interceptor should not be null");
            }
        }

        return new ChainImpl(interceptors.listIterator());
    }

    @NonNull
    private final ListIterator<Interceptor> interceptors;

    private int calls;

    ChainImpl(@NonNull ListIterator<Interceptor> interceptors) {
        this.interceptors = interceptors;
    }

    @Nullable // can be null on PreparedGetObject
    @Override
    public <Result, WrappedResult, Data> Result proceed(@NonNull PreparedOperation<Result, WrappedResult, Data> operation) {
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

        return nextInterceptor.intercept(operation, nextChain);
    }
}
