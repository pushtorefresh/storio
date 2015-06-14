package com.pushtorefresh.storio.sqlite.design;

import com.pushtorefresh.storio.sqlite.operation.delete.DeleteResult;
import com.pushtorefresh.storio.sqlite.operation.delete.DeleteResults;
import com.pushtorefresh.storio.sqlite.query.DeleteQuery;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;

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

        Observable<DeleteResult> deleteResultObservable = storIOSQLite()
                .delete()
                .object(user)
                .withDeleteResolver(UserTableMeta.DELETE_RESOLVER)
                .prepare()
                .createObservable();
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

        Observable<DeleteResults<User>> deleteResultObservable = storIOSQLite()
                .delete()
                .objects(users)
                .withDeleteResolver(UserTableMeta.DELETE_RESOLVER)
                .prepare()
                .createObservable();
    }

    @Test
    public void deleteByQueryBlocking() {
        DeleteResult deleteResult = storIOSQLite()
                .delete()
                .byQuery(new DeleteQuery.Builder()
                        .table("users")
                        .where("email = ?")
                        .whereArgs("artem.zinnatullin@gmail.com")
                        .build())
                .prepare()
                .executeAsBlocking();
    }

    @Test
    public void deleteByQueryObservable() {
        Observable<DeleteResult> deleteResultObservable = storIOSQLite()
                .delete()
                .byQuery(new DeleteQuery.Builder()
                        .table("users")
                        .where("email = ?")
                        .whereArgs("artem.zinnatullin@gmail.com")
                        .build())
                .prepare()
                .createObservable();
    }
}
