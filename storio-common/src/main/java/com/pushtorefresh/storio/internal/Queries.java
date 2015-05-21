package com.pushtorefresh.storio.internal;

import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * Util methods for queries.
 * <p/>
 * For internal usage only!
 */
public final class Queries {

    private Queries() {
        throw new IllegalStateException("No instances please");
    }

    /**
     * Converts varargs of String to {@code List<String>}.
     *
     * @param args varargs objects that will be converted to list of strings.
     * @return {@code null} if varargs array is {@code null}
     * or empty or list of items from varargs.
     */
    @Nullable
    public static List<String> varargsToList(@Nullable Object[] args) {
        if (args == null || args.length == 0) {
            return null;
        } else {
            final List<String> list = new ArrayList<String>(args.length);

            //noinspection ForLoopReplaceableByForEach -> on Android it's faster
            for (int i = 0; i < args.length; i++) {
                final Object arg = args[i];
                list.add(arg != null ? arg.toString() : "null");
            }

            return list;
        }
    }

    /**
     * Converts list of something to unmodifiable nullable list.
     *
     * @param list list to convert, can be {@code null}.
     * @param <T>  type of items.
     * @return {@code null} if list is {@code null} or empty OR unmodifiable list of items.
     */
    @Nullable
    public static <T> List<T> unmodifiableNullableList(@Nullable List<T> list) {
        return list == null || list.isEmpty()
                ? null
                : Collections.unmodifiableList(list);
    }

    /**
     * Converts set of something to unmodifiable nullable set.
     * @param set set to convert, can be {@code null}.
     * @param <T> type of items.
     * @return {@code null} if set is {@code null} or empty OR unmodifiable set of items.
     */
    @Nullable
    public static <T> Set<T> unmodifiableNullableSet(@Nullable Set<T> set) {
        return set == null || set.isEmpty()
                ? null
                : Collections.unmodifiableSet(set);
    }

    /**
     * Converts list of strings to array of strings.
     *
     * @param list of strings.
     * @return {@code null} if list is {@code null} or empty or array of strings from list.
     */
    @SuppressWarnings("unchecked")
    @Nullable
    public static String[] listToArray(@Nullable List<String> list) {
        return list == null || list.isEmpty()
                ? null
                : list.toArray(new String[list.size()]);
    }


}
