package com.pushtorefresh.bamboostorage;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.Collection;

/**
 * @author Artem Zinnatullin [artem.zinnatullin@gmail.com]
 */
public abstract class ABambooStorageListener {

    /**
     * Called when add to BambooStorage happened
     *
     * @param storableItem added item
     */
    public void onAdd(@NonNull IBambooStorableItem storableItem) { }

    /**
     * Called when update of storable item happened in BambooStorage
     *
     * @param storableItem updated item
     * @param count        count of updated entries
     */
    public void onUpdate(@NonNull IBambooStorableItem storableItem, int count) { }

    /**
     * Called when addAll to BambooStorage happened
     *
     * @param storableItems collection of items
     */
    public void onAddAll(@NonNull Collection<? extends IBambooStorableItem> storableItems) { }

    /**
     * Called when addOrUpdateAll to BambooStorage happened
     *
     * @param storableItems collection of items
     */
    public void onAddOrUpdateAll(@NonNull Collection<? extends IBambooStorableItem> storableItems) { }

    /**
     * Called when remove of storable item happened in BambooStorage
     *
     * @param storableItem removed item
     * @param count        count of removed entries
     */
    public void onRemove(@NonNull IBambooStorableItem storableItem, int count) { }

    /**
     * Called when remove happened in BambooStorage
     *
     * @param classOfStorableItems class of removed storable items
     * @param where                where clause
     * @param whereArgs            args for binding to where clause, same format as for ContentResolver
     * @param count                count of removed entries
     */
    public void onRemove(@NonNull Class<? extends IBambooStorableItem> classOfStorableItems, @Nullable String where, @Nullable String[] whereArgs, int count) { }

    /**
     * Called when remove all of type happened in BambooStorage
     *
     * @param classOfStorableItems class of removed storable items
     * @param count                count of removed entries
     */
    public void onRemoveAllOfType(@NonNull Class<? extends IBambooStorableItem> classOfStorableItems, int count) { }

    /**
     * Called on when any CRUD operation happened in BambooStorage
     *
     * @param classOfStorableItems class of removed storable items
     */
    public void onAnyCRUDOperation(@NonNull Class<? extends IBambooStorableItem> classOfStorableItems) { }
}
