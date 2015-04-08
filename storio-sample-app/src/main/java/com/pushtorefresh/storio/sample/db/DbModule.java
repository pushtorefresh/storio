package com.pushtorefresh.storio.sample.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;

import com.pushtorefresh.storio.sample.Logger;
import com.pushtorefresh.storio.sqlite.StorIOSQLiteDb;
import com.pushtorefresh.storio.sqlite.impl.DefaultStorIOSQLiteDb;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class DbModule {

    @Provides
    @NonNull
    @Singleton
    public StorIOSQLiteDb provideStorIOSQLiteDb(@NonNull SQLiteDatabase db) {
        return new DefaultStorIOSQLiteDb.Builder()
                .db(db)
                .build()
                .setLogListener(new Logger());
    }

    @Provides
    @NonNull
    @Singleton
    public SQLiteDatabase provideSQLiteDatabase(@NonNull Context context) {
        return new DbOpenHelper(context)
                .getWritableDatabase();
    }
}
