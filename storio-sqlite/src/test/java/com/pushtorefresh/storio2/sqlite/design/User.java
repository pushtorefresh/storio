package com.pushtorefresh.storio2.sqlite.design;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Test class that represents an object stored in Db
 */
class User {

    private final String email;
    private Long id;

    User(@Nullable Long id, @NonNull String email) {
        this.id = id;
        this.email = email;
    }

    @NonNull
    String email() {
        return email;
    }
}
