package com.pushtorefresh.storio.sample.db.resolvers;


import android.database.Cursor;
import android.support.annotation.NonNull;

import com.pushtorefresh.storio.sample.db.entities.Tweet;
import com.pushtorefresh.storio.sample.db.entities.TweetWithUser;
import com.pushtorefresh.storio.sample.db.entities.User;
import com.pushtorefresh.storio.sample.db.tables.TweetsTable;
import com.pushtorefresh.storio.sample.db.tables.UsersTable;
import com.pushtorefresh.storio.sqlite.operations.get.DefaultGetResolver;

public class TweetWithUserGetResolver extends DefaultGetResolver<TweetWithUser> {

    // We expect that cursor will contain both Tweet and User: SQL JOIN
    @NonNull
    @Override
    public TweetWithUser mapFromCursor(@NonNull Cursor cursor) {
        final Tweet tweet = Tweet.newTweet(
                cursor.getLong(cursor.getColumnIndexOrThrow(TweetsTable.COLUMN_ID)),
                cursor.getString(cursor.getColumnIndexOrThrow(TweetsTable.COLUMN_AUTHOR)),
                cursor.getString(cursor.getColumnIndexOrThrow(TweetsTable.COLUMN_CONTENT))
        );

        final User user = User.newUser(
                cursor.getLong(cursor.getColumnIndexOrThrow(UsersTable.COLUMN_ID)),
                cursor.getString(cursor.getColumnIndexOrThrow(UsersTable.COLUMN_NICK))
        );

        return new TweetWithUser(tweet, user);
    }
}
