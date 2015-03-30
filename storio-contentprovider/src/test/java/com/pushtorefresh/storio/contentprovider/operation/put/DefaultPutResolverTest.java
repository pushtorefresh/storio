package com.pushtorefresh.storio.contentprovider.operation.put;

import android.content.ContentValues;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.pushtorefresh.storio.contentprovider.StorIOContentProvider;
import com.pushtorefresh.storio.contentprovider.query.InsertQuery;
import com.pushtorefresh.storio.contentprovider.query.UpdateQuery;
import com.pushtorefresh.storio.operation.MapFunc;

import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.assertTrue;
import static junit.framework.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class DefaultPutResolverTest {

    private static class TestItem {

        static final String COLUMN_ID = "custom_id"; // why not?

        static final MapFunc<TestItem, ContentValues> MAP_TO_CONTENT_VALUES = new MapFunc<TestItem, ContentValues>() {

            // ContentValues should be mocked for usage in tests (damn you Android...)
            // but we can not mock equals() method
            // so, we will return SAME ContentValues for object and assertEquals() will pass
            @NonNull
            private final Map<TestItem, ContentValues> map = new HashMap<>();

            @Override
            public ContentValues map(TestItem testItem) {
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

        @Nullable
        private final Long id;

        TestItem(@Nullable Long id) {
            this.id = id;
        }

        @Nullable
        Long getId() {
            return id;
        }
    }

    /**
     * Verifies behavior of {@link DefaultPutResolver} for "insert"
     */
    @Test
    public void insert() {
        final StorIOContentProvider storIOContentProvider = mock(StorIOContentProvider.class);
        final StorIOContentProvider.Internal internal = mock(StorIOContentProvider.Internal.class);

        when(storIOContentProvider.internal())
                .thenReturn(internal);

        final Uri uri = mock(Uri.class);

        final PutResolver<TestItem> putResolver = new DefaultPutResolver<TestItem>() {
            @NonNull
            @Override
            protected String getIdColumnName(@NonNull ContentValues contentValues) {
                return TestItem.COLUMN_ID;
            }

            @NonNull
            @Override
            protected Uri getUri(@NonNull ContentValues contentValues) {
                return uri;
            }

            @Override
            public void afterPut(@NonNull TestItem object, @NonNull PutResult putResult) {
                fail("Should not be called");
            }
        };

        final TestItem testItem = new TestItem(null); // item without id, should be inserted
        final ContentValues expectedContentValues = TestItem.MAP_TO_CONTENT_VALUES.map(testItem);

        final InsertQuery expectedInsertQuery = new InsertQuery.Builder()
                .uri(uri)
                .build();

        final Uri expectedInsertedUri = mock(Uri.class);

        when(internal.insert(expectedInsertQuery, expectedContentValues))
                .thenReturn(expectedInsertedUri);

        // Performing Put that should "insert"
        final PutResult putResult = putResolver.performPut(storIOContentProvider, expectedContentValues);

        // checks that required insert was performed
        verify(internal, times(1)).insert(eq(expectedInsertQuery), eq(expectedContentValues));

        // only one insert should occur
        verify(internal, times(1)).insert(any(InsertQuery.class), any(ContentValues.class));

        // no updates should occur
        verify(internal, times(0)).update(any(UpdateQuery.class), any(ContentValues.class));

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
        final StorIOContentProvider storIOContentProvider = mock(StorIOContentProvider.class);
        final StorIOContentProvider.Internal internal = mock(StorIOContentProvider.Internal.class);

        when(storIOContentProvider.internal())
                .thenReturn(internal);

        final Uri uri = mock(Uri.class);

        final PutResolver<TestItem> putResolver = new DefaultPutResolver<TestItem>() {
            @NonNull
            @Override
            protected String getIdColumnName(@NonNull ContentValues contentValues) {
                return TestItem.COLUMN_ID;
            }

            @NonNull
            @Override
            protected Uri getUri(@NonNull ContentValues contentValues) {
                return uri;
            }

            @Override
            public void afterPut(@NonNull TestItem object, @NonNull PutResult putResult) {
                fail("Should not be called");
            }
        };

        final Long itemId = 24L;

        final TestItem testItem = new TestItem(itemId); // item with some id, should be updated
        final ContentValues expectedContentValues = TestItem.MAP_TO_CONTENT_VALUES.map(testItem);

        final UpdateQuery expectedUpdateQuery = new UpdateQuery.Builder()
                .uri(uri)
                .where(TestItem.COLUMN_ID + "=?")
                .whereArgs(String.valueOf(itemId))
                .build();

        final Integer expectedNumberOfRowsUpdated = 2;

        when(internal.update(eq(expectedUpdateQuery), eq(expectedContentValues)))
                .thenReturn(expectedNumberOfRowsUpdated);

        // Performing Put that should "update"
        final PutResult putResult = putResolver.performPut(storIOContentProvider, expectedContentValues);

        // checks that required update was performed
        verify(internal, times(1)).update(eq(expectedUpdateQuery), eq(expectedContentValues));

        // only one call to update should occur
        verify(internal, times(1)).update(any(UpdateQuery.class), any(ContentValues.class));

        // no inserts should occur
        verify(internal, times(0)).insert(any(InsertQuery.class), any(ContentValues.class));

        // put result checks
        assertTrue(putResult.wasUpdated());
        assertFalse(putResult.wasInserted());

        assertEquals(expectedNumberOfRowsUpdated, putResult.numberOfRowsUpdated());
        assertNull(putResult.insertedUri());
    }

    /**
     * Verifies behavior of {@link DefaultPutResolver} for situation,
     * when object has "id" and {@link DefaultPutResolver} will try to "update" values in the db,
     * but no values will be updated so it will make "insert" after "update"
     */
    @Test
    public void insertAfterFailedUpdate() {
        final StorIOContentProvider storIOContentProvider = mock(StorIOContentProvider.class);
        final StorIOContentProvider.Internal internal = mock(StorIOContentProvider.Internal.class);

        when(storIOContentProvider.internal())
                .thenReturn(internal);

        final Uri uri = mock(Uri.class);

        final PutResolver<TestItem> putResolver = new DefaultPutResolver<TestItem>() {
            @NonNull
            @Override
            protected String getIdColumnName(@NonNull ContentValues contentValues) {
                return TestItem.COLUMN_ID;
            }

            @NonNull
            @Override
            protected Uri getUri(@NonNull ContentValues contentValues) {
                return uri;
            }

            @Override
            public void afterPut(@NonNull TestItem object, @NonNull PutResult putResult) {
                fail("Should not be called");
            }
        };

        final Long itemId = 24L;

        final TestItem testItem = new TestItem(itemId); // item with some id, should be updated
        final ContentValues expectedContentValues = TestItem.MAP_TO_CONTENT_VALUES.map(testItem);

        final UpdateQuery expectedUpdateQuery = new UpdateQuery.Builder()
                .uri(uri)
                .where(TestItem.COLUMN_ID + "=?")
                .whereArgs(String.valueOf(itemId))
                .build();

        final Integer expectedNumberOfRowsUpdated = 0;

        when(internal.update(eq(expectedUpdateQuery), eq(expectedContentValues)))
                .thenReturn(expectedNumberOfRowsUpdated);

        final InsertQuery expectedInsertQuery = new InsertQuery.Builder()
                .uri(uri)
                .build();

        final Uri expectedInsertedUri = mock(Uri.class);

        when(internal.insert(eq(expectedInsertQuery), eq(expectedContentValues)))
                .thenReturn(expectedInsertedUri);

        // Performing Put that should "update", then "insert"
        final PutResult putResult = putResolver.performPut(storIOContentProvider, expectedContentValues);

        // checks that required update was performed
        verify(internal, times(1)).update(eq(expectedUpdateQuery), eq(expectedContentValues));

        // only one call to update should occur
        verify(internal, times(1)).update(any(UpdateQuery.class), any(ContentValues.class));

        // then one insert should occur
        verify(internal, times(1)).insert(eq(expectedInsertQuery), eq(expectedContentValues));

        // only one insert should occur
        verify(internal, times(1)).insert(any(InsertQuery.class), any(ContentValues.class));

        // put result checks
        assertTrue(putResult.wasInserted());
        assertFalse(putResult.wasUpdated());

        assertEquals(expectedInsertedUri, putResult.insertedUri());
        assertNull(putResult.numberOfRowsUpdated());
    }
}
