package com.pushtorefresh.storio.sample.db;

import android.content.Context;
import android.support.annotation.NonNull;

import com.pushtorefresh.storio.db.StorIODb;
import com.pushtorefresh.storio.db.impl.StorIOSQLiteDb;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class DbModule {

    @Provides @NonNull @Singleton StorIODb provideStorIODb(@NonNull Context context) {
        return new StorIOSQLiteDb.Builder()
                .sqliteOpenHelper(new DbOpenHelper(context))
                .build();
    }
}
