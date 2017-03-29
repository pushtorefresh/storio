package com.pushtorefresh.storio.sample.sample_code;

import com.pushtorefresh.storio.contentresolver.BuildConfig;
import com.pushtorefresh.storio.sample.SampleApp;
import com.pushtorefresh.storio.sample.db.entities.Tweet;
import com.pushtorefresh.storio.sample.db.entities.TweetWithUser;
import com.pushtorefresh.storio.sample.db.entities.User;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import java.util.List;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Java6Assertions.assertThat;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)
public class RelationsTest {

    @Test
    public void name() {
        SampleApp sampleApp = (SampleApp) RuntimeEnvironment.application;

        sampleApp.appComponent().storIOSQLite()
                .put()
                .objects(asList(
                        Tweet.newTweet(1L, "artem_zin", "test tweet 1"),
                        Tweet.newTweet(2L, "artem_zin", "test tweet 2"),
                        Tweet.newTweet(3L, "nikitin-da", "test tweet 3"),
                        User.newUser(1L, "artem_zin"),
                        User.newUser(2L, "nikitin-da"))
                )
                .prepare()
                .executeAsBlocking();

        Relations relations = new Relations(sampleApp.appComponent().storIOSQLite());
        List<TweetWithUser> tweetsWithUsers = relations.getTweetWithUser();

        assertThat(tweetsWithUsers).hasSize(3); // Same as count of tweets, not users.

        assertThat(tweetsWithUsers.get(0)).isEqualTo(new TweetWithUser(Tweet.newTweet(1L, "artem_zin", "test tweet 1"), User.newUser(1L, "artem_zin")));
        assertThat(tweetsWithUsers.get(1)).isEqualTo(new TweetWithUser(Tweet.newTweet(2L, "artem_zin", "test tweet 2"), User.newUser(1L, "artem_zin")));
        assertThat(tweetsWithUsers.get(2)).isEqualTo(new TweetWithUser(Tweet.newTweet(3L, "nikitin-da", "test tweet 3"), User.newUser(2L, "nikitin-da")));
    }
}