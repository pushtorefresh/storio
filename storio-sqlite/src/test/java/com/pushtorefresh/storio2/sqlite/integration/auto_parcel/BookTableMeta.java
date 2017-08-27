package com.pushtorefresh.storio2.sqlite.integration.auto_parcel;

import android.content.ContentValues;
import android.database.Cursor;
import android.support.annotation.NonNull;

import com.pushtorefresh.storio2.sqlite.StorIOSQLite;
import com.pushtorefresh.storio2.sqlite.operations.delete.DefaultDeleteResolver;
import com.pushtorefresh.storio2.sqlite.operations.delete.DeleteResolver;
import com.pushtorefresh.storio2.sqlite.operations.get.DefaultGetResolver;
import com.pushtorefresh.storio2.sqlite.operations.get.GetResolver;
import com.pushtorefresh.storio2.sqlite.operations.put.DefaultPutResolver;
import com.pushtorefresh.storio2.sqlite.operations.put.PutResolver;
import com.pushtorefresh.storio2.sqlite.queries.DeleteQuery;
import com.pushtorefresh.storio2.sqlite.queries.InsertQuery;
import com.pushtorefresh.storio2.sqlite.queries.UpdateQuery;

final class BookTableMeta {

    private BookTableMeta() {
        throw new IllegalStateException("No instances please.");
    }

    @NonNull
    static final String TABLE = "books";

    @NonNull
    static final String COLUMN_ID = "_id";

    @NonNull
    static final String COLUMN_TITLE = "title";

    @NonNull
    static final String COLUMN_AUTHOR = "author";

    @NonNull
    static final String SQL_CREATE_TABLE = "CREATE TABLE " + TABLE + "(" +
            COLUMN_ID + " INTEGER PRIMARY KEY, " +
            COLUMN_TITLE + " TEXT NOT NULL," +
            COLUMN_AUTHOR + " TEXT NOT NULL);";

    @NonNull
    static final PutResolver<Book> PUT_RESOLVER = new DefaultPutResolver<Book>() {
        @NonNull
        @Override
        protected InsertQuery mapToInsertQuery(@NonNull Book object) {
            return InsertQuery.builder()
                    .table(TABLE)
                    .build();
        }

        @NonNull
        @Override
        protected UpdateQuery mapToUpdateQuery(@NonNull Book book) {
            return UpdateQuery.builder()
                    .table(TABLE)
                    .where(COLUMN_ID + " = ?")
                    .whereArgs(book.id())
                    .build();
        }

        @NonNull
        @Override
        protected ContentValues mapToContentValues(@NonNull Book book) {
            final ContentValues contentValues = new ContentValues(3);

            contentValues.put(COLUMN_ID, book.id());
            contentValues.put(COLUMN_TITLE, book.title());
            contentValues.put(COLUMN_AUTHOR, book.author());

            return contentValues;
        }
    };

    @NonNull
    static final GetResolver<Book> GET_RESOLVER = new DefaultGetResolver<Book>() {
        @NonNull
        @Override
        public Book mapFromCursor(@NonNull StorIOSQLite storIOSQLite, @NonNull Cursor cursor) {
            return Book.builder()
                    .id(cursor.getInt(cursor.getColumnIndex(COLUMN_ID)))
                    .title(cursor.getString(cursor.getColumnIndex(COLUMN_TITLE)))
                    .author(cursor.getString(cursor.getColumnIndex(COLUMN_AUTHOR)))
                    .build();
        }
    };

    @NonNull
    static final DeleteResolver<Book> DELETE_RESOLVER = new DefaultDeleteResolver<Book>() {
        @NonNull
        @Override
        protected DeleteQuery mapToDeleteQuery(@NonNull Book book) {
            return DeleteQuery.builder()
                    .table(TABLE)
                    .where(COLUMN_ID + " = ?")
                    .whereArgs(book.id())
                    .build();
        }
    };
}
