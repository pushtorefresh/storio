package com.pushtorefresh.storio.sqlitedb.impl;

import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.test.InstrumentationRegistry;

import com.pushtorefresh.storio.sqlitedb.StorIOSQLiteDb;
import com.pushtorefresh.storio.sqlitedb.operation.delete.DeleteResult;
import com.pushtorefresh.storio.sqlitedb.operation.put.PutCollectionResult;
import com.pushtorefresh.storio.sqlitedb.operation.put.PutResult;
import com.pushtorefresh.storio.sqlitedb.query.Query;

import org.junit.Before;

import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;

public abstract class BaseTest {

    @NonNull
    protected StorIOSQLiteDb storIOSQLiteDb;

    @NonNull
    protected SQLiteDatabase db;

    @Before
    public void setUp() throws Exception {
        db = new TestSQLiteOpenHelper(InstrumentationRegistry.getContext())
                .getWritableDatabase();

        storIOSQLiteDb = new DefaultStorIOSQLiteDb.Builder()
                .db(db)
                .build();

        // clearing db before each test case
        storIOSQLiteDb
                .delete()
                .byQuery(User.DELETE_ALL)
                .prepare()
                .executeAsBlocking();

        storIOSQLiteDb
                .delete()
                .byQuery(Tweet.DELETE_ALL)
                .prepare()
                .executeAsBlocking();
    }

    @Nullable
    List<User> getAllUsers() {
        return storIOSQLiteDb
                .get()
                .listOfObjects(User.class)
                .withMapFunc(User.MAP_FROM_CURSOR)
                .withQuery(new Query.Builder()
                        .table(User.TABLE)
                        .build())
                .prepare()
                .executeAsBlocking();
    }

    @NonNull
    User putUser() {
        final User user = TestFactory.newUser();
        return putUser(user);
    }

    @NonNull
    User putUser(@NonNull final User user) {

        final PutResult putResult = storIOSQLiteDb
                .put()
                .object(user)
                .withMapFunc(User.MAP_TO_CONTENT_VALUES)
                .withPutResolver(User.PUT_RESOLVER)
                .prepare()
                .executeAsBlocking();

        assertNotNull(putResult);
        assertTrue(putResult.wasInserted());
        return user;
    }

    @NonNull
    List<User> putUsers(final int size) {
        final List<User> users = TestFactory.newUsers(size);
        return putUsers(users);
    }

    @NonNull
    List<User> putUsers(@NonNull final List<User> users) {

        final PutCollectionResult<User> putResult = storIOSQLiteDb
                .put()
                .objects(users)
                .withMapFunc(User.MAP_TO_CONTENT_VALUES)
                .withPutResolver(User.PUT_RESOLVER)
                .prepare()
                .executeAsBlocking();

        assertEquals(users.size(), putResult.numberOfInserts());

        return users;
    }

    @NonNull
    DeleteResult deleteUser(@NonNull final User user) {
        final DeleteResult deleteResult = storIOSQLiteDb
                .delete()
                .object(user)
                .withMapFunc(User.MAP_TO_DELETE_QUERY)
                .prepare()
                .executeAsBlocking();

        assertEquals(1, deleteResult.numberOfRowsDeleted());

        return deleteResult;
    }
}
