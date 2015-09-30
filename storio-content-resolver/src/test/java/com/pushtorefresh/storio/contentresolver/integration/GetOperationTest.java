package com.pushtorefresh.storio.contentresolver.integration;

import android.content.ContentValues;
import android.database.Cursor;

import com.pushtorefresh.storio.contentresolver.BuildConfig;
import com.pushtorefresh.storio.contentresolver.Changes;
import com.pushtorefresh.storio.contentresolver.queries.Query;

import org.assertj.android.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import java.util.List;

import rx.observers.TestSubscriber;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.assertj.core.api.Assertions.assertThat;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)
public class GetOperationTest extends IntegrationTest {

    @Test
    public void getCursorExecuteAsBlocking() {
        TestSubscriber<Changes> changesTestSubscriber = new TestSubscriber<Changes>();

        storIOContentResolver
                .observeChangesOfUri(TestItem.CONTENT_URI)
                .take(1)
                .subscribe(changesTestSubscriber);

        TestItem testItemToInsert = TestItem.create(null, "value");
        contentResolver.insert(TestItem.CONTENT_URI, testItemToInsert.toContentValues());

        Cursor cursor = storIOContentResolver
                .get()
                .cursor()
                .withQuery(Query.builder()
                        .uri(TestItem.CONTENT_URI)
                        .build())
                .prepare()
                .executeAsBlocking();

        Assertions.assertThat(cursor).hasCount(1);

        cursor.moveToFirst();

        assertThat(testItemToInsert.equalsWithoutId(TestItem.fromCursor(cursor))).isTrue();

        changesTestSubscriber.awaitTerminalEvent(60, SECONDS);
        changesTestSubscriber.assertNoErrors();
        changesTestSubscriber.assertValue(Changes.newInstance(TestItem.CONTENT_URI));
    }

    @Test
    public void getListOfObjectsExecuteAsBlocking() {
        TestSubscriber<Changes> changesTestSubscriber = new TestSubscriber<Changes>();

        storIOContentResolver
                .observeChangesOfUri(TestItem.CONTENT_URI)
                .take(1)
                .subscribe(changesTestSubscriber);

        TestItem testItemToInsert = TestItem.create(null, "value");
        contentResolver.insert(TestItem.CONTENT_URI, testItemToInsert.toContentValues());

        List<TestItem> list = storIOContentResolver
                .get()
                .listOfObjects(TestItem.class)
                .withQuery(Query.builder()
                        .uri(TestItem.CONTENT_URI)
                        .build())
                .prepare()
                .executeAsBlocking();

        assertThat(list).hasSize(1);

        assertThat(testItemToInsert.equalsWithoutId(list.get(0))).isTrue();

        changesTestSubscriber.awaitTerminalEvent(60, SECONDS);
        changesTestSubscriber.assertNoErrors();
        changesTestSubscriber.assertValue(Changes.newInstance(TestItem.CONTENT_URI));
    }

    @Test
    public void getCursorAsObservableOnlyInitialValue() {
        final TestSubscriber<Changes> changesTestSubscriber = new TestSubscriber<Changes>();

        storIOContentResolver
                .observeChangesOfUri(TestItem.CONTENT_URI)
                .take(1)
                .subscribe(changesTestSubscriber);

        TestItem testItemToInsert = TestItem.create(null, "value");

        contentResolver.insert(TestItem.CONTENT_URI, testItemToInsert.toContentValues());

        final TestSubscriber<Cursor> cursorTestSubscriber = new TestSubscriber<Cursor>();

        storIOContentResolver
                .get()
                .cursor()
                .withQuery(Query.builder()
                        .uri(TestItem.CONTENT_URI)
                        .build())
                .prepare()
                .createObservable()
                .take(1)
                .subscribe(cursorTestSubscriber);

        cursorTestSubscriber.awaitTerminalEvent(60, SECONDS);
        cursorTestSubscriber.assertNoErrors();

        List<Cursor> listOfCursors = cursorTestSubscriber.getOnNextEvents();

        assertThat(listOfCursors).hasSize(1);

        Assertions.assertThat(listOfCursors.get(0)).hasCount(1);
        listOfCursors.get(0).moveToFirst();
        assertThat(testItemToInsert.equalsWithoutId(TestItem.fromCursor(listOfCursors.get(0))))
                .isTrue();

        changesTestSubscriber.awaitTerminalEvent(60, SECONDS);
        changesTestSubscriber.assertNoErrors();
        changesTestSubscriber.assertValues(Changes.newInstance(TestItem.CONTENT_URI));
    }

    @Test
    public void getListOfObjectsAsObservableOnlyInitialValue() {
        final TestSubscriber<Changes> changesTestSubscriber = new TestSubscriber<Changes>();

        storIOContentResolver
                .observeChangesOfUri(TestItem.CONTENT_URI)
                .take(1)
                .subscribe(changesTestSubscriber);

        TestItem testItemToInsert = TestItem.create(null, "value");

        contentResolver.insert(TestItem.CONTENT_URI, testItemToInsert.toContentValues());

        final TestSubscriber<List<TestItem>> listTestSubscriber = new TestSubscriber<List<TestItem>>();

        storIOContentResolver
                .get()
                .listOfObjects(TestItem.class)
                .withQuery(Query.builder()
                        .uri(TestItem.CONTENT_URI)
                        .build())
                .prepare()
                .createObservable()
                .take(1)
                .subscribe(listTestSubscriber);

        listTestSubscriber.awaitTerminalEvent(60, SECONDS);
        listTestSubscriber.assertNoErrors();

        assertThat(listTestSubscriber.getOnNextEvents()).hasSize(1);
        assertThat(testItemToInsert.equalsWithoutId(listTestSubscriber.getOnNextEvents().get(0).get(0)))
                .isTrue();

        changesTestSubscriber.awaitTerminalEvent(60, SECONDS);
        changesTestSubscriber.assertNoErrors();
        changesTestSubscriber.assertValues(Changes.newInstance(TestItem.CONTENT_URI));
    }

    @Test
    public void getNumberOfResults() {
        TestSubscriber<Changes> changesTestSubscriber = new TestSubscriber<Changes>();

        storIOContentResolver
                .observeChangesOfUri(TestItem.CONTENT_URI)
                .take(1)
                .subscribe(changesTestSubscriber);

        final int testItemsQuantity = 8;

        ContentValues[] contentValues = new ContentValues[testItemsQuantity];
        for (int i = 0; i < testItemsQuantity; ++i) {
            TestItem testItemToInsert = TestItem.create(null, "value");
            contentValues[i] = testItemToInsert.toContentValues();
        }
        contentResolver.bulkInsert(TestItem.CONTENT_URI, contentValues);

        Integer numberOfResults = storIOContentResolver
                .get()
                .numberOfResults()
                .withQuery(Query.builder()
                        .uri(TestItem.CONTENT_URI)
                        .build())
                .prepare()
                .executeAsBlocking();

        assertThat(numberOfResults).isEqualTo(testItemsQuantity);

        changesTestSubscriber.awaitTerminalEvent(60, SECONDS);
        changesTestSubscriber.assertNoErrors();
        changesTestSubscriber.assertValue(Changes.newInstance(TestItem.CONTENT_URI));
    }
}
