package com.pushtorefresh.storio.sample;

import android.app.Application;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.pushtorefresh.storio.sample.db.DbModule;
import com.squareup.leakcanary.LeakCanary;
import com.squareup.leakcanary.RefWatcher;

import timber.log.Timber;

public class SampleApp extends Application {

    @Nullable
    private volatile AppComponent appComponent;

    // Monitors Memory Leaks, because why not!
    // You can play with sample app and Rx subscriptions
    // To see how it can leak memory if you won't unsubscribe.
    @NonNull
    private RefWatcher refWatcher;

    @NonNull
    public static SampleApp get(@NonNull Context context) {
        return (SampleApp) context.getApplicationContext();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        refWatcher = LeakCanary.install(this);
        Timber.plant(new Timber.DebugTree());
    }

    @NonNull
    public AppComponent appComponent() {
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

    @NonNull
    public RefWatcher refWatcher() {
        return refWatcher;
    }
}
