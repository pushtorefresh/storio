package com.pushtorefresh.storio.sqlite.operations.put;

import android.content.ContentValues;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.pushtorefresh.storio.sqlite.StorIOSQLite;
import com.pushtorefresh.storio.sqlite.queries.InsertQuery;
import com.pushtorefresh.storio.sqlite.queries.Query;
import com.pushtorefresh.storio.sqlite.queries.UpdateQuery;

import org.junit.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import rx.functions.Func1;

import static java.util.Collections.singleton;
import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class DefaultPutResolverTest {

    /**
     * Verifies behavior of {@link DefaultPutResolver} for "insert"
     */
    @Test
    public void insert() {
        final StorIOSQLite storIOSQLite = mock(StorIOSQLite.class);
        final StorIOSQLite.LowLevel lowLevel = mock(StorIOSQLite.LowLevel.class);
        final TestItem testItem = new TestItem(null); // item without id, should be inserted

        when(storIOSQLite.lowLevel())
                .thenReturn(lowLevel);

        final Long expectedInsertedId = 24L;

        final Query expectedQuery = Query.builder()
                .table(TestItem.TABLE)
                .where(TestItem.COLUMN_ID + " = ?")
                .whereArgs(testItem.getId())
                .build();

        final Cursor cursor = mock(Cursor.class);

        when(lowLevel.query(eq(expectedQuery)))
                .thenReturn(cursor);

        when(cursor.getCount())
                .thenReturn(0); // No results -> insert should be performed

        when(lowLevel.insert(any(InsertQuery.class), any(ContentValues.class)))
                .thenReturn(expectedInsertedId);

        final Set<String> tags = singleton("test_tag");

        final InsertQuery expectedInsertQuery = InsertQuery.builder()
                .table(TestItem.TABLE)
                .affectsTags(tags)
                .nullColumnHack(null)
                .build();

        final PutResolver<TestItem> putResolver = new DefaultPutResolver<TestItem>() {
            @NonNull
            @Override
            protected InsertQuery mapToInsertQuery(@NonNull TestItem object) {
                return expectedInsertQuery;
            }

            @NonNull
            @Override
            protected UpdateQuery mapToUpdateQuery(@NonNull TestItem object) {
                return UpdateQuery.builder()
                        .table(TestItem.TABLE)
                        .where(TestItem.COLUMN_ID + " = ?")
                        .whereArgs(object.getId())
                        .build();
            }

            @NonNull
            @Override
            protected ContentValues mapToContentValues(@NonNull TestItem object) {
                return TestItem.MAP_TO_CONTENT_VALUES.call(object);
            }
        };

        final ContentValues expectedContentValues = TestItem.MAP_TO_CONTENT_VALUES.call(testItem);

        // Performing Put that should "insert"
        final PutResult putResult = putResolver.performPut(storIOSQLite, testItem);

        verify(lowLevel).beginTransaction();
        verify(lowLevel).setTransactionSuccessful();
        verify(lowLevel).endTransaction();

        // checks that it asks db for results
        verify(lowLevel).query(eq(expectedQuery));

        // checks that cursor was closed
        verify(cursor).close();

        // only one query should occur
        verify(lowLevel).query(any(Query.class));

        // checks that required insert was performed
        verify(lowLevel).insert(eq(expectedInsertQuery), eq(expectedContentValues));

        // only one insert should occur
        verify(lowLevel).insert(any(InsertQuery.class), any(ContentValues.class));

        // no updates should occur
        verify(lowLevel, never()).update(any(UpdateQuery.class), any(ContentValues.class));

        // put result checks
        assertThat(putResult.wasInserted()).isTrue();
        assertThat(putResult.wasUpdated()).isFalse();

        assertThat(putResult.insertedId()).isEqualTo(expectedInsertedId);
        assertThat(putResult.numberOfRowsUpdated()).isNull();
        
        assertThat(putResult.affectedTables()).containsExactly(TestItem.TABLE);
        assertThat(putResult.affectedTags()).isEqualTo(tags);
    }

    /**
     * Verifies behavior of {@link DefaultPutResolver} for "update"
     */
    @Test
    public void update() {
        final StorIOSQLite storIOSQLite = mock(StorIOSQLite.class);
        final StorIOSQLite.Internal internal = mock(StorIOSQLite.Internal.class);
        final TestItem testItem = new TestItem(null); // item with some id, should be updated

        when(storIOSQLite.lowLevel())
                .thenReturn(internal);

        final Query expectedQuery = Query.builder()
                .table(TestItem.TABLE)
                .where(TestItem.COLUMN_ID + " = ?")
                .whereArgs(testItem.getId())
                .build();

        final Cursor cursor = mock(Cursor.class);

        when(internal.query(eq(expectedQuery)))
                .thenReturn(cursor);

        when(cursor.getCount())
                .thenReturn(1); // Some rows already in db -> update should be performed

        final Integer expectedNumberOfRowsUpdated = 1;

        when(internal.update(any(UpdateQuery.class), any(ContentValues.class)))
                .thenReturn(expectedNumberOfRowsUpdated);

        final Set<String> tags = singleton("test_tag");

        final UpdateQuery expectedUpdateQuery = UpdateQuery.builder()
                .table(TestItem.TABLE)
                .affectsTags(tags)
                .where(TestItem.COLUMN_ID + " = ?")
                .whereArgs(testItem.getId())
                .build();

        final PutResolver<TestItem> putResolver = new DefaultPutResolver<TestItem>() {
            @NonNull
            @Override
            protected InsertQuery mapToInsertQuery(@NonNull TestItem object) {
                fail("Should not be called");
                return null;
            }

            @NonNull
            @Override
            protected UpdateQuery mapToUpdateQuery(@NonNull TestItem object) {
                return expectedUpdateQuery;
            }

            @NonNull
            @Override
            protected ContentValues mapToContentValues(@NonNull TestItem object) {
                return TestItem.MAP_TO_CONTENT_VALUES.call(object);
            }
        };

        final ContentValues expectedContentValues = TestItem.MAP_TO_CONTENT_VALUES.call(testItem);

        // Performing Put that should "update"
        final PutResult putResult = putResolver.performPut(storIOSQLite, testItem);

        verify(internal).beginTransaction();
        verify(internal).setTransactionSuccessful();
        verify(internal).endTransaction();

        // checks that it asks db for results
        verify(internal).query(eq(expectedQuery));

        // checks that cursor was closed
        verify(cursor).close();

        // only one query should occur
        verify(internal).query(any(Query.class));

        // checks that required update was performed
        verify(internal).update(eq(expectedUpdateQuery), eq(expectedContentValues));

        // only one update should occur
        verify(internal).update(any(UpdateQuery.class), any(ContentValues.class));

        // no inserts should occur
        verify(internal, never()).insert(any(InsertQuery.class), any(ContentValues.class));

        // put result checks
        assertThat(putResult.wasInserted()).isFalse();
        assertThat(putResult.wasUpdated()).isTrue();

        assertThat(putResult.numberOfRowsUpdated()).isEqualTo(expectedNumberOfRowsUpdated);
        assertThat(putResult.insertedId()).isNull();

        assertThat(putResult.affectedTables()).containsExactly(TestItem.TABLE);
        assertThat(putResult.affectedTags()).isEqualTo(tags);
    }

    private static class TestItem {

        final static String TABLE = "someTable";
        final static String COLUMN_ID = "customIdColumnName";
        @Nullable
        private final Long id;
        static final Func1<TestItem, ContentValues> MAP_TO_CONTENT_VALUES = new Func1<TestItem, ContentValues>() {

            // ContentValues should be mocked for usage in tests (damn you Android...)
            // but we can not mock equals() method
            // so, we will return SAME ContentValues for object and assertEquals() will pass
            @NonNull
            private final Map<TestItem, ContentValues> map = new HashMap<TestItem, ContentValues>();

            @NonNull
            @Override
            public ContentValues call(@NonNull TestItem testItem) {
                if (map.containsKey(testItem)) {
                    return map.get(testItem);
                } else {
                    final ContentValues contentValues = mock(ContentValues.class);

                    when(contentValues.get(COLUMN_ID))
                            .thenReturn(testItem.id);

                    map.put(testItem, contentValues); // storing pair of mapping

                    return contentValues;
                }
            }
        };

        TestItem(@Nullable Long id) {
            this.id = id;
        }

        @Nullable
        Long getId() {
            return id;
        }
    }
}
