package com.pushtorefresh.storio.contentresolver.test;

import android.support.annotation.NonNull;
import android.support.test.InstrumentationRegistry;

import com.pushtorefresh.storio.contentresolver.ContentResolverTypeMapping;
import com.pushtorefresh.storio.contentresolver.StorIOContentResolver;
import com.pushtorefresh.storio.contentresolver.impl.DefaultStorIOContentResolver;
import com.pushtorefresh.storio.contentresolver.query.DeleteQuery;
import com.pushtorefresh.storio.sample.db.entity.Tweet;
import com.pushtorefresh.storio.sample.provider.meta.TweetMeta;

import org.junit.Before;

/**
 * Part of tests for StorIOContentResolver moved to the sample app
 * because we can not normally have ContentProvider in androidTest in StorIOContentResolver tests.
 */
public class BaseTest {

    @NonNull // Initialized in setUp
    protected StorIOContentResolver storIOContentResolver;

    @Before
    public void setUp() {
        storIOContentResolver = DefaultStorIOContentResolver.builder()
                .contentResolver(InstrumentationRegistry.getContext().getContentResolver())
                .addTypeMapping(Tweet.class, ContentResolverTypeMapping.<Tweet>builder()
                        .putResolver(TweetMeta.PUT_RESOLVER)
                        .getResolver(TweetMeta.GET_RESOLVER)
                        .deleteResolver(TweetMeta.DELETE_RESOLVER)
                        .build())
                .build();

        // Clear content provider
        storIOContentResolver
                .delete()
                .byQuery(DeleteQuery.builder()
                        .uri(TweetMeta.CONTENT_URI)
                        .build())
                .prepare()
                .executeAsBlocking();
    }
}
