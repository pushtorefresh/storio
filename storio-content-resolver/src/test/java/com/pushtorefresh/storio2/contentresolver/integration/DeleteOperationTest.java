package com.pushtorefresh.storio2.contentresolver.integration;

import android.database.Cursor;

import com.pushtorefresh.storio2.contentresolver.BuildConfig;
import com.pushtorefresh.storio2.contentresolver.Changes;
import com.pushtorefresh.storio2.contentresolver.operations.delete.DeleteResult;
import com.pushtorefresh.storio2.contentresolver.operations.delete.DeleteResults;
import com.pushtorefresh.storio2.contentresolver.queries.DeleteQuery;

import org.assertj.android.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import io.reactivex.BackpressureStrategy;
import io.reactivex.subscribers.TestSubscriber;

import static java.util.Collections.singletonList;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.assertj.core.api.Java6Assertions.assertThat;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)
public class DeleteOperationTest extends IntegrationTest {

    @Test
    public void deleteByQueryExecuteAsBlocking() {
        TestSubscriber<Changes> changesTestSubscriber = new TestSubscriber<Changes>();

        storIOContentResolver
                .observeChangesOfUri(TestItem.CONTENT_URI, BackpressureStrategy.MISSING)
                .take(2)
                .subscribe(changesTestSubscriber);

        TestItem testItemToInsert = TestItem.create(null, "value");
        contentResolver.insert(TestItem.CONTENT_URI, testItemToInsert.toContentValues());

        Cursor firstDbState = contentResolver.query(TestItem.CONTENT_URI, null, null, null, null);
        Assertions.assertThat(firstDbState).hasCount(1);

        DeleteResult deleteResult = storIOContentResolver
                .delete()
                .byQuery(DeleteQuery.builder()
                        .uri(TestItem.CONTENT_URI)
                        .build())
                .prepare()
                .executeAsBlocking();

        assertThat(deleteResult.numberOfRowsDeleted()).isEqualTo(1);

        Cursor secondDbState = contentResolver.query(TestItem.CONTENT_URI, null, null, null, null);
        Assertions.assertThat(secondDbState).hasCount(0);

        changesTestSubscriber.awaitTerminalEvent(60, SECONDS);
        changesTestSubscriber.assertNoErrors();
        changesTestSubscriber.assertValues(Changes.newInstance(TestItem.CONTENT_URI), Changes.newInstance(TestItem.CONTENT_URI));
    }

    @Test
    public void deleteByQueryAsObservable() {
        TestSubscriber<Changes> changesTestSubscriber = new TestSubscriber<Changes>();

        storIOContentResolver
                .observeChangesOfUri(TestItem.CONTENT_URI, BackpressureStrategy.MISSING)
                .take(2)
                .subscribe(changesTestSubscriber);

        TestItem testItemToInsert = TestItem.create(null, "value");
        contentResolver.insert(TestItem.CONTENT_URI, testItemToInsert.toContentValues());

        Cursor firstDbState = contentResolver.query(TestItem.CONTENT_URI, null, null, null, null);
        Assertions.assertThat(firstDbState).hasCount(1);

        DeleteResult deleteResult = storIOContentResolver
                .delete()
                .byQuery(DeleteQuery.builder()
                        .uri(TestItem.CONTENT_URI)
                        .build())
                .prepare()
                .asRxFlowable(BackpressureStrategy.MISSING)
                .blockingFirst();

        assertThat(deleteResult.numberOfRowsDeleted()).isEqualTo(1);

        Cursor secondDbState = contentResolver.query(TestItem.CONTENT_URI, null, null, null, null);
        Assertions.assertThat(secondDbState).hasCount(0);

        changesTestSubscriber.awaitTerminalEvent(60, SECONDS);
        changesTestSubscriber.assertNoErrors();
        changesTestSubscriber.assertValues(Changes.newInstance(TestItem.CONTENT_URI), Changes.newInstance(TestItem.CONTENT_URI));
    }

    @Test
    public void deleteByQueryAsSingle() {
        TestSubscriber<Changes> changesTestSubscriber = new TestSubscriber<Changes>();

        storIOContentResolver
                .observeChangesOfUri(TestItem.CONTENT_URI, BackpressureStrategy.MISSING)
                .take(2)
                .subscribe(changesTestSubscriber);

        TestItem testItemToInsert = TestItem.create(null, "value");
        contentResolver.insert(TestItem.CONTENT_URI, testItemToInsert.toContentValues());

        Cursor firstDbState = contentResolver.query(TestItem.CONTENT_URI, null, null, null, null);
        Assertions.assertThat(firstDbState).hasCount(1);

        DeleteResult deleteResult = storIOContentResolver
                .delete()
                .byQuery(DeleteQuery.builder()
                        .uri(TestItem.CONTENT_URI)
                        .build())
                .prepare()
                .asRxSingle()
                .blockingGet();

        assertThat(deleteResult.numberOfRowsDeleted()).isEqualTo(1);

        Cursor secondDbState = contentResolver.query(TestItem.CONTENT_URI, null, null, null, null);
        Assertions.assertThat(secondDbState).hasCount(0);

        changesTestSubscriber.awaitTerminalEvent(60, SECONDS);
        changesTestSubscriber.assertNoErrors();
        changesTestSubscriber.assertValues(Changes.newInstance(TestItem.CONTENT_URI), Changes.newInstance(TestItem.CONTENT_URI));
    }

    @Test
    public void deleteByQueryAsCompletable() {
        TestSubscriber<Changes> changesTestSubscriber = new TestSubscriber<Changes>();

        storIOContentResolver
                .observeChangesOfUri(TestItem.CONTENT_URI, BackpressureStrategy.MISSING)
                .take(2)
                .subscribe(changesTestSubscriber);

        TestItem testItemToInsert = TestItem.create(null, "value");
        contentResolver.insert(TestItem.CONTENT_URI, testItemToInsert.toContentValues());

        Cursor firstDbState = contentResolver.query(TestItem.CONTENT_URI, null, null, null, null);
        Assertions.assertThat(firstDbState).hasCount(1);

        storIOContentResolver
                .delete()
                .byQuery(DeleteQuery.builder()
                        .uri(TestItem.CONTENT_URI)
                        .build())
                .prepare()
                .asRxCompletable()
                .blockingAwait(15, SECONDS);

        Cursor secondDbState = contentResolver.query(TestItem.CONTENT_URI, null, null, null, null);
        Assertions.assertThat(secondDbState).hasCount(0);

        changesTestSubscriber.awaitTerminalEvent(60, SECONDS);
        changesTestSubscriber.assertNoErrors();
        changesTestSubscriber.assertValues(Changes.newInstance(TestItem.CONTENT_URI), Changes.newInstance(TestItem.CONTENT_URI));
    }

    @Test
    public void deleteObjectExecuteAsBlocking() {
        TestSubscriber<Changes> changesTestSubscriber = new TestSubscriber<Changes>();

        storIOContentResolver
                .observeChangesOfUri(TestItem.CONTENT_URI, BackpressureStrategy.MISSING)
                .take(2)
                .subscribe(changesTestSubscriber);

        TestItem testItemToInsert = TestItem.create(null, "value");
        contentResolver.insert(TestItem.CONTENT_URI, testItemToInsert.toContentValues());

        Cursor firstDbState = contentResolver.query(TestItem.CONTENT_URI, null, null, null, null);
        Assertions.assertThat(firstDbState).hasCount(1);

        //noinspection ConstantConditions
        assertThat(firstDbState.moveToFirst()).isTrue();

        TestItem testItem = TestItem.fromCursor(firstDbState);

        DeleteResult deleteResult = storIOContentResolver
                .delete()
                .object(testItem)
                .prepare()
                .executeAsBlocking();

        assertThat(deleteResult.numberOfRowsDeleted()).isEqualTo(1);

        Cursor secondDbState = contentResolver.query(TestItem.CONTENT_URI, null, null, null, null);
        Assertions.assertThat(secondDbState).hasCount(0);

        changesTestSubscriber.awaitTerminalEvent(60, SECONDS);
        changesTestSubscriber.assertNoErrors();
        changesTestSubscriber.assertValues(Changes.newInstance(TestItem.CONTENT_URI), Changes.newInstance(TestItem.CONTENT_URI));
    }

    @Test
    public void deleteObjectasRxObservable() {
        TestSubscriber<Changes> changesTestSubscriber = new TestSubscriber<Changes>();

        storIOContentResolver
                .observeChangesOfUri(TestItem.CONTENT_URI, BackpressureStrategy.MISSING)
                .take(2)
                .subscribe(changesTestSubscriber);

        TestItem testItemToInsert = TestItem.create(null, "value");
        contentResolver.insert(TestItem.CONTENT_URI, testItemToInsert.toContentValues());

        Cursor firstDbState = contentResolver.query(TestItem.CONTENT_URI, null, null, null, null);
        Assertions.assertThat(firstDbState).hasCount(1);

        //noinspection ConstantConditions
        assertThat(firstDbState.moveToFirst()).isTrue();

        TestItem testItem = TestItem.fromCursor(firstDbState);

        DeleteResult deleteResult = storIOContentResolver
                .delete()
                .object(testItem)
                .prepare()
                .asRxFlowable(BackpressureStrategy.MISSING)
                .blockingFirst();

        assertThat(deleteResult.numberOfRowsDeleted()).isEqualTo(1);

        Cursor secondDbState = contentResolver.query(TestItem.CONTENT_URI, null, null, null, null);
        Assertions.assertThat(secondDbState).hasCount(0);

        changesTestSubscriber.awaitTerminalEvent(60, SECONDS);
        changesTestSubscriber.assertNoErrors();
        changesTestSubscriber.assertValues(Changes.newInstance(TestItem.CONTENT_URI), Changes.newInstance(TestItem.CONTENT_URI));
    }

    @Test
    public void deleteObjectAsSingle() {
        TestSubscriber<Changes> changesTestSubscriber = new TestSubscriber<Changes>();

        storIOContentResolver
                .observeChangesOfUri(TestItem.CONTENT_URI, BackpressureStrategy.MISSING)
                .take(2)
                .subscribe(changesTestSubscriber);

        TestItem testItemToInsert = TestItem.create(null, "value");
        contentResolver.insert(TestItem.CONTENT_URI, testItemToInsert.toContentValues());

        Cursor firstDbState = contentResolver.query(TestItem.CONTENT_URI, null, null, null, null);
        Assertions.assertThat(firstDbState).hasCount(1);

        //noinspection ConstantConditions
        assertThat(firstDbState.moveToFirst()).isTrue();

        TestItem testItem = TestItem.fromCursor(firstDbState);

        DeleteResult deleteResult = storIOContentResolver
                .delete()
                .object(testItem)
                .prepare()
                .asRxSingle()
                .blockingGet();

        assertThat(deleteResult.numberOfRowsDeleted()).isEqualTo(1);

        Cursor secondDbState = contentResolver.query(TestItem.CONTENT_URI, null, null, null, null);
        Assertions.assertThat(secondDbState).hasCount(0);

        changesTestSubscriber.awaitTerminalEvent(60, SECONDS);
        changesTestSubscriber.assertNoErrors();
        changesTestSubscriber.assertValues(Changes.newInstance(TestItem.CONTENT_URI), Changes.newInstance(TestItem.CONTENT_URI));
    }

    @Test
    public void deleteObjectAsCompletable() {
        TestSubscriber<Changes> changesTestSubscriber = new TestSubscriber<Changes>();

        storIOContentResolver
                .observeChangesOfUri(TestItem.CONTENT_URI, BackpressureStrategy.MISSING)
                .take(2)
                .subscribe(changesTestSubscriber);

        TestItem testItemToInsert = TestItem.create(null, "value");
        contentResolver.insert(TestItem.CONTENT_URI, testItemToInsert.toContentValues());

        Cursor firstDbState = contentResolver.query(TestItem.CONTENT_URI, null, null, null, null);
        Assertions.assertThat(firstDbState).hasCount(1);

        //noinspection ConstantConditions
        assertThat(firstDbState.moveToFirst()).isTrue();

        TestItem testItem = TestItem.fromCursor(firstDbState);

        storIOContentResolver
                .delete()
                .object(testItem)
                .prepare()
                .asRxCompletable()
                .blockingAwait(15, SECONDS);

        Cursor secondDbState = contentResolver.query(TestItem.CONTENT_URI, null, null, null, null);
        Assertions.assertThat(secondDbState).hasCount(0);

        changesTestSubscriber.awaitTerminalEvent(60, SECONDS);
        changesTestSubscriber.assertNoErrors();
        changesTestSubscriber.assertValues(Changes.newInstance(TestItem.CONTENT_URI), Changes.newInstance(TestItem.CONTENT_URI));
    }

    @Test
    public void deleteObjectsExecuteAsBlocking() {
        TestSubscriber<Changes> changesTestSubscriber = new TestSubscriber<Changes>();

        storIOContentResolver
                .observeChangesOfUri(TestItem.CONTENT_URI, BackpressureStrategy.MISSING)
                .take(2)
                .subscribe(changesTestSubscriber);

        TestItem testItemToInsert = TestItem.create(null, "value");
        contentResolver.insert(TestItem.CONTENT_URI, testItemToInsert.toContentValues());

        Cursor firstDbState = contentResolver.query(TestItem.CONTENT_URI, null, null, null, null);
        Assertions.assertThat(firstDbState).hasCount(1);

        //noinspection ConstantConditions
        assertThat(firstDbState.moveToFirst()).isTrue();

        TestItem testItem = TestItem.fromCursor(firstDbState);

        DeleteResults<TestItem> deleteResults = storIOContentResolver
                .delete()
                .objects(singletonList(testItem))
                .prepare()
                .executeAsBlocking();

        assertThat(deleteResults.wasDeleted(testItem)).isTrue();

        Cursor secondDbState = contentResolver.query(TestItem.CONTENT_URI, null, null, null, null);
        Assertions.assertThat(secondDbState).hasCount(0);

        changesTestSubscriber.awaitTerminalEvent(60, SECONDS);
        changesTestSubscriber.assertNoErrors();
        changesTestSubscriber.assertValues(Changes.newInstance(TestItem.CONTENT_URI), Changes.newInstance(TestItem.CONTENT_URI));
    }

    @Test
    public void deleteObjectsAsObservable() {
        TestSubscriber<Changes> changesTestSubscriber = new TestSubscriber<Changes>();

        storIOContentResolver
                .observeChangesOfUri(TestItem.CONTENT_URI, BackpressureStrategy.MISSING)
                .take(2)
                .subscribe(changesTestSubscriber);

        TestItem testItemToInsert = TestItem.create(null, "value");
        contentResolver.insert(TestItem.CONTENT_URI, testItemToInsert.toContentValues());

        Cursor firstDbState = contentResolver.query(TestItem.CONTENT_URI, null, null, null, null);
        Assertions.assertThat(firstDbState).hasCount(1);

        //noinspection ConstantConditions
        assertThat(firstDbState.moveToFirst()).isTrue();

        TestItem testItem = TestItem.fromCursor(firstDbState);

        DeleteResults<TestItem> deleteResults = storIOContentResolver
                .delete()
                .objects(singletonList(testItem))
                .prepare()
                .asRxFlowable(BackpressureStrategy.MISSING)
                .blockingFirst();

        assertThat(deleteResults.wasDeleted(testItem)).isTrue();

        Cursor secondDbState = contentResolver.query(TestItem.CONTENT_URI, null, null, null, null);
        Assertions.assertThat(secondDbState).hasCount(0);

        changesTestSubscriber.awaitTerminalEvent(60, SECONDS);
        changesTestSubscriber.assertNoErrors();
        changesTestSubscriber.assertValues(Changes.newInstance(TestItem.CONTENT_URI), Changes.newInstance(TestItem.CONTENT_URI));
    }

    @Test
    public void deleteObjectsAsSingle() {
        TestSubscriber<Changes> changesTestSubscriber = new TestSubscriber<Changes>();

        storIOContentResolver
                .observeChangesOfUri(TestItem.CONTENT_URI, BackpressureStrategy.MISSING)
                .take(2)
                .subscribe(changesTestSubscriber);

        TestItem testItemToInsert = TestItem.create(null, "value");
        contentResolver.insert(TestItem.CONTENT_URI, testItemToInsert.toContentValues());

        Cursor firstDbState = contentResolver.query(TestItem.CONTENT_URI, null, null, null, null);
        Assertions.assertThat(firstDbState).hasCount(1);

        //noinspection ConstantConditions
        assertThat(firstDbState.moveToFirst()).isTrue();

        TestItem testItem = TestItem.fromCursor(firstDbState);

        DeleteResults<TestItem> deleteResults = storIOContentResolver
                .delete()
                .objects(singletonList(testItem))
                .prepare()
                .asRxSingle()
                .blockingGet();

        assertThat(deleteResults.wasDeleted(testItem)).isTrue();

        Cursor secondDbState = contentResolver.query(TestItem.CONTENT_URI, null, null, null, null);
        Assertions.assertThat(secondDbState).hasCount(0);

        changesTestSubscriber.awaitTerminalEvent(60, SECONDS);
        changesTestSubscriber.assertNoErrors();
        changesTestSubscriber.assertValues(Changes.newInstance(TestItem.CONTENT_URI), Changes.newInstance(TestItem.CONTENT_URI));
    }

    @Test
    public void deleteObjectsAsCompletable() {
        TestSubscriber<Changes> changesTestSubscriber = new TestSubscriber<Changes>();

        storIOContentResolver
                .observeChangesOfUri(TestItem.CONTENT_URI, BackpressureStrategy.MISSING)
                .take(2)
                .subscribe(changesTestSubscriber);

        TestItem testItemToInsert = TestItem.create(null, "value");
        contentResolver.insert(TestItem.CONTENT_URI, testItemToInsert.toContentValues());

        Cursor firstDbState = contentResolver.query(TestItem.CONTENT_URI, null, null, null, null);
        Assertions.assertThat(firstDbState).hasCount(1);

        //noinspection ConstantConditions
        assertThat(firstDbState.moveToFirst()).isTrue();

        TestItem testItem = TestItem.fromCursor(firstDbState);

        storIOContentResolver
                .delete()
                .objects(singletonList(testItem))
                .prepare()
                .asRxCompletable()
                .blockingAwait(15, SECONDS);

        Cursor secondDbState = contentResolver.query(TestItem.CONTENT_URI, null, null, null, null);
        Assertions.assertThat(secondDbState).hasCount(0);

        changesTestSubscriber.awaitTerminalEvent(60, SECONDS);
        changesTestSubscriber.assertNoErrors();
        changesTestSubscriber.assertValues(Changes.newInstance(TestItem.CONTENT_URI), Changes.newInstance(TestItem.CONTENT_URI));
    }
}
