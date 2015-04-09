package com.pushtorefresh.storio.sqlite.operation.delete;

import com.pushtorefresh.storio.Loggi;
import com.pushtorefresh.storio.sqlite.StorIOSQLite;
import com.pushtorefresh.storio.sqlite.Changes;
import com.pushtorefresh.storio.operation.MapFunc;
import com.pushtorefresh.storio.sqlite.query.DeleteQuery;
import com.pushtorefresh.storio.sqlite.design.User;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class PreparedDeleteTest {

    // stub class to avoid violation of DRY in "deleteOne" tests
    private static class DeleteOneStub {
        final User user;
        final StorIOSQLite storIOSQLite;
        final StorIOSQLite.Internal internal;
        final MapFunc<User, DeleteQuery> mapFunc;
        final DeleteResolver deleteResolver;

        @SuppressWarnings("unchecked")
        DeleteOneStub() {
            user = new User(null, "test@example.com");
            storIOSQLite = mock(StorIOSQLite.class);
            internal = mock(StorIOSQLite.Internal.class);
            mapFunc = (MapFunc<User, DeleteQuery>) mock(MapFunc.class);
            deleteResolver = mock(DeleteResolver.class);

            when(storIOSQLite.internal())
                    .thenReturn(internal);

            when(storIOSQLite.delete())
                    .thenReturn(new PreparedDelete.Builder(storIOSQLite));

            when(mapFunc.map(user))
                    .thenReturn(User.MAP_TO_DELETE_QUERY.map(user));

            when(deleteResolver.performDelete(eq(storIOSQLite), any(DeleteQuery.class)))
                    .thenReturn(1);

            when(internal.getLoggi()).thenReturn(mock(Loggi.class));
        }

        void verifyBehavior() {
            // delete should be called only once
            verify(storIOSQLite, times(1)).delete();

            // object should be mapped to ContentValues only once
            verify(mapFunc, times(1)).map(user);

            // should be called once
            verify(deleteResolver, times(1)).performDelete(eq(storIOSQLite), any(DeleteQuery.class));

            // only one notification should be thrown
            verify(internal, times(1)).notifyAboutChanges(any(Changes.class));

            // change should occur only in users table
            verify(internal, times(1)).notifyAboutChanges(eq(Changes.newInstance(User.TABLE)));

            // we have mock deleteResolver, no real deletes should occur
            verify(internal, times(0)).delete(any(DeleteQuery.class));
        }
    }

    @Test public void deleteOneBlocking() {
        final DeleteOneStub deleteOneStub = new DeleteOneStub();

        deleteOneStub.storIOSQLite
                .delete()
                .object(deleteOneStub.user)
                .withMapFunc(deleteOneStub.mapFunc)
                .withDeleteResolver(deleteOneStub.deleteResolver)
                .prepare()
                .executeAsBlocking();

        deleteOneStub.verifyBehavior();
    }

    @Test public void deleteOneObservable() {
        final DeleteOneStub deleteOneStub = new DeleteOneStub();

        deleteOneStub.storIOSQLite
                .delete()
                .object(deleteOneStub.user)
                .withMapFunc(deleteOneStub.mapFunc)
                .withDeleteResolver(deleteOneStub.deleteResolver)
                .prepare()
                .createObservable()
                .toBlocking()
                .last();

        deleteOneStub.verifyBehavior();
    }

    // stub class to avoid violation of DRY in "deleteMultiple" tests
    private static class DeleteMultipleStub {
        final StorIOSQLite storIOSQLite;
        final StorIOSQLite.Internal internal;
        final MapFunc<User, DeleteQuery> mapFunc;
        final boolean useTransaction;
        final DeleteResolver deleteResolver;

        final List<User> users;
        final List<DeleteQuery> deleteQueries;


        private static final int NUMBER_OF_ITEMS_TO_DELETE = 3;

        @SuppressWarnings("unchecked")
        DeleteMultipleStub(boolean useTransaction) {
            this.useTransaction = useTransaction;

            users = new ArrayList<User>(NUMBER_OF_ITEMS_TO_DELETE);
            deleteQueries = new ArrayList<DeleteQuery>(NUMBER_OF_ITEMS_TO_DELETE);

            for (int i = 0; i < NUMBER_OF_ITEMS_TO_DELETE; i++) {
                final User user = new User((long) i, String.valueOf(i));
                users.add(user);
                deleteQueries.add(User.MAP_TO_DELETE_QUERY.map(user));
            }

            storIOSQLite = mock(StorIOSQLite.class);
            internal = mock(StorIOSQLite.Internal.class);
            mapFunc = (MapFunc<User, DeleteQuery>) mock(MapFunc.class);
            deleteResolver = mock(DeleteResolver.class);

            when(internal.transactionsSupported())
                    .thenReturn(useTransaction);

            when(storIOSQLite.internal())
                    .thenReturn(internal);

            when(storIOSQLite.delete())
                    .thenReturn(new PreparedDelete.Builder(storIOSQLite));


            for (int i = 0; i < NUMBER_OF_ITEMS_TO_DELETE; i++) {
                when(mapFunc.map(users.get(i)))
                        .thenReturn(deleteQueries.get(i));

                when(deleteResolver.performDelete(storIOSQLite, deleteQueries.get(i)))
                        .thenReturn(1);
            }
        }

        void verifyBehavior() {
            // only one call to storIOSQLite.delete() should occur
            verify(storIOSQLite, times(1)).delete();

            for (int i = 0; i < NUMBER_OF_ITEMS_TO_DELETE; i++) {
                // map operation for each object should be called only once
                verify(mapFunc, times(1)).map(users.get(i));
                verify(deleteResolver, times(1)).performDelete(storIOSQLite, deleteQueries.get(i));
            }

            if (useTransaction) {
                // if delete() operation used transaction, only one notification should be thrown
                verify(internal, times(1)).notifyAboutChanges(any(Changes.class));
                verify(internal, times(1)).notifyAboutChanges(eq(Changes.newInstance(Collections.singleton(User.TABLE))));
            } else {
                // if delete() operation didn't use transaction,
                // number of notifications should be equal to number of objects
                verify(internal, times(users.size())).notifyAboutChanges(any(Changes.class));

                // number of notifications about changes in users table should be equal to number of users
                verify(internal, times(users.size())).notifyAboutChanges(eq(Changes.newInstance(User.TABLE)));
            }

            // no real deletes should occur
            verify(internal, times(0)).delete(any(DeleteQuery.class));
        }
    }

    @Test public void deleteMultipleBlocking() {
        final DeleteMultipleStub deleteMultipleStub = new DeleteMultipleStub(true);

        deleteMultipleStub.storIOSQLite
                .delete()
                .objects(deleteMultipleStub.users)
                .withMapFunc(deleteMultipleStub.mapFunc)
                .withDeleteResolver(deleteMultipleStub.deleteResolver)
                .prepare()
                .executeAsBlocking();

        deleteMultipleStub.verifyBehavior();
    }

    @Test public void deleteMultipleObservable() {
        final DeleteMultipleStub deleteMultipleStub = new DeleteMultipleStub(true);

        deleteMultipleStub.storIOSQLite
                .delete()
                .objects(deleteMultipleStub.users)
                .withMapFunc(deleteMultipleStub.mapFunc)
                .withDeleteResolver(deleteMultipleStub.deleteResolver)
                .prepare()
                .createObservable()
                .toBlocking()
                .last();

        deleteMultipleStub.verifyBehavior();
    }

    @Test public void deleteMultipleBlockingWithoutTransaction() {
        final DeleteMultipleStub deleteMultipleStub = new DeleteMultipleStub(false);

        deleteMultipleStub.storIOSQLite
                .delete()
                .objects(deleteMultipleStub.users)
                .withMapFunc(deleteMultipleStub.mapFunc)
                .withDeleteResolver(deleteMultipleStub.deleteResolver)
                .dontUseTransaction()
                .prepare()
                .executeAsBlocking();

        deleteMultipleStub.verifyBehavior();
    }

    @Test public void deleteMultipleObservableWithoutTransaction() {
        final DeleteMultipleStub deleteMultipleStub = new DeleteMultipleStub(false);

        deleteMultipleStub.storIOSQLite
                .delete()
                .objects(deleteMultipleStub.users)
                .withMapFunc(deleteMultipleStub.mapFunc)
                .withDeleteResolver(deleteMultipleStub.deleteResolver)
                .dontUseTransaction()
                .prepare()
                .createObservable()
                .toBlocking()
                .last();

        deleteMultipleStub.verifyBehavior();
    }

    @Test public void deleteMultipleBlockingWithTransaction() {
        final DeleteMultipleStub deleteMultipleStub = new DeleteMultipleStub(true);

        deleteMultipleStub.storIOSQLite
                .delete()
                .objects(deleteMultipleStub.users)
                .withMapFunc(deleteMultipleStub.mapFunc)
                .withDeleteResolver(deleteMultipleStub.deleteResolver)
                .useTransactionIfPossible()
                .prepare()
                .executeAsBlocking();

        deleteMultipleStub.verifyBehavior();
    }

    @Test public void deleteMultipleObservableWithTransaction() {
        final DeleteMultipleStub deleteMultipleStub = new DeleteMultipleStub(true);

        deleteMultipleStub.storIOSQLite
                .delete()
                .objects(deleteMultipleStub.users)
                .withMapFunc(deleteMultipleStub.mapFunc)
                .withDeleteResolver(deleteMultipleStub.deleteResolver)
                .useTransactionIfPossible()
                .prepare()
                .createObservable()
                .toBlocking()
                .last();

        deleteMultipleStub.verifyBehavior();
    }
}
