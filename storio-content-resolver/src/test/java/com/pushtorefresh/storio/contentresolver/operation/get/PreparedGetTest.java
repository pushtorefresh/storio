package com.pushtorefresh.storio.contentresolver.operation.get;

import android.database.Cursor;

import com.pushtorefresh.storio.contentresolver.StorIOContentResolver;
import com.pushtorefresh.storio.contentresolver.query.Query;
import com.pushtorefresh.storio.operation.MapFunc;

import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertSame;
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
        final StorIOContentResolver storIOContentResolver;
        private final StorIOContentResolver.Internal internal;
        final Query query;
        final GetResolver getResolver;
        final MapFunc<Cursor, TestItem> mapFunc;
        final Cursor cursor;
        final List<TestItem> testItems;

        @SuppressWarnings("unchecked")
        GetStub() {
            storIOContentResolver = mock(StorIOContentResolver.class);
            internal = mock(StorIOContentResolver.Internal.class);
            query = mock(Query.class);
            getResolver = mock(GetResolver.class);
            mapFunc = (MapFunc<Cursor, TestItem>) mock(MapFunc.class);
            cursor = mock(Cursor.class);

            testItems = new ArrayList<TestItem>();
            testItems.add(new TestItem());
            testItems.add(new TestItem());
            testItems.add(new TestItem());

            when(storIOContentResolver.internal())
                    .thenReturn(internal);

            when(cursor.moveToNext())
                    .thenAnswer(new Answer<Boolean>() {
                        int invocationsCount = 0;

                        @Override
                        public Boolean answer(InvocationOnMock invocation) throws Throwable {
                            return invocationsCount++ < testItems.size();
                        }
                    });

            when(storIOContentResolver.get())
                    .thenReturn(new PreparedGet.Builder(storIOContentResolver));

            when(getResolver.performGet(storIOContentResolver, query))
                    .thenReturn(cursor);

            when(mapFunc.map(cursor))
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

        void verifyQueryBehavior(Cursor actualCursor) {
            verify(storIOContentResolver, times(1)).get();
            verify(getResolver, times(1)).performGet(storIOContentResolver, query);
            assertSame(cursor, actualCursor);
        }

        private void verifyQueryBehaviorForList(List<TestItem> actualList) {
            verify(storIOContentResolver, times(1)).get();
            verify(getResolver, times(1)).performGet(storIOContentResolver, query);
            verify(mapFunc, times(testItems.size())).map(cursor);
            assertEquals(testItems, actualList);
        }
    }

    @Test
    public void getCursorBlocking() {
        final GetStub getStub = new GetStub();

        final Cursor cursor = getStub.storIOContentResolver
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

        final List<TestItem> testItems = getStub.storIOContentResolver
                .get()
                .listOfObjects(TestItem.class)
                .withQuery(getStub.query)
                .withMapFunc(getStub.mapFunc)
                .withGetResolver(getStub.getResolver)
                .prepare()
                .executeAsBlocking();

        getStub.verifyQueryBehaviorForList(testItems);
    }

    @Test
    public void getCursorObservable() {
        final GetStub getStub = new GetStub();

        final Cursor cursor = getStub.storIOContentResolver
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

        final List<TestItem> testItems = getStub.storIOContentResolver
                .get()
                .listOfObjects(TestItem.class)
                .withQuery(getStub.query)
                .withMapFunc(getStub.mapFunc)
                .withGetResolver(getStub.getResolver)
                .prepare()
                .createObservable()
                .toBlocking()
                .last();

        getStub.verifyQueryBehaviorForList(testItems);
    }
}
