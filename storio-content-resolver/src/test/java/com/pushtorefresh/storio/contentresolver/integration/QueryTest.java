package com.pushtorefresh.storio.contentresolver.integration;

import android.database.Cursor;
import android.support.annotation.NonNull;

import com.pushtorefresh.storio.StorIOException;
import com.pushtorefresh.storio.contentresolver.BuildConfig;
import com.pushtorefresh.storio.contentresolver.TestUtils;
import com.pushtorefresh.storio.contentresolver.operations.get.DefaultGetResolver;
import com.pushtorefresh.storio.contentresolver.operations.get.PreparedGetCursor;
import com.pushtorefresh.storio.contentresolver.queries.Query;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)
public class QueryTest extends IntegrationTest {

    @Test
    public void queryAll() {
        final List<User> users = insertUsers(3);
        checkThatTheseUsersInStorage(users);
    }

    @Test
    public void queryOneByField() {
        final List<User> users = insertUsers(3);

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

        insertUsers(users);

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

        insertUsers(users);

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
        final List<User> users = insertUsers(3);

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

    @Test
    public void shouldThrowExceptionIfCursorNullBlocking() {
        final PreparedGetCursor queryWithNullResult = createQueryWithNullResult();
        try {
            queryWithNullResult.executeAsBlocking();
            fail("StorIOException should be thrown");
        } catch (StorIOException expected) {
            // it's okay, cursor was null
            TestUtils.checkException(
                    expected,
                    IllegalStateException.class,
                    "Cursor returned by content provider is null");
        }
    }
}