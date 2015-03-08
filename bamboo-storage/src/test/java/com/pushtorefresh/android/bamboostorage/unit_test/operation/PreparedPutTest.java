package com.pushtorefresh.android.bamboostorage.unit_test.operation;

import android.content.ContentValues;

import com.pushtorefresh.android.bamboostorage.BambooStorage;
import com.pushtorefresh.android.bamboostorage.operation.MapFunc;
import com.pushtorefresh.android.bamboostorage.operation.put.PreparedPut;
import com.pushtorefresh.android.bamboostorage.operation.put.PutCollectionResult;
import com.pushtorefresh.android.bamboostorage.operation.put.PutResolver;
import com.pushtorefresh.android.bamboostorage.operation.put.PutResult;
import com.pushtorefresh.android.bamboostorage.unit_test.design.User;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class PreparedPutTest {

    // stub class to avoid violation of DRY in "putOne" tests
    private static class PutOneStub {
        final User user;
        final BambooStorage bambooStorage;
        final MapFunc<User, ContentValues> mapFunc;
        final PutResolver<User> putResolver;

        PutOneStub() {
            user = new User(null, "test@example.com");
            bambooStorage = mock(BambooStorage.class);

            when(bambooStorage.put())
                    .thenReturn(new PreparedPut.Builder(bambooStorage));

            //noinspection unchecked
            mapFunc = (MapFunc<User, ContentValues>) mock(MapFunc.class);

            //noinspection unchecked
            putResolver = (PutResolver<User>) mock(PutResolver.class);

            when(putResolver.performPut(eq(bambooStorage), any(ContentValues.class)))
                    .thenReturn(mock(PutResult.class));

            when(mapFunc.map(user))
                    .thenReturn(mock(ContentValues.class));

        }
    }

    @Test public void putOneBlocking() {
        final PutOneStub putOneStub = new PutOneStub();

        final PutResult putResult = putOneStub.bambooStorage
                .put()
                .object(putOneStub.user)
                .withMapFunc(putOneStub.mapFunc)
                .withPutResolver(putOneStub.putResolver)
                .prepare()
                .executeAsBlocking();

        verify(putOneStub.bambooStorage, times(1)).put();
        verify(putOneStub.mapFunc, times(1)).map(putOneStub.user);
        verify(putOneStub.putResolver, times(1)).performPut(eq(putOneStub.bambooStorage), any(ContentValues.class));
        verify(putOneStub.putResolver, times(1)).afterPut(putOneStub.user, putResult);
    }

    @Test public void putOneObservable() {
        final PutOneStub putOneStub = new PutOneStub();

        final PutResult putResult = putOneStub.bambooStorage
                .put()
                .object(putOneStub.user)
                .withMapFunc(putOneStub.mapFunc)
                .withPutResolver(putOneStub.putResolver)
                .prepare()
                .createObservable()
                .toBlocking()
                .last();

        verify(putOneStub.bambooStorage, times(1)).put();
        verify(putOneStub.mapFunc, times(1)).map(putOneStub.user);
        verify(putOneStub.putResolver, times(1)).performPut(eq(putOneStub.bambooStorage), any(ContentValues.class));
        verify(putOneStub.putResolver, times(1)).afterPut(putOneStub.user, putResult);
    }

    // stub class to avoid violation of DRY in "putMultiple" tests
    private static class PutMultipleStub {
        final List<User> users;
        final BambooStorage bambooStorage;
        final MapFunc<User, ContentValues> mapFunc;
        final PutResolver<User> putResolver;

        PutMultipleStub() {
            users = new ArrayList<>();
            users.add(new User(null, "1"));
            users.add(new User(null, "2"));
            users.add(new User(null, "3"));

            bambooStorage = mock(BambooStorage.class);

            when(bambooStorage.put())
                    .thenReturn(new PreparedPut.Builder(bambooStorage));

            //noinspection unchecked
            putResolver = (PutResolver<User>) mock(PutResolver.class);

            when(putResolver.performPut(eq(bambooStorage), any(ContentValues.class)))
                    .thenReturn(mock(PutResult.class));

            //noinspection unchecked
            mapFunc = (MapFunc<User, ContentValues>) mock(MapFunc.class);

            when(mapFunc.map(users.get(0)))
                    .thenReturn(mock(ContentValues.class));

            when(mapFunc.map(users.get(1)))
                    .thenReturn(mock(ContentValues.class));

            when(mapFunc.map(users.get(2)))
                    .thenReturn(mock(ContentValues.class));
        }
    }

    @Test public void putMultipleBlocking() {
        final PutMultipleStub putMultipleStub = new PutMultipleStub();

        final PutCollectionResult<User> putCollectionResult = putMultipleStub.bambooStorage
                .put()
                .objects(putMultipleStub.users)
                .withMapFunc(putMultipleStub.mapFunc)
                .withPutResolver(putMultipleStub.putResolver)
                .prepare()
                .executeAsBlocking();

        verify(putMultipleStub.bambooStorage, times(1)).put();
        verify(putMultipleStub.putResolver, times(3)).performPut(eq(putMultipleStub.bambooStorage), any(ContentValues.class));

        for (User user : putMultipleStub.users) {
            verify(putMultipleStub.mapFunc, times(1)).map(user);

            verify(putMultipleStub.putResolver, times(1))
                    .afterPut(user, putCollectionResult.getResults().get(user));
        }
    }

    @Test public void putMultipleObservable() {
        final PutMultipleStub putMultipleStub = new PutMultipleStub();

        final PutCollectionResult<User> putCollectionResult = putMultipleStub.bambooStorage
                .put()
                .objects(putMultipleStub.users)
                .withMapFunc(putMultipleStub.mapFunc)
                .withPutResolver(putMultipleStub.putResolver)
                .prepare()
                .createObservable()
                .toBlocking()
                .last();

        verify(putMultipleStub.bambooStorage, times(1)).put();
        verify(putMultipleStub.putResolver, times(3)).performPut(eq(putMultipleStub.bambooStorage), any(ContentValues.class));

        for (User user : putMultipleStub.users) {
            verify(putMultipleStub.mapFunc, times(1)).map(user);

            verify(putMultipleStub.putResolver, times(1))
                    .afterPut(user, putCollectionResult.getResults().get(user));
        }
    }
}
