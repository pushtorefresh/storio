package com.pushtorefresh.bamboostorage;

import android.support.annotation.NonNull;

/**
 * @author Artem Zinnatullin [artem.zinnatullin@gmail.com]
 */
public interface IBambooStorageListener {

    /**
     * Called when add to BambooStorage happened
     * @param storableItem added item
     */
    void onAdd(@NonNull IBambooStorableItem storableItem);

    /**
     * Called when update of storable item happened in BambooStorage
     * @param storableItem updated item
     * @param count count of updated entries
     */
    void onUpdate(@NonNull IBambooStorableItem storableItem, int count);

    /**
     * Called when remove of storable item happened in BambooStorage
     * @param storableItem removed item
     * @param count count of removed entries
     */
    void onRemove(@NonNull IBambooStorableItem storableItem, int count);

    /**
     * Called when remove happened in BambooStorage
     * @param classOfStorableItems class of removed storable items
     * @param where where clause
     * @param whereArgs args for binding to where clause, same format as for ContentResolver
     * @param count count of removed entries
     */
    void onRemove(@NonNull Class<? extends IBambooStorableItem> classOfStorableItems, String where, String[] whereArgs, int count);

    /**
     * Called when remove all of type happened in BambooStorage
     * @param classOfStorableItems class of removed storable items
     * @param count count of removed entries
     */
    void onRemoveAllOfType(@NonNull Class<? extends IBambooStorableItem> classOfStorableItems, int count);

    /**
     * Called on when any CRUD operation happened in BambooStorage
     * @param classOfStorableItems class of removed storable items
     */
    void onAnyCRUDOperation(@NonNull Class<? extends IBambooStorableItem> classOfStorableItems);
}
