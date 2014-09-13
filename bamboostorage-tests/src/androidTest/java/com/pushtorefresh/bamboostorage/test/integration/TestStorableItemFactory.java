package com.pushtorefresh.bamboostorage.test.integration;

import android.support.annotation.NonNull;

import com.pushtorefresh.bamboostorage.test.app.TestStorableItem;

import java.util.Random;

/**
 * @author Artem Zinnatullin [artem.zinnatullin@gmail.com]
 */
public class TestStorableItemFactory {

    private static Random sRandom = new Random();

    @NonNull public static TestStorableItem newRandomItem() {
        return new TestStorableItem()
                .setTestStringField("test string " + sRandom.nextLong())
                .setTestIntField(sRandom.nextInt())
                .setTestLongField(sRandom.nextLong());
    }
}
