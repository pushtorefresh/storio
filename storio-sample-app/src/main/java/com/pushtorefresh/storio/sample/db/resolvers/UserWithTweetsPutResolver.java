package com.pushtorefresh.storio.sample.db.resolvers;

import android.support.annotation.NonNull;

import com.pushtorefresh.storio.sample.db.entities.UserWithTweets;
import com.pushtorefresh.storio.sample.db.tables.TweetsTable;
import com.pushtorefresh.storio.sample.db.tables.UsersTable;
import com.pushtorefresh.storio.sqlite.StorIOSQLite;
import com.pushtorefresh.storio.sqlite.operations.put.PutResolver;
import com.pushtorefresh.storio.sqlite.operations.put.PutResult;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class UserWithTweetsPutResolver extends PutResolver<UserWithTweets> {

    @NonNull
    @Override
    public PutResult performPut(@NonNull StorIOSQLite storIOSQLite, @NonNull UserWithTweets userWithTweets) {
        // 1 for user and other for his/her tweets
        final List<Object> objectsToPut = new ArrayList<Object>(1 + userWithTweets.tweets().size());

        objectsToPut.add(userWithTweets.user());
        objectsToPut.addAll(userWithTweets.tweets());

        storIOSQLite
                .put()
                .objects(objectsToPut)
                .prepare()
                .executeAsBlocking();

        // BTW, you can save it as static final
        final Set<String> affectedTables = new HashSet<String>(2);

        affectedTables.add(UsersTable.TABLE);
        affectedTables.add(TweetsTable.TABLE);

        // Actually, we don't know what to return for such complex operation, so let's return update result
        return PutResult.newUpdateResult(objectsToPut.size(), affectedTables);
    }
}
