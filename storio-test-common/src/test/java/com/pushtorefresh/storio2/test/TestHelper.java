package com.pushtorefresh.storio2.test;

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.reactivex.functions.Consumer;
import io.reactivex.plugins.RxJavaPlugins;

public final class TestHelper {

    private TestHelper() {
        throw new IllegalStateException("No instances please");
    }

    @NonNull
    public static List<Throwable> trackPluginErrors() {
        final List<Throwable> list = Collections.synchronizedList(new ArrayList<Throwable>());

        RxJavaPlugins.setErrorHandler(new Consumer<Throwable>() {
            @Override
            public void accept(Throwable t) {
                list.add(t);
            }
        });

        return list;
    }
}
