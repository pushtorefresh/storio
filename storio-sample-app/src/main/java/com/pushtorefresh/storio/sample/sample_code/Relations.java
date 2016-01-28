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
    public static final String QUERY_COLUMN_TWEET_ID = "tweet_id";

    @NonNull
    public static final String QUERY_COLUMN_TWEET_AUTHOR = "tweet_author";

    @NonNull
    public static final String QUERY_COLUMN_TWEET_CONTENT = "tweet_content";

    @NonNull
    public static final String QUERY_COLUMN_USER_ID = "user_id";

    @NonNull
    public static final String QUERY_COLUMN_USER_NICK = "user_nick";

    @NonNull
    private final StorIOSQLite storIOSQLite;

    public Relations(@NonNull StorIOSQLite storIOSQLite) {
        this.storIOSQLite = storIOSQLite;
    }

    @NonNull
    public List<TweetWithUser> getTweetWithUser() {
        return storIOSQLite
                .get()
                .listOfObjects(TweetWithUser.class)
                .withQuery(RawQuery.builder()
                        .query("SELECT "
                                // Unfortunately we have columns with same names, so we need to give them aliases.
                                + TweetsTable.COLUMN_ID_WITH_TABLE_PREFIX + " AS \"" + QUERY_COLUMN_TWEET_ID + "\""
                                + ", "
                                + TweetsTable.COLUMN_AUTHOR_WITH_TABLE_PREFIX + " AS \"" + QUERY_COLUMN_TWEET_AUTHOR + "\""
                                + ", "
                                + TweetsTable.COLUMN_CONTENT_WITH_TABLE_PREFIX + " AS \"" + QUERY_COLUMN_TWEET_CONTENT + "\""
                                + ", "
                                + UsersTable.COLUMN_ID_WITH_TABLE_PREFIX + " AS \"" + QUERY_COLUMN_USER_ID + "\""
                                + ", "
                                + UsersTable.COLUMN_NICK_WITH_TABLE_PREFIX + " AS \"" + QUERY_COLUMN_USER_NICK + "\""
                                + " FROM " + TweetsTable.TABLE
                                + " JOIN " + UsersTable.TABLE
                                + " ON " + TweetsTable.COLUMN_AUTHOR_WITH_TABLE_PREFIX
                                + " = " + UsersTable.COLUMN_NICK_WITH_TABLE_PREFIX)
                        .build())
                .prepare()
                .executeAsBlocking();
    }
}
