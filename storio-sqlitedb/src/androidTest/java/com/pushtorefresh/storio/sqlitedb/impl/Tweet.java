package com.pushtorefresh.storio.sqlitedb.impl;

import android.content.ContentValues;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.pushtorefresh.storio.operation.MapFunc;
import com.pushtorefresh.storio.sqlitedb.operation.put.DefaultPutResolver;
import com.pushtorefresh.storio.sqlitedb.operation.put.PutResolver;
import com.pushtorefresh.storio.sqlitedb.operation.put.PutResult;
import com.pushtorefresh.storio.sqlitedb.query.DeleteQuery;

/**
 * Test class with custom internal id field name.
 */
public class Tweet {

    public static final String TABLE = "tweets";

    // Custom internal id field name, that used instead of "_id".
    public static final String COLUMN_ID = "tweet_internal_id";

    public static final String COLUMN_AUTHOR_ID = "author_id";
    public static final String COLUMN_CONTENT = "content";

    public static final String CREATE_TABLE = "CREATE TABLE " + TABLE + "(" +
            COLUMN_ID + " INTEGER PRIMARY KEY, " +
            COLUMN_AUTHOR_ID + " INTEGER NOT NULL, " +
            COLUMN_CONTENT + " TEXT NOT NULL" +
            ");";

    public static final DeleteQuery DELETE_ALL = new DeleteQuery.Builder()
            .table(TABLE)
            .build();

    public static final MapFunc<Cursor, Tweet> MAP_FROM_CURSOR = new MapFunc<Cursor, Tweet>() {
        @Override public Tweet map(Cursor cursor) {
            return new Tweet(
                    cursor.getLong(cursor.getColumnIndex(COLUMN_ID)),
                    cursor.getLong(cursor.getColumnIndex(COLUMN_AUTHOR_ID)),
                    cursor.getString(cursor.getColumnIndex(COLUMN_CONTENT))
            );
        }
    };

    public static final MapFunc<Tweet, ContentValues> MAP_TO_CONTENT_VALUES = new MapFunc<Tweet, ContentValues>() {
        @Override public ContentValues map(Tweet tweet) {
            final ContentValues contentValues = new ContentValues(3);

            contentValues.put(COLUMN_ID, tweet.id);
            contentValues.put(COLUMN_AUTHOR_ID, tweet.authorId);
            contentValues.put(COLUMN_CONTENT, tweet.content);

            return contentValues;
        }
    };

    public static final PutResolver<Tweet> PUT_RESOLVER = new DefaultPutResolver<Tweet>() {
        @NonNull @Override protected String getTable() {
            return TABLE;
        }

        @Override public void afterPut(@NonNull Tweet object, @NonNull PutResult putResult) {
            if (putResult.wasInserted()) {
                object.id = putResult.insertedId();
            }
        }

        @NonNull
        @Override
        protected String getIdColumnName(@NonNull ContentValues contentValues) {
            return COLUMN_ID;   //  Specific internal id field name.
        }
    };

    @Nullable private volatile Long id;
    @NonNull private final Long authorId;
    @NonNull private final String content;

    public Tweet(@Nullable Long id, @NonNull Long authorId, @NonNull String content) {
        this.id = id;
        this.authorId = authorId;
        this.content = content;
    }

    @Nullable public Long getId() {
        return id;
    }

    @NonNull public Long getAuthorId() {
        return authorId;
    }

    @NonNull public String getContent() {
        return content;
    }

    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Tweet tweet = (Tweet) o;

        if (id != null ? !id.equals(tweet.id) : tweet.id != null) return false;
        if (!authorId.equals(tweet.authorId)) return false;
        return content.equals(tweet.content);

    }

    @Override public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + authorId.hashCode();
        result = 31 * result + content.hashCode();
        return result;
    }
}
