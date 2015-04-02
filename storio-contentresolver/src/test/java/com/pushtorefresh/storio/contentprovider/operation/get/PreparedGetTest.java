package com.pushtorefresh.storio.contentprovider.operation.get;

import android.database.Cursor;

import com.pushtorefresh.storio.contentprovider.StorIOContentProvider;
import com.pushtorefresh.storio.contentprovider.query.Query;
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
        final StorIOContentProvider storIOContentProvider;
        final Query query;
        final GetResolver getResolver;
        final MapFunc<Cursor, TestItem> mapFunc;
        final Cursor cursor;
        final List<TestItem> testItems;

        @SuppressWarnings("unchecked")
        GetStub() {
            storIOContentProvider = mock(StorIOContentProvider.class);
            query = mock(Query.class);
            getResolver = mock(GetResolver.class);
            mapFunc = (MapFunc<Cursor, TestItem>) mock(MapFunc.class);
            cursor = mock(Cursor.class);

            testItems = new ArrayList<>();
            testItems.add(new TestItem());
            testItems.add(new TestItem());
            testItems.add(new TestItem());

            when(cursor.moveToNext())
                    .thenAnswer(new Answer<Boolean>() {
                        int invocationsCount = 0;

                        @Override
                        public Boolean answer(InvocationOnMock invocation) throws Throwable {
                            return invocationsCount++ < testItems.size();
                        }
                    });

            when(storIOContentProvider.get())
                    .thenReturn(new PreparedGet.Builder(storIOContentProvider));

            when(getResolver.performGet(storIOContentProvider, query))
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
            verify(storIOContentProvider, times(1)).get();
            verify(getResolver, times(1)).performGet(storIOContentProvider, query);
            assertSame(cursor, actualCursor);
        }

        private void verifyQueryBehaviorForList(List<TestItem> actualList) {
            verify(storIOContentProvider, times(1)).get();
            verify(getResolver, times(1)).performGet(storIOContentProvider, query);
            verify(mapFunc, times(testItems.size())).map(cursor);
            assertEquals(testItems, actualList);
        }
    }

    @Test
    public void getCursorBlocking() {
        final GetStub getStub = new GetStub();

        final Cursor cursor = getStub.storIOContentProvider
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

        final List<TestItem> testItems = getStub.storIOContentProvider
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

        final Cursor cursor = getStub.storIOContentProvider
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

        final List<TestItem> testItems = getStub.storIOContentProvider
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
}
