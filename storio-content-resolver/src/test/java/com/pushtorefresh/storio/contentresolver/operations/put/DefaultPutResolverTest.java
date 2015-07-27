package com.pushtorefresh.storio.contentresolver.operations.put;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.pushtorefresh.storio.contentresolver.StorIOContentResolver;
import com.pushtorefresh.storio.contentresolver.queries.InsertQuery;
import com.pushtorefresh.storio.contentresolver.queries.Query;
import com.pushtorefresh.storio.contentresolver.queries.UpdateQuery;

import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import rx.functions.Func1;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.assertTrue;
import static junit.framework.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

public class DefaultPutResolverTest {

    /**
     * Verifies behavior of {@link DefaultPutResolver} for "insert"
     */
    @Test
    public void insert() {
        final StorIOContentResolver storIOContentResolver = mock(StorIOContentResolver.class);
        final StorIOContentResolver.Internal internal = mock(StorIOContentResolver.Internal.class);
        final TestItem testItem = new TestItem(null); // item without id, should be inserted

        when(storIOContentResolver.internal())
                .thenReturn(internal);

        final Uri expectedInsertedUri = mock(Uri.class);

        final Query expectedQuery = Query.builder()
                .uri(TestItem.CONTENT_URI)
                .where(TestItem.COLUMN_ID + " = ?")
                .whereArgs(testItem.getId())
                .build();

        final Cursor cursor = mock(Cursor.class);

        when(internal.query(eq(expectedQuery)))
                .thenReturn(cursor);

        when(cursor.getCount())
                .thenReturn(0); // No results -> insert should be performed

        when(internal.insert(any(InsertQuery.class), any(ContentValues.class)))
                .thenReturn(expectedInsertedUri);

        final InsertQuery expectedInsertQuery = InsertQuery.builder()
                .uri(TestItem.CONTENT_URI)
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
                        .uri(TestItem.CONTENT_URI)
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
        final PutResult putResult = putResolver.performPut(storIOContentResolver, testItem);

        // checks that it asks db for results
        verify(internal).query(eq(expectedQuery));

        // checks that cursor was closed
        verify(cursor).close();

        // only one query should occur
        verify(internal).query(any(Query.class));

        // checks that required insert was performed
        verify(internal).insert(eq(expectedInsertQuery), eq(expectedContentValues));

        // only one insert should occur
        verify(internal).insert(any(InsertQuery.class), any(ContentValues.class));

        // no updates should occur
        verify(internal, never()).update(any(UpdateQuery.class), any(ContentValues.class));

        // put result checks
        assertTrue(putResult.wasInserted());
        assertFalse(putResult.wasUpdated());

        assertEquals(expectedInsertedUri, putResult.insertedUri());
        assertNull(putResult.numberOfRowsUpdated());
    }

    /**
     * Verifies behavior of {@link DefaultPutResolver} for "update"
     */
    @Test
    public void update() {
        final StorIOContentResolver storIOContentResolver = mock(StorIOContentResolver.class);
        final StorIOContentResolver.Internal internal = mock(StorIOContentResolver.Internal.class);
        final TestItem testItem = new TestItem(1L); // item with some id, should be updated

        when(storIOContentResolver.internal())
                .thenReturn(internal);

        final Query expectedQuery = Query.builder()
                .uri(TestItem.CONTENT_URI)
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

        final UpdateQuery expectedUpdateQuery = UpdateQuery.builder()
                .uri(TestItem.CONTENT_URI)
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
                return UpdateQuery.builder()
                        .uri(TestItem.CONTENT_URI)
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

        // Performing Put that should "update"
        final PutResult putResult = putResolver.performPut(storIOContentResolver, testItem);

        // checks that it asks db for results
        verify(internal, times(1)).query(eq(expectedQuery));

        // checks that cursor was closed
        verify(cursor, times(1)).close();

        // only one query should occur
        verify(internal, times(1)).query(any(Query.class));

        // checks that required update was performed
        verify(internal, times(1)).update(eq(expectedUpdateQuery), eq(expectedContentValues));

        // only one update should occur
        verify(internal, times(1)).update(any(UpdateQuery.class), any(ContentValues.class));

        // no inserts should occur
        verify(internal, times(0)).insert(any(InsertQuery.class), any(ContentValues.class));

        // put result checks
        assertFalse(putResult.wasInserted());
        assertTrue(putResult.wasUpdated());

        assertEquals(expectedNumberOfRowsUpdated, putResult.numberOfRowsUpdated());
        assertNull(putResult.insertedUri());
    }

    @Test
    public void shouldCloseCursorIfUpdateThrowsException() {
        final StorIOContentResolver storIOContentResolver = mock(StorIOContentResolver.class);
        final StorIOContentResolver.Internal internal = mock(StorIOContentResolver.Internal.class);

        when(storIOContentResolver.internal())
                .thenReturn(internal);

        final TestItem testItem = new TestItem(1L); // item with some id, should be updated

        final Query expectedQuery = Query.builder()
                .uri(TestItem.CONTENT_URI)
                .where(TestItem.COLUMN_ID + " = ?")
                .whereArgs(testItem.getId())
                .build();

        final Cursor cursor = mock(Cursor.class);

        when(internal.query(eq(expectedQuery)))
                .thenReturn(cursor);

        when(cursor.getCount())
                .thenReturn(1); // One result -> update should be performed

        final UpdateQuery expectedUpdateQuery = UpdateQuery.builder()
                .uri(TestItem.CONTENT_URI)
                .where(TestItem.COLUMN_ID + " = ?")
                .whereArgs(testItem.getId())
                .build();

        when(internal.update(eq(expectedUpdateQuery), any(ContentValues.class)))
                .thenThrow(new IllegalStateException("Fake exception from ContentResolver"));

        final PutResolver<TestItem> putResolver = new DefaultPutResolver<TestItem>() {
            @NonNull
            @Override
            protected InsertQuery mapToInsertQuery(@NonNull TestItem object) {
                return InsertQuery.builder()
                        .uri(TestItem.CONTENT_URI)
                        .build();
            }

            @NonNull
            @Override
            protected UpdateQuery mapToUpdateQuery(@NonNull TestItem object) {
                return UpdateQuery.builder()
                        .uri(TestItem.CONTENT_URI)
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


        try {
            putResolver.performPut(storIOContentResolver, testItem);
            fail();
        } catch (IllegalStateException expected) {
            assertEquals("Fake exception from ContentResolver", expected.getMessage());

            verify(storIOContentResolver).internal();

            // Checks that it asks actual ContentResolver for results
            verify(internal).query(eq(expectedQuery));

            verify(cursor).getCount();

            // Cursor must be closed in case of exception!
            verify(cursor).close();

            verify(internal).update(eq(expectedUpdateQuery), any(ContentValues.class));

            verifyNoMoreInteractions(storIOContentResolver, internal, cursor);
        }
    }

    @Test
    public void shouldCloseCursorIfInsertThrowsException() {
        final StorIOContentResolver storIOContentResolver = mock(StorIOContentResolver.class);
        final StorIOContentResolver.Internal internal = mock(StorIOContentResolver.Internal.class);

        when(storIOContentResolver.internal())
                .thenReturn(internal);

        final TestItem testItem = new TestItem(null); // item without id, should be inserted

        final Query expectedQuery = Query.builder()
                .uri(TestItem.CONTENT_URI)
                .where(TestItem.COLUMN_ID + " = ?")
                .whereArgs(testItem.getId())
                .build();

        final Cursor cursor = mock(Cursor.class);

        when(internal.query(eq(expectedQuery)))
                .thenReturn(cursor);

        when(cursor.getCount())
                .thenReturn(0); // No results -> insert should be performed

        final InsertQuery expectedInsertQuery = InsertQuery.builder()
                .uri(TestItem.CONTENT_URI)
                .build();

        when(internal.insert(eq(expectedInsertQuery), any(ContentValues.class)))
                .thenThrow(new IllegalStateException("Fake exception from ContentResolver"));

        final PutResolver<TestItem> putResolver = new DefaultPutResolver<TestItem>() {
            @NonNull
            @Override
            protected InsertQuery mapToInsertQuery(@NonNull TestItem object) {
                return InsertQuery.builder()
                        .uri(TestItem.CONTENT_URI)
                        .build();
            }

            @NonNull
            @Override
            protected UpdateQuery mapToUpdateQuery(@NonNull TestItem object) {
                return UpdateQuery.builder()
                        .uri(TestItem.CONTENT_URI)
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


        try {
            putResolver.performPut(storIOContentResolver, testItem);
            fail();
        } catch (IllegalStateException expected) {
            assertEquals("Fake exception from ContentResolver", expected.getMessage());

            verify(storIOContentResolver).internal();

            // Checks that it asks actual ContentResolver for results
            verify(internal).query(eq(expectedQuery));

            verify(cursor).getCount();

            // Cursor must be closed in case of exception!
            verify(cursor).close();

            verify(internal).insert(eq(expectedInsertQuery), any(ContentValues.class));

            verifyNoMoreInteractions(storIOContentResolver, internal, cursor);
        }
    }

    private static class TestItem {

        @NonNull
        final static Uri CONTENT_URI = mock(Uri.class);

        @NonNull
        final static String COLUMN_ID = "customIdColumnName";

        @Nullable
        private final Long id;

        @NonNull
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
