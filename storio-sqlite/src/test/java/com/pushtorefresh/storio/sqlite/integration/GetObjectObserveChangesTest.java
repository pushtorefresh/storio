package com.pushtorefresh.storio.sqlite.integration;


import com.pushtorefresh.storio.sqlite.operations.get.PreparedGetObject;

import org.junit.Test;

public class GetObjectObserveChangesTest extends BaseOperationObserveChangesTest {

    @Test
    public void repeatsOperationWithQueryByChangeOfTable() {
        User user = putUserBlocking();

        PreparedGetObject<User> operation = storIOSQLite
                .get()
                .object(User.class)
                .withQuery(query)
                .prepare();

        verifyChangesReceived(operation, tableChanges, user);
    }

    @Test
    public void repeatsOperationWithRawQueryByChangeOfTable() {
        User user = putUserBlocking();

        PreparedGetObject<User> operation = storIOSQLite
                .get()
                .object(User.class)
                .withQuery(query)
                .prepare();

        verifyChangesReceived(operation, tableChanges, user);
    }

    @Test
    public void repeatsOperationWithQueryByChangeOfTag() {
        User user = putUserBlocking();

        PreparedGetObject<User> operation = storIOSQLite
                .get()
                .object(User.class)
                .withQuery(query)
                .prepare();

        verifyChangesReceived(operation, tagChanges, user);
    }

    @Test
    public void repeatsOperationWithRawQueryByChangeOfTag() {
        User user = putUserBlocking();

        PreparedGetObject<User> operation = storIOSQLite
                .get()
                .object(User.class)
                .withQuery(query)
                .prepare();

        verifyChangesReceived(operation, tagChanges, user);
    }
}
