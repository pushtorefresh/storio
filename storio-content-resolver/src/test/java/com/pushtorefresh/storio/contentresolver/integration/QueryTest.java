package com.pushtorefresh.storio.contentresolver.integration;

import android.database.Cursor;
import android.support.annotation.NonNull;

import com.pushtorefresh.storio.StorIOException;
import com.pushtorefresh.storio.contentresolver.BuildConfig;
import com.pushtorefresh.storio.contentresolver.operations.get.DefaultGetResolver;
import com.pushtorefresh.storio.contentresolver.operations.get.PreparedGetCursor;
import com.pushtorefresh.storio.contentresolver.queries.Query;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.failBecauseExceptionWasNotThrown;

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

        assertThat(usersFromQueryOrdered).isNotNull();
        assertThat(usersFromQueryOrdered).hasSize(users.size());

        // Reverse sorting by email for check ordering.
        Collections.reverse(users);

        for (int i = 0; i < users.size(); i++) {
            assertThat(usersFromQueryOrdered.get(i)).isEqualTo(users.get(i));
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

        assertThat(usersFromStorage).isNotNull();
        assertThat(usersFromStorage.size()).isEqualTo(users.size());

        for (int i = 0; i < users.size(); i++) {
            final User user = users.get(i);
            final User userFromStorage = usersFromStorage.get(i);
            assertThat(userFromStorage.id()).isEqualTo(user.id());
            assertThat(userFromStorage.email()).isNull();
        }
    }

    @Test
    public void shouldThrowExceptionIfCursorNullBlocking() {
        final PreparedGetCursor queryWithNullResult = createQueryWithNullResult();

        try {
            queryWithNullResult.executeAsBlocking();
            failBecauseExceptionWasNotThrown(StorIOException.class);
        } catch (StorIOException expected) {
            // it's okay, cursor was null
            assertThat(expected.getCause())
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessage("Cursor returned by content provider is null");
        }
    }
}