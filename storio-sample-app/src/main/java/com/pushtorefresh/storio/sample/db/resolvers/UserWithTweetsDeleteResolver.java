package com.pushtorefresh.storio.sample.db.resolvers;

import android.support.annotation.NonNull;

import com.pushtorefresh.storio.sample.db.entities.UserWithTweets;
import com.pushtorefresh.storio.sample.db.tables.TweetsTable;
import com.pushtorefresh.storio.sample.db.tables.UsersTable;
import com.pushtorefresh.storio.sqlite.StorIOSQLite;
import com.pushtorefresh.storio.sqlite.operations.delete.DeleteResolver;
import com.pushtorefresh.storio.sqlite.operations.delete.DeleteResult;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class UserWithTweetsDeleteResolver extends DeleteResolver<UserWithTweets> {

    @NonNull
    @Override
    public DeleteResult performDelete(@NonNull StorIOSQLite storIOSQLite, @NonNull UserWithTweets userWithTweets) {
        // 1 for user and other for his/her tweets
        final List<Object> objectsToDelete = new ArrayList<Object>(1 + userWithTweets.tweets().size());

        objectsToDelete.add(userWithTweets.user());
        objectsToDelete.addAll(userWithTweets.tweets());

        storIOSQLite
                .delete()
                .objects(objectsToDelete)
                .prepare()
                .executeAsBlocking();

        // BTW, you can save it as static final
        final Set<String> affectedTables = new HashSet<String>(2);

        affectedTables.add(UsersTable.TABLE);
        affectedTables.add(TweetsTable.TABLE);

        return DeleteResult.newInstance(objectsToDelete.size(), affectedTables);
    }
}
