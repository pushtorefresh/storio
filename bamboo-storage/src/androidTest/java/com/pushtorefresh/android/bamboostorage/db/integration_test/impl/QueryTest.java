package com.pushtorefresh.android.bamboostorage.db.integration_test.impl;

import android.support.test.runner.AndroidJUnit4;

import com.pushtorefresh.android.bamboostorage.db.operation.put.PutCollectionResult;
import com.pushtorefresh.android.bamboostorage.db.query.QueryBuilder;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class QueryTest extends BaseTest {

    @Test public void queryAll() {
        final List<User> users = TestFactory.newUsers(3);

        final PutCollectionResult<User> putResult = bambooStorage
                .put()
                .objects(users)
                .withMapFunc(User.MAP_TO_CONTENT_VALUES)
                .withPutResolver(User.PUT_RESOLVER)
                .prepare()
                .executeAsBlocking();

        assertEquals(users.size(), putResult.numberOfInserts());

        final List<User> usersFromQuery = bambooStorage
                .get()
                .listOfObjects(User.class)
                .withMapFunc(User.MAP_FROM_CURSOR)
                .withQuery(new QueryBuilder()
                        .table(User.TABLE)
                        .build())
                .prepare()
                .executeAsBlocking();

        assertTrue(users.equals(usersFromQuery));
    }
}
