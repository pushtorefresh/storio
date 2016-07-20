package com.pushtorefresh.storio.sqlite.operations.execute;

import android.support.annotation.NonNull;

import com.pushtorefresh.storio.sqlite.Changes;
import com.pushtorefresh.storio.sqlite.StorIOSQLite;
import com.pushtorefresh.storio.sqlite.operations.SchedulerChecker;
import com.pushtorefresh.storio.sqlite.queries.RawQuery;
import com.pushtorefresh.storio.test.ObservableBehaviorChecker;

import org.junit.Test;

import java.util.HashSet;

import rx.Observable;
import rx.Single;
import rx.functions.Action1;

import static java.util.Arrays.asList;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class PreparedExecuteSQLTest {

    @Test
    public void executeSQLBlockingWithoutNotifications() {
        final Stub stub = Stub.newInstanceWithoutNotification();

        stub.storIOSQLite
                .executeSQL()
                .withQuery(stub.rawQuery)
                .prepare()
                .executeAsBlocking();

        verify(stub.storIOSQLite, never()).defaultScheduler();
        stub.verifyBehavior();
    }

    @Test
    public void executeSQLBlockingWithNotification() {
        final Stub stub = Stub.newInstanceWithNotification();

        stub.storIOSQLite
                .executeSQL()
                .withQuery(stub.rawQuery)
                .prepare()
                .executeAsBlocking();

        verify(stub.storIOSQLite, never()).defaultScheduler();
        stub.verifyBehavior();
    }

    @Test
    public void executeSQLObservableWithoutNotifications() {
        final Stub stub = Stub.newInstanceWithoutNotification();

        final Observable<Object> observable = stub.storIOSQLite
                .executeSQL()
                .withQuery(stub.rawQuery)
                .prepare()
                .asRxObservable();

        verify(stub.storIOSQLite).defaultScheduler();
        stub.verifyBehavior(observable);
    }

    @Test
    public void executeSQLSingleWithNotification() {
        final Stub stub = Stub.newInstanceWithNotification();

        final Single<Object> single = stub.storIOSQLite
                .executeSQL()
                .withQuery(stub.rawQuery)
                .prepare()
                .asRxSingle();

        verify(stub.storIOSQLite).defaultScheduler();
        stub.verifyBehavior(single);
    }

    @Test
    public void executeSQLSingleWithoutNotifications() {
        final Stub stub = Stub.newInstanceWithoutNotification();

        final Single<Object> single = stub.storIOSQLite
                .executeSQL()
                .withQuery(stub.rawQuery)
                .prepare()
                .asRxSingle();

        verify(stub.storIOSQLite).defaultScheduler();
        stub.verifyBehavior(single);
    }

    @Test
    public void executeSQLObservableWithNotification() {
        final Stub stub = Stub.newInstanceWithNotification();

        final Observable<Object> observable = stub.storIOSQLite
                .executeSQL()
                .withQuery(stub.rawQuery)
                .prepare()
                .asRxObservable();

        verify(stub.storIOSQLite).defaultScheduler();
        stub.verifyBehavior(observable);
    }

    @Test
    public void executeSQLObservableExecutesOnSpecifiedScheduler() {
        final Stub stub = Stub.newInstanceWithNotification();
        final SchedulerChecker schedulerChecker = SchedulerChecker.create(stub.storIOSQLite);

        final PreparedExecuteSQL operation = stub.storIOSQLite
                .executeSQL()
                .withQuery(stub.rawQuery)
                .prepare();

        schedulerChecker.checkAsObservable(operation);
    }

    @Test
    public void executeSQLSingleExecutesOnSpecifiedScheduler() {
        final Stub stub = Stub.newInstanceWithNotification();
        final SchedulerChecker schedulerChecker = SchedulerChecker.create(stub.storIOSQLite);

        final PreparedExecuteSQL operation = stub.storIOSQLite
                .executeSQL()
                .withQuery(stub.rawQuery)
                .prepare();

        schedulerChecker.checkAsSingle(operation);
    }

    static class Stub {

        private final StorIOSQLite storIOSQLite;
        private final StorIOSQLite.Internal internal;
        private final RawQuery rawQuery;
        private final boolean queryWithNotification;

        private final String[] affectedTables = {"test_table1", "test_table2"};

        @NonNull
        public static Stub newInstanceWithoutNotification() {
            return new Stub(false);
        }

        @NonNull
        public static Stub newInstanceWithNotification() {
            return new Stub(true);
        }

        private Stub(boolean queryWithNotification) {
            this.queryWithNotification = queryWithNotification;

            storIOSQLite = mock(StorIOSQLite.class);
            internal = mock(StorIOSQLite.Internal.class);

            if (queryWithNotification) {
                rawQuery = RawQuery.builder()
                        .query("DROP TABLE users!")
                        .affectsTables(affectedTables)
                        .build();
            } else {
                rawQuery = RawQuery.builder()
                        .query("DROP TABLE users!")
                        .build();
            }

            when(storIOSQLite.lowLevel())
                    .thenReturn(internal);

            when(storIOSQLite.executeSQL())
                    .thenReturn(new PreparedExecuteSQL.Builder(storIOSQLite));
        }

        @SuppressWarnings("unchecked")
        void verifyBehavior() {
            // storIOSQLite.executeSQL() should be called once
            verify(storIOSQLite).executeSQL();

            // storIOSQLite.internal.executeSQL() should be called once for ANY RawQuery
            verify(internal).executeSQL(any(RawQuery.class));

            // storIOSQLite.internal.executeSQL() should be called once for required RawQuery
            verify(internal).executeSQL(rawQuery);

            if (queryWithNotification) {
                verify(internal).notifyAboutChanges(eq(Changes.newInstance(new HashSet<String>(asList(affectedTables)))));
            } else {
                verify(internal, never()).notifyAboutChanges(any(Changes.class));
            }
        }

        void verifyBehavior(@NonNull Observable<Object> observable) {
            new ObservableBehaviorChecker<Object>()
                    .observable(observable)
                    .expectedNumberOfEmissions(1)
                    .testAction(new Action1<Object>() {
                        @Override
                        public void call(Object anObject) {
                            verifyBehavior();
                        }
                    })
                    .checkBehaviorOfObservable();
        }

        void verifyBehavior(@NonNull Single<Object> single) {
            new ObservableBehaviorChecker<Object>()
                    .observable(single.toObservable())
                    .expectedNumberOfEmissions(1)
                    .testAction(new Action1<Object>() {
                        @Override
                        public void call(Object anObject) {
                            verifyBehavior();
                        }
                    })
                    .checkBehaviorOfObservable();
        }
    }
}
