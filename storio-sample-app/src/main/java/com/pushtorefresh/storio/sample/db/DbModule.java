package com.pushtorefresh.storio.sample.db;

import android.content.Context;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.NonNull;

import com.pushtorefresh.storio.sample.db.entity.Tweet;
import com.pushtorefresh.storio.sample.db.entity.TweetStorIOSQLiteDeleteResolver;
import com.pushtorefresh.storio.sample.db.entity.TweetStorIOSQLiteGetResolver;
import com.pushtorefresh.storio.sample.db.entity.TweetStorIOSQLitePutResolver;
import com.pushtorefresh.storio.sqlite.SQLiteTypeMapping;
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
    public StorIOSQLite provideStorIOSQLite(@NonNull SQLiteOpenHelper sqLiteOpenHelper) {
        return new DefaultStorIOSQLite.Builder()
                .sqliteOpenHelper(sqLiteOpenHelper)
                .addTypeMapping(Tweet.class, new SQLiteTypeMapping.Builder<Tweet>()
                        .putResolver(new TweetStorIOSQLitePutResolver())
                        .getResolver(new TweetStorIOSQLiteGetResolver())
                        .deleteResolver(new TweetStorIOSQLiteDeleteResolver())
                        .build())
                .build();
    }

    @Provides
    @NonNull
    @Singleton
    public SQLiteOpenHelper provideSQSqLiteOpenHelper(@NonNull Context context) {
        return new DbOpenHelper(context);
    }
}
