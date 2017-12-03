package com.pushtorefresh.storio3.sample;

import android.support.annotation.NonNull;

import org.junit.runners.model.InitializationError;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.lang.reflect.Method;

public class SampleRobolectricTestRunner extends RobolectricTestRunner {

    public SampleRobolectricTestRunner(Class<?> testClass) throws InitializationError {
        super(testClass);
    }

    @Override
    @NonNull
    public Config getConfig(@NonNull Method method) {
        return new Config.Builder(super.getConfig(method))
                .setApplication(SampleApp.class)
                .build();
    }
}
