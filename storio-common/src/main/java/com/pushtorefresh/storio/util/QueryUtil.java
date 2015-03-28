package com.pushtorefresh.storio.util;

import android.support.annotation.Nullable;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Util methods for queries
 */
public class QueryUtil {

    private QueryUtil() {
    }

    /**
     * Converts varargs of String to List<String>
     *
     * @param args varargs strings
     * @return null if varargs array is null or empty or list of items from varargs
     */
    @Nullable public static List<String> varargsToList(@Nullable String... args) {
        return args == null || args.length == 0
                ? null
                : Arrays.asList(args);
    }

    /**
     * Converts list of something to unmodifiable list
     *
     * @param list list to convert
     * @param <T>  type of items
     * @return null if list is null or empty or unmodifiable list of items
     */
    @Nullable public static <T> List<T> listToUnmodifiable(@Nullable List<T> list) {
        return list == null || list.isEmpty()
                ? null
                : Collections.unmodifiableList(list);
    }

    /**
     * Converts list of strings to array of strings
     * @param list of strings
     * @return null if list is null or empty or array of strings from list
     */
    @SuppressWarnings("unchecked") @Nullable public static String[] listToArray(@Nullable List<String> list) {
        return list == null || list.isEmpty()
                ? null
                : list.toArray(new String[list.size()]);
    }
}
