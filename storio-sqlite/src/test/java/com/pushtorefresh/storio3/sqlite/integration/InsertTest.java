package com.pushtorefresh.storio3.sqlite.integration;

import android.database.Cursor;

import com.pushtorefresh.storio3.sqlite.BuildConfig;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.List;

import androidx.sqlite.db.SupportSQLiteQueryBuilder;

import static org.assertj.core.api.Java6Assertions.assertThat;

@RunWith(RobolectricTestRunner.class)
public class InsertTest extends BaseTest {

    @Test
    public void insertOne() {
        final User user = putUserBlocking();

        // why we created StorIOSQLite: nobody loves nulls
        final Cursor cursor = db.query(SupportSQLiteQueryBuilder.builder(UserTableMeta.TABLE).create());

        // asserting that values was really inserted to db
        assertThat(cursor.getCount()).isEqualTo(1);
        assertThat(cursor.moveToFirst()).isTrue();

        final User insertedUser = UserTableMeta.GET_RESOLVER.mapFromCursor(storIOSQLite, cursor);

        assertThat(insertedUser.id()).isNotNull();
        assertThat(user.equalsExceptId(insertedUser)).isTrue();

        cursor.close();
    }

    @Test
    public void insertCollection() {
        final List<User> users = putUsersBlocking(3);

        // asserting that values was really inserted to db
        final Cursor cursor = db.query(SupportSQLiteQueryBuilder.builder(UserTableMeta.TABLE).create());

        assertThat(cursor.getCount()).isEqualTo(users.size());

        for (int i = 0; i < users.size(); i++) {
            assertThat(cursor.moveToNext()).isTrue();
            assertThat(UserTableMeta.GET_RESOLVER.mapFromCursor(storIOSQLite, cursor)).isEqualTo(users.get(i));
        }

        cursor.close();
    }

    @Test
    public void insertAndDeleteTwice() {
        final User user = TestFactory.newUser();

        for (int i = 0; i < 2; i++) {
            putUserBlocking(user);

            final List<User> existUsers = getAllUsersBlocking();

            assertThat(existUsers).isNotNull();
            assertThat(existUsers).hasSize(1);

            final Cursor cursorAfterPut = db.query(SupportSQLiteQueryBuilder.builder(UserTableMeta.TABLE).create());

            assertThat(cursorAfterPut.getCount()).isEqualTo(1);
            cursorAfterPut.close();

            deleteUserBlocking(user);

            final Cursor cursorAfterDelete = db.query(SupportSQLiteQueryBuilder.builder(UserTableMeta.TABLE).create());
            assertThat(cursorAfterDelete.getCount()).isEqualTo(0);
            cursorAfterDelete.close();
        }
    }

    @Test
    public void insertOneWithNullField() {
        User user = User.newInstance(null, "user@example.com", null); // phone is null
        putUserBlocking(user);

        final Cursor cursor = db.query(SupportSQLiteQueryBuilder.builder(UserTableMeta.TABLE).create());

        // asserting that values was really inserted to db
        assertThat(cursor.getCount()).isEqualTo(1);
        assertThat(cursor.moveToFirst()).isTrue();

        final User insertedUser = UserTableMeta.GET_RESOLVER.mapFromCursor(storIOSQLite, cursor);

        assertThat(insertedUser.id()).isNotNull();
        assertThat(user.equalsExceptId(insertedUser)).isTrue();

        cursor.close();
    }
}
