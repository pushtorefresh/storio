package com.pushtorefresh.storio.sample.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;

import com.pushtorefresh.storio.sample.Logger;
import com.pushtorefresh.storio.sqlite.StorIOSQLite;
import com.pushtorefresh.storio.sqlite.impl.DefaultStorIOSQLite;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class DbModule {

    @Provides
    @NonNull
    @Singleton
    public StorIOSQLite provideStorIOSQLiteDb(@NonNull SQLiteDatabase db) {
        return new DefaultStorIOSQLite.Builder()
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
