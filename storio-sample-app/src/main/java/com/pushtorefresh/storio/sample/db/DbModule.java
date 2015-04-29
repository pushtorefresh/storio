package com.pushtorefresh.storio.sample.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;

import com.pushtorefresh.storio.sample.db.entity.Tweet;
import com.pushtorefresh.storio.sample.db.entity.TweetDeleteResolver;
import com.pushtorefresh.storio.sample.db.entity.TweetGetResolver;
import com.pushtorefresh.storio.sample.db.entity.TweetPutResolver;
import com.pushtorefresh.storio.sqlite.SQLiteTypeDefaults;
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
    public StorIOSQLite provideStorIOSQLite(@NonNull SQLiteDatabase db) {
        return new DefaultStorIOSQLite.Builder()
                .db(db)
                .addDefaultsForType(Tweet.class, new SQLiteTypeDefaults.Builder<Tweet>()
                        .putResolver(new TweetPutResolver())
                        .getResolver(new TweetGetResolver())
                        .deleteResolver(new TweetDeleteResolver())
                        .build())
                .build();
    }

    @Provides
    @NonNull
    @Singleton
    public SQLiteDatabase provideSQLiteDatabase(@NonNull Context context) {
        return new DbOpenHelper(context)
                .getWritableDatabase();
    }
}
