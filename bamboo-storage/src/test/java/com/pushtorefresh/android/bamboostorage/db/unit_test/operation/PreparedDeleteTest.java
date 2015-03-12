package com.pushtorefresh.android.bamboostorage.db.unit_test.operation;

import com.pushtorefresh.android.bamboostorage.db.BambooStorage;
import com.pushtorefresh.android.bamboostorage.db.operation.MapFunc;
import com.pushtorefresh.android.bamboostorage.db.operation.delete.PreparedDelete;
import com.pushtorefresh.android.bamboostorage.db.query.DeleteQuery;
import com.pushtorefresh.android.bamboostorage.db.unit_test.design.User;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class PreparedDeleteTest {

    // stub class to avoid violation of DRY in "deleteOne" tests
    private static class DeleteOneStub {
        final User user;
        final BambooStorage bambooStorage;
        final BambooStorage.Internal internal;
        final MapFunc<User, DeleteQuery> mapFunc;

        DeleteOneStub() {
            user = new User(null, "test@example.com");
            bambooStorage = mock(BambooStorage.class);
            internal = mock(BambooStorage.Internal.class);

            when(bambooStorage.internal())
                    .thenReturn(internal);

            when(bambooStorage.delete())
                    .thenReturn(new PreparedDelete.Builder(bambooStorage));

            //noinspection unchecked
            mapFunc = (MapFunc<User, DeleteQuery>) mock(MapFunc.class);

            when(mapFunc.map(user))
                    .thenReturn(mock(DeleteQuery.class));

        }

        void verifyBehavior() {
            // delete should be called only once
            verify(bambooStorage, times(1)).delete();

            // object should be mapped to ContentValues only once
            verify(mapFunc, times(1)).map(user);

            // only one notification should be thrown
            //noinspection unchecked
            verify(internal, times(1)).notifyAboutChanges(any(Set.class));
        }
    }

    @Test public void deleteOneBlocking() {
        final DeleteOneStub deleteOneStub = new DeleteOneStub();

        deleteOneStub.bambooStorage
                .delete()
                .object(deleteOneStub.user)
                .withMapFunc(deleteOneStub.mapFunc)
                .prepare()
                .executeAsBlocking();

        deleteOneStub.verifyBehavior();
    }

    @Test public void deleteOneObservable() {
        final DeleteOneStub deleteOneStub = new DeleteOneStub();

        deleteOneStub.bambooStorage
                .delete()
                .object(deleteOneStub.user)
                .withMapFunc(deleteOneStub.mapFunc)
                .prepare()
                .createObservable()
                .toBlocking()
                .last();

        deleteOneStub.verifyBehavior();
    }

    // stub class to avoid violation of DRY in "deleteMultiple" tests
    private static class DeleteMultipleStub {
        final List<User> users;
        final BambooStorage bambooStorage;
        final BambooStorage.Internal internal;
        final MapFunc<User, DeleteQuery> mapFunc;
        final boolean useTransaction;

        private static final int ITEMS_TO_DELETE_COUNT = 3;

        DeleteMultipleStub(boolean useTransaction) {
            this.useTransaction = useTransaction;

            users = new ArrayList<>(ITEMS_TO_DELETE_COUNT);
            for (int i = 0; i < ITEMS_TO_DELETE_COUNT; i++) {
                users.add(new User(null, String.valueOf(i)));
            }

            bambooStorage = mock(BambooStorage.class);
            internal = mock(BambooStorage.Internal.class);

            when(internal.areTransactionsSupported())
                    .thenReturn(useTransaction);

            when(bambooStorage.internal())
                    .thenReturn(internal);

            when(bambooStorage.delete())
                    .thenReturn(new PreparedDelete.Builder(bambooStorage));

            //noinspection unchecked
            mapFunc = (MapFunc<User, DeleteQuery>) mock(MapFunc.class);

            for (int i = 0; i < ITEMS_TO_DELETE_COUNT; i++) {
                when(mapFunc.map(users.get(i)))
                        .thenReturn(mock(DeleteQuery.class));
            }
        }

        void verifyBehavior() {
            // only one call to bambooStorage.delete() should occur
            verify(bambooStorage, times(1)).delete();

            for (final User user : users) {
                // map operation for each object should be called only once
                verify(mapFunc, times(1)).map(user);
            }

            if (useTransaction) {
                // if delete() operation used transaction, only one notification should be thrown

                //noinspection unchecked
                verify(internal, times(1)).notifyAboutChanges(any(Set.class));

            } else {
                // if delete() operation didn't use transaction,
                // number of notifications should be equal to number of objects

                //noinspection unchecked
                verify(internal, times(users.size())).notifyAboutChanges(any(Set.class));
            }
        }
    }

    @Test public void deleteMultipleBlocking() {
        final DeleteMultipleStub deleteMultipleStub = new DeleteMultipleStub(true);

        deleteMultipleStub.bambooStorage
                .delete()
                .objects(deleteMultipleStub.users)
                .withMapFunc(deleteMultipleStub.mapFunc)
                .prepare()
                .executeAsBlocking();

        deleteMultipleStub.verifyBehavior();
    }

    @Test public void deleteMultipleObservable() {
        final DeleteMultipleStub deleteMultipleStub = new DeleteMultipleStub(true);

        deleteMultipleStub.bambooStorage
                .delete()
                .objects(deleteMultipleStub.users)
                .withMapFunc(deleteMultipleStub.mapFunc)
                .prepare()
                .createObservable()
                .toBlocking()
                .last();

        deleteMultipleStub.verifyBehavior();
    }

    @Test public void deleteMultipleBlockingWithoutTransaction() {
        final DeleteMultipleStub deleteMultipleStub = new DeleteMultipleStub(false);

        deleteMultipleStub.bambooStorage
                .delete()
                .objects(deleteMultipleStub.users)
                .withMapFunc(deleteMultipleStub.mapFunc)
                .dontUseTransaction()
                .prepare()
                .executeAsBlocking();

        deleteMultipleStub.verifyBehavior();
    }

    @Test public void deleteMultipleObservableWithoutTransaction() {
        final DeleteMultipleStub deleteMultipleStub = new DeleteMultipleStub(false);

        deleteMultipleStub.bambooStorage
                .delete()
                .objects(deleteMultipleStub.users)
                .withMapFunc(deleteMultipleStub.mapFunc)
                .dontUseTransaction()
                .prepare()
                .createObservable()
                .toBlocking()
                .last();

        deleteMultipleStub.verifyBehavior();
    }

    @Test public void deleteMultipleBlockingWithTransaction() {
        final DeleteMultipleStub deleteMultipleStub = new DeleteMultipleStub(true);

        deleteMultipleStub.bambooStorage
                .delete()
                .objects(deleteMultipleStub.users)
                .withMapFunc(deleteMultipleStub.mapFunc)
                .useTransactionIfPossible()
                .prepare()
                .executeAsBlocking();

        deleteMultipleStub.verifyBehavior();
    }

    @Test public void deleteMultipleObservableWithTransaction() {
        final DeleteMultipleStub deleteMultipleStub = new DeleteMultipleStub(true);

        deleteMultipleStub.bambooStorage
                .delete()
                .objects(deleteMultipleStub.users)
                .withMapFunc(deleteMultipleStub.mapFunc)
                .useTransactionIfPossible()
                .prepare()
                .createObservable()
                .toBlocking()
                .last();

        deleteMultipleStub.verifyBehavior();
    }
}
