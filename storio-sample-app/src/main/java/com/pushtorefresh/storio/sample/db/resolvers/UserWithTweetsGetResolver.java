package com.pushtorefresh.storio.sample.db.resolvers;

import android.database.Cursor;
import android.support.annotation.NonNull;

import com.pushtorefresh.storio.sample.db.entities.Tweet;
import com.pushtorefresh.storio.sample.db.entities.User;
import com.pushtorefresh.storio.sample.db.entities.UserWithTweets;
import com.pushtorefresh.storio.sample.db.tables.TweetsTable;
import com.pushtorefresh.storio.sqlite.StorIOSQLite;
import com.pushtorefresh.storio.sqlite.operations.get.GetResolver;
import com.pushtorefresh.storio.sqlite.queries.Query;
import com.pushtorefresh.storio.sqlite.queries.RawQuery;

import java.util.List;

public final class UserWithTweetsGetResolver extends GetResolver<UserWithTweets> {

    // We can even reuse existing get resolvers for our needs
    // But, you can always write custom code, of course.
    @NonNull
    private final GetResolver<User> userGetResolver;

    // Sorry for this hack :(
    // We will pass you an instance of StorIO
    // into the mapFromCursor() in v2.0.0.
    //
    // At the moment, you can save this instance in performGet() and then null it at the end
    @NonNull
    private final ThreadLocal<StorIOSQLite> storIOSQLiteFromPerformGet = new ThreadLocal<StorIOSQLite>();

    public UserWithTweetsGetResolver(@NonNull GetResolver<User> userGetResolver) {
        this.userGetResolver = userGetResolver;
    }

    @NonNull
    @Override
    public UserWithTweets mapFromCursor(@NonNull Cursor cursor) {
        final StorIOSQLite storIOSQLite = storIOSQLiteFromPerformGet.get();

        // Or you can manually parse cursor (it will be sliiightly faster)
        final User user = userGetResolver.mapFromCursor(cursor);

        // Yep, you can reuse StorIO here!
        // Or, you can do manual low level requests here
        // BTW, if you profiled your app and found that such queries are not very fast
        // You can always add some optimized version for particular queries to improve the performance
        final List<Tweet> tweetsOfTheUser = storIOSQLite
                .get()
                .listOfObjects(Tweet.class)
                .withQuery(Query.builder()
                        .table(TweetsTable.TABLE)
                        .where(TweetsTable.COLUMN_AUTHOR + "=?")
                        .whereArgs(user.nick())
                        .build())
                .prepare()
                .executeAsBlocking();

        return new UserWithTweets(user, tweetsOfTheUser);
    }

    @NonNull
    @Override
    public Cursor performGet(@NonNull StorIOSQLite storIOSQLite, @NonNull RawQuery rawQuery) {
        storIOSQLiteFromPerformGet.set(storIOSQLite);
        return storIOSQLite.lowLevel().rawQuery(rawQuery);
    }

    @NonNull
    @Override
    public Cursor performGet(@NonNull StorIOSQLite storIOSQLite, @NonNull Query query) {
        storIOSQLiteFromPerformGet.set(storIOSQLite);
        return storIOSQLite.lowLevel().query(query);
    }
}
