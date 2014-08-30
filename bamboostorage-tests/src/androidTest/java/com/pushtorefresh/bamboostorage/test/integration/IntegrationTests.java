package com.pushtorefresh.bamboostorage.test.integration;

import android.test.AndroidTestCase;

import com.pushtorefresh.bamboostorage.BambooStorage;
import com.pushtorefresh.bamboostorage.test.app.TestStorableItem;

import java.util.Random;

/**
 * @author Artem Zinnatullin [artem.zinnatullin@gmail.com]
 */
public class IntegrationTests extends AndroidTestCase {

    private final Random mRandom = new Random(System.currentTimeMillis());

    private BambooStorage mBambooStorage;

    @Override protected void setUp() throws Exception {
        super.setUp();

        mBambooStorage = new BambooStorage(getContext(), "com.pushtorefresh.bamboostorage.test");

        // clearing storage before each test
        removeAllTestStorableItems();
    }

    private void removeAllTestStorableItems() {
        mBambooStorage.removeAllOfType(TestStorableItem.class);
        assertEquals(0, mBambooStorage.countOfItems(TestStorableItem.class));
    }

    private TestStorableItem generateRandomStorableItem() {
        return new TestStorableItem()
                .setTestStringField("test string " + mRandom.nextLong())
                .setTestIntField(mRandom.nextInt())
                .setTestLongField(mRandom.nextLong());
    }

    public void testAdd() {
        TestStorableItem storableItem = generateRandomStorableItem();

        mBambooStorage.add(storableItem);

        assertTrue(mBambooStorage.contains(storableItem));
    }

    public void testAddSameItemTwice() {
        TestStorableItem storableItem = generateRandomStorableItem();

        mBambooStorage.add(storableItem);
        mBambooStorage.add(storableItem);

        assertEquals(2, mBambooStorage.countOfItems(TestStorableItem.class));
    }

    public void testUpdate() {
        TestStorableItem storableItem = generateRandomStorableItem();

        mBambooStorage.add(storableItem);

        storableItem.setTestStringField("updated");
        storableItem.setTestIntField(mRandom.nextInt());
        storableItem.setTestLongField(mRandom.nextLong());

        int countOfUpdatedItems = mBambooStorage.update(storableItem);

        assertEquals(1, countOfUpdatedItems);
        assertEquals(1, mBambooStorage.countOfItems(storableItem.getClass()));

        TestStorableItem itemFromStorage = mBambooStorage.getByInternalId(storableItem.getClass(), storableItem.get_id());

        assertEquals(storableItem, itemFromStorage);
    }

    public void testAddOrUpdateShouldUpdate() {
        TestStorableItem storableItem = generateRandomStorableItem();

        mBambooStorage.add(storableItem);

        storableItem.setTestStringField("should update");
        storableItem.setTestIntField(mRandom.nextInt());
        storableItem.setTestLongField(mRandom.nextLong());

        boolean trueIfAddedFalseIfUpdated = mBambooStorage.addOrUpdate(storableItem);

        assertFalse(trueIfAddedFalseIfUpdated);
        assertEquals(1, mBambooStorage.countOfItems(storableItem.getClass()));

        TestStorableItem itemFromStorage = mBambooStorage.getByInternalId(storableItem.getClass(), storableItem.get_id());

        assertEquals(storableItem, itemFromStorage);
    }

    public void testAddOrUpdateShouldAdd() {
        TestStorableItem storableItem = generateRandomStorableItem();

        mBambooStorage.add(storableItem);

        storableItem.set_id(TestStorableItem.DEFAULT_INTERNAL_ID);
        storableItem.setTestStringField("should add new item");
        storableItem.setTestIntField(mRandom.nextInt());
        storableItem.setTestLongField(mRandom.nextLong());

        boolean trueIfAddedFalseIfUpdated = mBambooStorage.addOrUpdate(storableItem);

        assertTrue(trueIfAddedFalseIfUpdated);
        assertEquals(2, mBambooStorage.countOfItems(storableItem.getClass()));

        assertEquals(storableItem, mBambooStorage.getLast(storableItem.getClass()));
    }

    public void testGetByInternalIdPositive() {
        TestStorableItem storableItem = generateRandomStorableItem();

        mBambooStorage.add(storableItem);

        TestStorableItem storedItem = mBambooStorage.getByInternalId(storableItem.getClass(), storableItem.get_id());

        assertTrue(storableItem != storedItem);
        assertEquals(storableItem, storedItem);
    }

    public void testGetByInternalIdNegative() {
        TestStorableItem storableItem = generateRandomStorableItem();

        mBambooStorage.add(storableItem);

        // NOTICE: internal_id + 1
        TestStorableItem storedItem = mBambooStorage.getByInternalId(storableItem.getClass(), storableItem.get_id() + 1);

        assertNull(storedItem);
    }
}
