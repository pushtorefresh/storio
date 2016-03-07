package com.pushtorefresh.storio.contentresolver.integration;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.pushtorefresh.storio.contentresolver.operations.delete.DefaultDeleteResolver;
import com.pushtorefresh.storio.contentresolver.operations.delete.DeleteResolver;
import com.pushtorefresh.storio.contentresolver.operations.get.DefaultGetResolver;
import com.pushtorefresh.storio.contentresolver.operations.get.GetResolver;
import com.pushtorefresh.storio.contentresolver.operations.put.DefaultPutResolver;
import com.pushtorefresh.storio.contentresolver.operations.put.PutResolver;
import com.pushtorefresh.storio.contentresolver.queries.DeleteQuery;
import com.pushtorefresh.storio.contentresolver.queries.InsertQuery;
import com.pushtorefresh.storio.contentresolver.queries.UpdateQuery;

class TestItem {

    @NonNull
    static final Uri CONTENT_URI = Uri.parse("content://" + IntegrationContentProvider.AUTHORITY + "/test_item");

    @NonNull
    static final String CONTENT_PATH = "test_item";

    @NonNull
    static final String COLUMN_ID = "_id";

    @NonNull
    static final String COLUMN_VALUE = "value";

    @NonNull
    static final String COLUMN_OPTIONAL_VALUE = "optional_value";

    @Nullable
    private final Long id;

    @NonNull
    private final String value;

    @Nullable
    private final String optionalValue;

    private TestItem(@Nullable Long id, @NonNull String value, @Nullable String optionalValue) {
        this.id = id;
        this.value = value;
        this.optionalValue = optionalValue;
    }

    @Nullable
    public Long id() {
        return id;
    }

    @NonNull
    public String value() {
        return value;
    }

    @Nullable
    public String optionalValue() {
        return optionalValue;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TestItem testItem = (TestItem) o;

        if (id != null ? !id.equals(testItem.id) : testItem.id != null) return false;
        if (!value.equals(testItem.value)) return false;
        return !(optionalValue != null ? !optionalValue.equals(testItem.optionalValue) : testItem.optionalValue != null);

    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + value.hashCode();
        result = 31 * result + (optionalValue != null ? optionalValue.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "TestItem{" +
                "id=" + id +
                ", value='" + value + '\'' +
                ", optionalValue='" + optionalValue + '\'' +
                '}';
    }

    public boolean equalsWithoutId(@NonNull TestItem another) {
        if (!value.equals(another.value)) return false;
        return !(optionalValue != null ? !optionalValue.equals(another.optionalValue) : another.optionalValue != null);
    }

    @NonNull
    ContentValues toContentValues() {
        final ContentValues cv = new ContentValues(2);

        cv.put(COLUMN_ID, id);
        cv.put(COLUMN_VALUE, value);
        cv.put(COLUMN_OPTIONAL_VALUE, optionalValue);

        return cv;
    }

    @NonNull
    static TestItem create(@Nullable Long id, @NonNull String value) {
        return create(id, value, null);
    }

    @NonNull
    static TestItem create(@Nullable Long id, @NonNull String value, @Nullable String optionalValue) {
        return new TestItem(id, value, optionalValue);
    }

    @NonNull
    static TestItem fromCursor(@NonNull Cursor cursor) {
        return create(
                cursor.getLong(cursor.getColumnIndex(COLUMN_ID)),
                cursor.getString(cursor.getColumnIndex(COLUMN_VALUE)),
                cursor.getString(cursor.getColumnIndex(COLUMN_OPTIONAL_VALUE))
        );
    }

    @NonNull
    static final PutResolver<TestItem> PUT_RESOLVER = new DefaultPutResolver<TestItem>() {
        @NonNull
        @Override
        protected InsertQuery mapToInsertQuery(@NonNull TestItem object) {
            return InsertQuery.builder()
                    .uri(CONTENT_URI)
                    .build();
        }

        @NonNull
        @Override
        protected UpdateQuery mapToUpdateQuery(@NonNull TestItem object) {
            return UpdateQuery.builder()
                    .uri(CONTENT_URI)
                    .where(COLUMN_ID + "=?")
                    .whereArgs(object.id)
                    .build();
        }

        @NonNull
        @Override
        protected ContentValues mapToContentValues(@NonNull TestItem object) {
            return object.toContentValues();
        }
    };

    @NonNull
    static final GetResolver<TestItem> GET_RESOLVER = new DefaultGetResolver<TestItem>() {
        @NonNull
        @Override
        public TestItem mapFromCursor(@NonNull Cursor cursor) {
            return fromCursor(cursor);
        }
    };

    @NonNull
    static final DeleteResolver<TestItem> DELETE_RESOLVER = new DefaultDeleteResolver<TestItem>() {
        @NonNull
        @Override
        protected DeleteQuery mapToDeleteQuery(@NonNull TestItem object) {
            return DeleteQuery.builder()
                    .uri(CONTENT_URI)
                    .where(COLUMN_ID + "=?")
                    .whereArgs(object.id)
                    .build();
        }
    };
}
