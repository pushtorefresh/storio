package com.pushtorefresh.storio3.sqlite.integration;

import android.content.ContentValues;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.pushtorefresh.storio3.operations.PreparedOperation;
import com.pushtorefresh.storio3.sqlite.BuildConfig;
import com.pushtorefresh.storio3.Interceptor;
import com.pushtorefresh.storio3.sqlite.SQLiteTypeMapping;
import com.pushtorefresh.storio3.sqlite.StorIOSQLite;
import com.pushtorefresh.storio3.sqlite.impl.DefaultStorIOSQLite;
import com.pushtorefresh.storio3.sqlite.operations.put.DefaultPutResolver;
import com.pushtorefresh.storio3.sqlite.queries.DeleteQuery;
import com.pushtorefresh.storio3.sqlite.queries.InsertQuery;
import com.pushtorefresh.storio3.sqlite.queries.Query;
import com.pushtorefresh.storio3.sqlite.queries.RawQuery;
import com.pushtorefresh.storio3.sqlite.queries.UpdateQuery;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import java.util.Collections;
import java.util.concurrent.atomic.AtomicInteger;

import static com.pushtorefresh.storio3.sqlite.integration.TweetTableMeta.COLUMN_AUTHOR_ID;
import static com.pushtorefresh.storio3.sqlite.integration.TweetTableMeta.COLUMN_CONTENT_TEXT;
import static com.pushtorefresh.storio3.sqlite.integration.TweetTableMeta.COLUMN_ID;
import static org.assertj.core.api.Java6Assertions.assertThat;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)
public class InterceptorTest {

    @SuppressWarnings("NullableProblems") // @Before
    @NonNull
    private StorIOSQLite storIOSQLite;

    @SuppressWarnings("NullableProblems") // @Before
    @NonNull
    private Interceptor interceptor1;

    @SuppressWarnings("NullableProblems") // @Before
    @NonNull
    private Interceptor interceptor2;

    @SuppressWarnings("NullableProblems") // @Before
    @NonNull
    private AtomicInteger callCount;

    @Before
    public void setUp() throws Exception {
        final SQLiteOpenHelper sqLiteOpenHelper = new TestSQLiteOpenHelper(RuntimeEnvironment.application);

        callCount = new AtomicInteger(0);
        interceptor1 = createInterceptor();
        interceptor2 = createInterceptor();

        storIOSQLite = DefaultStorIOSQLite.builder()
                .sqliteOpenHelper(sqLiteOpenHelper)
                .addTypeMapping(Tweet.class, SQLiteTypeMapping.<Tweet>builder()
                        .putResolver(TweetTableMeta.PUT_RESOLVER)
                        .getResolver(TweetTableMeta.GET_RESOLVER)
                        .deleteResolver(TweetTableMeta.DELETE_RESOLVER)
                        .build())
                .addInterceptor(interceptor1)
                .addInterceptor(interceptor2)
                .build();
    }

    @Test
    public void deleteByQuery() {
        storIOSQLite.delete()
                .byQuery(DeleteQuery.builder()
                        .table(TweetTableMeta.TABLE)
                        .build()
                )
                .prepare()
                .executeAsBlocking();
        checkInterceptorsCalls();
    }

    @Test
    public void deleteCollectionOfObjects() {
        storIOSQLite.delete()
                .objects(Collections.singleton(createTweet()))
                .prepare()
                .executeAsBlocking();
        checkInterceptorsCalls();
    }

    @Test
    public void deleteObject() {
        storIOSQLite.delete()
                .object(createTweet())
                .prepare()
                .executeAsBlocking();
        checkInterceptorsCalls();
    }

    @Test
    public void execSql() {
        storIOSQLite.executeSQL()
                .withQuery(RawQuery.builder()
                        .query("select * from " + TweetTableMeta.TABLE)
                        .build()
                )
                .prepare()
                .executeAsBlocking();
        checkInterceptorsCalls();
    }

    @Test
    public void getCursorWithRawQuery() {
        storIOSQLite.get()
                .cursor()
                .withQuery(RawQuery.builder()
                        .query("select * from " + TweetTableMeta.TABLE)
                        .build()
                )
                .prepare()
                .executeAsBlocking();
        checkInterceptorsCalls();
    }

    @Test
    public void getCursorWithQuery() {
        storIOSQLite.get()
                .cursor()
                .withQuery(Query.builder()
                        .table(TweetTableMeta.TABLE)
                        .build()
                )
                .prepare()
                .executeAsBlocking();
        checkInterceptorsCalls();
    }

    @Test
    public void getListOfObjectsWithRawQuery() {
        storIOSQLite.get()
                .listOfObjects(Tweet.class)
                .withQuery(RawQuery.builder()
                        .query("select * from " + TweetTableMeta.TABLE)
                        .build()
                )
                .prepare()
                .executeAsBlocking();
        checkInterceptorsCalls();
    }

    @Test
    public void getListOfObjectsWithQuery() {
        storIOSQLite.get()
                .listOfObjects(Tweet.class)
                .withQuery(Query.builder()
                        .table(TweetTableMeta.TABLE)
                        .build()
                )
                .prepare()
                .executeAsBlocking();
        checkInterceptorsCalls();
    }

    @Test
    public void getNumberOfResultsWithRawQuery() {
        storIOSQLite.get()
                .numberOfResults()
                .withQuery(RawQuery.builder()
                        .query("select * from " + TweetTableMeta.TABLE)
                        .build()
                )
                .prepare()
                .executeAsBlocking();
        checkInterceptorsCalls();
    }

    @Test
    public void getNumberOfResultsWithQuery() {
        storIOSQLite.get()
                .numberOfResults()
                .withQuery(Query.builder()
                        .table(TweetTableMeta.TABLE)
                        .build()
                )
                .prepare()
                .executeAsBlocking();
        checkInterceptorsCalls();
    }

    @Test
    public void getObjectWithRawQuery() {
        storIOSQLite.get()
                .object(Tweet.class)
                .withQuery(RawQuery.builder()
                        .query("select * from " + TweetTableMeta.TABLE)
                        .build()
                )
                .prepare()
                .executeAsBlocking();
        checkInterceptorsCalls();
    }

    @Test
    public void getObjectWithQuery() {
        storIOSQLite.get()
                .object(Tweet.class)
                .withQuery(Query.builder()
                        .table(TweetTableMeta.TABLE)
                        .build()
                )
                .prepare()
                .executeAsBlocking();
        checkInterceptorsCalls();
    }

    @Test
    public void putCollection() {
        storIOSQLite.put()
                .objects(Collections.singleton(createTweet()))
                .prepare()
                .executeAsBlocking();
        checkInterceptorsCalls();
    }

    @Test
    public void putContentValues() {
        storIOSQLite.put()
                .contentValues(createContentValues())
                .withPutResolver(createCVPutResolver())
                .prepare()
                .executeAsBlocking();
        checkInterceptorsCalls();
    }

    @Test
    public void putContentValuesIterable() {
        storIOSQLite.put()
                .contentValues(createContentValues(), createContentValues())
                .withPutResolver(createCVPutResolver())
                .prepare()
                .executeAsBlocking();
        checkInterceptorsCalls();
    }

    @Test
    public void putObject() {
        storIOSQLite.put()
                .object(createTweet())
                .prepare()
                .executeAsBlocking();
        checkInterceptorsCalls();
    }

    private void checkInterceptorsCalls() {
        assertThat(callCount.get()).isEqualTo(2);
    }

    @NonNull
    private Interceptor createInterceptor() {
        return new Interceptor() {
            @Nullable
            @Override
            public <Result, WrappedData, Data> Result intercept(@NonNull PreparedOperation<Result, WrappedData, Data> operation, @NonNull Chain chain) {
                callCount.incrementAndGet();
                return chain.proceed(operation);
            }
        };
    }

    @NonNull
    private Tweet createTweet() {
        return Tweet.newInstance(1L, 1L, "message");
    }

    @NonNull
    private ContentValues createContentValues() {
        final ContentValues contentValues = new ContentValues(3);
        contentValues.put(COLUMN_ID, 1);
        contentValues.put(COLUMN_AUTHOR_ID, 1);
        contentValues.put(COLUMN_CONTENT_TEXT, "message");
        return contentValues;
    }

    @NonNull
    private DefaultPutResolver<ContentValues> createCVPutResolver() {
        return new DefaultPutResolver<ContentValues>() {
            @NonNull
            @Override
            protected InsertQuery mapToInsertQuery(@NonNull ContentValues object) {
                return InsertQuery.builder().table(TweetTableMeta.TABLE).build();
            }

            @NonNull
            @Override
            protected UpdateQuery mapToUpdateQuery(@NonNull ContentValues object) {
                return UpdateQuery.builder().table(TweetTableMeta.TABLE).build();
            }

            @NonNull
            @Override
            protected ContentValues mapToContentValues(@NonNull ContentValues object) {
                return object;
            }
        };
    }
}