package com.pushtorefresh.storio.sqlite.impl;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.pushtorefresh.storio.operations.PreparedOperation;
import com.pushtorefresh.storio.sqlite.Interceptor;

import java.util.ArrayList;
import java.util.List;

public class ChainImpl implements Interceptor.Chain {

    @NonNull
    public static Interceptor.Chain buildChain(
            @NonNull List<Interceptor> registeredInterceptors,
            @NonNull Interceptor realInterceptor
    ) {
        final List<Interceptor> interceptors = new ArrayList<Interceptor>(registeredInterceptors.size() + 1);
        interceptors.addAll(registeredInterceptors);
        interceptors.add(realInterceptor);

        return new ChainImpl(interceptors, 0);
    }

    @NonNull
    private final List<Interceptor> interceptors;

    @NonNull
    private final int index;

    private int calls;

    public ChainImpl(@NonNull List<Interceptor> interceptors, int index) {
        this.interceptors = interceptors;
        this.index = index;
    }

    @Nullable // can be null on PreparedGetObject
    @Override
    public <Result> Result proceed(@NonNull PreparedOperation<Result> operation) {
        if (index >= interceptors.size()) throw new AssertionError();

        calls++;

        // Confirm that this is the only call to chain.proceed().
        if (calls > 1) {
            throw new IllegalStateException("nextInterceptor " + interceptors.get(index - 1)
                    + " must call proceed() exactly once");
        }

        // Call the nextChain nextInterceptor in the chain.
        final ChainImpl nextChain = new ChainImpl(interceptors, index + 1);
        final Interceptor nextInterceptor = interceptors.get(index);
        final Result result = nextInterceptor.intercept(operation, nextChain);

        // Confirm that the nextChain nextInterceptor made its required call to chain.proceed().
        if (index + 1 < interceptors.size() && nextChain.calls != 1) {
            throw new IllegalStateException("network nextInterceptor " + nextInterceptor
                    + " must call proceed() exactly once");
        }

        return result;
    }
}
