package com.pushtorefresh.storio.contentresolver.operation.get;

import android.database.Cursor;

import com.pushtorefresh.storio.contentresolver.StorIOContentResolver;
import com.pushtorefresh.storio.contentresolver.query.Query;

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

    @Test
    public void getCursorBlocking() {
        final GetStub getStub = new GetStub();

        final Cursor cursor = getStub.storIOContentResolver
                .get()
                .cursor()
                .withQuery(getStub.query)
                .withGetResolver(getStub.getResolverForCursor)
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
                .withGetResolver(getStub.getResolverForTestItems)
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
                .withGetResolver(getStub.getResolverForCursor)
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
                .withGetResolver(getStub.getResolverForTestItems)
                .prepare()
                .createObservable()
                .toBlocking()
                .last();

        getStub.verifyQueryBehaviorForList(testItems);
    }

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
        final Query query;
        final GetResolver<TestItem> getResolverForTestItems;
        final GetResolver<Cursor> getResolverForCursor;
        final Cursor cursor;
        final List<TestItem> testItems;
        private final StorIOContentResolver.Internal internal;

        @SuppressWarnings("unchecked")
        GetStub() {
            storIOContentResolver = mock(StorIOContentResolver.class);
            internal = mock(StorIOContentResolver.Internal.class);
            query = mock(Query.class);
            getResolverForTestItems = mock(GetResolver.class);
            getResolverForCursor = mock(GetResolver.class);
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

            when(cursor.getCount())
                    .thenReturn(testItems.size());

            when(storIOContentResolver.get())
                    .thenReturn(new PreparedGet.Builder(storIOContentResolver));

            when(getResolverForTestItems.performGet(storIOContentResolver, query))
                    .thenReturn(cursor);

            when(getResolverForCursor.performGet(storIOContentResolver, query))
                    .thenReturn(cursor);

            when(getResolverForTestItems.mapFromCursor(cursor))
                    .thenAnswer(new Answer<TestItem>() {
                        int invocationsCount = 0;

                        @Override
                        public TestItem answer(InvocationOnMock invocation) throws Throwable {
                            final TestItem testItem = testItems.get(invocationsCount);
                            invocationsCount++;
                            return testItem;
                        }
                    });

            when(getResolverForCursor.mapFromCursor(cursor))
                    .thenReturn(cursor);
        }

        void verifyQueryBehavior(Cursor actualCursor) {
            verify(storIOContentResolver, times(1)).get();
            verify(getResolverForCursor, times(1)).performGet(storIOContentResolver, query);
            assertSame(cursor, actualCursor);
        }

        private void verifyQueryBehaviorForList(List<TestItem> actualList) {
            verify(storIOContentResolver, times(1)).get();
            verify(getResolverForTestItems, times(1)).performGet(storIOContentResolver, query);
            verify(getResolverForTestItems, times(testItems.size())).mapFromCursor(cursor);
            assertEquals(testItems, actualList);
        }
    }
}
