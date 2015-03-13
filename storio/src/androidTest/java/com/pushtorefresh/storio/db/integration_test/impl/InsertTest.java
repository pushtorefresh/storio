package com.pushtorefresh.storio.db.integration_test.impl;

import android.database.Cursor;
import android.support.test.runner.AndroidJUnit4;

import com.pushtorefresh.storio.db.operation.put.PutCollectionResult;
import com.pushtorefresh.storio.db.operation.put.PutResult;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class InsertTest extends BaseTest {

    @Test public void insertOne() {
        final User user = TestFactory.newUser();

        final PutResult putResult = storIODb
                .put()
                .object(user)
                .withMapFunc(User.MAP_TO_CONTENT_VALUES)
                .withPutResolver(User.PUT_RESOLVER)
                .prepare()
                .executeAsBlocking();

        assertTrue(putResult.wasInserted());

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
        final List<User> users = TestFactory.newUsers(3);

        final PutCollectionResult<User> putResult = storIODb
                .put()
                .objects(users)
                .withMapFunc(User.MAP_TO_CONTENT_VALUES)
                .withPutResolver(User.PUT_RESOLVER)
                .prepare()
                .executeAsBlocking();

        assertEquals(users.size(), putResult.numberOfInserts());

        // asserting that values was really inserted to db
        final Cursor cursor = db.query(User.TABLE, null, null, null, null, null, null);

        assertEquals(users.size(), cursor.getCount());

        for (int i = 0; i < users.size(); i++) {
            assertTrue(cursor.moveToNext());
            assertEquals(users.get(i), User.MAP_FROM_CURSOR.map(cursor));
        }

        cursor.close();
    }
}
