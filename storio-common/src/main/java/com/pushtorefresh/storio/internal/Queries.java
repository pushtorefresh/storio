package com.pushtorefresh.storio.internal;

import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Util methods for queries
 * <p/>
 * For internal usage only!
 */
public final class Queries {

    private Queries() {
        throw new IllegalStateException("No instances please");
    }

    /**
     * Converts varargs of String to List<String>
     *
     * @param args varargs objects that will be converted to list of strings
     * @return null if varargs array is null or empty or list of items from varargs
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
     * Converts list of something to unmodifiable list
     *
     * @param list list to convert
     * @param <T>  type of items
     * @return null if list is null or empty or unmodifiable list of items
     */
    @Nullable
    public static <T> List<T> listToUnmodifiable(@Nullable List<T> list) {
        return list == null || list.isEmpty()
                ? null
                : Collections.unmodifiableList(list);
    }

    /**
     * Converts list of strings to array of strings
     *
     * @param list of strings
     * @return null if list is null or empty or array of strings from list
     */
    @SuppressWarnings("unchecked")
    @Nullable
    public static String[] listToArray(@Nullable List<String> list) {
        return list == null || list.isEmpty()
                ? null
                : list.toArray(new String[list.size()]);
    }
}
