package com.pushtorefresh.storio.content_resolver.impl;

import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.test.runner.AndroidJUnit4;

import com.pushtorefresh.storio.contentresolver.operations.get.DefaultGetResolver;
import com.pushtorefresh.storio.contentresolver.queries.Query;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Collections;
import java.util.List;

@RunWith(AndroidJUnit4.class)
public class QueryTest extends BaseTest {

    public QueryTest() {
        super();
    }

    @Test
    public void queryAll() {
        final List<User> users = putUsers(3);
        usersInStorageCheck(users);
    }

    @Test
    public void queryOneByField() {
        final List<User> users = putUsers(3);

        for (User user : users) {
            final List<User> usersFromQuery = storIOContentResolver
                    .get()
                    .listOfObjects(User.class)
                    .withQuery(Query.builder()
                            .uri(UserMeta.CONTENT_URI)
                            .where(UserMeta.COLUMN_EMAIL + "=?")
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

        putUsers(users);

        final List<User> usersFromQueryOrdered = storIOContentResolver
                .get()
                .listOfObjects(User.class)
                .withQuery(Query.builder()
                        .uri(UserMeta.CONTENT_URI)
                        .sortOrder(UserMeta.COLUMN_EMAIL)
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

        putUsers(users);

        final List<User> usersFromQueryOrdered = storIOContentResolver
                .get()
                .listOfObjects(User.class)
                .withQuery(Query.builder()
                        .uri(UserMeta.CONTENT_URI)
                        .sortOrder(UserMeta.COLUMN_EMAIL + " DESC")
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
    public void queryProjection() {
        final List<User> users = putUsers(3);

        final List<User> usersFromStorage = storIOContentResolver
                .get()
                .listOfObjects(User.class)
                .withQuery(Query.builder()
                        .uri(UserMeta.CONTENT_URI)
                        .columns(UserMeta.COLUMN_ID)
                        .build())
                .withGetResolver(new DefaultGetResolver<User>() {
                    @NonNull
                    @Override
                    public User mapFromCursor(@NonNull Cursor cursor) {
                        final Long id = cursor.getLong(cursor.getColumnIndex(UserMeta.COLUMN_ID));
                        return User.newInstance(id, null);
                    }
                })
                .prepare()
                .executeAsBlocking();

        assertNotNull(usersFromStorage);
        assertEquals(users.size(), usersFromStorage.size());

        for (int i = 0; i < users.size(); i++) {
            final User user = users.get(i);
            final User userFromStorage = usersFromStorage.get(i);
            assertEquals(user.id(), userFromStorage.id());
            assertNull(userFromStorage.email());
        }
    }
}