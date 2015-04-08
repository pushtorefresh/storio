package com.pushtorefresh.storio.sqlitedb.impl;

import android.database.Cursor;
import android.support.test.runner.AndroidJUnit4;

import com.pushtorefresh.storio.sqlitedb.query.RawQuery;
import com.pushtorefresh.storio.operation.MapFunc;
import com.pushtorefresh.storio.sqlitedb.query.Query;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class QueryTest extends BaseTest {

    @Test public void queryAll() {
        final List<User> users = putUsers(3);

        final List<User> usersFromQuery = getAllUsers();

        assertTrue(users.equals(usersFromQuery));
    }

    @Test public void queryOneByField() {
        final List<User> users = putUsers(3);

        for (User user : users) {
            final List<User> usersFromQuery = storIOSQLiteDb
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

        final List<User> usersFromQueryOrdered = storIOSQLiteDb
                .get()
                .listOfObjects(User.class)
                .withMapFunc(User.MAP_FROM_CURSOR)
                .withQuery(new Query.Builder()
                        .table(User.TABLE)
                        .orderBy(User.COLUMN_EMAIL)
                        .build())
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

        final List<User> usersFromQueryOrdered = storIOSQLiteDb
                .get()
                .listOfObjects(User.class)
                .withMapFunc(User.MAP_FROM_CURSOR)
                .withQuery(new Query.Builder()
                        .table(User.TABLE)
                        .orderBy(User.COLUMN_EMAIL + " DESC")
                        .build())
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

    @Test public void querySingleLimit() {
        putUsers(10);

        final int limit = 8;
        final List<User> usersFromQuery = storIOSQLiteDb
                .get()
                .listOfObjects(User.class)
                .withMapFunc(User.MAP_FROM_CURSOR)
                .withQuery(new Query.Builder()
                        .table(User.TABLE)
                        .limit(String.valueOf(limit))
                        .build())
                .prepare()
                .executeAsBlocking();

        assertNotNull(usersFromQuery);
        assertEquals(usersFromQuery.size(), limit);
    }

    @Test public void queryLimitOffset() {
        final List<User> users = putUsers(10);

        final int offset = 5;
        final int limit = 3;
        final List<User> usersFromQuery = storIOSQLiteDb
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

        assertNotNull(usersFromQuery);
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

        final List<User> groupsOfUsers = storIOSQLiteDb
                .get()
                .listOfObjects(User.class)
                .withMapFunc(mapFuncOnlyEmail)
                .withQuery(new Query.Builder()
                        .columns(User.COLUMN_EMAIL)
                        .table(User.TABLE)
                        .groupBy(User.COLUMN_EMAIL)
                        .build())
                .prepare()
                .executeAsBlocking();

        assertNotNull(groupsOfUsers);
        assertEquals(2, groupsOfUsers.size());
    }

    @Test public void queryHaving() {
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

        final int bigGroupThreshold = 5;

        final List<User> groupsOfUsers = storIOSQLiteDb
                .get()
                .listOfObjects(User.class)
                .withMapFunc(mapFuncOnlyEmail)
                .withQuery(new Query.Builder()
                        .columns(User.COLUMN_EMAIL)
                        .table(User.TABLE)
                        .groupBy(User.COLUMN_EMAIL)
                        .having("COUNT(*) >= " + bigGroupThreshold)
                        .build())
                .prepare()
                .executeAsBlocking();

        assertNotNull(groupsOfUsers);
        assertEquals(1, groupsOfUsers.size());
    }

    @Test public void queryDistinct() {
        final List<User> users = TestFactory.newUsers(10);

        for (User user : users) {
            user.setEmail("same@gmail.com");
        }

        putUsers(users);

        final List<User> uniqueUsersFromQuery = storIOSQLiteDb
                .get()
                .listOfObjects(User.class)
                .withMapFunc(mapFuncOnlyEmail)
                .withQuery(new Query.Builder()
                        .distinct(true)
                        .columns(User.COLUMN_EMAIL)
                        .table(User.TABLE)
                        .build())
                .prepare()
                .executeAsBlocking();

        assertNotNull(uniqueUsersFromQuery);
        assertEquals(1, uniqueUsersFromQuery.size());

        final List<User> allUsersFromQuery = storIOSQLiteDb
                .get()
                .listOfObjects(User.class)
                .withMapFunc(mapFuncOnlyEmail)
                .withQuery(new Query.Builder()
                        .distinct(false)
                        .columns(User.COLUMN_EMAIL)
                        .table(User.TABLE)
                        .build())
                .prepare()
                .executeAsBlocking();

        assertNotNull(allUsersFromQuery);
        assertEquals(users.size(), allUsersFromQuery.size());
    }

    private final MapFunc<Cursor, User> mapFuncOnlyEmail = new MapFunc<Cursor, User>() {
        @Override public User map(Cursor cursor) {
            return new User(null, cursor.getString(cursor.getColumnIndex(User.COLUMN_EMAIL)));
        }
    };

    @Test public void queryWithRawQuery() {

        final List<User> users = TestFactory.newUsers(20);

        int counter = 1;
        for (User user : users) {

            char[] chars = new char[counter++];
            Arrays.fill(chars, '*');
            user.setEmail(new String(chars));
        }

        putUsers(users);

        final List<User> usersWithLongName = new ArrayList<User>(users.size());

        int lengthSum = 0;
        for (User user : users) {
            lengthSum += user.getEmail().length();
        }
        final int avrLength = lengthSum / users.size();
        for (User user : users) {
            if (user.getEmail().length() > avrLength) {
                usersWithLongName.add(user);
            }
        }

        final String query = "Select * from " + User.TABLE
                + " where length(" + User.COLUMN_EMAIL + ") > "
                + "(select avg(length(" + User.COLUMN_EMAIL + ")) from users)";

        final List<User> usersFromQuery = storIOSQLiteDb
                .get()
                .listOfObjects(User.class)
                .withMapFunc(User.MAP_FROM_CURSOR)
                .withQuery(new RawQuery.Builder()
                        .query(query)
                        .build())
                .prepare()
                .executeAsBlocking();

        assertEquals(usersWithLongName, usersFromQuery);
    }

    @Test public void queryWithRawQueryAndArguments() {

        final User testUser = new User(null, "testUserName");

        final List<User> users = TestFactory.newUsers(10);
        users.add(testUser);
        putUsers(users);

        final String query = "Select * from " + User.TABLE
                + " where " + User.COLUMN_EMAIL + " like ?";

        final List<User> usersFromQuery = storIOSQLiteDb
                .get()
                .listOfObjects(User.class)
                .withMapFunc(User.MAP_FROM_CURSOR)
                .withQuery(new RawQuery.Builder()
                        .query(query)
                        .args(testUser.getEmail())
                        .build())
                .prepare()
                .executeAsBlocking();

        assertNotNull(usersFromQuery);
        assertEquals(1, usersFromQuery.size());
        assertEquals(testUser, usersFromQuery.get(0));
    }

    @Test public void queryWithRawQuerySqlInjection() {

        final List<User> users = putUsers(10);

        final String query = "Select * from " + User.TABLE
                + " where " + User.COLUMN_EMAIL + " like ?";
        final String arg = "(delete from " + User.TABLE + ")";

        storIOSQLiteDb.get()
                .listOfObjects(User.class)
                .withMapFunc(User.MAP_FROM_CURSOR)
                .withQuery(new RawQuery.Builder()
                        .query(query)
                        .args(arg)
                        .build())
                .prepare()
                .executeAsBlocking();

        assertEquals(users, getAllUsers());
    }
}