package com.pushtorefresh.storio.sample.sample_code;

import android.support.annotation.NonNull;

import com.pushtorefresh.storio.sample.db.entities.TweetWithUser;
import com.pushtorefresh.storio.sample.db.tables.TweetsTable;
import com.pushtorefresh.storio.sample.db.tables.UsersTable;
import com.pushtorefresh.storio.sqlite.StorIOSQLite;
import com.pushtorefresh.storio.sqlite.queries.RawQuery;

import java.util.List;

/**
 * Examples with relations (SQL JOIN)
 */
public class Relations {

    @NonNull
    private final StorIOSQLite storIOSQLite;

    public Relations(@NonNull StorIOSQLite storIOSQLite) {
        this.storIOSQLite = storIOSQLite;
    }

    public List<TweetWithUser> tweetWithUserGet() {
        return storIOSQLite
                .get()
                .listOfObjects(TweetWithUser.class)
                .withQuery(RawQuery.builder()
                        .query("SELECT * FROM " + TweetsTable.TABLE
                                + " JOIN " + UsersTable.TABLE
                                + " ON " + TweetsTable.TABLE + "." + TweetsTable.COLUMN_AUTHOR
                                + " = " + UsersTable.TABLE + "." + UsersTable.COLUMN_NICK)
                        .build())
                .prepare()
                .executeAsBlocking();
    }
}
