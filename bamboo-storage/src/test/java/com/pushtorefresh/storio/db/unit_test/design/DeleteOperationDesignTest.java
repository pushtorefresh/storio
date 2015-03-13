package com.pushtorefresh.storio.db.unit_test.design;

import com.pushtorefresh.storio.db.operation.delete.DeleteCollectionOfObjectsResult;
import com.pushtorefresh.storio.db.operation.delete.DeleteResult;
import com.pushtorefresh.storio.db.query.DeleteQueryBuilder;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;

public class DeleteOperationDesignTest extends OperationDesignTest {

    @Test public void deleteObjectBlocking() {
        User user = newUser();

        DeleteResult deleteResult = bambooStorageDb()
                .delete()
                .object(user)
                .withMapFunc(User.MAP_TO_DELETE_QUERY)
                .prepare()
                .executeAsBlocking();
    }

    @Test public void deleteObjectObservable() {
        User user = newUser();

        Observable<DeleteResult> deleteResultObservable = bambooStorageDb()
                .delete()
                .object(user)
                .withMapFunc(User.MAP_TO_DELETE_QUERY)
                .prepare()
                .createObservable();
    }

    @Test public void deleteCollectionOfObjectsBlocking() {
        List<User> users = new ArrayList<>();

        DeleteCollectionOfObjectsResult<User> deleteResult = bambooStorageDb()
                .delete()
                .objects(users)
                .withMapFunc(User.MAP_TO_DELETE_QUERY)
                .prepare()
                .executeAsBlocking();
    }

    @Test public void deleteCollectionOfObjectsObservable() {
        List<User> users = new ArrayList<>();

        Observable<DeleteCollectionOfObjectsResult<User>> deleteResultObservable = bambooStorageDb()
                .delete()
                .objects(users)
                .withMapFunc(User.MAP_TO_DELETE_QUERY)
                .prepare()
                .createObservable();
    }

    @Test public void deleteByQueryBlocking() {
        DeleteResult deleteResult = bambooStorageDb()
                .delete()
                .byQuery(new DeleteQueryBuilder()
                        .table("users")
                        .where("email = ?")
                        .whereArgs("artem.zinnatullin@gmail.com")
                        .build())
                .prepare()
                .executeAsBlocking();
    }

    @Test public void deleteByQueryObservable() {
        Observable<DeleteResult> deleteResultObservable = bambooStorageDb()
                .delete()
                .byQuery(new DeleteQueryBuilder()
                        .table("users")
                        .where("email = ?")
                        .whereArgs("artem.zinnatullin@gmail.com")
                        .build())
                .prepare()
                .createObservable();
    }
}
