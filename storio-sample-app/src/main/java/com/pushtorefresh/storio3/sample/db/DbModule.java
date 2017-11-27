package com.pushtorefresh.storio3.sample.db;

import android.content.Context;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.NonNull;

import com.pushtorefresh.storio3.sample.db.entities.Tweet;
import com.pushtorefresh.storio3.sample.db.entities.TweetSQLiteTypeMapping;
import com.pushtorefresh.storio3.sample.db.entities.TweetWithUser;
import com.pushtorefresh.storio3.sample.db.entities.User;
import com.pushtorefresh.storio3.sample.db.entities.UserSQLiteTypeMapping;
import com.pushtorefresh.storio3.sample.db.resolvers.TweetWithUserDeleteResolver;
import com.pushtorefresh.storio3.sample.db.resolvers.TweetWithUserGetResolver;
import com.pushtorefresh.storio3.sample.db.resolvers.TweetWithUserPutResolver;
import com.pushtorefresh.storio3.sample.many_to_many_sample.entities.Car;
import com.pushtorefresh.storio3.sample.many_to_many_sample.entities.CarStorIOSQLiteGetResolver;
import com.pushtorefresh.storio3.sample.many_to_many_sample.entities.CarStorIOSQLitePutResolver;
import com.pushtorefresh.storio3.sample.many_to_many_sample.entities.Person;
import com.pushtorefresh.storio3.sample.many_to_many_sample.entities.PersonStorIOSQLiteGetResolver;
import com.pushtorefresh.storio3.sample.many_to_many_sample.entities.PersonStorIOSQLitePutResolver;
import com.pushtorefresh.storio3.sample.many_to_many_sample.resolvers.CarPersonRelationPutResolver;
import com.pushtorefresh.storio3.sample.many_to_many_sample.resolvers.CarRelationsDeleteResolver;
import com.pushtorefresh.storio3.sample.many_to_many_sample.resolvers.CarRelationsGetResolver;
import com.pushtorefresh.storio3.sample.many_to_many_sample.resolvers.CarRelationsPutResolver;
import com.pushtorefresh.storio3.sample.many_to_many_sample.resolvers.PersonRelationsDeleteResolver;
import com.pushtorefresh.storio3.sample.many_to_many_sample.resolvers.PersonRelationsGetResolver;
import com.pushtorefresh.storio3.sample.many_to_many_sample.resolvers.PersonRelationsPutResolver;
import com.pushtorefresh.storio3.sqlite.SQLiteTypeMapping;
import com.pushtorefresh.storio3.sqlite.StorIOSQLite;
import com.pushtorefresh.storio3.sqlite.impl.DefaultStorIOSQLite;

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
        final CarStorIOSQLitePutResolver carStorIOSQLitePutResolver = new CarStorIOSQLitePutResolver();
        final CarStorIOSQLiteGetResolver carStorIOSQLiteGetResolver = new CarStorIOSQLiteGetResolver();

        final PersonStorIOSQLitePutResolver personStorIOSQLitePutResolver = new PersonStorIOSQLitePutResolver();
        final PersonStorIOSQLiteGetResolver personStorIOSQLiteGetResolver = new PersonStorIOSQLiteGetResolver();

        final CarPersonRelationPutResolver carPersonRelationPutResolver = new CarPersonRelationPutResolver();

        return DefaultStorIOSQLite.builder()
                .sqliteOpenHelper(sqLiteOpenHelper)
                .addTypeMapping(Tweet.class, new TweetSQLiteTypeMapping())
                .addTypeMapping(User.class, new UserSQLiteTypeMapping())
                .addTypeMapping(TweetWithUser.class, SQLiteTypeMapping.<TweetWithUser>builder()
                        .putResolver(new TweetWithUserPutResolver())
                        .getResolver(new TweetWithUserGetResolver())
                        .deleteResolver(new TweetWithUserDeleteResolver())
                        .build())

                .addTypeMapping(Person.class, SQLiteTypeMapping.<Person>builder()
                        .putResolver(new PersonRelationsPutResolver(carStorIOSQLitePutResolver, carPersonRelationPutResolver))
                        .getResolver(new PersonRelationsGetResolver(carStorIOSQLiteGetResolver))
                        .deleteResolver(new PersonRelationsDeleteResolver())
                        .build())
                .addTypeMapping(Car.class, SQLiteTypeMapping.<Car>builder()
                        .putResolver(new CarRelationsPutResolver(personStorIOSQLitePutResolver, carPersonRelationPutResolver))
                        .getResolver(new CarRelationsGetResolver(personStorIOSQLiteGetResolver))
                        .deleteResolver(new CarRelationsDeleteResolver())

                        .build()
                )
                .build();
    }

    @Provides
    @NonNull
    @Singleton
    public SQLiteOpenHelper provideSQLiteOpenHelper(@NonNull Context context) {
        return new DbOpenHelper(context);
    }
}
