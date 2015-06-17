package com.pushtorefresh.storio.sqlite.impl;

import android.database.Cursor;
import android.support.test.runner.AndroidJUnit4;

import com.pushtorefresh.storio.sqlite.operation.put.PutResult;
import com.pushtorefresh.storio.sqlite.operation.put.PutResults;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class UpdateTest extends BaseTest {

    @Test
    public void updateOne() {
        final User userForInsert = TestFactory.newUser();

        final PutResult insertResult = storIOSQLite
                .put()
                .object(userForInsert)
                .prepare()
                .executeAsBlocking();

        assertTrue(insertResult.wasInserted());

        final User userForUpdate = User.newInstance(
                userForInsert.id(), // using id of inserted user
                "new@email.com" // new value
        );

        final PutResult updateResult = storIOSQLite
                .put()
                .object(userForUpdate)
                .prepare()
                .executeAsBlocking();

        assertTrue(updateResult.wasUpdated());

        final Cursor cursor = db.query(UserTableMeta.TABLE, null, null, null, null, null, null);

        assertEquals(1, cursor.getCount()); // update should not add new rows!
        assertTrue(cursor.moveToFirst());

        final User updatedUser = UserTableMeta.GET_RESOLVER.mapFromCursor(cursor);
        assertEquals(userForUpdate, updatedUser);

        cursor.close();
    }

    @Test
    public void updateCollection() {
        final List<User> usersForInsert = TestFactory.newUsers(3);

        final PutResults<User> insertResults = storIOSQLite
                .put()
                .objects(usersForInsert)
                .prepare()
                .executeAsBlocking();

        assertEquals(usersForInsert.size(), insertResults.numberOfInserts());

        final List<User> usersForUpdate = new ArrayList<User>(usersForInsert.size());

        for (int i = 0; i < usersForInsert.size(); i++) {
            usersForUpdate.add(User.newInstance(usersForInsert.get(i).id(), "new" + i + "@email.com" + i));
        }

        final PutResults<User> updateResults = storIOSQLite
                .put()
                .objects(usersForUpdate)
                .prepare()
                .executeAsBlocking();

        assertEquals(usersForUpdate.size(), updateResults.numberOfUpdates());

        final Cursor cursor = db.query(UserTableMeta.TABLE, null, null, null, null, null, null);

        assertEquals(usersForUpdate.size(), cursor.getCount()); // update should not add new rows!

        for (int i = 0; i < usersForUpdate.size(); i++) {
            assertTrue(cursor.moveToNext());
            assertEquals(usersForUpdate.get(i), UserTableMeta.GET_RESOLVER.mapFromCursor(cursor));
        }

        cursor.close();
    }
}
