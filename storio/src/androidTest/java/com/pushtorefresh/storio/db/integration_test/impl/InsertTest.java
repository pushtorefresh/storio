package com.pushtorefresh.storio.db.integration_test.impl;

import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.test.runner.AndroidJUnit4;

import com.pushtorefresh.storio.db.operation.delete.DeleteResult;
import com.pushtorefresh.storio.db.operation.put.PutCollectionResult;
import com.pushtorefresh.storio.db.operation.put.PutResult;
import com.pushtorefresh.storio.db.query.Query;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class InsertTest extends BaseTest {

    @Test public void insertOne() {
        final User user = putUser();

        // why we created StorIODb: nobody loves nulls
        final Cursor cursor = db.query(User.TABLE, null, null, null, null, null, null);

        // asserting that values was really inserted to db
        assertEquals(1, cursor.getCount());
        assertTrue(cursor.moveToFirst());

        final User insertedUser = User.MAP_FROM_CURSOR.map(cursor);

        assertNotNull(insertedUser.getId());
        assertTrue(user.equalsExceptId(insertedUser));

        cursor.close();
    }

    @Test public void insertCollection() {
        final List<User> users = putUsers(3);

        // asserting that values was really inserted to db
        final Cursor cursor = db.query(User.TABLE, null, null, null, null, null, null);

        assertEquals(users.size(), cursor.getCount());

        for (int i = 0; i < users.size(); i++) {
            assertTrue(cursor.moveToNext());
            assertEquals(users.get(i), User.MAP_FROM_CURSOR.map(cursor));
        }

        cursor.close();
    }

    @Test public void insertTwice() {
        final User user = TestFactory.newUser();

        for (int i = 0; i < 2; i++) {
            putUser(user);

            final List<User> existUsers = storIODb
                    .get()
                    .listOfObjects(User.class)
                    .withMapFunc(User.MAP_FROM_CURSOR)
                    .withQuery(new Query.Builder()
                            .table(User.TABLE)
                            .build())
                    .prepare()
                    .executeAsBlocking();

            assertEquals(1, existUsers.size());

            final Cursor cursorAfterPut = db.query(User.TABLE, null, null, null, null, null, null);
            assertEquals(1, cursorAfterPut.getCount());
            cursorAfterPut.close();

            final DeleteResult deleteResult = storIODb
                    .delete()
                    .object(user)
                    .withMapFunc(User.MAP_TO_DELETE_QUERY)
                    .prepare()
                    .executeAsBlocking();

            assertEquals(1, deleteResult.numberOfDeletedRows());

            final Cursor cursorAfterDelete = db.query(User.TABLE, null, null, null, null, null, null);
            assertEquals(0, cursorAfterDelete.getCount());
            cursorAfterDelete.close();
        }
    }
}
