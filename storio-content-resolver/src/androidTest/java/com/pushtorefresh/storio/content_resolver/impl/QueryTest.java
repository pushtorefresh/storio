package com.pushtorefresh.storio.content_resolver.impl;

import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.test.runner.AndroidJUnit4;

import com.pushtorefresh.storio.contentresolver.query.Query;
import com.pushtorefresh.storio.operation.MapFunc;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Collections;
import java.util.List;

@RunWith(AndroidJUnit4.class)
public class QueryTest extends BaseTest {

    public QueryTest() {
        super();
    }

    @Test public void queryAll() {
        final List<User> users = putUsers(3);
        usersInStorageCheck(users);
    }

    @Test public void queryOneByField() {
        final List<User> users = putUsers(3);

        for (User user : users) {
            final List<User> usersFromQuery = storIOContentResolver
                    .get()
                    .listOfObjects(User.class)
                    .withQuery(new Query.Builder()
                            .uri(User.CONTENT_URI)
                            .where(User.COLUMN_EMAIL + "=?")
                            .whereArgs(user.getEmail())
                            .build())
                    .withMapFunc(User.MAP_FROM_CURSOR)
                    .prepare()
                    .executeAsBlocking();

            assertNotNull(usersFromQuery);
            assertEquals(usersFromQuery.size(), 1);
            assertEquals(usersFromQuery.get(0), user);
        }
    }

    @Test public void queryOrdered() {
        final List<User> users = TestFactory.newUsers(3);

        // Reverse sorting by email before inserting, for the purity of the experiment.
        Collections.reverse(users);

        putUsers(users);

        final List<User> usersFromQueryOrdered = storIOContentResolver
                .get()
                .listOfObjects(User.class)
                .withQuery(new Query.Builder()
                        .uri(User.CONTENT_URI)
                        .sortOrder(User.COLUMN_EMAIL)
                        .build())
                .withMapFunc(User.MAP_FROM_CURSOR)
                .prepare()
                .executeAsBlocking();

        assertNotNull(usersFromQueryOrdered);
        assertEquals(users.size(), usersFromQueryOrdered.size());

        // Sorting by email for check ordering.
        Collections.sort(users);

        for (int i = 0; i < users.size(); i++) {
            assertEquals(users.get(i), usersFromQueryOrdered.get(i));
        }
    }

    @Test public void queryOrderedDesc() {
        final List<User> users = TestFactory.newUsers(3);

        // Sorting by email before inserting, for the purity of the experiment.
        Collections.sort(users);

        putUsers(users);

        final List<User> usersFromQueryOrdered = storIOContentResolver
                .get()
                .listOfObjects(User.class)
                .withQuery(new Query.Builder()
                        .uri(User.CONTENT_URI)
                        .sortOrder(User.COLUMN_EMAIL + " DESC")
                        .build())
                .withMapFunc(User.MAP_FROM_CURSOR)
                .prepare()
                .executeAsBlocking();

        assertNotNull(usersFromQueryOrdered);
        assertEquals(users.size(), usersFromQueryOrdered.size());

        // Reverse sorting by email for check ordering.
        Collections.reverse(users);

        for (int i = 0; i < users.size(); i++) {
            assertEquals(users.get(i), usersFromQueryOrdered.get(i));
        }
    }

    @Test public void queryProjection() {
        final List<User> users = putUsers(3);

        final List<User> usersFromStorage = storIOContentResolver
                .get()
                .listOfObjects(User.class)
                .withQuery(new Query.Builder()
                        .uri(User.CONTENT_URI)
                        .projection(User.COLUMN_ID)
                        .build())
                .withMapFunc(new MapFunc<Cursor, User>() {
                    @NonNull
                    @Override
                    public User map(@NonNull Cursor cursor) {
                        final Long id = cursor.getLong(cursor.getColumnIndex(User.COLUMN_ID));
                        return new User(id);
                    }
                })
                .prepare()
                .executeAsBlocking();

        assertNotNull(usersFromStorage);
        assertEquals(users.size(), usersFromStorage.size());

        for (int i = 0; i < users.size(); i++) {
            final User user = users.get(i);
            final User userFromStorage = usersFromStorage.get(i);
            assertEquals(user.getId(), userFromStorage.getId());
            assertNull(userFromStorage.getEmail());
        }
    }
}