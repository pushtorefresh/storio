package com.pushtorefresh.storio.db.unit_test.operation.get;

import android.database.Cursor;

import com.pushtorefresh.storio.db.StorIODb;
import com.pushtorefresh.storio.operation.MapFunc;
import com.pushtorefresh.storio.db.operation.get.GetResolver;
import com.pushtorefresh.storio.db.operation.get.PreparedGet;
import com.pushtorefresh.storio.db.query.Query;
import com.pushtorefresh.storio.db.query.RawQuery;
import com.pushtorefresh.storio.db.unit_test.design.User;

import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class PreparedGetTest {

    private static class GetStub {
        final StorIODb storIODb;
        final Query query;
        final RawQuery rawQuery;
        final GetResolver getResolver;
        final MapFunc<Cursor, User> mapFunc;
        final int numberOfMockObjects = 3;

        GetStub() {
            storIODb = mock(StorIODb.class);
            query = mock(Query.class);
            rawQuery = mock(RawQuery.class);
            getResolver = mock(GetResolver.class);

            //noinspection unchecked
            mapFunc = (MapFunc<Cursor, User>) mock(MapFunc.class);

            final Cursor cursorMock = mock(Cursor.class);

            when(cursorMock.moveToNext()).thenAnswer(new Answer<Boolean>() {
                int invocationsCount = 0;

                @Override public Boolean answer(InvocationOnMock invocation) throws Throwable {
                    return invocationsCount++ < numberOfMockObjects;
                }
            });

            when(storIODb.get())
                    .thenReturn(new PreparedGet.Builder(storIODb));

            when(getResolver.performGet(storIODb, query))
                    .thenReturn(cursorMock);

            when(getResolver.performGet(storIODb, rawQuery))
                    .thenReturn(cursorMock);

            when(mapFunc.map(any(Cursor.class)))
                    .thenReturn(mock(User.class));
        }

        private void verifyQueryBehavior() {
            verify(storIODb, times(1)).get();
            verify(getResolver, times(1)).performGet(eq(storIODb), any(Query.class));
        }

        private void verifyQueryBehaviorForList() {
            verify(storIODb, times(1)).get();
            verify(getResolver, times(1)).performGet(eq(storIODb), any(Query.class));
            verify(mapFunc, times(numberOfMockObjects)).map(any(Cursor.class));
        }

        private void verifyRawQueryBehavior() {
            verify(storIODb, times(1)).get();
            verify(getResolver, times(1)).performGet(eq(storIODb), any(RawQuery.class));
        }

        private void verifyRawQueryBehaviorForList() {
            verify(storIODb, times(1)).get();
            verify(getResolver, times(1)).performGet(eq(storIODb), any(RawQuery.class));
            verify(mapFunc, times(numberOfMockObjects)).map(any(Cursor.class));
        }

    }

    @Test public void getCursorBlocking() {
        final GetStub getStub = new GetStub();

        getStub.storIODb
                .get()
                .cursor()
                .withQuery(getStub.query)
                .withGetResolver(getStub.getResolver)
                .prepare()
                .executeAsBlocking();

        getStub.verifyQueryBehavior();
    }

    @Test public void getListOfObjectsBlocking() {
        final GetStub getStub = new GetStub();

        getStub.storIODb
                .get()
                .listOfObjects(User.class)
                .withMapFunc(getStub.mapFunc)
                .withQuery(getStub.query)
                .withGetResolver(getStub.getResolver)
                .prepare()
                .executeAsBlocking();

        getStub.verifyQueryBehaviorForList();
    }

    @Test public void getCursorObservable() {
        final GetStub getStub = new GetStub();

        getStub.storIODb
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


    @Test public void getListOfObjectsObservable() {
        final GetStub getStub = new GetStub();

        getStub.storIODb
                .get()
                .listOfObjects(User.class)
                .withMapFunc(getStub.mapFunc)
                .withQuery(getStub.query)
                .withGetResolver(getStub.getResolver)
                .prepare()
                .createObservable()
                .toBlocking()
                .last();

        getStub.verifyQueryBehaviorForList();
    }

    @Test public void getCursorWithRawQueryBlocking() {
        final GetStub getStub = new GetStub();

        getStub.storIODb
                .get()
                .cursor()
                .withQuery(getStub.rawQuery)
                .withGetResolver(getStub.getResolver)
                .prepare()
                .executeAsBlocking();

        getStub.verifyRawQueryBehavior();
    }

    @Test public void getCursorWithRawQueryObservable() {
        final GetStub getStub = new GetStub();

        getStub.storIODb
                .get()
                .cursor()
                .withQuery(getStub.rawQuery)
                .withGetResolver(getStub.getResolver)
                .prepare()
                .createObservable()
                .toBlocking()
                .last();

        getStub.verifyRawQueryBehavior();
    }

    @Test public void getListOfObjectsWithRawQueryBlocking() {
        final GetStub getStub = new GetStub();

        getStub.storIODb
                .get()
                .listOfObjects(User.class)
                .withMapFunc(getStub.mapFunc)
                .withQuery(getStub.rawQuery)
                .withGetResolver(getStub.getResolver)
                .prepare()
                .executeAsBlocking();

        getStub.verifyRawQueryBehaviorForList();
    }

    @Test public void getListOfObjectsWithRawQueryObservable() {
        final GetStub getStub = new GetStub();

        getStub.storIODb
                .get()
                .listOfObjects(User.class)
                .withMapFunc(getStub.mapFunc)
                .withQuery(getStub.rawQuery)
                .withGetResolver(getStub.getResolver)
                .prepare()
                .createObservable()
                .toBlocking()
                .last();

        getStub.verifyRawQueryBehaviorForList();
    }
}
