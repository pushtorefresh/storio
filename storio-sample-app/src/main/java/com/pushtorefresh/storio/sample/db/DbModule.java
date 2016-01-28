package com.pushtorefresh.storio.sample.db;

import android.content.Context;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.NonNull;

import com.pushtorefresh.storio.sample.db.entities.Tweet;
import com.pushtorefresh.storio.sample.db.entities.TweetSQLiteTypeMapping;
import com.pushtorefresh.storio.sample.db.entities.TweetWithUser;
import com.pushtorefresh.storio.sample.db.entities.User;
import com.pushtorefresh.storio.sample.db.entities.UserSQLiteTypeMapping;
import com.pushtorefresh.storio.sample.db.resolvers.TweetWithUserDeleteResolver;
import com.pushtorefresh.storio.sample.db.resolvers.TweetWithUserGetResolver;
import com.pushtorefresh.storio.sample.db.resolvers.TweetWithUserPutResolver;
import com.pushtorefresh.storio.sqlite.SQLiteTypeMapping;
import com.pushtorefresh.storio.sqlite.StorIOSQLite;
import com.pushtorefresh.storio.sqlite.impl.DefaultStorIOSQLite;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class DbModule {

    // We suggest to keep one instance of StorIO (SQLite or ContentResolver)
    // It's thread safe and so on, so just share it.
    // But if you need you can have multiple instances of StorIO
    // (SQLite or ContentResolver) with different settings such as type mapping, logging and so on.
    // But keep in mind that different instances of StorIOSQLite won't share notifications!
    @Provides
    @NonNull
    @Singleton
    public StorIOSQLite provideStorIOSQLite(@NonNull SQLiteOpenHelper sqLiteOpenHelper) {
        return DefaultStorIOSQLite.builder()
                .sqliteOpenHelper(sqLiteOpenHelper)
                .addTypeMapping(Tweet.class, new TweetSQLiteTypeMapping())
                .addTypeMapping(User.class, new UserSQLiteTypeMapping())
                .addTypeMapping(TweetWithUser.class, SQLiteTypeMapping.<TweetWithUser>builder()
                        .putResolver(new TweetWithUserPutResolver())
                        .getResolver(new TweetWithUserGetResolver())
                        .deleteResolver(new TweetWithUserDeleteResolver())
                        .build())
                .build();
    }

    @Provides
    @NonNull
    @Singleton
    public SQLiteOpenHelper provideSQLiteOpenHelper(@NonNull Context context) {
        return new DbOpenHelper(context);
    }
}
