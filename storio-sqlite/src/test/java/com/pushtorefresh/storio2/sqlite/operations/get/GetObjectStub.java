package com.pushtorefresh.storio2.sqlite.operations.get;

import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.pushtorefresh.storio2.sqlite.Changes;
import com.pushtorefresh.storio2.sqlite.SQLiteTypeMapping;
import com.pushtorefresh.storio2.sqlite.StorIOSQLite;
import com.pushtorefresh.storio2.sqlite.queries.Query;
import com.pushtorefresh.storio2.sqlite.queries.RawQuery;
import com.pushtorefresh.storio2.test.FlowableBehaviorChecker;

import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.Single;
import io.reactivex.functions.Consumer;

import static io.reactivex.BackpressureStrategy.LATEST;
import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

class GetObjectStub {

    @NonNull
    final StorIOSQLite storIOSQLite;

    @NonNull
    private final StorIOSQLite.LowLevel lowLevel;

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
        lowLevel = mock(StorIOSQLite.LowLevel.class);

        when(storIOSQLite.lowLevel())
                .thenReturn(lowLevel);

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

        when(storIOSQLite.observeChanges(any(BackpressureStrategy.class))).thenReturn(Flowable.<Changes>empty());

        assertThat(rawQuery.observesTables()).isNotNull();

        when(getResolver.performGet(storIOSQLite, query))
                .thenReturn(cursor);

        when(getResolver.performGet(storIOSQLite, rawQuery))
                .thenReturn(cursor);

        when(getResolver.mapFromCursor(storIOSQLite, cursor))
                .thenReturn(item);

        //noinspection unchecked
        typeMapping = mock(SQLiteTypeMapping.class);

        if (withTypeMapping) {
            when(lowLevel.typeMapping(TestItem.class)).thenReturn(typeMapping);
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
        verify(storIOSQLite).interceptors();

        // should be called once
        verify(storIOSQLite).get();

        // should be called only once
        verify(getResolver).performGet(storIOSQLite, query);

        // should be called only once
        verify(getResolver).mapFromCursor(storIOSQLite, cursor);

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
            verify(lowLevel).typeMapping(TestItem.class);

            // should be called only once because of Performance!
            verify(typeMapping).getResolver();
        }

        verifyNoMoreInteractions(storIOSQLite, lowLevel, cursor);
    }

    void verifyQueryBehavior(@NonNull Flowable<TestItem> flowable) {
        new FlowableBehaviorChecker<TestItem>()
                .flowable(flowable)
                .expectedNumberOfEmissions(1)
                .testAction(new Consumer<TestItem>() {
                    @Override
                    public void accept(TestItem testItem) {
                        // Get Operation should be subscribed to changes of tables from query
                        verify(storIOSQLite).observeChanges(LATEST);
                        verify(storIOSQLite).defaultRxScheduler();
                        verifyQueryBehavior(testItem);
                    }
                })
                .checkBehaviorOfFlowable();
    }

    void verifyQueryBehavior(@NonNull Single<TestItem> single) {
        new FlowableBehaviorChecker<TestItem>()
                .flowable(single.toFlowable())
                .expectedNumberOfEmissions(1)
                .testAction(new Consumer<TestItem>() {
                    @Override
                    public void accept(TestItem testItem) {
                        verify(storIOSQLite).defaultRxScheduler();
                        verifyQueryBehavior(testItem);
                    }
                })
                .checkBehaviorOfFlowable();
    }

    void verifyRawQueryBehavior(@Nullable TestItem actualItem) {
        verify(storIOSQLite).get();
        verify(getResolver).performGet(storIOSQLite, rawQuery);
        verify(getResolver).mapFromCursor(storIOSQLite, cursor);
        verify(cursor).close();
        assertThat(actualItem).isEqualTo(item);
    }

    void verifyRawQueryBehavior(@NonNull Flowable<TestItem> flowable) {
        new FlowableBehaviorChecker<TestItem>()
                .flowable(flowable)
                .expectedNumberOfEmissions(1)
                .testAction(new Consumer<TestItem>() {
                    @Override
                    public void accept(TestItem testItem) {
                        // Get Operation should be subscribed to changes of tables from query
                        verify(storIOSQLite).observeChanges(LATEST);
                        verifyRawQueryBehavior(testItem);
                    }
                })
                .checkBehaviorOfFlowable();

        assertThat(rawQuery.observesTables()).isNotNull();
    }

    void verifyRawQueryBehavior(@NonNull Single<TestItem> single) {
        new FlowableBehaviorChecker<TestItem>()
                .flowable(single.toFlowable())
                .expectedNumberOfEmissions(1)
                .testAction(new Consumer<TestItem>() {
                    @Override
                    public void accept(TestItem testItem) {
                       verifyRawQueryBehavior(testItem);
                    }
                })
                .checkBehaviorOfFlowable();

        assertThat(rawQuery.observesTables()).isNotNull();
    }
}
