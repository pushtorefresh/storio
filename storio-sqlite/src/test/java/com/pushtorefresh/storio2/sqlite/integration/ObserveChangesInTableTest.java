package com.pushtorefresh.storio2.sqlite.integration;

import android.support.annotation.NonNull;

import com.pushtorefresh.storio2.sqlite.BuildConfig;
import com.pushtorefresh.storio2.sqlite.Changes;
import com.pushtorefresh.storio2.test.AbstractEmissionChecker;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

import static io.reactivex.BackpressureStrategy.LATEST;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)
public class ObserveChangesInTableTest extends BaseTest {

    public class EmissionChecker extends AbstractEmissionChecker<Changes> {

        public EmissionChecker(@NonNull Queue<Changes> expected) {
            super(expected);
        }

        @Override
        @NonNull
        public Disposable subscribe() {
            return storIOSQLite
                    .observeChangesInTable(UserTableMeta.TABLE, LATEST)
                    .subscribe(new Consumer<Changes>() {
                        @Override
                        public void accept(@NonNull Changes changes) {
                            onNextObtained(changes);
                        }
                    });
        }
    }

    @Test
    public void insertEmission() {
        final List<User> users = TestFactory.newUsers(10);

        final Queue<Changes> expectedChanges = new LinkedList<Changes>();
        expectedChanges.add(Changes.newInstance(UserTableMeta.TABLE, UserTableMeta.NOTIFICATION_TAG));

        final EmissionChecker emissionChecker = new EmissionChecker(expectedChanges);
        final Disposable disposable = emissionChecker.subscribe();

        putUsersBlocking(users);

        // Should receive changes of Users table
        emissionChecker.awaitNextExpectedValue();

        emissionChecker.assertThatNoExpectedValuesLeft();

        disposable.dispose();
    }

    @Test
    public void updateEmission() {
        final List<User> users = putUsersBlocking(10);
        final List<User> updated = new ArrayList<User>(users.size());

        for (User user : users) {
            updated.add(User.newInstance(user.id(), user.email()));
        }

        final Queue<Changes> expectedChanges = new LinkedList<Changes>();
        expectedChanges.add(Changes.newInstance(UserTableMeta.TABLE, UserTableMeta.NOTIFICATION_TAG));

        final EmissionChecker emissionChecker = new EmissionChecker(expectedChanges);
        final Disposable disposable = emissionChecker.subscribe();

        storIOSQLite
                .put()
                .objects(updated)
                .prepare()
                .executeAsBlocking();

        // Should receive changes of Users table
        emissionChecker.awaitNextExpectedValue();

        emissionChecker.assertThatNoExpectedValuesLeft();

        disposable.dispose();
    }

    @Test
    public void deleteEmission() {
        final List<User> users = putUsersBlocking(10);

        final Queue<Changes> expectedChanges = new LinkedList<Changes>();
        expectedChanges.add(Changes.newInstance(UserTableMeta.TABLE, UserTableMeta.NOTIFICATION_TAG));

        final EmissionChecker emissionChecker = new EmissionChecker(expectedChanges);
        final Disposable disposable = emissionChecker.subscribe();

        deleteUsersBlocking(users);

        // Should receive changes of Users table
        emissionChecker.awaitNextExpectedValue();

        emissionChecker.assertThatNoExpectedValuesLeft();

        disposable.dispose();
    }
}
