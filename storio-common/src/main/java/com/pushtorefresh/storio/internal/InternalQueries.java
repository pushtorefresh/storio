package com.pushtorefresh.storio.internal;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import static java.util.Collections.emptyList;
import static java.util.Collections.emptySet;
import static java.util.Collections.unmodifiableList;

/**
 * Util methods for queries.
 * <p>
 * For internal usage only!
 */
public final class InternalQueries {

    private InternalQueries() {
        throw new IllegalStateException("No instances please");
    }

    /**
     * Converts array of objects to {@code List<String>}.
     *
     * @param args array objects that will be converted to list of strings.
     * @return not-null, unmodifiable list of strings.
     */
    @NonNull
    public static List<String> unmodifiableNonNullListOfStrings(@Nullable Object[] args) {
        if (args == null || args.length == 0) {
            return emptyList();
        } else {
            final List<String> list = new ArrayList<String>(args.length);

            //noinspection ForLoopReplaceableByForEach -> on Android it's faster
            for (int i = 0; i < args.length; i++) {
                final Object arg = args[i];
                list.add(arg != null ? arg.toString() : "null");
            }

            return unmodifiableList(list);
        }
    }

    /**
     * Coverts list of objects to {@code List<String>}.
     *
     * @param args list of objects that will be converted to list of strings.
     * @return not-null, unmodifiable list of strings.
     */
    @NonNull
    public static List<String> unmodifiableNonNullListOfStrings(@Nullable List<?> args) {
        if (args == null || args.isEmpty()) {
            return emptyList();
        } else {
            final List<String> list = new ArrayList<String>(args.size());

            for (Object arg : args) {
                list.add(arg != null ? arg.toString() : "null");
            }

            return unmodifiableList(list);
        }
    }

    /**
     * Converts list of something to unmodifiable not-null list.
     *
     * @param list list to convert, can be {@code null}.
     * @return not-null, unmodifiable list of something.
     */
    @SuppressWarnings("unchecked")
    @NonNull
    public static <T> List<T> unmodifiableNonNullList(@Nullable List<T> list) {
        return list == null || list.isEmpty()
                ? (List<T>) emptyList()
                : unmodifiableList(list);
    }

    /**
     * Converts set of something to unmodifiable not-null set.
     *
     * @param set set to convert, can be {@code null}.
     * @return not-null, unmodifiable set of something.
     */
    @SuppressWarnings("unchecked")
    @NonNull
    public static <T> Set<T> unmodifiableNonNullSet(@Nullable Set<T> set) {
        return set == null || set.isEmpty()
                ? (Set<T>) emptySet()
                : Collections.unmodifiableSet(set);
    }

    /**
     * Converts list of strings to not-null array of strings.
     *
     * @param list of strings.
     * @return not-null array of strings.
     */
    @NonNull
    public static String[] nonNullArrayOfStrings(@Nullable List<String> list) {
        return list == null || list.isEmpty()
                ? new String[]{}
                : list.toArray(new String[list.size()]);
    }

    /**
     * Converts list of strings to nullable array of strings.
     *
     * @param list list of strings.
     * @return nullable array of strings.
     */
    @Nullable
    public static String[] nullableArrayOfStrings(@Nullable List<String> list) {
        return list == null || list.isEmpty()
                ? null
                : list.toArray(new String[list.size()]);
    }

    /**
     * Converts nullable string to not-null empty string if string was null
     * and returns string as is otherwise.
     *
     * @param str string to convert, can be null.
     * @return not-null string, can be empty.
     */
    @NonNull
    public static String nonNullString(@Nullable String str) {
        return str == null ? "" : str;
    }

    /**
     * Coverts nullable string to nullable string and if string was null or empty
     * and returns string as is otherwise.
     *
     * @param str string to convert, can be null.
     * @return nullable string, can not be empty.
     */
    @Nullable
    public static String nullableString(@Nullable String str) {
        return str == null || str.isEmpty() ? null : str;
    }
}
