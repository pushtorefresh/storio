package com.pushtorefresh.storio.db.integration_test.impl;

import android.database.Cursor;
import android.support.test.runner.AndroidJUnit4;

import com.pushtorefresh.storio.db.operation.MapFunc;
import com.pushtorefresh.storio.db.query.Query;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Collections;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class QueryTest extends BaseTest {

    @Test public void queryAll() {
        final List<User> users = putUsers(3);

        final List<User> usersFromQuery = storIODb
                .get()
                .listOfObjects(User.class)
                .withMapFunc(User.MAP_FROM_CURSOR)
                .withQuery(new Query.Builder()
                        .table(User.TABLE)
                        .build())
                .prepare()
                .executeAsBlocking();

        assertTrue(users.equals(usersFromQuery));
    }

    @Test public void queryOneByField() {
        final List<User> users = putUsers(3);

        for (User user : users) {
            final List<User> usersFromQuery = storIODb
                    .get()
                    .listOfObjects(User.class)
                    .withMapFunc(User.MAP_FROM_CURSOR)
                    .withQuery(new Query.Builder()
                            .table(User.TABLE)
                            .where(User.COLUMN_EMAIL + "=?")
                            .whereArgs(user.getEmail())
                            .build())
                    .prepare()
                    .executeAsBlocking();

            assertEquals(usersFromQuery.size(), 1);
            assertEquals(usersFromQuery.get(0), user);
        }
    }

    @Test public void queryOrdered() {
        final List<User> users = TestFactory.newUsers(3);
        Collections.reverse(users);
        putUsers(users);

        final List<User> usersFromQueryOrdered = storIODb
                .get()
                .listOfObjects(User.class)
                .withMapFunc(User.MAP_FROM_CURSOR)
                .withQuery(new Query.Builder()
                        .table(User.TABLE)
                        .orderBy(User.COLUMN_EMAIL)
                        .build())
                .prepare()
                .executeAsBlocking();

        Collections.sort(users);

        assertEquals(users.size(), usersFromQueryOrdered.size());

        for (int i = 0; i < users.size(); i++) {
            assertEquals(users.get(i), usersFromQueryOrdered.get(i));
        }
    }

    @Test public void queryOrderedDesc() {
        final List<User> users = TestFactory.newUsers(3);
        Collections.sort(users);
        putUsers(users);

        final List<User> usersFromQueryOrdered = storIODb
                .get()
                .listOfObjects(User.class)
                .withMapFunc(User.MAP_FROM_CURSOR)
                .withQuery(new Query.Builder()
                        .table(User.TABLE)
                        .orderBy(User.COLUMN_EMAIL + " DESC")
                        .build())
                .prepare()
                .executeAsBlocking();

        Collections.reverse(users);

        assertEquals(users.size(), usersFromQueryOrdered.size());

        for (int i = 0; i < users.size(); i++) {
            assertEquals(users.get(i), usersFromQueryOrdered.get(i));
        }
    }

    @Test public void querySingleLimit() {
        putUsers(10);

        final int limit = 8;
        final List<User> usersFromQuery = storIODb
                .get()
                .listOfObjects(User.class)
                .withMapFunc(User.MAP_FROM_CURSOR)
                .withQuery(new Query.Builder()
                        .table(User.TABLE)
                        .limit(String.valueOf(limit))
                        .build())
                .prepare()
                .executeAsBlocking();

        assertEquals(usersFromQuery.size(), limit);
    }

    @Test public void queryLimitOffset() {
        final List<User> users = putUsers(10);

        final int offset = 5;
        final int limit = 3;
        final List<User> usersFromQuery = storIODb
                .get()
                .listOfObjects(User.class)
                .withMapFunc(User.MAP_FROM_CURSOR)
                .withQuery(new Query.Builder()
                        .table(User.TABLE)
                        .orderBy(User.COLUMN_EMAIL)
                        .limit(offset + ", " + limit)
                        .build())
                .prepare()
                .executeAsBlocking();

        assertEquals(Math.min(limit, users.size() - offset), usersFromQuery.size());

        Collections.sort(users);

        int position = 0;
        for (int i = offset; i < offset + limit; i++) {
            assertEquals(users.get(i), usersFromQuery.get(position++));
        }
    }

    @Test public void queryGroupBy() {
        final List<User> users = TestFactory.newUsers(10);

        for (int i = 0; i < users.size(); i++) {
            final String commonEmail;
            if (i < 3) {
                commonEmail = "first_group@gmail.com";
            } else {
                commonEmail = "second_group@gmail.com";
            }
            users.get(i).setEmail(commonEmail);
        }

        putUsers(users);

        final MapFunc<Cursor, User> mapFunc = new MapFunc<Cursor, User>() {
            @Override public User map(Cursor cursor) {
                return new User(null, cursor.getString(cursor.getColumnIndex(User.COLUMN_EMAIL)));
            }
        };

        final List<User> groupsOfUsers = storIODb
                .get()
                .listOfObjects(User.class)
                .withMapFunc(mapFunc)
                .withQuery(new Query.Builder()
                        .columns(User.COLUMN_EMAIL)
                        .table(User.TABLE)
                        .groupBy(User.COLUMN_EMAIL)
                        .build())
                .prepare()
                .executeAsBlocking();

        assertEquals(2, groupsOfUsers.size());
    }
}