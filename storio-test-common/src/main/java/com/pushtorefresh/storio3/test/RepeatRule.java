package com.pushtorefresh.storio3.test;

import android.support.annotation.NonNull;

import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

public class RepeatRule implements TestRule {

    @Override
    @NonNull
    public Statement apply(@NonNull final Statement test, @NonNull final Description description) {
        final Repeat repeat = description.getAnnotation(Repeat.class);

        if (repeat != null) {
            return new Statement() {
                @Override
                public void evaluate() throws Throwable {
                    final int times = repeat.times();

                    if (times < 1) {
                        throw new IllegalArgumentException("Repeat times should be >= 1, times = " + times);
                    }

                    for (int i = 0; i < times; i++) {
                        System.out.println(description.getMethodName() + " iteration " + (i + 1));
                        test.evaluate();
                    }
                }
            };
        } else {
            return test;
        }
    }
}
