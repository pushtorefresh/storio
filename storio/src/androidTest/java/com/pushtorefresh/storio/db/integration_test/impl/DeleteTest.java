package com.pushtorefresh.storio.db.integration_test.impl;

import android.database.Cursor;
import android.support.test.runner.AndroidJUnit4;

import com.pushtorefresh.storio.db.operation.delete.DeleteCollectionOfObjectsResult;
import com.pushtorefresh.storio.db.operation.delete.DeleteResult;
import com.pushtorefresh.storio.db.operation.put.PutCollectionResult;
import com.pushtorefresh.storio.db.operation.put.PutResult;
import com.pushtorefresh.storio.db.query.Query;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class DeleteTest extends BaseTest {

    @Test public void deleteOne() {
        final User user = putUser();

        final Cursor cursorAfterInsert = db.query(User.TABLE, null, null, null, null, null, null);
        assertEquals(1, cursorAfterInsert.getCount());
        cursorAfterInsert.close();

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

    @Test public void deleteCollection() {
        final List<User> allUsers = putUsers(10);

        final List<User> usersToDelete = new ArrayList<>();
        for (int i = 0; i < allUsers.size(); i += 2) {  // I will delete every second
            usersToDelete.add(allUsers.get(i));
        }

        final DeleteCollectionOfObjectsResult<User> deleteResult = storIODb
                .delete()
                .objects(usersToDelete)
                .withMapFunc(User.MAP_TO_DELETE_QUERY)
                .prepare()
                .executeAsBlocking();

        final List<User> existUsers = storIODb
                .get()
                .listOfObjects(User.class)
                .withMapFunc(User.MAP_FROM_CURSOR)
                .withQuery(new Query.Builder()
                        .table(User.TABLE)
                        .build())
                .prepare()
                .executeAsBlocking();

        for (User user : allUsers) {
            final boolean deleted = usersToDelete.contains(user);
            assertEquals(deleteResult.results().containsKey(user), deleted);
            assertEquals(existUsers.contains(user), !deleted);
        }
    }
}
