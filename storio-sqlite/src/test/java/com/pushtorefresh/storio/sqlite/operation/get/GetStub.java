package com.pushtorefresh.storio.sqlite.operation.get;

import android.database.Cursor;
import android.support.annotation.NonNull;

import com.pushtorefresh.storio.sqlite.Changes;
import com.pushtorefresh.storio.sqlite.StorIOSQLite;
import com.pushtorefresh.storio.sqlite.query.Query;
import com.pushtorefresh.storio.sqlite.query.RawQuery;
import com.pushtorefresh.storio.test.ObservableBehaviorChecker;

import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import rx.Observable;
import rx.functions.Action1;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertSame;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class GetStub {
    final StorIOSQLite storIOSQLite;
    final Query query;
    final RawQuery rawQuery;
    final GetResolver<TestItem> getResolverForObject;
    final GetResolver<Cursor> getResolverForCursor;
    final Cursor cursor;
    final List<TestItem> testItems;
    private final StorIOSQLite.Internal internal;

    @SuppressWarnings("unchecked")
    GetStub() {
        storIOSQLite = mock(StorIOSQLite.class);
        internal = mock(StorIOSQLite.Internal.class);

        when(storIOSQLite.internal())
                .thenReturn(internal);

        query = new Query.Builder()
                .table("test_table")
                .build();

        rawQuery = new RawQuery.Builder()
                .query("test sql")
                .observesTables("test_table")
                .build();

        getResolverForObject = mock(GetResolver.class);
        getResolverForCursor = mock(GetResolver.class);
        cursor = mock(Cursor.class);

        testItems = new ArrayList<TestItem>();
        testItems.add(new TestItem());
        testItems.add(new TestItem());
        testItems.add(new TestItem());

        when(cursor.moveToNext()).thenAnswer(new Answer<Boolean>() {
            int invocationsCount = 0;

            @Override
            public Boolean answer(InvocationOnMock invocation) throws Throwable {
                return invocationsCount++ < testItems.size();
            }
        });

        when(storIOSQLite.get())
                .thenReturn(new PreparedGet.Builder(storIOSQLite));

        when(storIOSQLite.observeChangesInTables(eq(Collections.singleton(query.table()))))
                .thenReturn(Observable.<Changes>empty());

        assertNotNull(rawQuery.observesTables());

        when(storIOSQLite.observeChangesInTables(rawQuery.observesTables()))
                .thenReturn(Observable.<Changes>empty());

        when(getResolverForObject.performGet(storIOSQLite, query))
                .thenReturn(cursor);

        when(getResolverForObject.performGet(storIOSQLite, rawQuery))
                .thenReturn(cursor);

        when(getResolverForCursor.performGet(storIOSQLite, query))
                .thenReturn(cursor);

        when(getResolverForCursor.performGet(storIOSQLite, rawQuery))
                .thenReturn(cursor);

        when(getResolverForObject.mapFromCursor(cursor))
                .thenAnswer(new Answer<TestItem>() {
                    int invocationsCount = 0;

                    @Override
                    public TestItem answer(InvocationOnMock invocation) throws Throwable {
                        final TestItem testItem = testItems.get(invocationsCount);
                        invocationsCount++;
                        return testItem;
                    }
                });
    }

    void verifyQueryBehaviorForCursor(@NonNull Cursor actualCursor) {
        assertNotNull(actualCursor);
        verify(storIOSQLite, times(1)).get();
        verify(getResolverForCursor, times(1)).performGet(storIOSQLite, query);
        assertSame(cursor, actualCursor);
    }

    void verifyQueryBehaviorForCursor(@NonNull Observable<Cursor> observable) {
        new ObservableBehaviorChecker<Cursor>()
                .observable(observable)
                .expectedNumberOfEmissions(1)
                .testAction(new Action1<Cursor>() {
                    @Override
                    public void call(Cursor cursor) {
                        verifyQueryBehaviorForCursor(cursor);
                    }
                })
                .checkBehaviorOfObservable();

        verify(storIOSQLite, times(1)).observeChangesInTables(eq(Collections.singleton(query.table())));
    }

    void verifyQueryBehaviorForList(@NonNull List<TestItem> actualList) {
        assertNotNull(actualList);
        verify(storIOSQLite, times(1)).get();
        verify(getResolverForObject, times(1)).performGet(storIOSQLite, query);
        verify(getResolverForObject, times(testItems.size())).mapFromCursor(cursor);
        verify(cursor, times(1)).close();
        assertEquals(testItems, actualList);
    }

    void verifyQueryBehaviorForList(@NonNull Observable<List<TestItem>> observable) {
        new ObservableBehaviorChecker<List<TestItem>>()
                .observable(observable)
                .expectedNumberOfEmissions(1)
                .testAction(new Action1<List<TestItem>>() {
                    @Override
                    public void call(List<TestItem> testItems) {
                        verifyQueryBehaviorForList(testItems);
                    }
                })
                .checkBehaviorOfObservable();

        verify(storIOSQLite, times(1)).observeChangesInTables(eq(Collections.singleton(query.table())));
    }

    void verifyRawQueryBehaviorForCursor(@NonNull Cursor actualCursor) {
        assertNotNull(actualCursor);
        verify(storIOSQLite, times(1)).get();
        verify(getResolverForCursor, times(1)).performGet(storIOSQLite, rawQuery);
        assertSame(cursor, actualCursor);
    }

    void verifyRawQueryBehaviorForCursor(@NonNull Observable<Cursor> observable) {
        new ObservableBehaviorChecker<Cursor>()
                .observable(observable)
                .expectedNumberOfEmissions(1)
                .testAction(new Action1<Cursor>() {
                    @Override
                    public void call(Cursor cursor) {
                        verifyRawQueryBehaviorForCursor(cursor);
                    }
                })
                .checkBehaviorOfObservable();

        assertNotNull(rawQuery.observesTables());
        verify(storIOSQLite, times(1)).observeChangesInTables(rawQuery.observesTables());
    }

    void verifyRawQueryBehaviorForList(@NonNull List<TestItem> actualList) {
        assertNotNull(actualList);
        verify(storIOSQLite, times(1)).get();
        verify(getResolverForObject, times(1)).performGet(storIOSQLite, rawQuery);
        verify(getResolverForObject, times(testItems.size())).mapFromCursor(cursor);
        verify(cursor, times(1)).close();
        assertEquals(testItems, actualList);
    }

    void verifyRawQueryBehaviorForList(@NonNull Observable<List<TestItem>> observable) {
        new ObservableBehaviorChecker<List<TestItem>>()
                .observable(observable)
                .expectedNumberOfEmissions(1)
                .testAction(new Action1<List<TestItem>>() {
                    @Override
                    public void call(List<TestItem> testItems) {
                        verifyRawQueryBehaviorForList(testItems);
                    }
                })
                .checkBehaviorOfObservable();

        assertNotNull(rawQuery.observesTables());
        verify(storIOSQLite, times(1)).observeChangesInTables(rawQuery.observesTables());
    }
}
