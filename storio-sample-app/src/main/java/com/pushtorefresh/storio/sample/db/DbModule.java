package com.pushtorefresh.storio.sample.db;

import android.content.Context;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.NonNull;

import com.pushtorefresh.storio.sample.db.entities.Ant;
import com.pushtorefresh.storio.sample.db.entities.AntStorIOSQLiteDeleteResolver;
import com.pushtorefresh.storio.sample.db.entities.AntStorIOSQLiteGetResolver;
import com.pushtorefresh.storio.sample.db.entities.AntStorIOSQLitePutResolver;
import com.pushtorefresh.storio.sample.db.entities.Car;
import com.pushtorefresh.storio.sample.db.entities.CarStorIOSQLiteDeleteResolver;
import com.pushtorefresh.storio.sample.db.entities.CarStorIOSQLiteGetResolver;
import com.pushtorefresh.storio.sample.db.entities.CarStorIOSQLitePutResolver;
import com.pushtorefresh.storio.sample.db.entities.Person;
import com.pushtorefresh.storio.sample.db.entities.Queen;
import com.pushtorefresh.storio.sample.db.entities.QueenStorIOSQLiteDeleteResolver;
import com.pushtorefresh.storio.sample.db.entities.QueenStorIOSQLiteGetResolver;
import com.pushtorefresh.storio.sample.db.entities.QueenStorIOSQLitePutResolver;
import com.pushtorefresh.storio.sample.db.entities.Tweet;
import com.pushtorefresh.storio.sample.db.entities.TweetStorIOSQLiteDeleteResolver;
import com.pushtorefresh.storio.sample.db.entities.TweetStorIOSQLiteGetResolver;
import com.pushtorefresh.storio.sample.db.entities.TweetStorIOSQLitePutResolver;
import com.pushtorefresh.storio.sample.db.entities.TweetWithUser;
import com.pushtorefresh.storio.sample.db.entities.User;
import com.pushtorefresh.storio.sample.db.entities.UserStorIOSQLiteDeleteResolver;
import com.pushtorefresh.storio.sample.db.entities.UserStorIOSQLiteGetResolver;
import com.pushtorefresh.storio.sample.db.entities.UserStorIOSQLitePutResolver;
import com.pushtorefresh.storio.sample.db.resolvers.PersonDeleteResolver;
import com.pushtorefresh.storio.sample.db.resolvers.PersonGetResolver;
import com.pushtorefresh.storio.sample.db.resolvers.PersonPutResolver;
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
                .addTypeMapping(Tweet.class, SQLiteTypeMapping.<Tweet>builder()
                        .putResolver(new TweetStorIOSQLitePutResolver())
                        .getResolver(new TweetStorIOSQLiteGetResolver())
                        .deleteResolver(new TweetStorIOSQLiteDeleteResolver())
                        .build())
                .addTypeMapping(User.class, SQLiteTypeMapping.<User>builder()
                        .putResolver(new UserStorIOSQLitePutResolver())
                        .getResolver(new UserStorIOSQLiteGetResolver())
                        .deleteResolver(new UserStorIOSQLiteDeleteResolver())
                        .build())
                .addTypeMapping(TweetWithUser.class, SQLiteTypeMapping.<TweetWithUser>builder()
                        .putResolver(new TweetWithUserPutResolver())
                        .getResolver(new TweetWithUserGetResolver())
                        .deleteResolver(new TweetWithUserDeleteResolver())
                        .build())

                .addTypeMapping(Person.class, SQLiteTypeMapping.<Person>builder()
                        .putResolver(new PersonPutResolver())
                        .getResolver(new PersonGetResolver())
                        .deleteResolver(new PersonDeleteResolver())
                        .build())
                .addTypeMapping(Car.class, SQLiteTypeMapping.<Car>builder()
                        .putResolver(new CarStorIOSQLitePutResolver())
                        .getResolver(new CarStorIOSQLiteGetResolver())
                        .deleteResolver(new CarStorIOSQLiteDeleteResolver())
                        .build())

                .addTypeMapping(Queen.class, SQLiteTypeMapping.<Queen>builder()
                        .putResolver(new QueenStorIOSQLitePutResolver())
                        .getResolver(new QueenStorIOSQLiteGetResolver())
                        .deleteResolver(new QueenStorIOSQLiteDeleteResolver())
                        .build())
                .addTypeMapping(Ant.class, SQLiteTypeMapping.<Ant>builder()
                        .putResolver(new AntStorIOSQLitePutResolver())
                        .getResolver(new AntStorIOSQLiteGetResolver())
                        .deleteResolver(new AntStorIOSQLiteDeleteResolver())
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
