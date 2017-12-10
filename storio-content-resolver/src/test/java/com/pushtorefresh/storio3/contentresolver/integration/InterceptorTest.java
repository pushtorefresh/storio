package com.pushtorefresh.storio3.contentresolver.integration;

import android.content.ContentValues;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.pushtorefresh.storio3.Interceptor;
import com.pushtorefresh.storio3.contentresolver.BuildConfig;
import com.pushtorefresh.storio3.contentresolver.impl.DefaultStorIOContentResolver;
import com.pushtorefresh.storio3.contentresolver.operations.put.DefaultPutResolver;
import com.pushtorefresh.storio3.contentresolver.queries.DeleteQuery;
import com.pushtorefresh.storio3.contentresolver.queries.InsertQuery;
import com.pushtorefresh.storio3.contentresolver.queries.Query;
import com.pushtorefresh.storio3.contentresolver.queries.UpdateQuery;
import com.pushtorefresh.storio3.operations.PreparedOperation;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.Collections;
import java.util.concurrent.atomic.AtomicInteger;

import static com.pushtorefresh.storio3.contentresolver.integration.TestItem.COLUMN_ID;
import static com.pushtorefresh.storio3.contentresolver.integration.TestItem.COLUMN_VALUE;
import static java.util.Arrays.asList;
import static org.assertj.core.api.Java6Assertions.assertThat;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)
public class InterceptorTest extends IntegrationTest {

    @SuppressWarnings("NullableProblems") // @Before
    @NonNull
    private AtomicInteger callCount;

    @NonNull
    @Override
    protected DefaultStorIOContentResolver.CompleteBuilder createStoreIOContentResolver() {
        return super.createStoreIOContentResolver()
                .addInterceptor(createInterceptor())
                .addInterceptor(createInterceptor());
    }

    @Before
    public void beforeEachTest() {
        callCount = new AtomicInteger(0);
    }

    @Test
    public void deleteByQuery() {
        storIOContentResolver.delete()
                .byQuery(DeleteQuery.builder()
                        .uri(TestItem.CONTENT_URI)
                        .build()
                )
                .prepare()
                .executeAsBlocking();
        checkInterceptorsCalls();
    }

    @Test
    public void deleteCollectionOfObjects() {
        storIOContentResolver.delete()
                .objects(Collections.singleton(createTestItem()))
                .prepare()
                .executeAsBlocking();
        checkInterceptorsCalls();
    }

    @Test
    public void deleteObject() {
        storIOContentResolver.delete()
                .object(createTestItem())
                .prepare()
                .executeAsBlocking();
        checkInterceptorsCalls();
    }

    @Test
    public void getCursorWithQuery() {
        storIOContentResolver.get()
                .cursor()
                .withQuery(Query.builder()
                        .uri(TestItem.CONTENT_URI)
                        .build()
                )
                .prepare()
                .executeAsBlocking();
        checkInterceptorsCalls();
    }

    @Test
    public void getListOfObjectsWithQuery() {
        storIOContentResolver.get()
                .listOfObjects(TestItem.class)
                .withQuery(Query.builder()
                        .uri(TestItem.CONTENT_URI)
                        .build()
                )
                .prepare()
                .executeAsBlocking();
        checkInterceptorsCalls();
    }

    @Test
    public void getNumberOfResultsWithQuery() {
        storIOContentResolver.get()
                .numberOfResults()
                .withQuery(Query.builder()
                        .uri(TestItem.CONTENT_URI)
                        .build()
                )
                .prepare()
                .executeAsBlocking();
        checkInterceptorsCalls();
    }

    @Test
    public void getObjectWithQuery() {
        storIOContentResolver.get()
                .object(TestItem.class)
                .withQuery(Query.builder()
                        .uri(TestItem.CONTENT_URI)
                        .build()
                )
                .prepare()
                .executeAsBlocking();
        checkInterceptorsCalls();
    }

    @Test
    public void putCollection() {
        storIOContentResolver.put()
                .objects(Collections.singleton(createTestItem()))
                .prepare()
                .executeAsBlocking();
        checkInterceptorsCalls();
    }

    @Test
    public void putContentValues() {
        storIOContentResolver.put()
                .contentValues(createContentValues())
                .withPutResolver(createCVPutResolver())
                .prepare()
                .executeAsBlocking();
        checkInterceptorsCalls();
    }

    @Test
    public void putContentValuesIterable() {
        storIOContentResolver.put()
                .contentValues(asList(createContentValues(), createContentValues()))
                .withPutResolver(createCVPutResolver())
                .prepare()
                .executeAsBlocking();
        checkInterceptorsCalls();
    }

    @Test
    public void putObject() {
        storIOContentResolver.put()
                .object(createTestItem())
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
    private TestItem createTestItem() {
        return TestItem.create(1L, "message");
    }

    @NonNull
    private ContentValues createContentValues() {
        final ContentValues contentValues = new ContentValues(3);
        contentValues.put(COLUMN_ID, 1);
        contentValues.put(COLUMN_VALUE, "message");
        return contentValues;
    }

    @NonNull
    private DefaultPutResolver<ContentValues> createCVPutResolver() {
        return new DefaultPutResolver<ContentValues>() {
            @NonNull
            @Override
            protected InsertQuery mapToInsertQuery(@NonNull ContentValues object) {
                return InsertQuery.builder().uri(TestItem.CONTENT_URI).build();
            }

            @NonNull
            @Override
            protected UpdateQuery mapToUpdateQuery(@NonNull ContentValues object) {
                return UpdateQuery.builder().uri(TestItem.CONTENT_URI).build();
            }

            @NonNull
            @Override
            protected ContentValues mapToContentValues(@NonNull ContentValues object) {
                return object;
            }
        };
    }
}