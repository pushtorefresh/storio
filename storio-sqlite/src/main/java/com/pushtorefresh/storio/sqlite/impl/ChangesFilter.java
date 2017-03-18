package com.pushtorefresh.storio.sqlite.impl;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.pushtorefresh.storio.sqlite.Changes;

import java.util.Set;

import rx.Observable;
import rx.functions.Func1;

import static com.pushtorefresh.storio.internal.Checks.checkNotNull;

/**
 * FOR INTERNAL USAGE ONLY.
 * <p>
 * Hides RxJava from ClassLoader via separate class.
 */
public final class ChangesFilter implements Func1<Changes, Boolean> {

    @Nullable
    private final Set<String> tables;

    @Nullable
    private final Set<String> tags;

    private ChangesFilter(@Nullable Set<String> tables, @Nullable Set<String> tags) {
        this.tables = tables;
        this.tags = tags;
    }

    @NonNull
    public static Observable<Changes> applyForTables(@NonNull Observable<Changes> rxBus, @NonNull Set<String> tables) {
        checkNotNull(tables, "Set of tables can not be null");
        return rxBus
                .filter(new ChangesFilter(tables, null));
    }

    @NonNull
    public static Observable<Changes> applyForTags(@NonNull Observable<Changes> rxBus, @NonNull Set<String> tags) {
        checkNotNull(tags, "Set of tags can not be null");
        return rxBus
                .filter(new ChangesFilter(null, tags));
    }

    @NonNull
    public static Observable<Changes> applyForTablesAndTags(
            @NonNull Observable<Changes> rxBus,
            @NonNull Set<String> tables,
            @NonNull Set<String> tags
    ) {
        checkNotNull(tables, "Set of tables can not be null");
        checkNotNull(tags, "Set of tags can not be null");
        return rxBus
                .filter(new ChangesFilter(tables, tags));
    }

    @Override
    @NonNull
    public Boolean call(@NonNull Changes changes) {
        if (tables != null) {
            // if one of changed tables found in tables for subscription -> notify observer
            for (String affectedTable : changes.affectedTables()) {
                if (tables.contains(affectedTable)) {
                    return true;
                }
            }
        }
        if (tags != null) {
            // if one of changed tags found tag for subscription -> notify observer
            for (String affectedTag : changes.affectedTags()) {
                if (tags.contains(affectedTag)) {
                    return true;
                }
            }
        }

        return false;
    }
}
