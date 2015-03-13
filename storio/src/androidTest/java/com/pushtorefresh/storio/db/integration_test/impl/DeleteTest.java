package com.pushtorefresh.storio.db.integration_test.impl;

import android.database.Cursor;
import android.support.test.runner.AndroidJUnit4;

import com.pushtorefresh.storio.db.operation.delete.DeleteResult;
import com.pushtorefresh.storio.db.operation.put.PutResult;

import org.junit.Test;
import org.junit.runner.RunWith;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class DeleteTest extends BaseTest {

    @Test public void deleteOne() {
        final User user = TestFactory.newUser();

        final PutResult putResult = storIODb
                .put()
                .object(user)
                .withMapFunc(User.MAP_TO_CONTENT_VALUES)
                .withPutResolver(User.PUT_RESOLVER)
                .prepare()
                .executeAsBlocking();

        assertTrue(putResult.wasInserted());

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
}
