package com.pushtorefresh.storio.sample.db;

import android.content.Context;
import android.support.annotation.NonNull;

import com.pushtorefresh.storio.db.StorIODb;
import com.pushtorefresh.storio.db.impl.StorIOSQLiteDbImpl;
import com.pushtorefresh.storio.sample.Logger;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class DbModule {

    @Provides @NonNull @Singleton StorIODb provideStorIODb(@NonNull Context context) {
        return new StorIOSQLiteDbImpl.Builder()
                .sqliteOpenHelper(new DbOpenHelper(context))
                .build()
                .setLogListener(new Logger());
    }
}
