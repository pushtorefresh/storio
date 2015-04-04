package com.pushtorefresh.storio.sqlitedb.impl;

import android.database.Cursor;
import android.support.test.runner.AndroidJUnit4;

import com.pushtorefresh.storio.sqlitedb.operation.put.PutResults;
import com.pushtorefresh.storio.sqlitedb.operation.put.PutResult;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class UpdateTest extends BaseTest {

    @Test public void updateOne() {
        final User userForInsert = TestFactory.newUser();

        final PutResult insertResult = storIOSQLiteDb
                .put()
                .object(userForInsert)
                .withMapFunc(User.MAP_TO_CONTENT_VALUES)
                .withPutResolver(User.PUT_RESOLVER)
                .prepare()
                .executeAsBlocking();

        assertTrue(insertResult.wasInserted());

        final User userForUpdate = new User(
                userForInsert.getId(), // using id of inserted user
                "new@email.com" // new value
        );

        final PutResult updateResult = storIOSQLiteDb
                .put()
                .object(userForUpdate)
                .withMapFunc(User.MAP_TO_CONTENT_VALUES)
                .withPutResolver(User.PUT_RESOLVER)
                .prepare()
                .executeAsBlocking();

        assertTrue(updateResult.wasUpdated());

        final Cursor cursor = db.query(User.TABLE, null, null, null, null, null, null);

        assertEquals(1, cursor.getCount()); // update should not add new rows!
        assertTrue(cursor.moveToFirst());

        final User updatedUser = User.MAP_FROM_CURSOR.map(cursor);
        assertEquals(userForUpdate, updatedUser);

        cursor.close();
    }

    @Test public void updateCollection() {
        final List<User> usersForInsert = TestFactory.newUsers(3);

        final PutResults<User> insertResults = storIOSQLiteDb
                .put()
                .objects(usersForInsert)
                .withMapFunc(User.MAP_TO_CONTENT_VALUES)
                .withPutResolver(User.PUT_RESOLVER)
                .prepare()
                .executeAsBlocking();

        assertEquals(usersForInsert.size(), insertResults.numberOfInserts());

        final List<User> usersForUpdate = new ArrayList<>(usersForInsert.size());

        for (int i = 0; i < usersForInsert.size(); i++) {
            usersForUpdate.add(new User(usersForInsert.get(i).getId(), "new" + i + "@email.com" + i));
        }

        final PutResults<User> updateResults = storIOSQLiteDb
                .put()
                .objects(usersForUpdate)
                .withMapFunc(User.MAP_TO_CONTENT_VALUES)
                .withPutResolver(User.PUT_RESOLVER)
                .prepare()
                .executeAsBlocking();

        assertEquals(usersForUpdate.size(), updateResults.numberOfUpdates());

        final Cursor cursor = db.query(User.TABLE, null, null, null, null, null, null);

        assertEquals(usersForUpdate.size(), cursor.getCount()); // update should not add new rows!

        for (int i = 0; i < usersForUpdate.size(); i++) {
            assertTrue(cursor.moveToNext());
            assertEquals(usersForUpdate.get(i), User.MAP_FROM_CURSOR.map(cursor));
        }

        cursor.close();
    }
}
