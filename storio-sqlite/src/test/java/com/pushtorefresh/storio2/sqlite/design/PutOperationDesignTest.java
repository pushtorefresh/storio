package com.pushtorefresh.storio2.sqlite.design;

import android.content.ContentValues;
import android.support.annotation.NonNull;

import com.pushtorefresh.storio2.sqlite.operations.put.DefaultPutResolver;
import com.pushtorefresh.storio2.sqlite.operations.put.PutResolver;
import com.pushtorefresh.storio2.sqlite.operations.put.PutResult;
import com.pushtorefresh.storio2.sqlite.operations.put.PutResults;
import com.pushtorefresh.storio2.sqlite.queries.InsertQuery;
import com.pushtorefresh.storio2.sqlite.queries.UpdateQuery;

import org.junit.Test;

import java.util.Arrays;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Single;

import static org.mockito.Mockito.mock;

public class PutOperationDesignTest extends OperationDesignTest {

    private static final PutResolver<ContentValues> CONTENT_VALUES_PUT_RESOLVER = new DefaultPutResolver<ContentValues>() {
        @NonNull
        @Override
        protected InsertQuery mapToInsertQuery(@NonNull ContentValues object) {
            return InsertQuery.builder()
                    .table("some_table")
                    .build();
        }

        @NonNull
        @Override
        protected UpdateQuery mapToUpdateQuery(@NonNull ContentValues contentValues) {
            return UpdateQuery.builder()
                    .table("some_table") // it's just a sample, no need to specify params
                    .build();
        }

        @NonNull
        @Override
        protected ContentValues mapToContentValues(@NonNull ContentValues contentValues) {
            return contentValues; // easy
        }
    };

    @Test
    public void putObjectBlocking() {
        User user = newUser();

        PutResult putResult = storIOSQLite()
                .put()
                .object(user)
                .withPutResolver(UserTableMeta.PUT_RESOLVER)
                .prepare()
                .executeAsBlocking();
    }

    @Test
    public void putObjectFlowable() {
        User user = newUser();

        Flowable<PutResult> flowablePutResult = storIOSQLite()
                .put()
                .object(user)
                .withPutResolver(UserTableMeta.PUT_RESOLVER)
                .prepare()
                .asRxFlowable(BackpressureStrategy.MISSING);
    }

    @Test
    public void putContentValuesBlocking() {
        PutResult putResult = storIOSQLite()
                .put()
                .contentValues(mock(ContentValues.class))
                .withPutResolver(CONTENT_VALUES_PUT_RESOLVER)
                .prepare()
                .executeAsBlocking();
    }

    @Test
    public void putContentValuesFlowable() {
        Flowable<PutResult> putResult = storIOSQLite()
                .put()
                .contentValues(mock(ContentValues.class))
                .withPutResolver(CONTENT_VALUES_PUT_RESOLVER)
                .prepare()
                .asRxFlowable(BackpressureStrategy.MISSING);
    }

    @Test
    public void putContentValuesIterableBlocking() {
        Iterable<ContentValues> contentValuesIterable
                = Arrays.asList(mock(ContentValues.class));

        PutResults<ContentValues> putResults = storIOSQLite()
                .put()
                .contentValues(contentValuesIterable)
                .withPutResolver(CONTENT_VALUES_PUT_RESOLVER)
                .prepare()
                .executeAsBlocking();
    }

    @Test
    public void putContentValuesIterableFlowable() {
        Iterable<ContentValues> contentValuesIterable
                = Arrays.asList(mock(ContentValues.class));

        Flowable<PutResults<ContentValues>> putResults = storIOSQLite()
                .put()
                .contentValues(contentValuesIterable)
                .withPutResolver(CONTENT_VALUES_PUT_RESOLVER)
                .prepare()
                .asRxFlowable(BackpressureStrategy.MISSING);
    }

    @Test
    public void putContentValuesArrayBlocking() {
        ContentValues[] contentValuesArray = {mock(ContentValues.class)};

        PutResults<ContentValues> putResults = storIOSQLite()
                .put()
                .contentValues(contentValuesArray)
                .withPutResolver(CONTENT_VALUES_PUT_RESOLVER)
                .prepare()
                .executeAsBlocking();
    }

    @Test
    public void putContentValuesArrayFlowable() {
        ContentValues[] contentValuesArray = {mock(ContentValues.class)};

        Flowable<PutResults<ContentValues>> putResults = storIOSQLite()
                .put()
                .contentValues(contentValuesArray)
                .withPutResolver(CONTENT_VALUES_PUT_RESOLVER)
                .prepare()
                .asRxFlowable(BackpressureStrategy.MISSING);
    }

    @Test
    public void putObjectSingle() {
        User user = newUser();

        Single<PutResult> singlePutResult = storIOSQLite()
                .put()
                .object(user)
                .withPutResolver(UserTableMeta.PUT_RESOLVER)
                .prepare()
                .asRxSingle();
    }

    @Test
    public void putContentValuesSingle() {
        Single<PutResult> putResult = storIOSQLite()
                .put()
                .contentValues(mock(ContentValues.class))
                .withPutResolver(CONTENT_VALUES_PUT_RESOLVER)
                .prepare()
                .asRxSingle();
    }

    @Test
    public void putContentValuesIterableSingle() {
        Iterable<ContentValues> contentValuesIterable
                = Arrays.asList(mock(ContentValues.class));

        Single<PutResults<ContentValues>> putResults = storIOSQLite()
                .put()
                .contentValues(contentValuesIterable)
                .withPutResolver(CONTENT_VALUES_PUT_RESOLVER)
                .prepare()
                .asRxSingle();
    }

    @Test
    public void putObjectCompletable() {
        User user = newUser();

        Completable completablePut = storIOSQLite()
                .put()
                .object(user)
                .withPutResolver(UserTableMeta.PUT_RESOLVER)
                .prepare()
                .asRxCompletable();
    }

    @Test
    public void putContentValuesCompletable() {
        Completable completablePut = storIOSQLite()
                .put()
                .contentValues(mock(ContentValues.class))
                .withPutResolver(CONTENT_VALUES_PUT_RESOLVER)
                .prepare()
                .asRxCompletable();
    }

    @Test
    public void putContentValuesIterableCompletable() {
        Iterable<ContentValues> contentValuesIterable
                = Arrays.asList(mock(ContentValues.class));

        Completable completablePut = storIOSQLite()
                .put()
                .contentValues(contentValuesIterable)
                .withPutResolver(CONTENT_VALUES_PUT_RESOLVER)
                .prepare()
                .asRxCompletable();
    }

}
