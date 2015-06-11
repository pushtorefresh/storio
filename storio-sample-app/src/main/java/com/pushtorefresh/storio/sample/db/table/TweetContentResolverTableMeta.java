package com.pushtorefresh.storio.sample.db.table;

import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;

import com.pushtorefresh.storio.contentresolver.StorIOContentResolver;
import com.pushtorefresh.storio.contentresolver.operation.delete.DefaultDeleteResolver;
import com.pushtorefresh.storio.contentresolver.operation.delete.DeleteResolver;
import com.pushtorefresh.storio.contentresolver.operation.get.DefaultGetResolver;
import com.pushtorefresh.storio.contentresolver.operation.get.GetResolver;
import com.pushtorefresh.storio.contentresolver.operation.put.DefaultPutResolver;
import com.pushtorefresh.storio.contentresolver.operation.put.PutResolver;
import com.pushtorefresh.storio.contentresolver.operation.put.PutResult;
import com.pushtorefresh.storio.contentresolver.query.DeleteQuery;
import com.pushtorefresh.storio.contentresolver.query.InsertQuery;
import com.pushtorefresh.storio.contentresolver.query.UpdateQuery;
import com.pushtorefresh.storio.sample.db.entity.Tweet;
import com.pushtorefresh.storio.sample.provider.SampleContentProvider;

public class TweetContentResolverTableMeta extends TweetTableMeta {

    public static final String CONTENT_URI = "content://" + SampleContentProvider.AUTHORITY + "/" + TABLE;

    public static final PutResolver<Tweet> PUT_RESOLVER = new DefaultPutResolver<Tweet>() {

        @NonNull
        @Override
        public PutResult performPut(@NonNull StorIOContentResolver storIOContentResolver, @NonNull Tweet tweet) {
            final PutResult putResult = super.performPut(storIOContentResolver, tweet);

            if (putResult.wasInserted()) {
                final Uri insertedUri = putResult.insertedUri();
                tweet.setId(insertedUri != null ? ContentUris.parseId(insertedUri) : null);
            }

            return putResult;
        }

        @NonNull
        @Override
        protected InsertQuery mapToInsertQuery(@NonNull Tweet object) {
            return new InsertQuery.Builder()
                    .uri(CONTENT_URI)
                    .build();
        }

        @NonNull
        @Override
        protected UpdateQuery mapToUpdateQuery(@NonNull Tweet tweet) {
            return new UpdateQuery.Builder()
                    .uri(CONTENT_URI)
                    .where(COLUMN_ID + " = ?")
                    .whereArgs(tweet.id())
                    .build();
        }

        @NonNull
        @Override
        protected ContentValues mapToContentValues(@NonNull Tweet tweet) {
            final ContentValues contentValues = new ContentValues(3); // wow, such optimization

            contentValues.put(COLUMN_ID, tweet.id());
            contentValues.put(COLUMN_AUTHOR, tweet.author());
            contentValues.put(COLUMN_CONTENT, tweet.content());

            return contentValues;
        }
    };
    public static final GetResolver<Tweet> GET_RESOLVER = new DefaultGetResolver<Tweet>() {
        @NonNull
        @Override
        public Tweet mapFromCursor(@NonNull Cursor cursor) {
            return Tweet.newTweet(
                    cursor.getLong(cursor.getColumnIndex(COLUMN_ID)),
                    cursor.getString(cursor.getColumnIndex(COLUMN_AUTHOR)),
                    cursor.getString(cursor.getColumnIndex(COLUMN_CONTENT))
            );
        }
    };
    public static final DeleteResolver<Tweet> DELETE_RESOLVER = new DefaultDeleteResolver<Tweet>() {
        @NonNull
        @Override
        public DeleteQuery mapToDeleteQuery(@NonNull Tweet object) {
            return new DeleteQuery.Builder()
                    .uri(CONTENT_URI)
                    .where(COLUMN_ID + " = ?")
                    .whereArgs(object.id())
                    .build();
        }
    };
    public static final DeleteQuery DELETE_ALL = new DeleteQuery.Builder()
            .uri(Uri.parse(CONTENT_URI))
            .build();

    private TweetContentResolverTableMeta() {
        throw new IllegalStateException("No instances please");
    }
}
