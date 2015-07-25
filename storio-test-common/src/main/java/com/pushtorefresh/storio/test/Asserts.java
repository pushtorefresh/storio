package com.pushtorefresh.storio.test;

import android.support.annotation.NonNull;

import java.util.List;

public final class Asserts {

    private Asserts() {
        throw new IllegalStateException("No instances please.");
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public static void assertThatListIsImmutable(@NonNull List list) {
        try {
            list.add(new Object());
            throw new AssertionError("List is not immutable: list = " + list);
        } catch (UnsupportedOperationException expected) {
            // it's okay
        }

        try {
            list.remove(0);
            throw new AssertionError("List is not immutable: list = " + list);
        } catch (UnsupportedOperationException expected) {
            // it's okay
        }

        // All modify operations failed, list is probably immutable
    }
}
