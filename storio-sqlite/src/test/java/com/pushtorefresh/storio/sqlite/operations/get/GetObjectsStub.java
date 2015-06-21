package com.pushtorefresh.storio.sqlite.operations.get;

import android.database.Cursor;
import android.support.annotation.NonNull;

import com.pushtorefresh.storio.sqlite.Changes;
import com.pushtorefresh.storio.sqlite.SQLiteTypeMapping;
import com.pushtorefresh.storio.sqlite.StorIOSQLite;
import com.pushtorefresh.storio.sqlite.queries.Query;
import com.pushtorefresh.storio.sqlite.queries.RawQuery;
import com.pushtorefresh.storio.test.ObservableBehaviorChecker;

import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.functions.Action1;

import static com.pushtorefresh.storio.test.Asserts.assertThatListIsImmutable;
import static java.util.Collections.singleton;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

class GetObjectsStub {

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
    final List<TestItem> items;

    @NonNull
    private final SQLiteTypeMapping<TestItem> typeMapping;

    private final boolean withTypeMapping;

    @SuppressWarnings("unchecked")
    private GetObjectsStub(boolean withTypeMapping) {
        this.withTypeMapping = withTypeMapping;

        storIOSQLite = mock(StorIOSQLite.class);
        internal = mock(StorIOSQLite.Internal.class);

        when(storIOSQLite.internal())
                .thenReturn(internal);

        query = mock(Query.class);
        when(query.table()).thenReturn("test_table");

        rawQuery = mock(RawQuery.class);
        when(rawQuery.observesTables()).thenReturn(singleton("test_table"));

        getResolver = mock(GetResolver.class);
        cursor = mock(Cursor.class);

        items = new ArrayList<TestItem>();
        items.add(new TestItem());
        items.add(new TestItem());
        items.add(new TestItem());

        when(cursor.getCount())
                .thenReturn(items.size());

        when(cursor.moveToNext()).thenAnswer(new Answer<Boolean>() {
            int invocationsCount = 0;

            @Override
            public Boolean answer(InvocationOnMock invocation) throws Throwable {
                return invocationsCount++ < items.size();
            }
        });

        when(storIOSQLite.get())
                .thenReturn(new PreparedGet.Builder(storIOSQLite));

        when(storIOSQLite.observeChangesInTables(eq(singleton(query.table()))))
                .thenReturn(Observable.<Changes>empty());

        assertNotNull(rawQuery.observesTables());

        when(storIOSQLite.observeChangesInTables(rawQuery.observesTables()))
                .thenReturn(Observable.<Changes>empty());

        when(getResolver.performGet(storIOSQLite, query))
                .thenReturn(cursor);

        when(getResolver.performGet(storIOSQLite, rawQuery))
                .thenReturn(cursor);

        when(getResolver.mapFromCursor(cursor))
                .thenAnswer(new Answer<TestItem>() {
                    int invocationsCount = 0;

                    @Override
                    public TestItem answer(InvocationOnMock invocation) throws Throwable {
                        final TestItem testItem = items.get(invocationsCount);
                        invocationsCount++;
                        return testItem;
                    }
                });

        typeMapping = mock(SQLiteTypeMapping.class);

        if (withTypeMapping) {
            when(internal.typeMapping(TestItem.class)).thenReturn(typeMapping);
            when(typeMapping.getResolver()).thenReturn(getResolver);
        }
    }

    @NonNull
    static GetObjectsStub newInstanceWithoutTypeMapping() {
        return new GetObjectsStub(false);
    }

    @NonNull
    static GetObjectsStub newInstanceWithTypeMapping() {
        return new GetObjectsStub(true);
    }

    void verifyQueryBehavior(@NonNull List<TestItem> actualList) {
        // should be called once
        verify(storIOSQLite).get();

        // should be called only once
        verify(getResolver).performGet(storIOSQLite, query);

        // should be called same number of times as number of items
        verify(getResolver, times(items.size())).mapFromCursor(cursor);

        // should be called only once because of Performance!
        verify(cursor).getCount();

        // should be called same number of times as count of items in cursor + 1 (last -> false)
        verify(cursor, times(items.size() + 1)).moveToNext();

        // cursor must be closed!
        verify(cursor, times(1)).close();

        // actual items should be equals to expected
        assertEquals(items, actualList);

        // list should be immutable!
        assertThatListIsImmutable(actualList);

        if (withTypeMapping) {
            // should be called only once because of Performance!
            verify(storIOSQLite).internal();

            // should be called only once because of Performance!
            verify(internal).typeMapping(TestItem.class);

            // should be called only once because of Performance!
            verify(typeMapping).getResolver();
        }

        verifyNoMoreInteractions(storIOSQLite, internal, cursor);
    }

    void verifyQueryBehavior(@NonNull Observable<List<TestItem>> observable) {
        new ObservableBehaviorChecker<List<TestItem>>()
                .observable(observable)
                .expectedNumberOfEmissions(1)
                .testAction(new Action1<List<TestItem>>() {
                    @Override
                    public void call(List<TestItem> testItems) {
                        // Get Operation should be subscribed to changes of tables from query
                        verify(storIOSQLite).observeChangesInTables(eq(singleton(query.table())));

                        verifyQueryBehavior(testItems);
                    }
                })
                .checkBehaviorOfObservable();
    }

    void verifyRawQueryBehavior(@NonNull List<TestItem> actualList) {
        assertNotNull(actualList);
        verify(storIOSQLite, times(1)).get();
        verify(getResolver, times(1)).performGet(storIOSQLite, rawQuery);
        verify(getResolver, times(items.size())).mapFromCursor(cursor);
        verify(cursor, times(1)).close();
        assertEquals(items, actualList);
        assertThatListIsImmutable(actualList);
    }

    void verifyRawQueryBehavior(@NonNull Observable<List<TestItem>> observable) {
        new ObservableBehaviorChecker<List<TestItem>>()
                .observable(observable)
                .expectedNumberOfEmissions(1)
                .testAction(new Action1<List<TestItem>>() {
                    @Override
                    public void call(List<TestItem> testItems) {
                        // Get Operation should be subscribed to changes of tables from query
                        verify(storIOSQLite).observeChangesInTables(rawQuery.observesTables());

                        verifyRawQueryBehavior(testItems);
                    }
                })
                .checkBehaviorOfObservable();

        assertNotNull(rawQuery.observesTables());
    }
}
