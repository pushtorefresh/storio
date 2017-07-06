package com.pushtorefresh.storio.sqlite.integration;

import android.database.Cursor;
import android.support.annotation.NonNull;

import com.pushtorefresh.storio.sqlite.BuildConfig;
import com.pushtorefresh.storio.sqlite.operations.get.DefaultGetResolver;
import com.pushtorefresh.storio.sqlite.operations.get.GetResolver;
import com.pushtorefresh.storio.sqlite.queries.Query;
import com.pushtorefresh.storio.sqlite.queries.RawQuery;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Java6Assertions.assertThat;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)
public class QueryTest extends BaseTest {

    @Test
    public void queryAll() {
        final List<User> users = putUsersBlocking(3);
        final List<User> usersFromQuery = getAllUsersBlocking();
        assertThat(users.equals(usersFromQuery)).isTrue();
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

            assertThat(usersFromQuery).isNotNull();
            assertThat(usersFromQuery).hasSize(1);
            assertThat(usersFromQuery.get(0)).isEqualTo(user);
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

        assertThat(usersFromQueryOrdered).isNotNull();
        assertThat(usersFromQueryOrdered).hasSize(users.size());

        // Sorting by email for check ordering.
        Collections.sort(users);

        for (int i = 0; i < users.size(); i++) {
            assertThat(usersFromQueryOrdered.get(i)).isEqualTo(users.get(i));
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

        assertThat(usersFromQueryOrdered).isNotNull();
        assertThat(usersFromQueryOrdered).hasSize(users.size());

        // Reverse sorting by email for check ordering.
        Collections.reverse(users);

        for (int i = 0; i < users.size(); i++) {
            assertThat(usersFromQueryOrdered.get(i)).isEqualTo(users.get(i));
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

        assertThat(usersFromQuery).isNotNull();
        assertThat(usersFromQuery).hasSize(limit);
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

        assertThat(usersFromQuery).isNotNull();
        assertThat(usersFromQuery).hasSize(Math.min(limit, users.size() - offset));

        Collections.sort(users);

        int position = 0;
        for (int i = offset; i < offset + limit; i++) {
            assertThat(usersFromQuery.get(position++)).isEqualTo(users.get(i));
        }
    }

    @Test
    public void queryIntegerLimit() {
        putUsersBlocking(10);

        final int limit = 8;
        final List<User> usersFromQuery = storIOSQLite
                .get()
                .listOfObjects(User.class)
                .withQuery(Query.builder()
                        .table(UserTableMeta.TABLE)
                        .limit(limit)
                        .build())
                .prepare()
                .executeAsBlocking();

        assertThat(usersFromQuery).isNotNull();
        assertThat(usersFromQuery).hasSize(limit);
    }

    @Test
    public void queryLimitOffsetQuantity() {
        final List<User> users = putUsersBlocking(10);

        final int offset = 5;
        final int quantity = 3;
        final List<User> usersFromQuery = storIOSQLite
                .get()
                .listOfObjects(User.class)
                .withQuery(Query.builder()
                        .table(UserTableMeta.TABLE)
                        .orderBy(UserTableMeta.COLUMN_EMAIL)
                        .limit(offset, quantity)
                        .build())
                .prepare()
                .executeAsBlocking();

        assertThat(usersFromQuery).isNotNull();
        assertThat(usersFromQuery).hasSize(Math.min(quantity, users.size() - offset));

        Collections.sort(users);

        int position = 0;
        for (int i = offset; i < offset + quantity; i++) {
            assertThat(usersFromQuery.get(position++)).isEqualTo(users.get(i));
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

        assertThat(groupsOfUsers).isNotNull();
        assertThat(groupsOfUsers).hasSize(2);
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

        assertThat(groupsOfUsers).isNotNull();
        assertThat(groupsOfUsers).hasSize(1);
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

        assertThat(uniqueUsersFromQuery).isNotNull();
        assertThat(uniqueUsersFromQuery).hasSize(1);

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

        assertThat(allUsersFromQuery).isNotNull();
        assertThat(allUsersFromQuery).hasSize(users.size());
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

        assertThat(usersFromQuery).isEqualTo(usersWithLongName);
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

        assertThat(usersFromQuery).isNotNull();
        assertThat(usersFromQuery).hasSize(1);
        assertThat(usersFromQuery.get(0)).isEqualTo(testUser);
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

        assertThat(getAllUsersBlocking()).isEqualTo(users);
    }

    @Test
    public void getNumberOfResults() {
        putUsersBlocking(8);

        Integer numberOfResults = storIOSQLite
                .get()
                .numberOfResults()
                .withQuery(UserTableMeta.QUERY_ALL)
                .prepare()
                .executeAsBlocking();

        assertThat(numberOfResults).isEqualTo(8);
    }

    @Test
    public void queryOneExistedObject() {
        final List<User> users = putUsersBlocking(3);
        final User user = users.get(0);

        final User userFromQuery = storIOSQLite
                .get()
                .object(User.class)
                .withQuery(Query.builder()
                        .table(UserTableMeta.TABLE)
                        .where(UserTableMeta.COLUMN_EMAIL + "=?")
                        .whereArgs(user.email())
                        .build())
                .prepare()
                .executeAsBlocking();

        assertThat(userFromQuery).isNotNull();
        assertThat(userFromQuery).isEqualTo(user);
    }

    @Test
    public void queryOneNonExistedObject() {
        putUsersBlocking(3);

        final User userFromQuery = storIOSQLite
                .get()
                .object(User.class)
                .withQuery(Query.builder()
                        .table(UserTableMeta.TABLE)
                        .where(UserTableMeta.COLUMN_EMAIL + "=?")
                        .whereArgs("some arg")
                        .build())
                .prepare()
                .executeAsBlocking();

        assertThat(userFromQuery).isNull();
    }
}