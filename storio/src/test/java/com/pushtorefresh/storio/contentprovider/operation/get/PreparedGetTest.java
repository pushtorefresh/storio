package com.pushtorefresh.storio.contentprovider.operation.get;

import android.database.Cursor;

import com.pushtorefresh.storio.contentprovider.StorIOContentProvider;
import com.pushtorefresh.storio.contentprovider.query.Query;
import com.pushtorefresh.storio.operation.MapFunc;

import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class PreparedGetTest {

    private static class TestItem {
        private String someField;

        public String getSomeField() {
            return someField;
        }
    }

    private static class GetStub {
        final StorIOContentProvider storIOContentProvider;
        final Query query;
        final GetResolver getResolver;
        final MapFunc<Cursor, TestItem> mapFunc;
        final Cursor cursor;
        final int numberOfMockObjects = 3;

        @SuppressWarnings("unchecked")
        GetStub() {
            storIOContentProvider = mock(StorIOContentProvider.class);
            query = mock(Query.class);
            getResolver = mock(GetResolver.class);
            mapFunc = (MapFunc<Cursor, TestItem>) mock(MapFunc.class);
            cursor = mock(Cursor.class);

            when(cursor.moveToNext()).thenAnswer(new Answer<Boolean>() {
                int invocationsCount = 0;

                @Override
                public Boolean answer(InvocationOnMock invocation) throws Throwable {
                    return invocationsCount++ < numberOfMockObjects;
                }
            });

            when(storIOContentProvider.get())
                    .thenReturn(new PreparedGet.Builder(storIOContentProvider));

            when(getResolver.performGet(storIOContentProvider, query))
                    .thenReturn(cursor);

            when(mapFunc.map(cursor))
                    .thenReturn(mock(TestItem.class));
        }

        void verifyQueryBehavior() {
            verify(storIOContentProvider, times(1)).get();
            verify(getResolver, times(1)).performGet(storIOContentProvider, query);
        }

        private void verifyQueryBehaviorForList() {
            verify(storIOContentProvider, times(1)).get();
            verify(getResolver, times(1)).performGet(storIOContentProvider, query);
            verify(mapFunc, times(numberOfMockObjects)).map(cursor);
        }
    }

    @Test
    public void getCursorBlocking() {
        final GetStub getStub = new GetStub();

        getStub.storIOContentProvider
                .get()
                .cursor()
                .withQuery(getStub.query)
                .withGetResolver(getStub.getResolver)
                .prepare()
                .executeAsBlocking();

        getStub.verifyQueryBehavior();
    }

    @Test
    public void getListOfObjectsBlocking() {
        final GetStub getStub = new GetStub();

        getStub.storIOContentProvider
                .get()
                .listOfObjects(TestItem.class)
                .withMapFunc(getStub.mapFunc)
                .withQuery(getStub.query)
                .withGetResolver(getStub.getResolver)
                .prepare()
                .executeAsBlocking();

        getStub.verifyQueryBehaviorForList();
    }

    @Test
    public void getCursorObservable() {
        final GetStub getStub = new GetStub();

        getStub.storIOContentProvider
                .get()
                .cursor()
                .withQuery(getStub.query)
                .withGetResolver(getStub.getResolver)
                .prepare()
                .createObservable()
                .toBlocking()
                .last();

        getStub.verifyQueryBehavior();
    }

    @Test
    public void getListOfObjectsObservable() {
        final GetStub getStub = new GetStub();

        getStub.storIOContentProvider
                .get()
                .listOfObjects(TestItem.class)
                .withMapFunc(getStub.mapFunc)
                .withQuery(getStub.query)
                .withGetResolver(getStub.getResolver)
                .prepare()
                .createObservable()
                .toBlocking()
                .last();

        getStub.verifyQueryBehaviorForList();
    }
}
