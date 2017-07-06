package com.pushtorefresh.storio.sqlite.integration;


import com.pushtorefresh.storio.sqlite.operations.get.PreparedGetNumberOfResults;

import org.junit.Test;

public class GetNumberOfResultsObserveChangesTest extends BaseOperationObserveChangesTest {

    @Test
    public void repeatsOperationWithQueryByChangeOfTable() {
        putUserBlocking();

        PreparedGetNumberOfResults operation = storIOSQLite
                .get()
                .numberOfResults()
                .withQuery(query)
                .prepare();

        verifyChangesReceived(operation, tableChanges, 1);
    }

    @Test
    public void repeatsOperationWithRawQueryByChangeOfTable() {
        putUserBlocking();

        PreparedGetNumberOfResults operation = storIOSQLite
                .get()
                .numberOfResults()
                .withQuery(rawQuery)
                .prepare();

        verifyChangesReceived(operation, tableChanges, 1);
    }

    @Test
    public void repeatsOperationWithQueryByChangeOfTag() {
        putUserBlocking();

        PreparedGetNumberOfResults operation = storIOSQLite
                .get()
                .numberOfResults()
                .withQuery(query)
                .prepare();

        verifyChangesReceived(operation, tagChanges, 1);
    }

    @Test
    public void repeatsOperationWithRawQueryByChangeOfTag() {
        putUserBlocking();

        PreparedGetNumberOfResults operation = storIOSQLite
                .get()
                .numberOfResults()
                .withQuery(rawQuery)
                .prepare();

        verifyChangesReceived(operation, tagChanges, 1);
    }
}
