package com.pushtorefresh.android.bamboostorage.unit_test.design;

import android.support.annotation.NonNull;

import com.pushtorefresh.android.bamboostorage.BambooStorage;
import com.pushtorefresh.android.bamboostorage.result.SinglePutResult;

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

public class PutAllDesignTest {

    @NonNull BambooStorage getBambooStorage() {
        return new BambooStorageForDesignTest();
    }

    @Test public void putAllList() {
        List<User> users = new ArrayList<>();

        Map<User, SinglePutResult<User>> putResults = getBambooStorage()
                .forType(User.class)
                .putAll(users)
                .getPutResults();
    }

    @Test public void putAllSet() {
        Set<User> users = new HashSet<>();

        Map<User, SinglePutResult<User>> putResults = getBambooStorage()
                .forType(User.class)
                .putAll(users)
                .getPutResults();
    }

    @Test public void putAllQueue() {
        Queue<User> users = new LinkedList<>();

        Map<User, SinglePutResult<User>> putResults = getBambooStorage()
                .forType(User.class)
                .putAll(users)
                .getPutResults();
    }

    @Test public void putAllCollection() {
        Collection<User> users = new Stack<>();

        Map<User, SinglePutResult<User>> putResults = getBambooStorage()
                .forType(User.class)
                .putAll(users)
                .getPutResults();
    }
}
