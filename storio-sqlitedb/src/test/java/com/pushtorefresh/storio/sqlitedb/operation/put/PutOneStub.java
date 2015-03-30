package com.pushtorefresh.storio.sqlitedb.operation.put;

import android.content.ContentValues;
import android.support.annotation.NonNull;

import com.pushtorefresh.storio.operation.MapFunc;
import com.pushtorefresh.storio.sqlitedb.Changes;
import com.pushtorefresh.storio.sqlitedb.StorIOSQLiteDb;
import com.pushtorefresh.storio.sqlitedb.design.User;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

// stub class to avoid violation of DRY in tests
// TODO don't use User class
class PutOneStub {
    final User user;
    final StorIOSQLiteDb storIOSQLiteDb;
    final StorIOSQLiteDb.Internal internal;
    final MapFunc<User, ContentValues> mapFunc;
    final PutResolver<User> putResolver;

    PutOneStub() {
        user = new User(null, "test@example.com");
        storIOSQLiteDb = mock(StorIOSQLiteDb.class);
        internal = mock(StorIOSQLiteDb.Internal.class);

        when(storIOSQLiteDb.internal())
                .thenReturn(internal);

        when(storIOSQLiteDb.put())
                .thenReturn(new PreparedPut.Builder(storIOSQLiteDb));

        //noinspection unchecked
        mapFunc = (MapFunc<User, ContentValues>) mock(MapFunc.class);

        //noinspection unchecked
        putResolver = (PutResolver<User>) mock(PutResolver.class);

        when(putResolver.performPut(eq(storIOSQLiteDb), any(ContentValues.class)))
                .thenReturn(PutResult.newInsertResult(1, User.TABLE));

        when(mapFunc.map(user))
                .thenReturn(mock(ContentValues.class));

    }

    void verifyBehavior(@NonNull PutResult putResult) {
        // put should be called only once
        verify(storIOSQLiteDb, times(1)).put();

        // object should be mapped to ContentValues only once
        verify(mapFunc, times(1)).map(user);

        // putResolver's performPut() should be called only once
        verify(putResolver, times(1)).performPut(eq(storIOSQLiteDb), any(ContentValues.class));

        // putResolver's afterPut() callback should be called only once
        verify(putResolver, times(1)).afterPut(user, putResult);

        // only one notification should be thrown
        verify(internal, times(1)).notifyAboutChanges(eq(new Changes(User.TABLE)));
    }
}
