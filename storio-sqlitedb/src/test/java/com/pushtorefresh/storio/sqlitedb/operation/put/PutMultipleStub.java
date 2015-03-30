package com.pushtorefresh.storio.sqlitedb.operation.put;

import android.content.ContentValues;
import android.support.annotation.NonNull;

import com.pushtorefresh.storio.operation.MapFunc;
import com.pushtorefresh.storio.sqlitedb.Changes;
import com.pushtorefresh.storio.sqlitedb.StorIOSQLiteDb;
import com.pushtorefresh.storio.sqlitedb.design.User;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

// stub class to avoid violation of DRY in tests
// TODO don't use User class
class PutMultipleStub {
    final List<User> users;
    final StorIOSQLiteDb storIOSQLiteDb;
    final StorIOSQLiteDb.Internal internal;
    final MapFunc<User, ContentValues> mapFunc;
    final PutResolver<User> putResolver;
    final boolean useTransaction;

    PutMultipleStub(boolean useTransaction) {
        this.useTransaction = useTransaction;

        users = new ArrayList<>();
        users.add(new User(null, "1"));
        users.add(new User(null, "2"));
        users.add(new User(null, "3"));

        storIOSQLiteDb = mock(StorIOSQLiteDb.class);
        internal = mock(StorIOSQLiteDb.Internal.class);

        when(internal.transactionsSupported())
                .thenReturn(useTransaction);

        when(storIOSQLiteDb.internal())
                .thenReturn(internal);

        when(storIOSQLiteDb.put())
                .thenReturn(new PreparedPut.Builder(storIOSQLiteDb));

        //noinspection unchecked
        putResolver = (PutResolver<User>) mock(PutResolver.class);

        when(putResolver.performPut(eq(storIOSQLiteDb), any(ContentValues.class)))
                .thenReturn(PutResult.newInsertResult(1, User.TABLE));

        //noinspection unchecked
        mapFunc = (MapFunc<User, ContentValues>) mock(MapFunc.class);

        when(mapFunc.map(users.get(0)))
                .thenReturn(mock(ContentValues.class));

        when(mapFunc.map(users.get(1)))
                .thenReturn(mock(ContentValues.class));

        when(mapFunc.map(users.get(2)))
                .thenReturn(mock(ContentValues.class));
    }

    void verifyBehavior(@NonNull PutCollectionResult<User> putCollectionResult) {
        // only one call to storIOSQLiteDb.put() should occur
        verify(storIOSQLiteDb, times(1)).put();

        // number of calls to putResolver's performPut() should be equal to number of objects
        verify(putResolver, times(users.size())).performPut(eq(storIOSQLiteDb), any(ContentValues.class));

        for (final User user : users) {
            // map operation for each object should be called only once
            verify(mapFunc, times(1)).map(user);

            // putResolver's afterPut() callback should be called only once for each object
            verify(putResolver, times(1))
                    .afterPut(user, putCollectionResult.results().get(user));
        }

        if (useTransaction) {
            // if put() operation used transaction, only one notification should be thrown
            verify(internal, times(1))
                    .notifyAboutChanges(eq(new Changes(User.TABLE)));
        } else {
            // if put() operation didn't use transaction,
            // number of notifications should be equal to number of objects
            verify(internal, times(users.size()))
                    .notifyAboutChanges(eq(new Changes(User.TABLE)));
        }
    }
}
