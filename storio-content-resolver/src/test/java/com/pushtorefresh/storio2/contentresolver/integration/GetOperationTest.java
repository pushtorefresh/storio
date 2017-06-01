package com.pushtorefresh.storio2.contentresolver.integration;

import android.content.ContentValues;
import android.database.Cursor;

import com.pushtorefresh.storio2.contentresolver.BuildConfig;
import com.pushtorefresh.storio2.contentresolver.Changes;
import com.pushtorefresh.storio2.contentresolver.queries.Query;

import org.assertj.android.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.List;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.Single;
import io.reactivex.observers.TestObserver;
import io.reactivex.subscribers.TestSubscriber;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.assertj.core.api.Java6Assertions.assertThat;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)
public class GetOperationTest extends IntegrationTest {

    @Test
    public void getCursorExecuteAsBlocking() {
        final TestSubscriber<Changes> changesTestSubscriber = new TestSubscriber<Changes>();

        storIOContentResolver
                .observeChangesOfUri(TestItem.CONTENT_URI, BackpressureStrategy.MISSING)
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
        final TestSubscriber<Changes> changesTestSubscriber = new TestSubscriber<Changes>();

        storIOContentResolver
                .observeChangesOfUri(TestItem.CONTENT_URI, BackpressureStrategy.MISSING)
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
    public void getCursorAsFlowableOnlyInitialValue() {
        final TestSubscriber<Changes> changesTestSubscriber = new TestSubscriber<Changes>();

        storIOContentResolver
                .observeChangesOfUri(TestItem.CONTENT_URI, BackpressureStrategy.MISSING)
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
                .asRxFlowable(BackpressureStrategy.MISSING)
                .take(1)
                .subscribe(cursorTestSubscriber);

        cursorTestSubscriber.awaitTerminalEvent(60, SECONDS);
        cursorTestSubscriber.assertNoErrors();

        List<Cursor> listOfCursors = cursorTestSubscriber.values();

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
    public void getListOfObjectsAsFlowableOnlyInitialValue() {
        final TestSubscriber<Changes> changesTestSubscriber = new TestSubscriber<Changes>();

        storIOContentResolver
                .observeChangesOfUri(TestItem.CONTENT_URI, BackpressureStrategy.MISSING)
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
                .asRxFlowable(BackpressureStrategy.MISSING)
                .take(1)
                .subscribe(listTestSubscriber);

        listTestSubscriber.awaitTerminalEvent(60, SECONDS);
        listTestSubscriber.assertNoErrors();

        assertThat(listTestSubscriber.values()).hasSize(1);
        assertThat(testItemToInsert.equalsWithoutId(listTestSubscriber.values().get(0).get(0)))
                .isTrue();

        changesTestSubscriber.awaitTerminalEvent(60, SECONDS);
        changesTestSubscriber.assertNoErrors();
        changesTestSubscriber.assertValues(Changes.newInstance(TestItem.CONTENT_URI));
    }

    @Test
    public void getCursorAsSingleOnlyInitialValue() {
        final TestSubscriber<Changes> changesTestSubscriber = new TestSubscriber<Changes>();

        storIOContentResolver
                .observeChangesOfUri(TestItem.CONTENT_URI, BackpressureStrategy.MISSING)
                .take(1)
                .subscribe(changesTestSubscriber);

        TestItem testItemToInsert = TestItem.create(null, "value");

        contentResolver.insert(TestItem.CONTENT_URI, testItemToInsert.toContentValues());

        final TestObserver<Cursor> cursorTestObserver = new TestObserver<Cursor>();

        storIOContentResolver
                .get()
                .cursor()
                .withQuery(Query.builder()
                        .uri(TestItem.CONTENT_URI)
                        .build())
                .prepare()
                .asRxSingle()
                .subscribe(cursorTestObserver);

        cursorTestObserver.awaitTerminalEvent(60, SECONDS);
        cursorTestObserver.assertNoErrors();

        List<Cursor> listOfCursors = cursorTestObserver.values();

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
    public void getListOfObjectsAsSingleOnlyInitialValue() {
        final TestSubscriber<Changes> changesTestSubscriber = new TestSubscriber<Changes>();

        storIOContentResolver
                .observeChangesOfUri(TestItem.CONTENT_URI, BackpressureStrategy.MISSING)
                .take(1)
                .subscribe(changesTestSubscriber);

        TestItem testItemToInsert = TestItem.create(null, "value");

        contentResolver.insert(TestItem.CONTENT_URI, testItemToInsert.toContentValues());

        final TestObserver<List<TestItem>> listTestObserver = new TestObserver<List<TestItem>>();

        storIOContentResolver
                .get()
                .listOfObjects(TestItem.class)
                .withQuery(Query.builder()
                        .uri(TestItem.CONTENT_URI)
                        .build())
                .prepare()
                .asRxSingle()
                .subscribe(listTestObserver);

        listTestObserver.awaitTerminalEvent(60, SECONDS);
        listTestObserver.assertNoErrors();

        List<List<TestItem>> listOfObjects = listTestObserver.values();

        assertThat(listOfObjects).hasSize(1);

        assertThat(listTestObserver.values()).hasSize(1);
        assertThat(testItemToInsert.equalsWithoutId(listTestObserver.values().get(0).get(0)))
                .isTrue();

        changesTestSubscriber.awaitTerminalEvent(60, SECONDS);
        changesTestSubscriber.assertNoErrors();
        changesTestSubscriber.assertValues(Changes.newInstance(TestItem.CONTENT_URI));
    }

    @Test
    public void getNumberOfResults() {
        final TestSubscriber<Changes> changesTestSubscriber = new TestSubscriber<Changes>();

        storIOContentResolver
                .observeChangesOfUri(TestItem.CONTENT_URI, BackpressureStrategy.MISSING)
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

    @Test
    public void getExistedObjectExecuteAsBlocking() {
        final TestSubscriber<Changes> changesTestSubscriber = new TestSubscriber<Changes>();

        storIOContentResolver
                .observeChangesOfUri(TestItem.CONTENT_URI, BackpressureStrategy.MISSING)
                .take(1)
                .subscribe(changesTestSubscriber);

        TestItem testItemToInsert = TestItem.create(null, "value");
        contentResolver.insert(TestItem.CONTENT_URI, testItemToInsert.toContentValues());
        contentResolver.insert(TestItem.CONTENT_URI, TestItem.create(null, "value1").toContentValues());
        contentResolver.insert(TestItem.CONTENT_URI, TestItem.create(null, "value2").toContentValues());

        TestItem testItem = storIOContentResolver
                .get()
                .object(TestItem.class)
                .withQuery(Query.builder()
                        .uri(TestItem.CONTENT_URI)
                        .where(TestItem.COLUMN_VALUE + "=?")
                        .whereArgs("value")
                        .build())
                .prepare()
                .executeAsBlocking();

        assertThat(testItem).isNotNull();

        assertThat(testItemToInsert.equalsWithoutId(testItem)).isTrue();

        changesTestSubscriber.awaitTerminalEvent(60, SECONDS);
        changesTestSubscriber.assertNoErrors();
        changesTestSubscriber.assertValue(Changes.newInstance(TestItem.CONTENT_URI));
    }

    @Test
    public void getNonExistedObjectExecuteAsBlocking() {
        final TestSubscriber<Changes> changesTestSubscriber = new TestSubscriber<Changes>();

        storIOContentResolver
                .observeChangesOfUri(TestItem.CONTENT_URI, BackpressureStrategy.MISSING)
                .take(1)
                .subscribe(changesTestSubscriber);

        TestItem testItemToInsert = TestItem.create(null, "value");
        contentResolver.insert(TestItem.CONTENT_URI, testItemToInsert.toContentValues());

        TestItem testItem = storIOContentResolver
                .get()
                .object(TestItem.class)
                .withQuery(Query.builder()
                        .uri(TestItem.CONTENT_URI)
                        .where(TestItem.COLUMN_VALUE + "=?")
                        .whereArgs("some value")
                        .build())
                .prepare()
                .executeAsBlocking();

        assertThat(testItem).isNull();

        changesTestSubscriber.awaitTerminalEvent(60, SECONDS);
        changesTestSubscriber.assertNoErrors();
        changesTestSubscriber.assertValue(Changes.newInstance(TestItem.CONTENT_URI));
    }

    @Test
    public void getExistedObjectExecuteAsFlowable() {
        final TestSubscriber<Changes> changesTestSubscriber = new TestSubscriber<Changes>();

        storIOContentResolver
                .observeChangesOfUri(TestItem.CONTENT_URI, BackpressureStrategy.MISSING)
                .take(1)
                .subscribe(changesTestSubscriber);

        TestItem expectedItem = TestItem.create(null, "value");
        contentResolver.insert(TestItem.CONTENT_URI, expectedItem.toContentValues());
        contentResolver.insert(TestItem.CONTENT_URI, TestItem.create(null, "value1").toContentValues());
        contentResolver.insert(TestItem.CONTENT_URI, TestItem.create(null, "value2").toContentValues());

        Flowable<TestItem> testItemFlowable = storIOContentResolver
                .get()
                .object(TestItem.class)
                .withQuery(Query.builder()
                        .uri(TestItem.CONTENT_URI)
                        .where(TestItem.COLUMN_VALUE + "=?")
                        .whereArgs("value")
                        .build())
                .prepare()
                .asRxFlowable(BackpressureStrategy.MISSING)
                .take(1);

        TestSubscriber<TestItem> testSubscriber = new TestSubscriber<TestItem>();
        testItemFlowable.subscribe(testSubscriber);

        testSubscriber.awaitTerminalEvent(5, SECONDS);
        testSubscriber.assertNoErrors();

        List<TestItem> emmitedItems = testSubscriber.values();
        assertThat(emmitedItems.size()).isEqualTo(1);
        assertThat(expectedItem.equalsWithoutId(emmitedItems.get(0))).isTrue();

        changesTestSubscriber.awaitTerminalEvent(60, SECONDS);
        changesTestSubscriber.assertNoErrors();
        changesTestSubscriber.assertValue(Changes.newInstance(TestItem.CONTENT_URI));
    }

    @Test
    public void getNonExistedObjectExecuteAsFlowable() {
        final TestSubscriber<Changes> changesTestSubscriber = new TestSubscriber<Changes>();

        storIOContentResolver
                .observeChangesOfUri(TestItem.CONTENT_URI, BackpressureStrategy.MISSING)
                .take(1)
                .subscribe(changesTestSubscriber);

        contentResolver.insert(TestItem.CONTENT_URI, TestItem.create(null, "value").toContentValues());

        Flowable<TestItem> testItemFlowable = storIOContentResolver
                .get()
                .object(TestItem.class)
                .withQuery(Query.builder()
                        .uri(TestItem.CONTENT_URI)
                        .where(TestItem.COLUMN_VALUE + "=?")
                        .whereArgs("some value")
                        .build())
                .prepare()
                .asRxFlowable(BackpressureStrategy.MISSING)
                .take(1);

        TestSubscriber<TestItem> testSubscriber = new TestSubscriber<TestItem>();
        testItemFlowable.subscribe(testSubscriber);

        testSubscriber.awaitTerminalEvent(5, SECONDS);
        testSubscriber.assertValue((TestItem) null);
        testSubscriber.assertNoErrors();

        changesTestSubscriber.awaitTerminalEvent(60, SECONDS);
        changesTestSubscriber.assertNoErrors();
        changesTestSubscriber.assertValue(Changes.newInstance(TestItem.CONTENT_URI));
    }

    @Test
    public void getExistedObjectExecuteAsSingle() {
        final TestSubscriber<Changes> changesTestSubscriber = new TestSubscriber<Changes>();

        storIOContentResolver
                .observeChangesOfUri(TestItem.CONTENT_URI, BackpressureStrategy.MISSING)
                .take(1)
                .subscribe(changesTestSubscriber);

        TestItem expectedItem = TestItem.create(null, "value");
        contentResolver.insert(TestItem.CONTENT_URI, expectedItem.toContentValues());
        contentResolver.insert(TestItem.CONTENT_URI, TestItem.create(null, "value1").toContentValues());
        contentResolver.insert(TestItem.CONTENT_URI, TestItem.create(null, "value2").toContentValues());

        Single<TestItem> testItemSingle = storIOContentResolver
                .get()
                .object(TestItem.class)
                .withQuery(Query.builder()
                        .uri(TestItem.CONTENT_URI)
                        .where(TestItem.COLUMN_VALUE + "=?")
                        .whereArgs("value")
                        .build())
                .prepare()
                .asRxSingle();

        TestObserver<TestItem> testObserver = new TestObserver<TestItem>();
        testItemSingle.subscribe(testObserver);

        testObserver.awaitTerminalEvent(5, SECONDS);
        testObserver.assertNoErrors();

        List<TestItem> emmitedItems = testObserver.values();
        assertThat(emmitedItems.size()).isEqualTo(1);
        assertThat(expectedItem.equalsWithoutId(emmitedItems.get(0))).isTrue();

        changesTestSubscriber.awaitTerminalEvent(60, SECONDS);
        changesTestSubscriber.assertNoErrors();
        changesTestSubscriber.assertValue(Changes.newInstance(TestItem.CONTENT_URI));
    }

    @Test
    public void getNonExistedObjectExecuteAsSingle() {
        final TestSubscriber<Changes> changesTestSubscriber = new TestSubscriber<Changes>();

        storIOContentResolver
                .observeChangesOfUri(TestItem.CONTENT_URI, BackpressureStrategy.MISSING)
                .take(1)
                .subscribe(changesTestSubscriber);

        contentResolver.insert(TestItem.CONTENT_URI, TestItem.create(null, "value").toContentValues());

        Single<TestItem> testItemSingle = storIOContentResolver
                .get()
                .object(TestItem.class)
                .withQuery(Query.builder()
                        .uri(TestItem.CONTENT_URI)
                        .where(TestItem.COLUMN_VALUE + "=?")
                        .whereArgs("some value")
                        .build())
                .prepare()
                .asRxSingle();

        TestObserver<TestItem> testObserver = new TestObserver<TestItem>();
        testItemSingle.subscribe(testObserver);

        testObserver.awaitTerminalEvent(5, SECONDS);
        testObserver.assertValue((TestItem) null);
        testObserver.assertNoErrors();

        changesTestSubscriber.awaitTerminalEvent(60, SECONDS);
        changesTestSubscriber.assertNoErrors();
        changesTestSubscriber.assertValue(Changes.newInstance(TestItem.CONTENT_URI));
    }

    @Test
    public void getOneExistedObjectTableUpdate() {
        TestItem expectedItem = TestItem.create(null, "value");

        contentResolver.insert(TestItem.CONTENT_URI, TestItem.create(null, "value1").toContentValues());
        contentResolver.insert(TestItem.CONTENT_URI, TestItem.create(null, "value2").toContentValues());
        contentResolver.insert(TestItem.CONTENT_URI, TestItem.create(null, "value3").toContentValues());

        Flowable<TestItem> testItemFlowable = storIOContentResolver
                .get()
                .object(TestItem.class)
                .withQuery(Query.builder()
                        .uri(TestItem.CONTENT_URI)
                        .where(TestItem.COLUMN_VALUE + "=?")
                        .whereArgs("value")
                        .build())
                .prepare()
                .asRxFlowable(BackpressureStrategy.MISSING)
                .take(2);

        TestSubscriber<TestItem> testSubscriber = new TestSubscriber<TestItem>();
        testItemFlowable.subscribe(testSubscriber);

        testSubscriber.awaitTerminalEvent(5, SECONDS);
        testSubscriber.assertValue((TestItem) null);
        testSubscriber.assertNoErrors();

        contentResolver.insert(TestItem.CONTENT_URI, expectedItem.toContentValues());

        testSubscriber.awaitTerminalEvent(5, SECONDS);
        testSubscriber.assertNoErrors();

        List<TestItem> emmitedItems = testSubscriber.values();
        assertThat(emmitedItems.size()).isEqualTo(2);
        assertThat(emmitedItems.get(0)).isNull();
        assertThat(expectedItem.equalsWithoutId(emmitedItems.get(1))).isTrue();
    }

    @Test
    public void getOneNonexistedObjectTableUpdate() {
        contentResolver.insert(TestItem.CONTENT_URI, TestItem.create(null, "value1").toContentValues());
        contentResolver.insert(TestItem.CONTENT_URI, TestItem.create(null, "value2").toContentValues());
        contentResolver.insert(TestItem.CONTENT_URI, TestItem.create(null, "value3").toContentValues());

        Flowable<TestItem> testItemFlowable = storIOContentResolver
                .get()
                .object(TestItem.class)
                .withQuery(Query.builder()
                        .uri(TestItem.CONTENT_URI)
                        .where(TestItem.COLUMN_VALUE + "=?")
                        .whereArgs("value")
                        .build())
                .prepare()
                .asRxFlowable(BackpressureStrategy.MISSING)
                .take(2);

        TestSubscriber<TestItem> testSubscriber = new TestSubscriber<TestItem>();
        testItemFlowable.subscribe(testSubscriber);

        testSubscriber.awaitTerminalEvent(5, SECONDS);
        testSubscriber.assertValue((TestItem) null);
        testSubscriber.assertNoErrors();

        contentResolver.insert(TestItem.CONTENT_URI, TestItem.create(null, "value4").toContentValues());

        testSubscriber.awaitTerminalEvent(5, SECONDS);
        testSubscriber.assertValues(null, null);
        testSubscriber.assertNoErrors();
    }
}
