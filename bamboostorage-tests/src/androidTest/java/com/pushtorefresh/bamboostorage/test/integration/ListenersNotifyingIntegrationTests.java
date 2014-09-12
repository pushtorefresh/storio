package com.pushtorefresh.bamboostorage.test.integration;

import android.support.annotation.NonNull;
import android.test.AndroidTestCase;

import com.pushtorefresh.bamboostorage.BambooStorage;
import com.pushtorefresh.bamboostorage.IBambooStorableItem;
import com.pushtorefresh.bamboostorage.IBambooStorageListener;
import com.pushtorefresh.bamboostorage.test.app.TestStorableItem;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author Artem Zinnatullin [artem.zinnatullin@gmail.com]
 */
public class ListenersNotifyingIntegrationTests extends AndroidTestCase {

    private static class StubListener implements IBambooStorageListener {

        private AtomicBoolean mAddCalled    = new AtomicBoolean(false);
        private AtomicBoolean mUpdateCalled = new AtomicBoolean(false);
        private AtomicBoolean mRemoveCalled = new AtomicBoolean(false);
        private AtomicBoolean mRemoveAllOfTypeCalled = new AtomicBoolean(false);
        private AtomicBoolean mAnyCRUDOperationCalled = new AtomicBoolean(false);

        @Override public void onAdd(@NonNull IBambooStorableItem storableItem) {
            mAddCalled.set(true);
        }

        @Override public void onUpdate(@NonNull IBambooStorableItem storableItem, int count) {
            mUpdateCalled.set(true);
        }

        @Override public void onRemove(@NonNull IBambooStorableItem storableItem, int count) {
            mRemoveCalled.set(true);
        }

        @Override
        public void onRemove(@NonNull Class<? extends IBambooStorableItem> classOfStorableItems, String where, String[] whereArgs, int count) {
            mRemoveCalled.set(true);
        }

        @Override
        public void onRemoveAllOfType(@NonNull Class<? extends IBambooStorableItem> classOfStorableItems, int count) {
            mRemoveAllOfTypeCalled.set(true);
        }

        @Override
        public void onAnyCRUDOperation(@NonNull Class<? extends IBambooStorableItem> classOfStorableItems) {
            mAnyCRUDOperationCalled.set(true);
        }

        // of course, any CRUD operations listener should be called

        public void assertOnlyAddCalled() {
            assertTrue(mAddCalled.get());
            assertFalse(mUpdateCalled.get());
            assertFalse(mRemoveCalled.get());
            assertFalse(mRemoveAllOfTypeCalled.get());
            assertTrue(mAnyCRUDOperationCalled.get());
        }

        public void assertOnlyUpdateCalled() {
            assertFalse(mAddCalled.get());
            assertTrue(mUpdateCalled.get());
            assertFalse(mRemoveCalled.get());
            assertFalse(mRemoveAllOfTypeCalled.get());
            assertTrue(mAnyCRUDOperationCalled.get());
        }

        public void assertOnlyRemoveCalled() {
            assertFalse(mAddCalled.get());
            assertFalse(mUpdateCalled.get());
            assertTrue(mRemoveCalled.get());
            assertFalse(mRemoveAllOfTypeCalled.get());
            assertTrue(mAnyCRUDOperationCalled.get());
        }

        public void assertOnlyRemoveAllOfTypeCalled() {
            assertFalse(mAddCalled.get());
            assertFalse(mUpdateCalled.get());
            assertFalse(mRemoveCalled.get());
            assertTrue(mRemoveAllOfTypeCalled.get());
            assertTrue(mAnyCRUDOperationCalled.get());
        }
    }

    private BambooStorage mBambooStorage;

    @Override protected void setUp() throws Exception {
        super.setUp();

        if (mBambooStorage == null) {
            mBambooStorage = new BambooStorage(getContext(), "com.pushtorefresh.bamboostorage.test");
        }

        removeAllTestStorableItems();
    }

    private void removeAllTestStorableItems() {
        mBambooStorage.removeAllOfType(TestStorableItem.class);
        assertEquals(0, mBambooStorage.countOfItems(TestStorableItem.class));
    }

    private static void shouldBeTrueInFuture(AtomicBoolean atomicBoolean) {
        final long startTime = System.currentTimeMillis();

        while (System.currentTimeMillis() - startTime < 300) {
            if (atomicBoolean.get()) {
                return;
            }
        }

        fail("Should be true in future failed by timeout");
    }

    public void testNotifyAboutAdd() {
        StubListener addListener = new StubListener();

        mBambooStorage.addListener(addListener);

        TestStorableItem storableItem = TestStorableItemFactory.newRandomItem();

        mBambooStorage.add(storableItem);

        shouldBeTrueInFuture(addListener.mAddCalled);
        addListener.assertOnlyAddCalled();

        mBambooStorage.removeListener(addListener);
    }

    public void testNotifyAboutUpdate() {
        TestStorableItem storableItem = TestStorableItemFactory.newRandomItem();
        mBambooStorage.add(storableItem);

        StubListener updateListener = new StubListener();
        mBambooStorage.addListener(updateListener);

        mBambooStorage.update(storableItem);

        shouldBeTrueInFuture(updateListener.mUpdateCalled);
        updateListener.assertOnlyUpdateCalled();

        mBambooStorage.removeListener(updateListener);
    }

    public void testNotifyAboutAddOrUpdateAdd() {
        TestStorableItem storableItem = TestStorableItemFactory.newRandomItem();

        StubListener addListener = new StubListener();
        mBambooStorage.addListener(addListener);

        // no item to update, so add listener should be called
        mBambooStorage.addOrUpdate(storableItem);

        shouldBeTrueInFuture(addListener.mAddCalled);
        addListener.assertOnlyAddCalled();

        mBambooStorage.removeListener(addListener);
    }

    public void testNotifyAboutAddOrUpdateUpdate() {
        TestStorableItem storableItem = TestStorableItemFactory.newRandomItem();

        // no item to update, so add listener should be called
        mBambooStorage.add(storableItem);

        StubListener updateListener = new StubListener();
        mBambooStorage.addListener(updateListener);

        mBambooStorage.addOrUpdate(storableItem);

        shouldBeTrueInFuture(updateListener.mUpdateCalled);
        updateListener.assertOnlyUpdateCalled();

        mBambooStorage.removeListener(updateListener);
    }

    public void testNotifyAboutRemove() {
        TestStorableItem storableItem = TestStorableItemFactory.newRandomItem();
        mBambooStorage.add(storableItem);

        StubListener removeListener = new StubListener();
        mBambooStorage.addListener(removeListener);

        mBambooStorage.remove(storableItem);
        shouldBeTrueInFuture(removeListener.mRemoveCalled);
        removeListener.assertOnlyRemoveCalled();

        mBambooStorage.removeListener(removeListener);
    }
}
