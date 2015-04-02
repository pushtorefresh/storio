package com.pushtorefresh.storio.sample.db.entity;

import android.content.ContentValues;
import android.database.Cursor;
import android.provider.BaseColumns;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.pushtorefresh.storio.operation.MapFunc;
import com.pushtorefresh.storio.sqlitedb.operation.put.DefaultPutResolver;
import com.pushtorefresh.storio.sqlitedb.operation.put.PutResolver;
import com.pushtorefresh.storio.sqlitedb.operation.put.PutResult;
import com.pushtorefresh.storio.sqlitedb.query.Query;

/**
 * Just for demonstration, real Tweet structure is much more complex
 */
public class Tweet {

    public static final String TABLE = "tweets";

    public static final String COLUMN_ID = BaseColumns._ID;

    /**
     * For example: "artem_zin" without "@"
     */
    public static final String COLUMN_AUTHOR = "author";

    /**
     * For example: "Check out StorIO — modern API for SQLiteDatabase & ContentResolver #androiddev"
     */
    public static final String COLUMN_CONTENT = "content";

    public static final MapFunc<Cursor, Tweet> MAP_FROM_CURSOR = new MapFunc<Cursor, Tweet>() {
        @Override public Tweet map(Cursor cursor) {
            return new Tweet(
                    cursor.getLong(cursor.getColumnIndex(COLUMN_ID)),
                    cursor.getString(cursor.getColumnIndex(COLUMN_AUTHOR)),
                    cursor.getString(cursor.getColumnIndex(COLUMN_CONTENT))
            );
        }
    };

    public static final MapFunc<Tweet, ContentValues> MAP_TO_CONTENT_VALUES = new MapFunc<Tweet, ContentValues>() {
        @Override public ContentValues map(Tweet tweet) {
            final ContentValues contentValues = new ContentValues(3); // wow, such optimization

            contentValues.put(COLUMN_ID, tweet.id);
            contentValues.put(COLUMN_AUTHOR, tweet.author);
            contentValues.put(COLUMN_CONTENT, tweet.content);

            return contentValues;
        }
    };

    public static final Query GET_ALL_QUERY = new Query.Builder()
            .table(TABLE)
            .build();

    public static final PutResolver<Tweet> PUT_RESOLVER = new DefaultPutResolver<Tweet>() {
        @NonNull @Override protected String getTable() {
            return TABLE;
        }

        @Override public void afterPut(@NonNull Tweet object, @NonNull PutResult putResult) {
            if (putResult.wasInserted()) {
                object.id = putResult.insertedId();
            }
        }
    };

    // if object was not inserted into db, id will be null
    @Nullable private volatile Long id;

    @NonNull private final String author;
    @NonNull private final String content;

    private Tweet(Long id, @NonNull String author, @NonNull String content) {
        this.id = id;
        this.author = author;
        this.content = content;
    }

    @NonNull public static Tweet newTweet(@NonNull String author, @NonNull String content) {
        return new Tweet(null, author, content);
    }

    @Nullable public Long getId() {
        return id;
    }

    @NonNull public String getAuthor() {
        return author;
    }

    @NonNull public String getContent() {
        return content;
    }


}
