package com.pushtorefresh.bamboostorage.test.integration;

import android.database.Cursor;
import android.test.AndroidTestCase;

import com.pushtorefresh.bamboostorage.BambooStorage;
import com.pushtorefresh.bamboostorage.test.app.TestStorableItem;

import java.util.List;
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

        assertNotSame(storableItem, storedItem);
        assertEquals(storableItem, storedItem);
    }

    public void testGetByInternalIdNegative() {
        TestStorableItem storableItem = generateRandomStorableItem();

        mBambooStorage.add(storableItem);

        // NOTICE: internal_id + 1
        TestStorableItem storedItem = mBambooStorage.getByInternalId(storableItem.getClass(), storableItem.get_id() + 1);

        assertNull(storedItem);
    }

    public void testGetAsListByClassOnly() {
        TestStorableItem storableItem0 = generateRandomStorableItem();
        TestStorableItem storableItem1 = generateRandomStorableItem();
        TestStorableItem storableItem2 = generateRandomStorableItem();

        mBambooStorage.add(storableItem0);
        mBambooStorage.add(storableItem1);
        mBambooStorage.add(storableItem2);

        List<TestStorableItem> list = mBambooStorage.getAsList(TestStorableItem.class);

        assertEquals(3, list.size());

        assertEquals(storableItem0, list.get(0));
        assertEquals(storableItem1, list.get(1));
        assertEquals(storableItem2, list.get(2));
    }

    public void testGetAsListWithWhereCondition() {
        TestStorableItem storableItem0 = generateRandomStorableItem();
        TestStorableItem storableItem1 = generateRandomStorableItem();
        TestStorableItem storableItem2 = generateRandomStorableItem();

        storableItem0.setTestIntField(0);
        storableItem1.setTestIntField(1);
        storableItem2.setTestIntField(2);

        mBambooStorage.add(storableItem0);
        mBambooStorage.add(storableItem1);
        mBambooStorage.add(storableItem2);

        assertEquals(3, mBambooStorage.countOfItems(TestStorableItem.class));

        List<TestStorableItem> list = mBambooStorage.getAsList(
                TestStorableItem.class,
                TestStorableItem.TableInfo.TEST_INT_FIELD + " = ?",
                new String[] { String.valueOf(storableItem0.getTestIntField()) }
        );

        assertEquals(1, list.size());

        assertEquals(storableItem0, list.get(0));
    }

    public void testGetAsCursorWithoutWhere() {
        TestStorableItem storableItem0 = generateRandomStorableItem();
        TestStorableItem storableItem1 = generateRandomStorableItem();
        TestStorableItem storableItem2 = generateRandomStorableItem();

        mBambooStorage.add(storableItem0);
        mBambooStorage.add(storableItem1);
        mBambooStorage.add(storableItem2);

        Cursor cursor = mBambooStorage.getAsCursor(TestStorableItem.class, null, null, null);

        assertNotNull(cursor);
        assertEquals(3, cursor.getCount());
        cursor.close();
    }

    public void testGetAsCursorWithWhere() {
        TestStorableItem storableItem0 = generateRandomStorableItem();
        TestStorableItem storableItem1 = generateRandomStorableItem();
        TestStorableItem storableItem2 = generateRandomStorableItem();

        storableItem0.setTestIntField(0);
        storableItem1.setTestIntField(1);
        storableItem2.setTestIntField(2);

        mBambooStorage.add(storableItem0);
        mBambooStorage.add(storableItem1);
        mBambooStorage.add(storableItem2);

        Cursor cursor = mBambooStorage.getAsCursor(
                TestStorableItem.class,
                TestStorableItem.TableInfo.TEST_INT_FIELD + " = ?",
                new String[] { String.valueOf(storableItem0.getTestIntField()) },
                null
        );

        assertNotNull(cursor);
        assertEquals(1, cursor.getCount());

        cursor.moveToFirst();

        assertEquals(storableItem0, BambooStorage.createStorableItemFromCursor(TestStorableItem.class, cursor));

        cursor.close();
    }
}
