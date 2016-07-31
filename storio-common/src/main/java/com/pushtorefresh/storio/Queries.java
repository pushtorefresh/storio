package com.pushtorefresh.storio;

import android.support.annotation.NonNull;

/**
 * Public collection of util methods for Queries.
 */
public class Queries {

    private Queries() {
        // No instances.
    }

    /**
     * Generates required number of placeholders as string.
     *
     * Example: {@code numberOfPlaceholders == 1, result == "?"},
     * {@code numberOfPlaceholders == 2, result == "?,?"}.
     *
     * @param numberOfPlaceholders required amount of placeholders, should be {@code > 0}.
     * @return string with placeholders.
     */
    @NonNull
    public static String placeholders(final int numberOfPlaceholders) {
        if (numberOfPlaceholders == 1) {
            return "?"; // fffast
        } else if (numberOfPlaceholders == 0) {
            return "";
        } else if (numberOfPlaceholders < 0) {
            throw new IllegalArgumentException("numberOfPlaceholders must be >= 0, but was = " + numberOfPlaceholders);
        }

        final StringBuilder stringBuilder = new StringBuilder((numberOfPlaceholders * 2) - 1);

        for (int i = 0; i < numberOfPlaceholders; i++) {
            stringBuilder.append('?');

            if (i != numberOfPlaceholders - 1) {
                stringBuilder.append(',');
            }
        }

        return stringBuilder.toString();
    }
}
