package com.pushtorefresh.storio.contentresolver.integration;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;

import com.pushtorefresh.storio.contentresolver.operations.delete.DefaultDeleteResolver;
import com.pushtorefresh.storio.contentresolver.operations.delete.DeleteResolver;
import com.pushtorefresh.storio.contentresolver.operations.get.DefaultGetResolver;
import com.pushtorefresh.storio.contentresolver.operations.get.GetResolver;
import com.pushtorefresh.storio.contentresolver.operations.put.DefaultPutResolver;
import com.pushtorefresh.storio.contentresolver.operations.put.PutResolver;
import com.pushtorefresh.storio.contentresolver.queries.DeleteQuery;
import com.pushtorefresh.storio.contentresolver.queries.InsertQuery;
import com.pushtorefresh.storio.contentresolver.queries.UpdateQuery;

class TweetMeta {

    static final String TABLE = "tweets";

    // Custom internal id field name, that used instead of "_id".
    static final String COLUMN_ID = "tweet_internal_id";

    static final String COLUMN_AUTHOR_ID = "author_id";
    static final String COLUMN_CONTENT_TEXT = "content_text";

    static final String SQL_CREATE_TABLE = "CREATE TABLE " + TABLE + "(" +
            COLUMN_ID + " INTEGER PRIMARY KEY, " +
            COLUMN_AUTHOR_ID + " INTEGER NOT NULL, " +
            COLUMN_CONTENT_TEXT + " TEXT NOT NULL" +
            ");";

    static final Uri CONTENT_URI = Uri.parse("content://" + TestContentProvider.AUTHORITY + "/" + TABLE);

    static final PutResolver<Tweet> PUT_RESOLVER = new DefaultPutResolver<Tweet>() {
        @NonNull
        @Override
        protected InsertQuery mapToInsertQuery(@NonNull Tweet object) {
            return InsertQuery.builder()
                    .uri(CONTENT_URI)
                    .build();
        }

        @NonNull
        @Override
        protected UpdateQuery mapToUpdateQuery(@NonNull Tweet tweet) {
            return UpdateQuery.builder()
                    .uri(CONTENT_URI)
                    .where(COLUMN_ID + " = ?")
                    .whereArgs(tweet.id())
                    .build();
        }

        @NonNull
        @Override
        protected ContentValues mapToContentValues(@NonNull Tweet tweet) {
            final ContentValues contentValues = new ContentValues(3); // GC, relax, take it eeeeasy

            contentValues.put(COLUMN_ID, tweet.id());
            contentValues.put(COLUMN_AUTHOR_ID, tweet.authorId());
            contentValues.put(COLUMN_CONTENT_TEXT, tweet.contentText());

            return contentValues;
        }
    };

    static final GetResolver<Tweet> GET_RESOLVER = new DefaultGetResolver<Tweet>() {
        @NonNull
        @Override
        public Tweet mapFromCursor(@NonNull Cursor cursor) {
            return Tweet.newInstance(
                    cursor.getLong(cursor.getColumnIndex(COLUMN_ID)),
                    cursor.getLong(cursor.getColumnIndex(COLUMN_AUTHOR_ID)),
                    cursor.getString(cursor.getColumnIndex(COLUMN_CONTENT_TEXT))
            );
        }
    };

    static final DeleteResolver<Tweet> DELETE_RESOLVER = new DefaultDeleteResolver<Tweet>() {
        @NonNull
        @Override
        protected DeleteQuery mapToDeleteQuery(@NonNull Tweet tweet) {
            return DeleteQuery.builder()
                    .uri(CONTENT_URI)
                    .where(COLUMN_ID + " = ?")
                    .whereArgs(tweet.id())
                    .build();
        }
    };

    static final DeleteQuery DELETE_QUERY_ALL = DeleteQuery.builder()
            .uri(CONTENT_URI)
            .build();


    private TweetMeta() {
        throw new IllegalStateException("No instances please");
    }


}
