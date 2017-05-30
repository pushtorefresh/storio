package com.pushtorefresh.storio2.sqlite.impl;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.pushtorefresh.storio2.sqlite.Changes;

import java.util.Set;

import io.reactivex.Flowable;
import io.reactivex.functions.Predicate;

import static com.pushtorefresh.storio2.internal.Checks.checkNotNull;

/**
 * FOR INTERNAL USAGE ONLY.
 * <p>
 * Hides RxJava from ClassLoader via separate class.
 */
public final class ChangesFilter implements Predicate<Changes> {

    @Nullable
    private final Set<String> tables;

    @Nullable
    private final Set<String> tags;

    private ChangesFilter(@Nullable Set<String> tables, @Nullable Set<String> tags) {
        this.tables = tables;
        this.tags = tags;
    }

    @NonNull
    public static Flowable<Changes> applyForTables(@NonNull Flowable<Changes> changes, @NonNull Set<String> tables) {
        checkNotNull(tables, "Set of tables can not be null");
        return changes
                .filter(new ChangesFilter(tables, null));
    }

    @NonNull
    public static Flowable<Changes> applyForTags(@NonNull Flowable<Changes> changes, @NonNull Set<String> tags) {
        checkNotNull(tags, "Set of tags can not be null");
        return changes
                .filter(new ChangesFilter(null, tags));
    }

    @NonNull
    public static Flowable<Changes> applyForTablesAndTags(
            @NonNull Flowable<Changes> changes,
            @NonNull Set<String> tables,
            @NonNull Set<String> tags
    ) {
        checkNotNull(tables, "Set of tables can not be null");
        checkNotNull(tags, "Set of tags can not be null");
        return changes
                .filter(new ChangesFilter(tables, tags));
    }

    @Override
    public boolean test(@io.reactivex.annotations.NonNull Changes changes) throws Exception {
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
