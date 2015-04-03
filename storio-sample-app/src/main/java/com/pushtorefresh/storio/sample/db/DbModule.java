package com.pushtorefresh.storio.sample.db;

import android.content.Context;
import android.support.annotation.NonNull;

import com.pushtorefresh.storio.sample.Logger;
import com.pushtorefresh.storio.sqlitedb.StorIOSQLiteDb;
import com.pushtorefresh.storio.sqlitedb.impl.DefaultStorIOSQLiteDb;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class DbModule {

    @Provides
    @NonNull
    @Singleton
    public StorIOSQLiteDb provideStorIOSQLiteDb(@NonNull Context context) {
        return new DefaultStorIOSQLiteDb.Builder()
                .sqliteOpenHelper(new DbOpenHelper(context))
                .build()
                .setLogListener(new Logger());
    }
}
