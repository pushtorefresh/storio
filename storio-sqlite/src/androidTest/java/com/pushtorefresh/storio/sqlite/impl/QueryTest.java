package com.pushtorefresh.storio.sqlite.impl;

import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.test.runner.AndroidJUnit4;

import com.pushtorefresh.storio.sqlite.operations.get.DefaultGetResolver;
import com.pushtorefresh.storio.sqlite.operations.get.GetResolver;
import com.pushtorefresh.storio.sqlite.queries.Query;
import com.pushtorefresh.storio.sqlite.queries.RawQuery;

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

    @Test
    public void queryAll() {
        final List<User> users = putUsersBlocking(3);
        final List<User> usersFromQuery = getAllUsersBlocking();
        assertTrue(users.equals(usersFromQuery));
    }

    @Test
    public void queryOneByField() {
        final List<User> users = putUsersBlocking(3);

        for (User user : users) {
            final List<User> usersFromQuery = storIOSQLite
                    .get()
                    .listOfObjects(User.class)
                    .withQuery(Query.builder()
                            .table(UserTableMeta.TABLE)
                            .where(UserTableMeta.COLUMN_EMAIL + "=?")
                            .whereArgs(user.email())
                            .build())
                    .prepare()
                    .executeAsBlocking();

            assertNotNull(usersFromQuery);
            assertEquals(usersFromQuery.size(), 1);
            assertEquals(usersFromQuery.get(0), user);
        }
    }

    @Test
    public void queryOrdered() {
        final List<User> users = TestFactory.newUsers(3);

        // Reverse sorting by email before inserting, for the purity of the experiment.
        Collections.reverse(users);

        putUsersBlocking(users);

        final List<User> usersFromQueryOrdered = storIOSQLite
                .get()
                .listOfObjects(User.class)
                .withQuery(Query.builder()
                        .table(UserTableMeta.TABLE)
                        .orderBy(UserTableMeta.COLUMN_EMAIL)
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

    @Test
    public void queryOrderedDesc() {
        final List<User> users = TestFactory.newUsers(3);

        // Sorting by email before inserting, for the purity of the experiment.
        Collections.sort(users);

        putUsersBlocking(users);

        final List<User> usersFromQueryOrdered = storIOSQLite
                .get()
                .listOfObjects(User.class)
                .withQuery(Query.builder()
                        .table(UserTableMeta.TABLE)
                        .orderBy(UserTableMeta.COLUMN_EMAIL + " DESC")
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

    @Test
    public void querySingleLimit() {
        putUsersBlocking(10);

        final int limit = 8;
        final List<User> usersFromQuery = storIOSQLite
                .get()
                .listOfObjects(User.class)
                .withQuery(Query.builder()
                        .table(UserTableMeta.TABLE)
                        .limit(String.valueOf(limit))
                        .build())
                .prepare()
                .executeAsBlocking();

        assertNotNull(usersFromQuery);
        assertEquals(usersFromQuery.size(), limit);
    }

    @Test
    public void queryLimitOffset() {
        final List<User> users = putUsersBlocking(10);

        final int offset = 5;
        final int limit = 3;
        final List<User> usersFromQuery = storIOSQLite
                .get()
                .listOfObjects(User.class)
                .withQuery(Query.builder()
                        .table(UserTableMeta.TABLE)
                        .orderBy(UserTableMeta.COLUMN_EMAIL)
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

    @Test
    public void queryGroupBy() {
        final List<User> users = TestFactory.newUsers(10);

        for (int i = 0; i < users.size(); i++) {
            final String commonEmail;
            if (i < 3) {
                commonEmail = "first_group@gmail.com";
            } else {
                commonEmail = "second_group@gmail.com";
            }

            users.set(i, User.newInstance(null, commonEmail));
        }

        putUsersBlocking(users);

        final List<User> groupsOfUsers = storIOSQLite
                .get()
                .listOfObjects(User.class)
                .withQuery(Query.builder()
                        .table(UserTableMeta.TABLE)
                        .columns(UserTableMeta.COLUMN_EMAIL)
                        .groupBy(UserTableMeta.COLUMN_EMAIL)
                        .build())
                .withGetResolver(new DefaultGetResolver<User>() {
                    @NonNull
                    @Override
                    public User mapFromCursor(@NonNull Cursor cursor) {
                        return User.newInstance(null, cursor.getString(cursor.getColumnIndex(UserTableMeta.COLUMN_EMAIL)));
                    }
                })
                .prepare()
                .executeAsBlocking();

        assertNotNull(groupsOfUsers);
        assertEquals(2, groupsOfUsers.size());
    }

    @Test
    public void queryHaving() {
        final List<User> users = TestFactory.newUsers(10);

        for (int i = 0; i < users.size(); i++) {
            final String commonEmail;
            if (i < 3) {
                commonEmail = "first_group@gmail.com";
            } else {
                commonEmail = "second_group@gmail.com";
            }

            users.set(i, User.newInstance(null, commonEmail));
        }

        putUsersBlocking(users);

        final int bigGroupThreshold = 5;

        final List<User> groupsOfUsers = storIOSQLite
                .get()
                .listOfObjects(User.class)
                .withQuery(Query.builder()
                        .table(UserTableMeta.TABLE)
                        .columns(UserTableMeta.COLUMN_EMAIL)
                        .groupBy(UserTableMeta.COLUMN_EMAIL)
                        .having("COUNT(*) >= " + bigGroupThreshold)
                        .build())
                .withGetResolver(new DefaultGetResolver<User>() {
                    @NonNull
                    @Override
                    public User mapFromCursor(@NonNull Cursor cursor) {
                        return User.newInstance(null, cursor.getString(cursor.getColumnIndex(UserTableMeta.COLUMN_EMAIL)));
                    }
                })
                .prepare()
                .executeAsBlocking();

        assertNotNull(groupsOfUsers);
        assertEquals(1, groupsOfUsers.size());
    }

    @Test
    public void queryDistinct() {
        final List<User> users = new ArrayList<User>();

        for (int i = 0; i < 10; i++) {
            users.add(User.newInstance((long) i, "same@email.com"));
        }

        putUsersBlocking(users);

        final GetResolver<User> customGetResolver = new DefaultGetResolver<User>() {
            @NonNull
            @Override
            public User mapFromCursor(@NonNull Cursor cursor) {
                return User.newInstance(null, cursor.getString(cursor.getColumnIndex(UserTableMeta.COLUMN_EMAIL)));
            }
        };

        final List<User> uniqueUsersFromQuery = storIOSQLite
                .get()
                .listOfObjects(User.class)
                .withQuery(Query.builder()
                        .table(UserTableMeta.TABLE)
                        .distinct(true)
                        .columns(UserTableMeta.COLUMN_EMAIL)
                        .build())
                .withGetResolver(customGetResolver)
                .prepare()
                .executeAsBlocking();

        assertNotNull(uniqueUsersFromQuery);
        assertEquals(1, uniqueUsersFromQuery.size());

        final List<User> allUsersFromQuery = storIOSQLite
                .get()
                .listOfObjects(User.class)
                .withQuery(Query.builder()
                        .table(UserTableMeta.TABLE)
                        .distinct(false)
                        .columns(UserTableMeta.COLUMN_EMAIL)
                        .build())
                .withGetResolver(customGetResolver)
                .prepare()
                .executeAsBlocking();

        assertNotNull(allUsersFromQuery);
        assertEquals(users.size(), allUsersFromQuery.size());
    }

    @Test
    public void queryWithRawQuery() {
        final List<User> users = TestFactory.newUsers(20);

        int counter = 1;

        for (int i = 0; i < users.size(); i++) {
            char[] chars = new char[counter++];
            Arrays.fill(chars, '*'); // wtf is going on?
            users.set(i, User.newInstance(null, new String(chars)));
        }

        putUsersBlocking(users);

        final List<User> usersWithLongName = new ArrayList<User>(users.size());

        int lengthSum = 0;
        for (User user : users) {
            lengthSum += user.email().length();
        }

        final int avrLength = lengthSum / users.size();

        for (User user : users) {
            if (user.email().length() > avrLength) {
                usersWithLongName.add(user);
            }
        }

        final String query = "Select * from " + UserTableMeta.TABLE
                + " where length(" + UserTableMeta.COLUMN_EMAIL + ") > "
                + "(select avg(length(" + UserTableMeta.COLUMN_EMAIL + ")) from users)";

        final List<User> usersFromQuery = storIOSQLite
                .get()
                .listOfObjects(User.class)
                .withQuery(RawQuery.builder()
                        .query(query)
                        .build())
                .prepare()
                .executeAsBlocking();

        assertEquals(usersWithLongName, usersFromQuery);
    }

    @Test
    public void queryWithRawQueryAndArguments() {
        final User testUser = User.newInstance(null, "testUserName");

        final List<User> users = TestFactory.newUsers(10);
        users.add(testUser);
        putUsersBlocking(users);

        final String query = "SELECT * FROM " + UserTableMeta.TABLE
                + " WHERE " + UserTableMeta.COLUMN_EMAIL + " LIKE ?";

        final List<User> usersFromQuery = storIOSQLite
                .get()
                .listOfObjects(User.class)
                .withQuery(RawQuery.builder()
                        .query(query)
                        .args(testUser.email())
                        .build())
                .prepare()
                .executeAsBlocking();

        assertNotNull(usersFromQuery);
        assertEquals(1, usersFromQuery.size());
        assertEquals(testUser, usersFromQuery.get(0));
    }

    @Test
    public void queryWithRawQuerySqlInjectionFail() {
        final List<User> users = putUsersBlocking(10);

        final String query = "SELECT * FROM " + UserTableMeta.TABLE
                + " WHERE " + UserTableMeta.COLUMN_EMAIL + " LIKE ?";

        final String arg = "(DELETE FROM " + UserTableMeta.TABLE + ")";

        storIOSQLite.get()
                .listOfObjects(User.class)
                .withQuery(RawQuery.builder()
                        .query(query)
                        .args(arg)
                        .build())
                .prepare()
                .executeAsBlocking();

        assertEquals(users, getAllUsersBlocking());
    }
}