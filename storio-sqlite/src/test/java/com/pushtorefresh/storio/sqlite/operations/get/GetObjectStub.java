package com.pushtorefresh.storio.sqlite.operations.get;

import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.pushtorefresh.storio.sqlite.Changes;
import com.pushtorefresh.storio.sqlite.SQLiteTypeMapping;
import com.pushtorefresh.storio.sqlite.StorIOSQLite;
import com.pushtorefresh.storio.sqlite.queries.Query;
import com.pushtorefresh.storio.sqlite.queries.RawQuery;
import com.pushtorefresh.storio.test.ObservableBehaviorChecker;

import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import rx.Observable;
import rx.Single;
import rx.functions.Action1;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

class GetObjectStub {

    @NonNull
    final StorIOSQLite storIOSQLite;

    @NonNull
    private final StorIOSQLite.Internal internal;

    @NonNull
    final Query query;

    @NonNull
    final RawQuery rawQuery;

    @NonNull
    final GetResolver<TestItem> getResolver;

    @NonNull
    private final Cursor cursor;

    @NonNull
    final TestItem item;

    @NonNull
    private final SQLiteTypeMapping<TestItem> typeMapping;

    private final boolean withTypeMapping;

    private GetObjectStub(boolean withTypeMapping) {
        this.withTypeMapping = withTypeMapping;

        storIOSQLite = mock(StorIOSQLite.class);
        internal = mock(StorIOSQLite.Internal.class);

        when(storIOSQLite.lowLevel())
                .thenReturn(internal);

        String table = "test_table";
        String tag = "test_tag";

        query = Query
                .builder()
                .table(table)
                .observesTags(tag)
                .build();

        rawQuery = RawQuery
                .builder()
                .query("select * from who_cares")
                .observesTables(table)
                .observesTags(tag)
                .build();

        //noinspection unchecked
        getResolver = mock(GetResolver.class);
        cursor = mock(Cursor.class);

        item = new TestItem();

        when(cursor.getCount())
                .thenReturn(1);

        when(cursor.moveToNext()).thenAnswer(new Answer<Boolean>() {
            int invocationsCount = 0;

            @Override
            public Boolean answer(InvocationOnMock invocation) throws Throwable {
                return invocationsCount++ < 1;
            }
        });

        when(storIOSQLite.get())
                .thenReturn(new PreparedGet.Builder(storIOSQLite));

        when(storIOSQLite.observeChanges()).thenReturn(Observable.<Changes>empty());

        assertThat(rawQuery.observesTables()).isNotNull();

        when(getResolver.performGet(storIOSQLite, query))
                .thenReturn(cursor);

        when(getResolver.performGet(storIOSQLite, rawQuery))
                .thenReturn(cursor);

        when(getResolver.mapFromCursor(cursor))
                .thenReturn(item);

        //noinspection unchecked
        typeMapping = mock(SQLiteTypeMapping.class);

        if (withTypeMapping) {
            when(internal.typeMapping(TestItem.class)).thenReturn(typeMapping);
            when(typeMapping.getResolver()).thenReturn(getResolver);
        }
    }

    @NonNull
    static GetObjectStub newInstanceWithoutTypeMapping() {
        return new GetObjectStub(false);
    }

    @NonNull
    static GetObjectStub newInstanceWithTypeMapping() {
        return new GetObjectStub(true);
    }

    void verifyQueryBehavior(@Nullable TestItem actualItem) {
        // should be called once
        verify(storIOSQLite).get();

        // should be called only once
        verify(getResolver).performGet(storIOSQLite, query);

        // should be called only once
        verify(getResolver).mapFromCursor(cursor);

        // should be called only once because of Performance!
        verify(cursor).getCount();

        // should be called only once
        verify(cursor).moveToNext();

        // cursor must be closed!
        verify(cursor).close();

        // actual item should be equals to expected
        assertThat(actualItem).isEqualTo(item);

        if (withTypeMapping) {
            // should be called only once because of Performance!
            verify(storIOSQLite).lowLevel();

            // should be called only once because of Performance!
            verify(internal).typeMapping(TestItem.class);

            // should be called only once because of Performance!
            verify(typeMapping).getResolver();
        }

        verifyNoMoreInteractions(storIOSQLite, internal, cursor);
    }

    void verifyQueryBehavior(@NonNull Observable<TestItem> observable) {
        new ObservableBehaviorChecker<TestItem>()
                .observable(observable)
                .expectedNumberOfEmissions(1)
                .testAction(new Action1<TestItem>() {
                    @Override
                    public void call(TestItem testItem) {
                        // Get Operation should be subscribed to changes of tables from query
                        verify(storIOSQLite).observeChanges();
                        verify(storIOSQLite).defaultScheduler();
                        verifyQueryBehavior(testItem);
                    }
                })
                .checkBehaviorOfObservable();
    }

    void verifyQueryBehavior(@NonNull Single<TestItem> single) {
        new ObservableBehaviorChecker<TestItem>()
                .observable(single.toObservable())
                .expectedNumberOfEmissions(1)
                .testAction(new Action1<TestItem>() {
                    @Override
                    public void call(TestItem testItem) {
                        verify(storIOSQLite).defaultScheduler();
                        verifyQueryBehavior(testItem);
                    }
                })
                .checkBehaviorOfObservable();
    }

    void verifyRawQueryBehavior(@Nullable TestItem actualItem) {
        verify(storIOSQLite).get();
        verify(getResolver).performGet(storIOSQLite, rawQuery);
        verify(getResolver).mapFromCursor(cursor);
        verify(cursor).close();
        assertThat(actualItem).isEqualTo(item);
    }

    void verifyRawQueryBehavior(@NonNull Observable<TestItem> observable) {
        new ObservableBehaviorChecker<TestItem>()
                .observable(observable)
                .expectedNumberOfEmissions(1)
                .testAction(new Action1<TestItem>() {
                    @Override
                    public void call(TestItem testItem) {
                        // Get Operation should be subscribed to changes of tables from query
                        verify(storIOSQLite).observeChanges();
                        verifyRawQueryBehavior(testItem);
                    }
                })
                .checkBehaviorOfObservable();

        assertThat(rawQuery.observesTables()).isNotNull();
    }

    void verifyRawQueryBehavior(@NonNull Single<TestItem> single) {
        new ObservableBehaviorChecker<TestItem>()
                .observable(single.toObservable())
                .expectedNumberOfEmissions(1)
                .testAction(new Action1<TestItem>() {
                    @Override
                    public void call(TestItem testItem) {
                       verifyRawQueryBehavior(testItem);
                    }
                })
                .checkBehaviorOfObservable();

        assertThat(rawQuery.observesTables()).isNotNull();
    }
}
