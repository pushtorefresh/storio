package com.pushtorefresh.storio.sample.db;

import android.content.Context;
import android.support.annotation.NonNull;

import com.pushtorefresh.storio.sample.Logger;
import com.pushtorefresh.storio.sqlitedb.StorIOSQLiteDb;
import com.pushtorefresh.storio.sqlitedb.impl.StorIOSQLiteDbImpl;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class DbModule {

    @Provides
    @NonNull
    @Singleton
    public StorIOSQLiteDb provideStorIOSQLiteDb(@NonNull Context context) {
        return new StorIOSQLiteDbImpl.Builder()
                .sqliteOpenHelper(new DbOpenHelper(context))
                .build()
                .setLogListener(new Logger());
    }
}
