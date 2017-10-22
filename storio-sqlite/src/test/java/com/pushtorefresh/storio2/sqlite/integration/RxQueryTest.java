package com.pushtorefresh.storio2.sqlite.integration;

import android.support.annotation.NonNull;

import com.pushtorefresh.storio2.sqlite.BuildConfig;
import com.pushtorefresh.storio2.sqlite.Changes;
import com.pushtorefresh.storio2.sqlite.queries.Query;
import com.pushtorefresh.storio2.test.AbstractEmissionChecker;
import com.pushtorefresh.storio2.test.ConcurrencyTesting;
import com.pushtorefresh.storio2.test.Repeat;
import com.pushtorefresh.storio2.test.RepeatRule;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.CountDownLatch;

import io.reactivex.Flowable;
import io.reactivex.Single;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.observers.TestObserver;
import io.reactivex.subscribers.TestSubscriber;

import static io.reactivex.BackpressureStrategy.LATEST;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.assertj.core.api.Java6Assertions.assertThat;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)
public class RxQueryTest extends BaseTest {

    private class EmissionChecker extends AbstractEmissionChecker<List<User>> {

        EmissionChecker(@NonNull Queue<List<User>> expected) {
            super(expected);
        }

        @Override
        @NonNull
        public Disposable subscribe() {
            return storIOSQLite
                    .get()
                    .listOfObjects(User.class)
                    .withQuery(UserTableMeta.QUERY_ALL)
                    .prepare()
                    .asRxFlowable(LATEST)
                    .subscribe(new Consumer<List<User>>() {
                        @Override
                        public void accept(@NonNull List<User> users) {
                            onNextObtained(users);
                        }
                    });
        }
    }

    @Rule
    public RepeatRule repeat = new RepeatRule();

    @Test
    public void insertEmission() {
        final List<User> initialUsers = putUsersBlocking(10);
        final List<User> usersForInsert = TestFactory.newUsers(10);
        final List<User> allUsers = new ArrayList<User>(initialUsers.size() + usersForInsert.size());

        allUsers.addAll(initialUsers);
        allUsers.addAll(usersForInsert);

        final Queue<List<User>> expectedUsers = new LinkedList<List<User>>();
        expectedUsers.add(initialUsers);
        expectedUsers.add(allUsers);

        final EmissionChecker emissionChecker = new EmissionChecker(expectedUsers);
        final Disposable disposable = emissionChecker.subscribe();

        // Should receive initial users
        emissionChecker.awaitNextExpectedValue();

        putUsersBlocking(usersForInsert);

        // Should receive initial users + inserted users
        emissionChecker.awaitNextExpectedValue();

        emissionChecker.assertThatNoExpectedValuesLeft();

        disposable.dispose();
    }

    @Test
    public void updateEmission() {
        final List<User> users = putUsersBlocking(10);

        final Queue<List<User>> expectedUsers = new LinkedList<List<User>>();

        final List<User> updatedList = new ArrayList<User>(users.size());

        int count = 1;
        for (User user : users) {
            updatedList.add(User.newInstance(user.id(), "new_email" + count++));
        }
        expectedUsers.add(users);
        expectedUsers.add(updatedList);
        final EmissionChecker emissionChecker = new EmissionChecker(expectedUsers);
        final Disposable disposable = emissionChecker.subscribe();

        // Should receive all users
        emissionChecker.awaitNextExpectedValue();

        storIOSQLite
                .put()
                .objects(updatedList)
                .prepare()
                .executeAsBlocking();

        // Should receive updated users
        emissionChecker.awaitNextExpectedValue();

        emissionChecker.assertThatNoExpectedValuesLeft();

        disposable.dispose();
    }

    @Test
    public void deleteEmission() {
        final List<User> usersThatShouldBeSaved = TestFactory.newUsers(10);
        final List<User> usersThatShouldBeDeleted = TestFactory.newUsers(10);
        final List<User> allUsers = new ArrayList<User>();

        allUsers.addAll(usersThatShouldBeSaved);
        allUsers.addAll(usersThatShouldBeDeleted);

        putUsersBlocking(allUsers);

        final Queue<List<User>> expectedUsers = new LinkedList<List<User>>();

        expectedUsers.add(allUsers);
        expectedUsers.add(usersThatShouldBeSaved);

        final EmissionChecker emissionChecker = new EmissionChecker(expectedUsers);
        final Disposable disposable = emissionChecker.subscribe();

        // Should receive all users
        emissionChecker.awaitNextExpectedValue();

        deleteUsersBlocking(usersThatShouldBeDeleted);

        // Should receive users that should be saved
        emissionChecker.awaitNextExpectedValue();

        emissionChecker.assertThatNoExpectedValuesLeft();

        disposable.dispose();
    }

    @Test
    @Repeat(times = 20)
    public void concurrentPutWithoutGlobalTransaction() throws InterruptedException {
        final int numberOfConcurrentPuts = ConcurrencyTesting.optimalTestThreadsCount();

        TestSubscriber<Changes> testSubscriber = new TestSubscriber<Changes>();

        storIOSQLite
                .observeChangesInTable(TweetTableMeta.TABLE, LATEST)
                .subscribe(testSubscriber);

        final CountDownLatch concurrentPutLatch = new CountDownLatch(1);
        final CountDownLatch allPutsDoneLatch = new CountDownLatch(numberOfConcurrentPuts);

        for (int i = 0; i < numberOfConcurrentPuts; i++) {
            final int iCopy = i;

            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        concurrentPutLatch.await();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }

                    storIOSQLite
                            .put()
                            .object(Tweet.newInstance(null, 1L, "Some text: " + iCopy))
                            .prepare()
                            .executeAsBlocking();

                    allPutsDoneLatch.countDown();
                }
            }).start();
        }

        // Start concurrent Put operations.
        concurrentPutLatch.countDown();

        assertThat(allPutsDoneLatch.await(25, SECONDS)).isTrue();
        testSubscriber.assertNoErrors();

        // Put operation creates short-term transaction which might result in merge of some notifications.
        // So we have two extreme cases:
        // - no merged notifications → isEqualTo(numberOfParallelPuts)
        // - all notifications merged → isEqualTo(1)
        // Obviously truth is somewhere between those (depends on CPU of machine that runs test).
        assertThat(testSubscriber.valueCount())
                .isLessThanOrEqualTo(numberOfConcurrentPuts)
                .isGreaterThanOrEqualTo(1);
    }

    @Test
    public void nestedTransaction() {
        storIOSQLite.lowLevel().beginTransaction();

        storIOSQLite.lowLevel().beginTransaction();

        storIOSQLite.lowLevel().setTransactionSuccessful();
        storIOSQLite.lowLevel().endTransaction();

        storIOSQLite.lowLevel().setTransactionSuccessful();
        storIOSQLite.lowLevel().endTransaction();
    }

    @Test
    public void queryOneExistedObjectFlowable() {
        final List<User> users = putUsersBlocking(3);
        final User expectedUser = users.get(0);

        final Flowable<User> userFlowable = storIOSQLite
                .get()
                .object(User.class)
                .withQuery(Query.builder()
                        .table(UserTableMeta.TABLE)
                        .where(UserTableMeta.COLUMN_EMAIL + "=?")
                        .whereArgs(expectedUser.email())
                        .build())
                .prepare()
                .asRxFlowable(LATEST)
                .take(1);

        TestSubscriber<User> testSubscriber = new TestSubscriber<User>();
        userFlowable.subscribe(testSubscriber);

        testSubscriber.awaitTerminalEvent(5, SECONDS);
        testSubscriber.assertNoErrors();
        testSubscriber.assertValue(expectedUser);
    }

    @Test
    public void queryOneNonExistedObjectFlowable() {
        putUsersBlocking(3);

        final Flowable<User> userFlowable = storIOSQLite
                .get()
                .object(User.class)
                .withQuery(Query.builder()
                        .table(UserTableMeta.TABLE)
                        .where(UserTableMeta.COLUMN_EMAIL + "=?")
                        .whereArgs("some arg")
                        .build())
                .prepare()
                .asRxFlowable(LATEST)
                .take(1);

        TestSubscriber<User> testSubscriber = new TestSubscriber<User>();
        userFlowable.subscribe(testSubscriber);

        testSubscriber.awaitTerminalEvent(5, SECONDS);
        testSubscriber.assertNoErrors();
        testSubscriber.assertValue((User) null);
    }

    @Test
    public void queryOneExistedObjectTableUpdate() {
        User expectedUser = User.newInstance(null, "such@email.com");
        putUsersBlocking(3);

        final Flowable<User> userFlowable = storIOSQLite
                .get()
                .object(User.class)
                .withQuery(Query.builder()
                        .table(UserTableMeta.TABLE)
                        .where(UserTableMeta.COLUMN_EMAIL + "=?")
                        .whereArgs(expectedUser.email())
                        .build())
                .prepare()
                .asRxFlowable(LATEST)
                .take(2);

        TestSubscriber<User> testSubscriber = new TestSubscriber<User>();
        userFlowable.subscribe(testSubscriber);

        testSubscriber.awaitTerminalEvent(5, SECONDS);
        testSubscriber.assertNoErrors();
        testSubscriber.assertValue((User) null);

        putUserBlocking(expectedUser);

        testSubscriber.awaitTerminalEvent(5, SECONDS);
        testSubscriber.assertNoErrors();
        testSubscriber.assertValues(null, expectedUser);
    }

    @Test
    public void queryOneNonexistedObjectTableUpdate() {
        final Flowable<User> userFlowable = storIOSQLite
                .get()
                .object(User.class)
                .withQuery(Query.builder()
                        .table(UserTableMeta.TABLE)
                        .where(UserTableMeta.COLUMN_EMAIL + "=?")
                        .whereArgs("some arg")
                        .build())
                .prepare()
                .asRxFlowable(LATEST)
                .take(2);

        TestSubscriber<User> testSubscriber = new TestSubscriber<User>();
        userFlowable.subscribe(testSubscriber);

        testSubscriber.awaitTerminalEvent(5, SECONDS);
        testSubscriber.assertNoErrors();
        testSubscriber.assertValue((User) null);

        putUserBlocking();

        testSubscriber.awaitTerminalEvent(5, SECONDS);
        testSubscriber.assertNoErrors();
        testSubscriber.assertValues(null, null);
    }

    @Test
    public void queryListOfObjectsAsSingle() {
        final List<User> users = putUsersBlocking(10);

        final Single<List<User>> usersSingle = storIOSQLite
                .get()
                .listOfObjects(User.class)
                .withQuery(UserTableMeta.QUERY_ALL)
                .prepare()
                .asRxSingle();

        TestObserver<List<User>> testObserver = new TestObserver<List<User>>();
        usersSingle.subscribe(testObserver);

        testObserver.awaitTerminalEvent(5, SECONDS);
        testObserver.assertNoErrors();
        testObserver.assertValue(users);
        testObserver.assertComplete();
    }

    @Test
    public void queryObjectAsSingle() {
        final List<User> users = putUsersBlocking(3);

        final Single<User> usersSingle = storIOSQLite
                .get()
                .object(User.class)
                .withQuery(UserTableMeta.QUERY_ALL)
                .prepare()
                .asRxSingle();

        TestObserver<User> testObserver = new TestObserver<User>();
        usersSingle.subscribe(testObserver);

        testObserver.awaitTerminalEvent(5, SECONDS);
        testObserver.assertNoErrors();
        testObserver.assertValues(users.get(0));
        testObserver.assertComplete();
    }

    @Test
    public void queryNumberOfResultsAsSingle() {
        final List<User> users = putUsersBlocking(3);

        final Single<Integer> usersSingle = storIOSQLite
                .get()
                .numberOfResults()
                .withQuery(UserTableMeta.QUERY_ALL)
                .prepare()
                .asRxSingle();

        TestObserver<Integer> TestObserver = new TestObserver<Integer>();
        usersSingle.subscribe(TestObserver);

        TestObserver.awaitTerminalEvent(5, SECONDS);
        TestObserver.assertNoErrors();
        TestObserver.assertValue(users.size());
        TestObserver.assertComplete();
    }
}
