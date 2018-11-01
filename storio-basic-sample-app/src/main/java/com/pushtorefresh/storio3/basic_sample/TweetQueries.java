package com.pushtorefresh.storio3.basic_sample;

import androidx.annotation.NonNull;

import com.pushtorefresh.storio3.sqlite.queries.Query;

// We suggest to store table meta such as table name, columns names, queries, etc in separate class
// Because it makes code of the Entity itself cleaner and easier to read/understand/support
public class TweetQueries {

    // This is just class with Meta Data, we don't need instances
    private TweetQueries() {
        throw new IllegalStateException("No instances please");
    }

    // Yep, with StorIO you can safely store queries as objects and reuse them, they are immutable
    @NonNull
    public static final Query QUERY_ALL = Query.builder()
            .table(TweetTable.NAME)
            .build();
}
