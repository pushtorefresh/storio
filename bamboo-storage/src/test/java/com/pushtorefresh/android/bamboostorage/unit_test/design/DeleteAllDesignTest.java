package com.pushtorefresh.android.bamboostorage.unit_test.design;

import android.support.annotation.NonNull;

import com.pushtorefresh.android.bamboostorage.BambooStorage;
import com.pushtorefresh.android.bamboostorage.result.SingleDeleteResult;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.Stack;

public class DeleteAllDesignTest {

    @NonNull private BambooStorage getBambooStorage() {
        return new BambooStorageForDesignTest();
    }

    @Test public void deleteAllList() {
        List<User> users = new ArrayList<>();

        Map<User, SingleDeleteResult<User>> deleteResults = getBambooStorage()
                .forType(User.class)
                .deleteAll(users)
                .getDeleteResults();
    }

    @Test public void deleteAllSet() {
        Set<User> users = new HashSet<>();

        Map<User, SingleDeleteResult<User>> deleteResults = getBambooStorage()
                .forType(User.class)
                .deleteAll(users)
                .getDeleteResults();
    }

    @Test public void deleteAllQueue() {
        Queue<User> users = new LinkedList<>();

        Map<User, SingleDeleteResult<User>> deleteResults = getBambooStorage()
                .forType(User.class)
                .deleteAll(users)
                .getDeleteResults();
    }

    @Test public void deleteAllCollection() {
        Collection<User> users = new Stack<>();

        Map<User, SingleDeleteResult<User>> deleteResults = getBambooStorage()
                .forType(User.class)
                .deleteAll(users)
                .getDeleteResults();
    }
}
