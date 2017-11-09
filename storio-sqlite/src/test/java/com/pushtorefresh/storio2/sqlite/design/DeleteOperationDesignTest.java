package com.pushtorefresh.storio2.sqlite.design;

import com.pushtorefresh.storio2.sqlite.operations.delete.DeleteResult;
import com.pushtorefresh.storio2.sqlite.operations.delete.DeleteResults;
import com.pushtorefresh.storio2.sqlite.queries.DeleteQuery;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Single;

import static io.reactivex.BackpressureStrategy.MISSING;

public class DeleteOperationDesignTest extends OperationDesignTest {

    @Test
    public void deleteObjectBlocking() {
        User user = newUser();

        DeleteResult deleteResult = storIOSQLite()
                .delete()
                .object(user)
                .withDeleteResolver(UserTableMeta.DELETE_RESOLVER)
                .prepare()
                .executeAsBlocking();
    }

    @Test
    public void deleteObjectObservable() {
        User user = newUser();

        Flowable<DeleteResult> deleteResultObservable = storIOSQLite()
                .delete()
                .object(user)
                .withDeleteResolver(UserTableMeta.DELETE_RESOLVER)
                .prepare()
                .asRxFlowable(MISSING);
    }

    @Test
    public void deleteCollectionOfObjectsBlocking() {
        List<User> users = new ArrayList<User>();

        DeleteResults<User> deleteResult = storIOSQLite()
                .delete()
                .objects(users)
                .withDeleteResolver(UserTableMeta.DELETE_RESOLVER)
                .prepare()
                .executeAsBlocking();
    }

    @Test
    public void deleteCollectionOfObjectsObservable() {
        List<User> users = new ArrayList<User>();

        Flowable<DeleteResults<User>> deleteResultObservable = storIOSQLite()
                .delete()
                .objects(users)
                .withDeleteResolver(UserTableMeta.DELETE_RESOLVER)
                .prepare()
                .asRxFlowable(MISSING);
    }

    @Test
    public void deleteByQueryBlocking() {
        DeleteResult deleteResult = storIOSQLite()
                .delete()
                .byQuery(DeleteQuery.builder()
                        .table("users")
                        .where("email = ?")
                        .whereArgs("artem.zinnatullin@gmail.com")
                        .build())
                .prepare()
                .executeAsBlocking();
    }

    @Test
    public void deleteByQueryObservable() {
        Flowable<DeleteResult> deleteResultObservable = storIOSQLite()
                .delete()
                .byQuery(DeleteQuery.builder()
                        .table("users")
                        .where("email = ?")
                        .whereArgs("artem.zinnatullin@gmail.com")
                        .build())
                .prepare()
                .asRxFlowable(MISSING);
    }

    @Test
    public void deleteObjectSingle() {
        User user = newUser();

        Single<DeleteResult> deleteResultSingle = storIOSQLite()
                .delete()
                .object(user)
                .withDeleteResolver(UserTableMeta.DELETE_RESOLVER)
                .prepare()
                .asRxSingle();
    }

    @Test
    public void deleteCollectionOfObjectsSingle() {
        List<User> users = new ArrayList<User>();

        Single<DeleteResults<User>> deleteResultsSingle = storIOSQLite()
                .delete()
                .objects(users)
                .withDeleteResolver(UserTableMeta.DELETE_RESOLVER)
                .prepare()
                .asRxSingle();
    }

    @Test
    public void deleteByQuerySingle() {
        Single<DeleteResult> deleteResultSingle = storIOSQLite()
                .delete()
                .byQuery(DeleteQuery.builder()
                        .table("users")
                        .where("email = ?")
                        .whereArgs("artem.zinnatullin@gmail.com")
                        .build())
                .prepare()
                .asRxSingle();
    }

    @Test
    public void deleteObjectCompletable() {
        User user = newUser();

        Completable completableDelete = storIOSQLite()
                .delete()
                .object(user)
                .withDeleteResolver(UserTableMeta.DELETE_RESOLVER)
                .prepare()
                .asRxCompletable();
    }

    @Test
    public void deleteCollectionOfObjectsCompletable() {
        List<User> users = new ArrayList<User>();

        Completable completableDelete = storIOSQLite()
                .delete()
                .objects(users)
                .withDeleteResolver(UserTableMeta.DELETE_RESOLVER)
                .prepare()
                .asRxCompletable();
    }

    @Test
    public void deleteByQueryCompletable() {
        Completable completableDelete = storIOSQLite()
                .delete()
                .byQuery(DeleteQuery.builder()
                        .table("users")
                        .where("email = ?")
                        .whereArgs("artem.zinnatullin@gmail.com")
                        .build())
                .prepare()
                .asRxCompletable();
    }
}
