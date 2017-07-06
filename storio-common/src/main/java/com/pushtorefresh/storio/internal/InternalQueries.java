package com.pushtorefresh.storio.internal;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static java.util.Arrays.asList;
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
     * @return non-null, unmodifiable list of strings.
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
     * @return non-null, unmodifiable list of strings.
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
     * Converts array of objects to {@code List<Object>}.
     *
     * @param args array of objects.
     * @return non-null, unmodifiable list of objects.
     */
    @NonNull
    public static List<Object> unmodifiableNonNullList(@Nullable Object[] args) {
        return args == null || args.length == 0
                ? emptyList()
                : unmodifiableList(asList(args));
    }

    /**
     * Converts list of something to unmodifiable non-null list.
     *
     * @param list list to convert, can be {@code null}.
     * @return non-null, unmodifiable list of something.
     */
    @SuppressWarnings("unchecked")
    @NonNull
    public static <T> List<T> unmodifiableNonNullList(@Nullable List<T> list) {
        return list == null || list.isEmpty()
                ? (List<T>) emptyList()
                : unmodifiableList(list);
    }

    /**
     * Converts set of something to unmodifiable non-null set.
     *
     * @param set set to convert, can be {@code null}.
     * @return non-null, unmodifiable set of something.
     */
    @SuppressWarnings("unchecked")
    @NonNull
    public static <T> Set<T> unmodifiableNonNullSet(@Nullable Set<T> set) {
        return set == null || set.isEmpty()
                ? (Set<T>) emptySet()
                : Collections.unmodifiableSet(set);
    }

    /**
     * Converts list of strings to non-null array of strings.
     *
     * @param list of strings.
     * @return non-null array of strings.
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
    public static String[] nullableArrayOfStringsFromListOfStrings(@Nullable List<String> list) {
        return list == null || list.isEmpty()
                ? null
                : list.toArray(new String[list.size()]);
    }

    /**
     * Converts list of objects to nullable array of strings.
     *
     * @param list list of objects that will be converted to array of strings.
     * @return nullable array of strings.
     */
    @Nullable
    public static String[] nullableArrayOfStrings(@Nullable List<Object> list) {
        if (list == null || list.isEmpty()) {
            return null;
        } else {
            final String[] strings = new String[list.size()];

            //noinspection ForLoopReplaceableByForEach -> on Android it's faster
            for (int i = 0; i < list.size(); i++) {
                final Object arg = list.get(i);
                strings[i] = arg != null ? arg.toString() : "null";
            }
            return strings;
        }
    }

    /**
     * Converts nullable string to non-null empty string if string was null
     * and returns string as is otherwise.
     *
     * @param str string to convert, can be null.
     * @return non-null string, can be empty.
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

    /**
     * Converts collection of something to non-null set.
     *
     * @param collection source collection to convert, can be {@code null}.
     * @return non-null, set of something.
     */
    @SuppressWarnings("unchecked")
    @NonNull
    public static Set<String> nonNullSet(@Nullable Collection<String> collection) {
        return collection == null || collection.isEmpty()
                ? Collections.<String>emptySet()
                : new HashSet<String>(collection);
    }

    /**
     * Converts array of something to non-null set.
     *
     * @param items source items to convert, can be {@code null}.
     * @return non-null, set of something.
     */
    @SuppressWarnings("unchecked")
    @NonNull
    public static Set<String> nonNullSet(@Nullable String[] items) {
        return items == null || items.length == 0
                ? Collections.<String>emptySet()
                : new HashSet<String>(asList(items));
    }

    /**
     * Converts array of something to non-null set.
     *
     * @param firstItem  the first required source item.
     * @param otherItems other source items to convert, can be {@code null}.
     * @return non-null, set of something.
     */
    @SuppressWarnings("unchecked")
    @NonNull
    public static Set<String> nonNullSet(@NonNull String firstItem, @Nullable String[] otherItems) {
        final HashSet<String> set = new HashSet<String>();
        set.add(firstItem);
        if (otherItems != null) {
            set.addAll(asList(otherItems));
        }
        return set;
    }
}
