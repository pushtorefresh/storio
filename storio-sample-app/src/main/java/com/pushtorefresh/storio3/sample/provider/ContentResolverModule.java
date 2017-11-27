package com.pushtorefresh.storio3.sample.provider;

import android.content.Context;
import android.support.annotation.NonNull;

import com.pushtorefresh.storio3.contentresolver.StorIOContentResolver;
import com.pushtorefresh.storio3.contentresolver.impl.DefaultStorIOContentResolver;
import com.pushtorefresh.storio3.sample.db.entities.Tweet;
import com.pushtorefresh.storio3.sample.db.entities.TweetContentResolverTypeMapping;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class ContentResolverModule {

    // We suggest to keep one instance of StorIO (SQLite or ContentResolver)
    // It's thread safe and so on, so just share it.
    // But if you need you can have multiple instances of StorIO
    // (SQLite or ContentResolver) with different settings such as type mapping, logging and so on.
    // But keep in mind that different instances of StorIOSQLite won't share notifications!
    @Provides
    @NonNull
    @Singleton
    public StorIOContentResolver provideStorIOContentResolver(@NonNull Context context) {
        return DefaultStorIOContentResolver.builder()
                .contentResolver(context.getContentResolver())
                .addTypeMapping(Tweet.class, new TweetContentResolverTypeMapping())
                .build();
    }
}
