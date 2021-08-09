package com.pushtorefresh.storio3.sqlite.integration;

import android.support.annotation.NonNull;

import com.pushtorefresh.storio3.sqlite.BuildConfig;
import com.pushtorefresh.storio3.sqlite.SQLiteTypeMapping;
import com.pushtorefresh.storio3.sqlite.StorIOSQLite;
import com.pushtorefresh.storio3.sqlite.impl.DefaultStorIOSQLite;
import com.pushtorefresh.storio3.sqlite.operations.delete.DeleteResult;
import com.pushtorefresh.storio3.sqlite.operations.delete.DeleteResults;
import com.pushtorefresh.storio3.sqlite.operations.put.PutResult;
import com.pushtorefresh.storio3.sqlite.operations.put.PutResults;

import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.Timeout;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import java.util.List;

import androidx.sqlite.db.SupportSQLiteDatabase;
import androidx.sqlite.db.SupportSQLiteOpenHelper;
import androidx.sqlite.db.framework.FrameworkSQLiteOpenHelperFactory;
import io.reactivex.Scheduler;
import io.reactivex.schedulers.Schedulers;

import static org.assertj.core.api.Java6Assertions.assertThat;

@RunWith(RobolectricTestRunner.class)
public abstract class BaseTest {

    @Rule
    public Timeout timeout = Timeout.seconds(30);

    @NonNull
    protected StorIOSQLite storIOSQLite;

    @NonNull
    protected SupportSQLiteOpenHelper sqLiteOpenHelper;

    @NonNull
    protected SupportSQLiteDatabase db;

    @Before
    public void setUp() throws Exception {
        SupportSQLiteOpenHelper.Configuration configuration = SupportSQLiteOpenHelper.Configuration
                .builder(RuntimeEnvironment.application)
                .name(TestSQLiteCallback.DB_NAME)
                .callback(new TestSQLiteCallback())
                .build();

        sqLiteOpenHelper = new FrameworkSQLiteOpenHelperFactory().create(configuration);

        db = sqLiteOpenHelper.getWritableDatabase();

        storIOSQLite = DefaultStorIOSQLite.builder()
                .sqliteOpenHelper(sqLiteOpenHelper)
                .defaultRxScheduler(defaultRxScheduler())
                .addTypeMapping(User.class, SQLiteTypeMapping.<User>builder()
                        .putResolver(UserTableMeta.PUT_RESOLVER)
                        .getResolver(UserTableMeta.GET_RESOLVER)
                        .deleteResolver(UserTableMeta.DELETE_RESOLVER)
                        .build())
                .addTypeMapping(Tweet.class, SQLiteTypeMapping.<Tweet>builder()
                        .putResolver(TweetTableMeta.PUT_RESOLVER)
                        .getResolver(TweetTableMeta.GET_RESOLVER)
                        .deleteResolver(TweetTableMeta.DELETE_RESOLVER)
                        .build())
                .build();

        // clearing db before each test case
        storIOSQLite
                .delete()
                .byQuery(UserTableMeta.DELETE_QUERY_ALL)
                .prepare()
                .executeAsBlocking();

        storIOSQLite
                .delete()
                .byQuery(TweetTableMeta.DELETE_QUERY_ALL)
                .prepare()
                .executeAsBlocking();
    }

    @NonNull
    protected Scheduler defaultRxScheduler() {
        return Schedulers.io();
    }

    @NonNull
    List<User> getAllUsersBlocking() {
        return storIOSQLite
                .get()
                .listOfObjects(User.class)
                .withQuery(UserTableMeta.QUERY_ALL)
                .prepare()
                .executeAsBlocking();
    }

    @NonNull
    User putUserBlocking() {
        final User user = TestFactory.newUser();
        return putUserBlocking(user);
    }

    @NonNull
    User putUserBlocking(@NonNull final User user) {

        final PutResult putResult = storIOSQLite
                .put()
                .object(user)
                .prepare()
                .executeAsBlocking();

        assertThat(putResult).isNotNull();
        assertThat(putResult.wasInserted()).isTrue();
        return user;
    }

    @NonNull
    List<User> putUsersBlocking(final int size) {
        final List<User> users = TestFactory.newUsers(size);
        return putUsersBlocking(users);
    }

    @NonNull
    List<User> putUsersBlocking(@NonNull final List<User> users) {
        final PutResults<User> putResults = storIOSQLite
                .put()
                .objects(users)
                .prepare()
                .executeAsBlocking();

        assertThat(putResults.numberOfInserts()).isEqualTo(users.size());

        return users;
    }

    @NonNull
    DeleteResult deleteUserBlocking(@NonNull final User user) {
        final DeleteResult deleteResult = storIOSQLite
                .delete()
                .object(user)
                .prepare()
                .executeAsBlocking();

        assertThat(deleteResult.numberOfRowsDeleted()).isEqualTo(1);

        return deleteResult;
    }

    @NonNull
    DeleteResults<User> deleteUsersBlocking(@NonNull final List<User> users) {
        final DeleteResults<User> deleteResults = storIOSQLite
                .delete()
                .objects(users)
                .prepare()
                .executeAsBlocking();

        for (User user : users) {
            assertThat(deleteResults.wasDeleted(user)).isTrue();
        }

        return deleteResults;
    }
}
