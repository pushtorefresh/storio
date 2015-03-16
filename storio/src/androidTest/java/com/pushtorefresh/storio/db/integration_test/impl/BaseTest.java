package com.pushtorefresh.storio.db.integration_test.impl;

import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;
import android.support.test.InstrumentationRegistry;

import com.pushtorefresh.storio.db.StorIODb;
import com.pushtorefresh.storio.db.impl.StorIOSQLiteDb;
import com.pushtorefresh.storio.db.operation.delete.DeleteResult;
import com.pushtorefresh.storio.db.operation.put.PutCollectionResult;
import com.pushtorefresh.storio.db.operation.put.PutResult;
import com.pushtorefresh.storio.db.query.Query;

import org.junit.Before;

import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

public abstract class BaseTest {

    @NonNull protected StorIODb storIODb;
    @NonNull protected SQLiteDatabase db;

    @Before public void setUp() throws Exception {
        db = new TestSQLiteOpenHelper(InstrumentationRegistry.getContext())
                .getWritableDatabase();

        storIODb = new StorIOSQLiteDb.Builder()
                .db(db)
                .build();

        // clearing db before each test case
        storIODb
                .delete()
                .byQuery(User.DELETE_ALL)
                .prepare()
                .executeAsBlocking();
    }

    @NonNull List<User> getAllUsers() {
        return storIODb
                .get()
                .listOfObjects(User.class)
                .withMapFunc(User.MAP_FROM_CURSOR)
                .withQuery(new Query.Builder()
                        .table(User.TABLE)
                        .build())
                .prepare()
                .executeAsBlocking();
    }

    @NonNull User putUser() {
        final User user = TestFactory.newUser();
        return putUser(user);
    }

    @NonNull User putUser(@NonNull final User user) {

        final PutResult putResult = storIODb
                .put()
                .object(user)
                .withMapFunc(User.MAP_TO_CONTENT_VALUES)
                .withPutResolver(User.PUT_RESOLVER)
                .prepare()
                .executeAsBlocking();

        assertTrue(putResult.wasInserted());
        return user;
    }

    @NonNull List<User> putUsers(final int size) {
        final List<User> users = TestFactory.newUsers(size);
        return putUsers(users);
    }

    @NonNull List<User> putUsers(@NonNull final List<User> users) {

        final PutCollectionResult<User> putResult = storIODb
                .put()
                .objects(users)
                .withMapFunc(User.MAP_TO_CONTENT_VALUES)
                .withPutResolver(User.PUT_RESOLVER)
                .prepare()
                .executeAsBlocking();

        assertEquals(users.size(), putResult.numberOfInserts());

        return users;
    }

    @NonNull DeleteResult deleteUser(@NonNull final User user) {
        final DeleteResult deleteResult = storIODb
                .delete()
                .object(user)
                .withMapFunc(User.MAP_TO_DELETE_QUERY)
                .prepare()
                .executeAsBlocking();

        assertEquals(1, deleteResult.numberOfDeletedRows());

        return deleteResult;
    }
}
