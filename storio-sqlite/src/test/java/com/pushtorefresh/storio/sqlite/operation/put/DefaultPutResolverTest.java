package com.pushtorefresh.storio.sqlite.operation.put;

import android.content.ContentValues;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.pushtorefresh.storio.sqlite.StorIOSQLite;
import com.pushtorefresh.storio.sqlite.query.InsertQuery;
import com.pushtorefresh.storio.sqlite.query.UpdateQuery;
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

        final static String TABLE = "someTable";
        final static String ID_COLUMN_NAME = "customIdColumnName";

        static final MapFunc<TestItem, ContentValues> MAP_TO_CONTENT_VALUES = new MapFunc<TestItem, ContentValues>() {

            // ContentValues should be mocked for usage in tests (damn you Android...)
            // but we can not mock equals() method
            // so, we will return SAME ContentValues for object and assertEquals() will pass
            @NonNull
            private final Map<TestItem, ContentValues> map = new HashMap<TestItem, ContentValues>();

            @NonNull
            @Override
            public ContentValues map(@NonNull TestItem testItem) {
                if (map.containsKey(testItem)) {
                    return map.get(testItem);
                } else {
                    final ContentValues contentValues = mock(ContentValues.class);

                    when(contentValues.get(ID_COLUMN_NAME))
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
        final StorIOSQLite storIOSQLite = mock(StorIOSQLite.class);
        final StorIOSQLite.Internal internal = mock(StorIOSQLite.Internal.class);

        when(storIOSQLite.internal())
                .thenReturn(internal);

        final Long expectedInsertedId = 24L;

        when(internal.insert(any(InsertQuery.class), any(ContentValues.class)))
                .thenReturn(expectedInsertedId);

        final InsertQuery expectedInsertQuery = new InsertQuery.Builder()
                .table(TestItem.TABLE)
                .nullColumnHack(null)
                .build();

        final PutResolver<TestItem> putResolver = new DefaultPutResolver<TestItem>() {
            @NonNull
            @Override
            protected String getTable() {
                return TestItem.TABLE;
            }

            @NonNull
            @Override
            protected String getIdColumnName() {
                return TestItem.ID_COLUMN_NAME;
            }

            @Override
            public void afterPut(@NonNull TestItem object, @NonNull PutResult putResult) {
                fail("Should not be called");
            }
        };

        final TestItem testItem = new TestItem(null); // item without id, should be inserted
        final ContentValues expectedContentValues = TestItem.MAP_TO_CONTENT_VALUES.map(testItem);

        // Performing Put that should "insert"
        final PutResult putResult = putResolver.performPut(storIOSQLite, TestItem.MAP_TO_CONTENT_VALUES.map(testItem));

        // checks that required insert was performed
        verify(internal, times(1)).insert(eq(expectedInsertQuery), eq(expectedContentValues));

        // only one insert should occur
        verify(internal, times(1)).insert(any(InsertQuery.class), any(ContentValues.class));

        // no updates should occur
        verify(internal, times(0)).update(any(UpdateQuery.class), any(ContentValues.class));

        // put result checks
        assertTrue(putResult.wasInserted());
        assertFalse(putResult.wasUpdated());

        assertEquals(expectedInsertedId, putResult.insertedId());
        assertNull(putResult.numberOfRowsUpdated());
    }

    /**
     * Verifies behavior of {@link DefaultPutResolver} for "update"
     */
    @Test
    public void update() {
        final StorIOSQLite storIOSQLite = mock(StorIOSQLite.class);
        final StorIOSQLite.Internal internal = mock(StorIOSQLite.Internal.class);

        when(storIOSQLite.internal())
                .thenReturn(internal);

        when(internal.update(any(UpdateQuery.class), any(ContentValues.class)))
                .thenReturn(1);

        final PutResolver<TestItem> putResolver = new DefaultPutResolver<TestItem>() {
            @NonNull
            @Override
            protected String getTable() {
                return TestItem.TABLE;
            }

            @NonNull
            @Override
            protected String getIdColumnName() {
                return TestItem.ID_COLUMN_NAME;
            }

            @Override
            public void afterPut(@NonNull TestItem object, @NonNull PutResult putResult) {
                fail("Should not be called");
            }
        };

        final TestItem testItem = new TestItem(1234L); // item with some id will be updated
        final ContentValues expectedContentValues = TestItem.MAP_TO_CONTENT_VALUES.map(testItem);

        final PutResult putResult = putResolver.performPut(storIOSQLite, expectedContentValues);

        final UpdateQuery expectedUpdateQuery = new UpdateQuery.Builder()
                .table(TestItem.TABLE)
                .where(TestItem.ID_COLUMN_NAME + "=?")
                .whereArgs(String.valueOf(testItem.getId()))
                .build();

        // checks that required update was performed
        verify(internal, times(1)).update(eq(expectedUpdateQuery), eq(expectedContentValues));

        // only one update should occur
        verify(internal, times(1)).update(any(UpdateQuery.class), any(ContentValues.class));

        // no inserts should occur
        verify(internal, times(0)).insert(any(InsertQuery.class), any(ContentValues.class));

        // put result checks
        assertTrue(putResult.wasUpdated());
        assertFalse(putResult.wasInserted());

        assertEquals(Integer.valueOf(1), putResult.numberOfRowsUpdated());
        assertNull(putResult.insertedId());
    }

    /**
     * Verifies behavior of {@link DefaultPutResolver} for situation,
     * when object has "id" and {@link DefaultPutResolver} will try to "update" values in the db,
     * but no values will be updated so it will make "insert" after "update"
     */
    @Test
    public void insertAfterFailedUpdate() {
        final StorIOSQLite storIOSQLite = mock(StorIOSQLite.class);
        final StorIOSQLite.Internal internal = mock(StorIOSQLite.Internal.class);

        when(storIOSQLite.internal())
                .thenReturn(internal);

        when(internal.update(any(UpdateQuery.class), any(ContentValues.class)))
                .thenReturn(0);

        final Long expectedInsertId = 24L;

        when(internal.insert(any(InsertQuery.class), any(ContentValues.class)))
                .thenReturn(expectedInsertId);

        final PutResolver<TestItem> putResolver = new DefaultPutResolver<TestItem>() {
            @NonNull
            @Override
            protected String getTable() {
                return TestItem.TABLE;
            }

            @NonNull
            @Override
            protected String getIdColumnName() {
                return TestItem.ID_COLUMN_NAME;
            }

            @Override
            public void afterPut(@NonNull TestItem object, @NonNull PutResult putResult) {
                fail("Should not be called");
            }
        };

        final TestItem testItem = new TestItem(123L);
        final ContentValues expectedContentValues = TestItem.MAP_TO_CONTENT_VALUES.map(testItem);

        final PutResult putResult = putResolver.performPut(storIOSQLite, expectedContentValues);

        final UpdateQuery expectedUpdateQuery = new UpdateQuery.Builder()
                .table(TestItem.TABLE)
                .where(TestItem.ID_COLUMN_NAME + "=?")
                .whereArgs(String.valueOf(testItem.getId()))
                .build();

        final InsertQuery expectedInsertQuery = new InsertQuery.Builder()
                .table(TestItem.TABLE)
                .nullColumnHack(null)
                .build();

        // checks that required update was performed
        verify(internal, times(1)).update(eq(expectedUpdateQuery), eq(expectedContentValues));

        // only one update should occur
        verify(internal, times(1)).update(any(UpdateQuery.class), any(ContentValues.class));

        // then one insert should occur
        verify(internal, times(1)).insert(eq(expectedInsertQuery), eq(expectedContentValues));

        // only one insert should occur
        verify(internal, times(1)).insert(any(InsertQuery.class), any(ContentValues.class));

        // put result checks
        assertTrue(putResult.wasInserted());
        assertFalse(putResult.wasUpdated());

        assertEquals(expectedInsertId, putResult.insertedId());
        assertNull(putResult.numberOfRowsUpdated());
    }
}
