package com.pushtorefresh.storio.sample.provider.meta;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;

import com.pushtorefresh.storio.contentresolver.operation.delete.DefaultDeleteResolver;
import com.pushtorefresh.storio.contentresolver.operation.delete.DeleteResolver;
import com.pushtorefresh.storio.contentresolver.operation.get.DefaultGetResolver;
import com.pushtorefresh.storio.contentresolver.operation.get.GetResolver;
import com.pushtorefresh.storio.contentresolver.operation.put.DefaultPutResolver;
import com.pushtorefresh.storio.contentresolver.operation.put.PutResolver;
import com.pushtorefresh.storio.contentresolver.query.DeleteQuery;
import com.pushtorefresh.storio.contentresolver.query.InsertQuery;
import com.pushtorefresh.storio.contentresolver.query.UpdateQuery;
import com.pushtorefresh.storio.sample.db.entity.Tweet;
import com.pushtorefresh.storio.sample.db.table.TweetTableMeta;
import com.pushtorefresh.storio.sample.provider.SampleContentProvider;

public class TweetMeta {

    @NonNull
    public static final Uri CONTENT_URI = Uri.parse("content://" + SampleContentProvider.AUTHORITY + "/tweets");

    @NonNull
    public static final PutResolver<Tweet> PUT_RESOLVER = new DefaultPutResolver<Tweet>() {
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
                    .where(TweetTableMeta.COLUMN_ID + " = ?")
                    .whereArgs(tweet.id())
                    .build();
        }

        @NonNull
        @Override
        protected ContentValues mapToContentValues(@NonNull Tweet object) {
            ContentValues contentValues = new ContentValues();

            contentValues.put(TweetTableMeta.COLUMN_ID, object.id());
            contentValues.put(TweetTableMeta.COLUMN_AUTHOR, object.author());
            contentValues.put(TweetTableMeta.COLUMN_CONTENT, object.content());

            return contentValues;
        }
    };

    @NonNull
    public static final GetResolver<Tweet> GET_RESOLVER = new DefaultGetResolver<Tweet>() {
        @NonNull
        @Override
        public Tweet mapFromCursor(@NonNull Cursor cursor) {
            return Tweet.newTweet(
                    cursor.getLong(cursor.getColumnIndexOrThrow(TweetTableMeta.COLUMN_ID)),
                    cursor.getString(cursor.getColumnIndexOrThrow(TweetTableMeta.COLUMN_AUTHOR)),
                    cursor.getString(cursor.getColumnIndexOrThrow(TweetTableMeta.COLUMN_CONTENT))
            );
        }
    };

    @NonNull
    public static final DeleteResolver<Tweet> DELETE_RESOLVER = new DefaultDeleteResolver<Tweet>() {
        @NonNull
        @Override
        protected DeleteQuery mapToDeleteQuery(@NonNull Tweet tweet) {
            return DeleteQuery.builder()
                    .uri(CONTENT_URI)
                    .where(TweetTableMeta.COLUMN_ID + " = ?")
                    .whereArgs(tweet.id())
                    .build();
        }
    };
}
