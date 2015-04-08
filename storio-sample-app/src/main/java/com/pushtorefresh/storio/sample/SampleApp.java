package com.pushtorefresh.storio.sample;

import android.app.Application;
import android.content.Context;
import android.support.annotation.NonNull;

import com.pushtorefresh.storio.sample.db.DbModule;

import timber.log.Timber;

public class SampleApp extends Application {

    @NonNull
    private AppComponent appComponent;

    @Override
    public void onCreate() {
        super.onCreate();

        appComponent = DaggerAppComponent
                .builder()
                .appModule(new AppModule(this))
                .dbModule(new DbModule())
                .build();

        Timber.plant(new Timber.DebugTree());
    }

    @NonNull
    public static SampleApp get(@NonNull Context context) {
        return (SampleApp) context.getApplicationContext();
    }

    @NonNull
    public AppComponent getAppComponent() {
        return appComponent;
    }
}
