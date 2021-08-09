package com.pushtorefresh.storio3.basic_sample;

import android.os.Bundle;
import android.widget.Toast;

import com.pushtorefresh.storio3.StorIOException;
import com.pushtorefresh.storio3.sqlite.StorIOSQLite;
import com.pushtorefresh.storio3.sqlite.impl.DefaultStorIOSQLite;
import com.pushtorefresh.storio3.sqlite.operations.put.PutResults;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.sqlite.db.SupportSQLiteOpenHelper;
import androidx.sqlite.db.framework.FrameworkSQLiteOpenHelperFactory;


public class MainActivity extends AppCompatActivity {

    private StorIOSQLite storIOSQLite;

    private RecyclerView recyclerView;
    private TweetsAdapter tweetsAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SupportSQLiteOpenHelper.Configuration configuration = SupportSQLiteOpenHelper.Configuration
                .builder(this)
                .name(DbOpenCallback.DB_NAME)
                .callback(new DbOpenCallback())
                .build();

        storIOSQLite = DefaultStorIOSQLite.builder()
            .sqliteOpenHelper(new FrameworkSQLiteOpenHelperFactory().create(configuration))
            .addTypeMapping(Tweet.class, new TweetSQLiteTypeMapping())
            .build();

        tweetsAdapter = new TweetsAdapter();
        recyclerView = (RecyclerView) findViewById(R.id.tweets_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(tweetsAdapter);

        addTweets();
    }

    void addTweets() {
        final List<Tweet> tweets = new ArrayList<Tweet>();

        tweets.add(Tweet.newTweet("artem_zin", "Checkout StorIO — modern API for SQLiteDatabase & ContentResolver"));
        tweets.add(Tweet.newTweet("HackerNews", "It's revolution! Dolphins can write news on HackerNews with our new app!"));
        tweets.add(Tweet.newTweet("AndroidDevReddit", "Awesome library — StorIO"));
        tweets.add(Tweet.newTweet("Facebook", "Facebook community in Twitter is more popular than Facebook community in Facebook and Instagram!"));
        tweets.add(Tweet.newTweet("Google", "Android be together not the same: AOSP, AOSP + Google Apps, Samsung Android"));
        tweets.add(Tweet.newTweet("Reddit", "Now we can send funny gifs directly into your brain via Oculus Rift app!"));
        tweets.add(Tweet.newTweet("ElonMusk", "Tesla Model S OTA update with Android Auto 7.2, fixes for memory leaks"));
        tweets.add(Tweet.newTweet("AndroidWeekly", "Special issue #1: StorIO — forget about SQLiteDatabase, ContentResolver APIs, ORMs suck!"));
        tweets.add(Tweet.newTweet("Apple", "Yosemite update: fixes for Wifi issues, yosemite-wifi-patch#142"));

        // Looks/reads nice, isn't it?
        try {
            PutResults<Tweet> results = storIOSQLite
                .put()
                .objects(tweets)
                .prepare()
                .executeAsBlocking();

            int count = results.results().size();
            Toast.makeText(this, getResources().getQuantityString(R.plurals.tweets_inserted, count, count),
                    Toast.LENGTH_SHORT).show();

            List<Tweet> receivedTweets = storIOSQLite
                .get()
                .listOfObjects(Tweet.class)
                .withQuery(TweetQueries.QUERY_ALL)
                .prepare()
                .executeAsBlocking();

            int rcvCount = receivedTweets.size();
            Toast.makeText(this, getResources().getQuantityString(R.plurals.tweets_loaded, rcvCount, rcvCount),
                    Toast.LENGTH_SHORT).show();

            tweetsAdapter.setTweets(receivedTweets);
        } catch (StorIOException e) {
            Toast.makeText(this, R.string.tweets_add_error_toast, Toast.LENGTH_LONG).show();
        }
    }
}
