package com.pushtorefresh.storio.sqlitedb.operation.get;

import android.database.Cursor;

import com.pushtorefresh.storio.sqlitedb.StorIOSQLiteDb;
import com.pushtorefresh.storio.sqlitedb.query.Query;
import com.pushtorefresh.storio.sqlitedb.query.RawQuery;
import com.pushtorefresh.storio.operation.MapFunc;

import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertSame;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class PreparedGetTest {

    private static class TestItem {

        private static final AtomicLong COUNTER = new AtomicLong(0);

        private Long id = COUNTER.incrementAndGet();

        public Long getId() {
            return id;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            TestItem testItem = (TestItem) o;

            return !(id != null ? !id.equals(testItem.id) : testItem.id != null);

        }

        @Override
        public int hashCode() {
            return id != null ? id.hashCode() : 0;
        }
    }

    private static class GetStub {
        final StorIOSQLiteDb storIOSQLiteDb;
        final Query query;
        final RawQuery rawQuery;
        final GetResolver getResolver;
        final MapFunc<Cursor, TestItem> mapFunc;
        final Cursor cursor;
        final List<TestItem> testItems;

        @SuppressWarnings("unchecked")
        GetStub() {
            storIOSQLiteDb = mock(StorIOSQLiteDb.class);
            query = mock(Query.class);
            rawQuery = mock(RawQuery.class);
            getResolver = mock(GetResolver.class);
            mapFunc = (MapFunc<Cursor, TestItem>) mock(MapFunc.class);
            cursor = mock(Cursor.class);

            testItems = new ArrayList<>();
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

            when(storIOSQLiteDb.get())
                    .thenReturn(new PreparedGet.Builder(storIOSQLiteDb));

            when(getResolver.performGet(storIOSQLiteDb, query))
                    .thenReturn(cursor);

            when(getResolver.performGet(storIOSQLiteDb, rawQuery))
                    .thenReturn(cursor);

            when(mapFunc.map(any(Cursor.class)))
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

        private void verifyQueryBehavior(Cursor actualCursor) {
            verify(storIOSQLiteDb, times(1)).get();
            verify(getResolver, times(1)).performGet(storIOSQLiteDb, query);
            assertSame(cursor, actualCursor);
        }

        private void verifyQueryBehaviorForList(List<TestItem> actualList) {
            verify(storIOSQLiteDb, times(1)).get();
            verify(getResolver, times(1)).performGet(storIOSQLiteDb, query);
            verify(mapFunc, times(testItems.size())).map(cursor);
            assertEquals(testItems, actualList);
        }

        private void verifyRawQueryBehavior(Cursor actualCursor) {
            verify(storIOSQLiteDb, times(1)).get();
            verify(getResolver, times(1)).performGet(storIOSQLiteDb, rawQuery);
            assertSame(cursor, actualCursor);
        }

        private void verifyRawQueryBehaviorForList(List<TestItem> actualList) {
            verify(storIOSQLiteDb, times(1)).get();
            verify(getResolver, times(1)).performGet(storIOSQLiteDb, rawQuery);
            verify(mapFunc, times(testItems.size())).map(cursor);
            assertEquals(testItems, actualList);
        }
    }

    @Test
    public void getCursorBlocking() {
        final GetStub getStub = new GetStub();

        final Cursor cursor = getStub.storIOSQLiteDb
                .get()
                .cursor()
                .withQuery(getStub.query)
                .withGetResolver(getStub.getResolver)
                .prepare()
                .executeAsBlocking();

        getStub.verifyQueryBehavior(cursor);
    }

    @Test
    public void getListOfObjectsBlocking() {
        final GetStub getStub = new GetStub();

        final List<TestItem> testItems = getStub.storIOSQLiteDb
                .get()
                .listOfObjects(TestItem.class)
                .withMapFunc(getStub.mapFunc)
                .withQuery(getStub.query)
                .withGetResolver(getStub.getResolver)
                .prepare()
                .executeAsBlocking();

        getStub.verifyQueryBehaviorForList(testItems);
    }

    @Test
    public void getCursorObservable() {
        final GetStub getStub = new GetStub();

        final Cursor cursor = getStub.storIOSQLiteDb
                .get()
                .cursor()
                .withQuery(getStub.query)
                .withGetResolver(getStub.getResolver)
                .prepare()
                .createObservable()
                .toBlocking()
                .last();

        getStub.verifyQueryBehavior(cursor);
    }


    @Test
    public void getListOfObjectsObservable() {
        final GetStub getStub = new GetStub();

        final List<TestItem> testItems = getStub.storIOSQLiteDb
                .get()
                .listOfObjects(TestItem.class)
                .withMapFunc(getStub.mapFunc)
                .withQuery(getStub.query)
                .withGetResolver(getStub.getResolver)
                .prepare()
                .createObservable()
                .toBlocking()
                .last();

        getStub.verifyQueryBehaviorForList(testItems);
    }

    @Test
    public void getCursorWithRawQueryBlocking() {
        final GetStub getStub = new GetStub();

        final Cursor cursor = getStub.storIOSQLiteDb
                .get()
                .cursor()
                .withQuery(getStub.rawQuery)
                .withGetResolver(getStub.getResolver)
                .prepare()
                .executeAsBlocking();

        getStub.verifyRawQueryBehavior(cursor);
    }

    @Test
    public void getCursorWithRawQueryObservable() {
        final GetStub getStub = new GetStub();

        final Cursor cursor = getStub.storIOSQLiteDb
                .get()
                .cursor()
                .withQuery(getStub.rawQuery)
                .withGetResolver(getStub.getResolver)
                .prepare()
                .createObservable()
                .toBlocking()
                .last();

        getStub.verifyRawQueryBehavior(cursor);
    }

    @Test
    public void getListOfObjectsWithRawQueryBlocking() {
        final GetStub getStub = new GetStub();

        final List<TestItem> testItems = getStub.storIOSQLiteDb
                .get()
                .listOfObjects(TestItem.class)
                .withMapFunc(getStub.mapFunc)
                .withQuery(getStub.rawQuery)
                .withGetResolver(getStub.getResolver)
                .prepare()
                .executeAsBlocking();

        getStub.verifyRawQueryBehaviorForList(testItems);
    }

    @Test
    public void getListOfObjectsWithRawQueryObservable() {
        final GetStub getStub = new GetStub();

        final List<TestItem> testItems = getStub.storIOSQLiteDb
                .get()
                .listOfObjects(TestItem.class)
                .withMapFunc(getStub.mapFunc)
                .withQuery(getStub.rawQuery)
                .withGetResolver(getStub.getResolver)
                .prepare()
                .createObservable()
                .toBlocking()
                .last();

        getStub.verifyRawQueryBehaviorForList(testItems);
    }
}
