package com.pushtorefresh.storio.sample;

import android.app.Application;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.pushtorefresh.storio.sample.db.DbModule;

import timber.log.Timber;

public class SampleApp extends Application {

    @Nullable
    private volatile AppComponent appComponent;

    @NonNull
    public static SampleApp get(@NonNull Context context) {
        return (SampleApp) context.getApplicationContext();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Timber.plant(new Timber.DebugTree());
    }

    // When another process of the app created (for example, for ContentProvider), onCreate() method of application object won't be called
    // so we can't be sure, that it will be initialized
    @NonNull
    public AppComponent getAppComponent() {
        if (appComponent == null) {
            synchronized (SampleApp.class) {
                if (appComponent == null) {
                    appComponent = createAppComponent();
                }
            }
        }

        //noinspection ConstantConditions
        return appComponent;
    }

    @NonNull
    private AppComponent createAppComponent() {
        return DaggerAppComponent
                .builder()
                .appModule(new AppModule(this))
                .dbModule(new DbModule())
                .build();
    }
}
