package com.pushtorefresh.android.bamboostorage.unit_test.design;

import android.support.annotation.NonNull;

import com.pushtorefresh.android.bamboostorage.BambooStorage;
import com.pushtorefresh.android.bamboostorage.wtf.Query;
import com.pushtorefresh.android.bamboostorage.wtf.QueryBuilder;

import org.junit.Test;

public class DeleteDesignTest {

    @NonNull private BambooStorage getBambooStorage() {
        return new BambooStorageForDesignTest();
    }

    @Test public void deleteObjectDeletedCount() {
        User user = new User();

        int deletedCount = getBambooStorage()
                .forType(User.class)
                .delete(user)
                .getDeletedCount();
    }

    @Test public void deleteObjectWasDeleted() {
        User user = new User();

        boolean wasDeleted = getBambooStorage()
                .forType(User.class)
                .delete(user)
                .wasDeleted();
    }

    @Test public void deleteObjectWasNotDeleted() {
        User user = new User();

        boolean wasNotDeleted = getBambooStorage()
                .forType(User.class)
                .delete(user)
                .wasNotDeleted();
    }

    @Test public void deleteQueryDeletedCount() {
        Query query = new QueryBuilder().where("name = ?", "artem").build();

        int deletedCount = getBambooStorage()
                .forType(User.class)
                .delete(query)
                .getDeletedCount();
    }

    @Test public void deleteQueryWasDeleted() {
        Query query = new QueryBuilder().where("name = ?", "dog").build();

        boolean wasDeleted = getBambooStorage()
                .forType(User.class)
                .delete(query)
                .wasDeleted();
    }

    @Test public void deleteQueryWasNotDeleted() {
        Query query = new QueryBuilder().where("name = ?", "what").build();

        boolean wasNotDeleted = getBambooStorage()
                .forType(User.class)
                .delete(query)
                .wasNotDeleted();
    }
}
