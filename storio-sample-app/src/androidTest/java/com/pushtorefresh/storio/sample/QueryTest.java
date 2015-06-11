package com.pushtorefresh.storio.sample;

import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.test.runner.AndroidJUnit4;

import com.pushtorefresh.storio.contentresolver.operation.get.DefaultGetResolver;
import com.pushtorefresh.storio.contentresolver.query.Query;
import com.pushtorefresh.storio.sample.db.entity.Tweet;
import com.pushtorefresh.storio.sample.db.table.TweetContentResolverTableMeta;
import com.pushtorefresh.storio.sample.db.table.TweetTableMeta;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;

@RunWith(AndroidJUnit4.class)
public class QueryTest extends BaseTest {

    public QueryTest() {
        super();
    }

    @Test
    public void queryAll() {
        final List<Tweet> tweets = putTweets(3);
        tweetsInStorageCheck(tweets);
    }

    @Test
    public void queryOneByField() {
        final List<Tweet> tweets = putTweets(3);

        for (Tweet tweet : tweets) {
            final List<Tweet> tweetsFromQuery = storIOContentResolver
                    .get()
                    .listOfObjects(Tweet.class)
                    .withQuery(new Query.Builder()
                            .uri(TweetContentResolverTableMeta.CONTENT_URI)
                            .where(TweetTableMeta.COLUMN_CONTENT + "=?")
                            .whereArgs(tweet.content())
                            .build())
                    .prepare()
                    .executeAsBlocking();

            assertNotNull(tweetsFromQuery);
            assertEquals(tweetsFromQuery.size(), 1);
            assertEquals(tweetsFromQuery.get(0), tweet);
        }
    }

    @Test
    public void queryOrdered() {
        final List<Tweet> tweets = new ArrayList<Tweet>();
        for (int i = 1; i <= 3; ++i) {
            tweets.add(Tweet.newTweet("author" + i, "content" + i));
        }

        // Reverse sorting by author before inserting, for the purity of the experiment.
        Collections.sort(tweets, Collections.reverseOrder(byAuthorComparator));

        putTweets(tweets);

        final List<Tweet> tweetsFromQueryOrdered = storIOContentResolver
                .get()
                .listOfObjects(Tweet.class)
                .withQuery(new Query.Builder()
                        .uri(TweetContentResolverTableMeta.CONTENT_URI)
                        .sortOrder(TweetTableMeta.COLUMN_AUTHOR)
                        .build())
                .prepare()
                .executeAsBlocking();

        assertNotNull(tweetsFromQueryOrdered);
        assertEquals(tweets.size(), tweetsFromQueryOrdered.size());

        // Sorting by author for check ordering.
        Collections.sort(tweets, byAuthorComparator);

        for (int i = 0; i < tweets.size(); i++) {
            assertEquals(tweets.get(i), tweetsFromQueryOrdered.get(i));
        }
    }

    @Test
    public void queryOrderedDesc() {
        final List<Tweet> tweets = new ArrayList<Tweet>();
        for (int i = 1; i <= 3; ++i) {
            tweets.add(Tweet.newTweet("author" + i, "content" + i));
        }

        // Sorting by author before inserting, for the purity of the experiment.
        Collections.sort(tweets, byAuthorComparator);

        putTweets(tweets);

        final List<Tweet> tweetsFromQueryOrdered = storIOContentResolver
                .get()
                .listOfObjects(Tweet.class)
                .withQuery(new Query.Builder()
                        .uri(TweetContentResolverTableMeta.CONTENT_URI)
                        .sortOrder(TweetTableMeta.COLUMN_AUTHOR + " DESC")
                        .build())
                .prepare()
                .executeAsBlocking();

        assertNotNull(tweetsFromQueryOrdered);
        assertEquals(tweets.size(), tweetsFromQueryOrdered.size());

        // Reverse sorting by author for check ordering.
        Collections.sort(tweets, Collections.reverseOrder(byAuthorComparator));


        for (int i = 0; i < tweets.size(); i++) {
            assertEquals(tweets.get(i), tweetsFromQueryOrdered.get(i));
        }
    }

    @Test
    public void queryProjection() {
        final List<Tweet> tweets = putTweets(3);

        final String anotherAuthor = "anotherAuthor";
        final String anotherContent = "anotherContent";

        final List<Tweet> tweetsFromStorage = storIOContentResolver
                .get()
                .listOfObjects(Tweet.class)
                .withQuery(new Query.Builder()
                        .uri(TweetContentResolverTableMeta.CONTENT_URI)
                        .columns(TweetTableMeta.COLUMN_ID)
                        .build())
                .withGetResolver(new DefaultGetResolver<Tweet>() {
                    @NonNull
                    @Override
                    public Tweet mapFromCursor(@NonNull Cursor cursor) {
                        final Long id = cursor.getLong(cursor.getColumnIndex(TweetTableMeta.COLUMN_ID));
                        return Tweet.newTweet(id, anotherAuthor, anotherContent);
                    }
                })
                .prepare()
                .executeAsBlocking();

        assertNotNull(tweetsFromStorage);
        assertEquals(tweets.size(), tweetsFromStorage.size());

        for (int i = 0; i < tweets.size(); i++) {
            final Tweet tweet = tweets.get(i);
            final Tweet tweetFromStorage = tweetsFromStorage.get(i);
            assertEquals(tweet.id(), tweetFromStorage.id());
            assertEquals(anotherAuthor, tweetFromStorage.author());
            assertEquals(anotherContent, tweetFromStorage.content());
        }
    }

    private final Comparator<Tweet> byAuthorComparator = new Comparator<Tweet>() {
        @Override public int compare(Tweet left, Tweet right) {
           return left.author().compareTo(right.author());
        }
    };
}