package com.pushtorefresh.storio.sqlite.integration;


import com.pushtorefresh.storio.sqlite.operations.get.PreparedGetListOfObjects;

import org.junit.Test;

import static java.util.Collections.singletonList;

public class GetListOfObjectsObserveChangesTest extends BaseOperationObserveChangesTest {

    @Test
    public void repeatsOperationWithQueryByChangeOfTable() {
        User user = putUserBlocking();

        PreparedGetListOfObjects<User> operation = storIOSQLite
                .get()
                .listOfObjects(User.class)
                .withQuery(query)
                .prepare();

        verifyChangesReceived(operation, tableChanges, singletonList(user));
    }

    @Test
    public void repeatsOperationWithRawQueryByChangeOfTable() {
        User user = putUserBlocking();

        PreparedGetListOfObjects<User> operation = storIOSQLite
                .get()
                .listOfObjects(User.class)
                .withQuery(rawQuery)
                .prepare();

        verifyChangesReceived(operation, tableChanges, singletonList(user));
    }

    @Test
    public void repeatsOperationWithQueryByChangeOfTag() {
        User user = putUserBlocking();

        PreparedGetListOfObjects<User> operation = storIOSQLite
                .get()
                .listOfObjects(User.class)
                .withQuery(query)
                .prepare();

        verifyChangesReceived(operation, tagChanges, singletonList(user));
    }

    @Test
    public void repeatsOperationWithRawQueryByChangeOfTag() {
        User user = putUserBlocking();

        PreparedGetListOfObjects<User> operation = storIOSQLite
                .get()
                .listOfObjects(User.class)
                .withQuery(rawQuery)
                .prepare();

        verifyChangesReceived(operation, tagChanges, singletonList(user));
    }
}
