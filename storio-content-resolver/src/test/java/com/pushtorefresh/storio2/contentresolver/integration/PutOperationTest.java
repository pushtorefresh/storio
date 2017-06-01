package com.pushtorefresh.storio2.contentresolver.integration;

import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;

import com.pushtorefresh.storio2.contentresolver.BuildConfig;
import com.pushtorefresh.storio2.contentresolver.Changes;
import com.pushtorefresh.storio2.contentresolver.operations.put.DefaultPutResolver;
import com.pushtorefresh.storio2.contentresolver.operations.put.PutResolver;
import com.pushtorefresh.storio2.contentresolver.operations.put.PutResult;
import com.pushtorefresh.storio2.contentresolver.queries.InsertQuery;
import com.pushtorefresh.storio2.contentresolver.queries.UpdateQuery;

import org.assertj.android.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import io.reactivex.BackpressureStrategy;
import io.reactivex.subscribers.TestSubscriber;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.assertj.core.api.Java6Assertions.assertThat;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)
public class PutOperationTest extends IntegrationTest {

    @NonNull
    private final PutResolver<ContentValues> testItemContentValuesPutResolver = new DefaultPutResolver<ContentValues>() {
        @NonNull
        @Override
        protected InsertQuery mapToInsertQuery(@NonNull ContentValues object) {
            return InsertQuery.builder()
                    .uri(TestItem.CONTENT_URI)
                    .build();
        }

        @NonNull
        @Override
        protected UpdateQuery mapToUpdateQuery(@NonNull ContentValues object) {
            return UpdateQuery.builder()
                    .uri(TestItem.CONTENT_URI)
                    .where(TestItem.COLUMN_ID + "=?")
                    .whereArgs(object.get(TestItem.COLUMN_ID))
                    .build();
        }

        @NonNull
        @Override
        protected ContentValues mapToContentValues(@NonNull ContentValues object) {
            return object;
        }
    };

    @Test
    public void insertContentValuesExecuteAsBlocking() {
        TestSubscriber<Changes> changesTestSubscriber = new TestSubscriber<Changes>();

        storIOContentResolver
                .observeChangesOfUri(TestItem.CONTENT_URI, BackpressureStrategy.MISSING)
                .take(1)
                .subscribe(changesTestSubscriber);

        TestItem testItem = TestItem.create(null, "value");
        ContentValues cv = testItem.toContentValues();

        PutResult insertResult = storIOContentResolver
                .put()
                .contentValues(cv)
                .withPutResolver(testItemContentValuesPutResolver)
                .prepare()
                .executeAsBlocking();

        assertThat(insertResult.wasInserted()).isTrue();

        Cursor cursor = contentResolver.query(TestItem.CONTENT_URI, null, null, null, null);

        Assertions.assertThat(cursor).hasCount(1);

        cursor.moveToFirst();

        assertThat(testItem.equalsWithoutId(TestItem.fromCursor(cursor))).isTrue();

        changesTestSubscriber.awaitTerminalEvent(60, SECONDS);
        changesTestSubscriber.assertNoErrors();
        changesTestSubscriber.assertValue(Changes.newInstance(TestItem.CONTENT_URI));
    }

    @Test
    public void insertContentValuesAsObservable() {
        TestSubscriber<Changes> changesTestSubscriber = new TestSubscriber<Changes>();

        storIOContentResolver
                .observeChangesOfUri(TestItem.CONTENT_URI, BackpressureStrategy.MISSING)
                .take(1)
                .subscribe(changesTestSubscriber);

        TestItem testItem = TestItem.create(null, "value");
        ContentValues cv = testItem.toContentValues();

        PutResult insertResult = storIOContentResolver
                .put()
                .contentValues(cv)
                .withPutResolver(testItemContentValuesPutResolver)
                .prepare()
                .asRxFlowable(BackpressureStrategy.MISSING)
                .blockingFirst();

        assertThat(insertResult.wasInserted()).isTrue();

        Cursor cursor = contentResolver.query(TestItem.CONTENT_URI, null, null, null, null);

        Assertions.assertThat(cursor).hasCount(1);

        cursor.moveToFirst();

        assertThat(testItem.equalsWithoutId(TestItem.fromCursor(cursor))).isTrue();

        changesTestSubscriber.awaitTerminalEvent(60, SECONDS);
        changesTestSubscriber.assertNoErrors();
        changesTestSubscriber.assertValue(Changes.newInstance(TestItem.CONTENT_URI));
    }

    @Test
    public void insertContentValuesAsSingle() {
        TestSubscriber<Changes> changesTestSubscriber = new TestSubscriber<Changes>();

        storIOContentResolver
                .observeChangesOfUri(TestItem.CONTENT_URI, BackpressureStrategy.MISSING)
                .take(1)
                .subscribe(changesTestSubscriber);

        TestItem testItem = TestItem.create(null, "value");
        ContentValues cv = testItem.toContentValues();

        PutResult insertResult = storIOContentResolver
                .put()
                .contentValues(cv)
                .withPutResolver(testItemContentValuesPutResolver)
                .prepare()
                .asRxSingle()
                .blockingGet();

        assertThat(insertResult.wasInserted()).isTrue();

        Cursor cursor = contentResolver.query(TestItem.CONTENT_URI, null, null, null, null);

        Assertions.assertThat(cursor).hasCount(1);

        cursor.moveToFirst();

        assertThat(testItem.equalsWithoutId(TestItem.fromCursor(cursor))).isTrue();

        changesTestSubscriber.awaitTerminalEvent(60, SECONDS);
        changesTestSubscriber.assertNoErrors();
        changesTestSubscriber.assertValue(Changes.newInstance(TestItem.CONTENT_URI));
    }

    @Test
    public void insertContentValuesAsCompletable() {
        TestSubscriber<Changes> changesTestSubscriber = new TestSubscriber<Changes>();

        storIOContentResolver
                .observeChangesOfUri(TestItem.CONTENT_URI, BackpressureStrategy.MISSING)
                .take(1)
                .subscribe(changesTestSubscriber);

        TestItem testItem = TestItem.create(null, "value");
        ContentValues cv = testItem.toContentValues();

        storIOContentResolver
                .put()
                .contentValues(cv)
                .withPutResolver(testItemContentValuesPutResolver)
                .prepare()
                .asRxCompletable()
                .blockingAwait(15, SECONDS);

        Cursor cursor = contentResolver.query(TestItem.CONTENT_URI, null, null, null, null);

        Assertions.assertThat(cursor).hasCount(1);

        cursor.moveToFirst();

        assertThat(testItem.equalsWithoutId(TestItem.fromCursor(cursor))).isTrue();

        changesTestSubscriber.awaitTerminalEvent(60, SECONDS);
        changesTestSubscriber.assertNoErrors();
        changesTestSubscriber.assertValue(Changes.newInstance(TestItem.CONTENT_URI));
    }

    @Test
    public void updateContentValuesExecuteAsBlocking() {
        TestSubscriber<Changes> changesTestSubscriber = new TestSubscriber<Changes>();

        storIOContentResolver
                .observeChangesOfUri(TestItem.CONTENT_URI, BackpressureStrategy.MISSING)
                .take(2)
                .subscribe(changesTestSubscriber);

        Uri insertedUri = contentResolver.insert(TestItem.CONTENT_URI, TestItem.create(null, "value").toContentValues());

        TestItem testItem = TestItem.create(ContentUris.parseId(insertedUri), "value2");

        PutResult updateResult = storIOContentResolver
                .put()
                .contentValues(testItem.toContentValues())
                .withPutResolver(testItemContentValuesPutResolver)
                .prepare()
                .executeAsBlocking();

        assertThat(updateResult.wasUpdated()).isTrue();

        Cursor cursor = contentResolver.query(TestItem.CONTENT_URI, null, null, null, null);

        Assertions.assertThat(cursor).hasCount(1);

        cursor.moveToFirst();

        assertThat(testItem).isEqualTo(TestItem.fromCursor(cursor));

        changesTestSubscriber.awaitTerminalEvent(60, SECONDS);
        changesTestSubscriber.assertNoErrors();
        changesTestSubscriber.assertValues(Changes.newInstance(TestItem.CONTENT_URI), Changes.newInstance(TestItem.CONTENT_URI));
    }

    @Test
    public void updateContentValuesAsObservable() {
        TestSubscriber<Changes> changesTestSubscriber = new TestSubscriber<Changes>();

        storIOContentResolver
                .observeChangesOfUri(TestItem.CONTENT_URI, BackpressureStrategy.MISSING)
                .take(2)
                .subscribe(changesTestSubscriber);

        Uri insertedUri = contentResolver.insert(TestItem.CONTENT_URI, TestItem.create(null, "value").toContentValues());

        TestItem testItem = TestItem.create(ContentUris.parseId(insertedUri), "value2");

        PutResult updateResult = storIOContentResolver
                .put()
                .contentValues(testItem.toContentValues())
                .withPutResolver(testItemContentValuesPutResolver)
                .prepare()
                .asRxFlowable(BackpressureStrategy.MISSING)
                .blockingFirst();

        assertThat(updateResult.wasUpdated()).isTrue();

        Cursor cursor = contentResolver.query(TestItem.CONTENT_URI, null, null, null, null);

        Assertions.assertThat(cursor).hasCount(1);

        cursor.moveToFirst();

        assertThat(testItem).isEqualTo(TestItem.fromCursor(cursor));

        changesTestSubscriber.awaitTerminalEvent(60, SECONDS);
        changesTestSubscriber.assertNoErrors();
        changesTestSubscriber.assertValues(Changes.newInstance(TestItem.CONTENT_URI), Changes.newInstance(TestItem.CONTENT_URI));
    }

    @Test
    public void updateContentValuesAsSingle() {
        TestSubscriber<Changes> changesTestSubscriber = new TestSubscriber<Changes>();

        storIOContentResolver
                .observeChangesOfUri(TestItem.CONTENT_URI, BackpressureStrategy.MISSING)
                .take(2)
                .subscribe(changesTestSubscriber);

        Uri insertedUri = contentResolver.insert(TestItem.CONTENT_URI, TestItem.create(null, "value").toContentValues());

        TestItem testItem = TestItem.create(ContentUris.parseId(insertedUri), "value2");

        PutResult updateResult = storIOContentResolver
                .put()
                .contentValues(testItem.toContentValues())
                .withPutResolver(testItemContentValuesPutResolver)
                .prepare()
                .asRxSingle()
                .blockingGet();

        assertThat(updateResult.wasUpdated()).isTrue();

        Cursor cursor = contentResolver.query(TestItem.CONTENT_URI, null, null, null, null);

        Assertions.assertThat(cursor).hasCount(1);

        cursor.moveToFirst();

        assertThat(testItem).isEqualTo(TestItem.fromCursor(cursor));

        changesTestSubscriber.awaitTerminalEvent(60, SECONDS);
        changesTestSubscriber.assertNoErrors();
        changesTestSubscriber.assertValues(Changes.newInstance(TestItem.CONTENT_URI), Changes.newInstance(TestItem.CONTENT_URI));
    }

    @Test
    public void updateContentValuesAsCompletable() {
        TestSubscriber<Changes> changesTestSubscriber = new TestSubscriber<Changes>();

        storIOContentResolver
                .observeChangesOfUri(TestItem.CONTENT_URI, BackpressureStrategy.MISSING)
                .take(2)
                .subscribe(changesTestSubscriber);

        Uri insertedUri = contentResolver.insert(TestItem.CONTENT_URI, TestItem.create(null, "value").toContentValues());

        TestItem testItem = TestItem.create(ContentUris.parseId(insertedUri), "value2");

        storIOContentResolver
                .put()
                .contentValues(testItem.toContentValues())
                .withPutResolver(testItemContentValuesPutResolver)
                .prepare()
                .asRxCompletable()
                .blockingAwait(15, SECONDS);

        Cursor cursor = contentResolver.query(TestItem.CONTENT_URI, null, null, null, null);

        Assertions.assertThat(cursor).hasCount(1);

        cursor.moveToFirst();

        assertThat(testItem).isEqualTo(TestItem.fromCursor(cursor));

        changesTestSubscriber.awaitTerminalEvent(60, SECONDS);
        changesTestSubscriber.assertNoErrors();
        changesTestSubscriber.assertValues(Changes.newInstance(TestItem.CONTENT_URI), Changes.newInstance(TestItem.CONTENT_URI));
    }

    @Test
    public void insertObjectExecuteAsBlocking() {
        TestSubscriber<Changes> changesTestSubscriber = new TestSubscriber<Changes>();

        storIOContentResolver
                .observeChangesOfUri(TestItem.CONTENT_URI, BackpressureStrategy.MISSING)
                .take(1)
                .subscribe(changesTestSubscriber);

        TestItem testItem = TestItem.create(null, "value");

        PutResult insertResult = storIOContentResolver
                .put()
                .object(testItem)
                .prepare()
                .executeAsBlocking();

        assertThat(insertResult.wasInserted()).isTrue();

        Cursor cursor = contentResolver.query(TestItem.CONTENT_URI, null, null, null, null);

        Assertions.assertThat(cursor).hasCount(1);

        cursor.moveToFirst();

        assertThat(testItem.equalsWithoutId(TestItem.fromCursor(cursor))).isTrue();

        cursor.close();

        changesTestSubscriber.awaitTerminalEvent(60, SECONDS);
        changesTestSubscriber.assertNoErrors();
        changesTestSubscriber.assertValue(Changes.newInstance(TestItem.CONTENT_URI));
    }

    @Test
    public void insertObjectAsObservable() {
        TestSubscriber<Changes> changesTestSubscriber = new TestSubscriber<Changes>();

        storIOContentResolver
                .observeChangesOfUri(TestItem.CONTENT_URI, BackpressureStrategy.MISSING)
                .take(1)
                .subscribe(changesTestSubscriber);

        TestItem testItem = TestItem.create(null, "value");

        PutResult insertResult = storIOContentResolver
                .put()
                .object(testItem)
                .prepare()
                .asRxFlowable(BackpressureStrategy.MISSING)
                .blockingFirst();

        assertThat(insertResult.wasInserted()).isTrue();

        Cursor cursor = contentResolver.query(TestItem.CONTENT_URI, null, null, null, null);

        Assertions.assertThat(cursor).hasCount(1);

        cursor.moveToFirst();

        assertThat(testItem.equalsWithoutId(TestItem.fromCursor(cursor))).isTrue();

        cursor.close();

        changesTestSubscriber.awaitTerminalEvent(60, SECONDS);
        changesTestSubscriber.assertNoErrors();
        changesTestSubscriber.assertValue(Changes.newInstance(TestItem.CONTENT_URI));
    }

    @Test
    public void insertObjectAsSingle() {
        TestSubscriber<Changes> changesTestSubscriber = new TestSubscriber<Changes>();

        storIOContentResolver
                .observeChangesOfUri(TestItem.CONTENT_URI, BackpressureStrategy.MISSING)
                .take(1)
                .subscribe(changesTestSubscriber);

        TestItem testItem = TestItem.create(null, "value");

        PutResult insertResult = storIOContentResolver
                .put()
                .object(testItem)
                .prepare()
                .asRxSingle()
                .blockingGet();

        assertThat(insertResult.wasInserted()).isTrue();

        Cursor cursor = contentResolver.query(TestItem.CONTENT_URI, null, null, null, null);

        Assertions.assertThat(cursor).hasCount(1);

        cursor.moveToFirst();

        assertThat(testItem.equalsWithoutId(TestItem.fromCursor(cursor))).isTrue();

        cursor.close();

        changesTestSubscriber.awaitTerminalEvent(60, SECONDS);
        changesTestSubscriber.assertNoErrors();
        changesTestSubscriber.assertValue(Changes.newInstance(TestItem.CONTENT_URI));
    }

    @Test
    public void insertObjectAsCompletable() {
        TestSubscriber<Changes> changesTestSubscriber = new TestSubscriber<Changes>();

        storIOContentResolver
                .observeChangesOfUri(TestItem.CONTENT_URI, BackpressureStrategy.MISSING)
                .take(1)
                .subscribe(changesTestSubscriber);

        TestItem testItem = TestItem.create(null, "value");

        storIOContentResolver
                .put()
                .object(testItem)
                .prepare()
                .asRxCompletable()
                .blockingAwait(15, SECONDS);

        Cursor cursor = contentResolver.query(TestItem.CONTENT_URI, null, null, null, null);

        Assertions.assertThat(cursor).hasCount(1);

        cursor.moveToFirst();

        assertThat(testItem.equalsWithoutId(TestItem.fromCursor(cursor))).isTrue();

        cursor.close();

        changesTestSubscriber.awaitTerminalEvent(60, SECONDS);
        changesTestSubscriber.assertNoErrors();
        changesTestSubscriber.assertValue(Changes.newInstance(TestItem.CONTENT_URI));
    }


    @Test
    public void updateObjectExecuteAsBlocking() {
        TestSubscriber<Changes> changesTestSubscriber = new TestSubscriber<Changes>();

        storIOContentResolver
                .observeChangesOfUri(TestItem.CONTENT_URI, BackpressureStrategy.MISSING)
                .take(2)
                .subscribe(changesTestSubscriber);

        Uri insertedUri = contentResolver.insert(TestItem.CONTENT_URI, TestItem.create(null, "value").toContentValues());

        TestItem testItem = TestItem.create(ContentUris.parseId(insertedUri), "value2");

        PutResult updateResult = storIOContentResolver
                .put()
                .object(testItem)
                .prepare()
                .executeAsBlocking();

        assertThat(updateResult.wasUpdated()).isTrue();

        Cursor cursor = contentResolver.query(TestItem.CONTENT_URI, null, null, null, null);

        Assertions.assertThat(cursor).hasCount(1);

        cursor.moveToFirst();

        assertThat(testItem).isEqualTo(TestItem.fromCursor(cursor));

        changesTestSubscriber.awaitTerminalEvent(60, SECONDS);
        changesTestSubscriber.assertNoErrors();
        changesTestSubscriber.assertValues(Changes.newInstance(TestItem.CONTENT_URI), Changes.newInstance(TestItem.CONTENT_URI));
    }

    @Test
    public void updateObjectAsObservable() {
        TestSubscriber<Changes> changesTestSubscriber = new TestSubscriber<Changes>();

        storIOContentResolver
                .observeChangesOfUri(TestItem.CONTENT_URI, BackpressureStrategy.MISSING)
                .take(2)
                .subscribe(changesTestSubscriber);

        Uri insertedUri = contentResolver.insert(TestItem.CONTENT_URI, TestItem.create(null, "value").toContentValues());

        TestItem testItem = TestItem.create(ContentUris.parseId(insertedUri), "value2");

        PutResult updateResult = storIOContentResolver
                .put()
                .object(testItem)
                .prepare()
                .asRxFlowable(BackpressureStrategy.MISSING)
                .blockingFirst();

        assertThat(updateResult.wasUpdated()).isTrue();

        Cursor cursor = contentResolver.query(TestItem.CONTENT_URI, null, null, null, null);

        Assertions.assertThat(cursor).hasCount(1);

        cursor.moveToFirst();

        assertThat(testItem).isEqualTo(TestItem.fromCursor(cursor));

        changesTestSubscriber.awaitTerminalEvent(60, SECONDS);
        changesTestSubscriber.assertNoErrors();
        changesTestSubscriber.assertValues(Changes.newInstance(TestItem.CONTENT_URI), Changes.newInstance(TestItem.CONTENT_URI));
    }

    @Test
    public void updateObjectAsSingle() {
        TestSubscriber<Changes> changesTestSubscriber = new TestSubscriber<Changes>();

        storIOContentResolver
                .observeChangesOfUri(TestItem.CONTENT_URI, BackpressureStrategy.MISSING)
                .take(2)
                .subscribe(changesTestSubscriber);

        Uri insertedUri = contentResolver.insert(TestItem.CONTENT_URI, TestItem.create(null, "value").toContentValues());

        TestItem testItem = TestItem.create(ContentUris.parseId(insertedUri), "value2");

        PutResult updateResult = storIOContentResolver
                .put()
                .object(testItem)
                .prepare()
                .asRxSingle()
                .blockingGet();

        assertThat(updateResult.wasUpdated()).isTrue();

        Cursor cursor = contentResolver.query(TestItem.CONTENT_URI, null, null, null, null);

        Assertions.assertThat(cursor).hasCount(1);

        cursor.moveToFirst();

        assertThat(testItem).isEqualTo(TestItem.fromCursor(cursor));

        changesTestSubscriber.awaitTerminalEvent(60, SECONDS);
        changesTestSubscriber.assertNoErrors();
        changesTestSubscriber.assertValues(Changes.newInstance(TestItem.CONTENT_URI), Changes.newInstance(TestItem.CONTENT_URI));
    }

    @Test
    public void updateObjectAsCompletable() {
        TestSubscriber<Changes> changesTestSubscriber = new TestSubscriber<Changes>();

        storIOContentResolver
                .observeChangesOfUri(TestItem.CONTENT_URI, BackpressureStrategy.MISSING)
                .take(2)
                .subscribe(changesTestSubscriber);

        Uri insertedUri = contentResolver.insert(TestItem.CONTENT_URI, TestItem.create(null, "value").toContentValues());

        TestItem testItem = TestItem.create(ContentUris.parseId(insertedUri), "value2");

        storIOContentResolver
                .put()
                .object(testItem)
                .prepare()
                .asRxCompletable()
                .blockingAwait(15, SECONDS);

        Cursor cursor = contentResolver.query(TestItem.CONTENT_URI, null, null, null, null);

        Assertions.assertThat(cursor).hasCount(1);

        cursor.moveToFirst();

        assertThat(testItem).isEqualTo(TestItem.fromCursor(cursor));

        changesTestSubscriber.awaitTerminalEvent(60, SECONDS);
        changesTestSubscriber.assertNoErrors();
        changesTestSubscriber.assertValues(Changes.newInstance(TestItem.CONTENT_URI), Changes.newInstance(TestItem.CONTENT_URI));
    }

    @Test
    public void insertOneWithNullField() {
        TestSubscriber<Changes> changesTestSubscriber = new TestSubscriber<Changes>();

        storIOContentResolver
                .observeChangesOfUri(TestItem.CONTENT_URI, BackpressureStrategy.MISSING)
                .take(1)
                .subscribe(changesTestSubscriber);

        TestItem testItem = TestItem.create(null, "value", null); // optional value is null

        PutResult insertResult = storIOContentResolver
                .put()
                .object(testItem)
                .prepare()
                .executeAsBlocking();

        assertThat(insertResult.wasInserted()).isTrue();

        Cursor cursor = contentResolver.query(TestItem.CONTENT_URI, null, null, null, null);

        Assertions.assertThat(cursor).hasCount(1);

        cursor.moveToFirst();

        assertThat(testItem.equalsWithoutId(TestItem.fromCursor(cursor))).isTrue();

        cursor.close();

        changesTestSubscriber.awaitTerminalEvent(60, SECONDS);
        changesTestSubscriber.assertNoErrors();
        changesTestSubscriber.assertValue(Changes.newInstance(TestItem.CONTENT_URI));
    }

    @Test
    public void updateNullFieldToNotNull() {
        TestSubscriber<Changes> changesTestSubscriber = new TestSubscriber<Changes>();

        storIOContentResolver
                .observeChangesOfUri(TestItem.CONTENT_URI, BackpressureStrategy.MISSING)
                .take(2)
                .subscribe(changesTestSubscriber);

        Uri insertedUri =
                contentResolver.insert(TestItem.CONTENT_URI, TestItem.create(null, "value", null).toContentValues()); // firstly, optional value is null

        TestItem testItem = TestItem.create(ContentUris.parseId(insertedUri), "value", "optionalValue"); // change to not null

        PutResult updateResult = storIOContentResolver
                .put()
                .object(testItem)
                .prepare()
                .executeAsBlocking();

        assertThat(updateResult.wasUpdated()).isTrue();

        Cursor cursor = contentResolver.query(TestItem.CONTENT_URI, null, null, null, null);

        Assertions.assertThat(cursor).hasCount(1);

        cursor.moveToFirst();

        assertThat(testItem).isEqualTo(TestItem.fromCursor(cursor));

        changesTestSubscriber.awaitTerminalEvent(60, SECONDS);
        changesTestSubscriber.assertNoErrors();
        changesTestSubscriber.assertValues(Changes.newInstance(TestItem.CONTENT_URI), Changes.newInstance(TestItem.CONTENT_URI));
    }

    @Test
    public void updateNotNullFieldToNull() {
        TestSubscriber<Changes> changesTestSubscriber = new TestSubscriber<Changes>();

        storIOContentResolver
                .observeChangesOfUri(TestItem.CONTENT_URI, BackpressureStrategy.MISSING)
                .take(2)
                .subscribe(changesTestSubscriber);

        Uri insertedUri = contentResolver.insert(TestItem.CONTENT_URI, TestItem.create(null, "value", "optionalValue").toContentValues());

        TestItem testItem = TestItem.create(ContentUris.parseId(insertedUri), "value", null);  // optional value changes to null

        PutResult updateResult = storIOContentResolver
                .put()
                .object(testItem)
                .prepare()
                .executeAsBlocking();

        assertThat(updateResult.wasUpdated()).isTrue();

        Cursor cursor = contentResolver.query(TestItem.CONTENT_URI, null, null, null, null);

        Assertions.assertThat(cursor).hasCount(1);

        cursor.moveToFirst();

        assertThat(testItem).isEqualTo(TestItem.fromCursor(cursor));

        changesTestSubscriber.awaitTerminalEvent(60, SECONDS);
        changesTestSubscriber.assertNoErrors();
        changesTestSubscriber.assertValues(Changes.newInstance(TestItem.CONTENT_URI), Changes.newInstance(TestItem.CONTENT_URI));
    }
}
