package com.pushtorefresh.bamboostorage;

import android.content.ContentValues;
import android.content.res.Resources;
import android.database.Cursor;
import android.support.annotation.NonNull;

/**
 * StorableItem is an abstraction for items representation in StorageManager
 *
 * @author Artem Zinnatullin [artem.zinnatullin@gmail.com]
 */
public abstract class StorableItem {

    /**
     * Default internal id of the item, if internal item id == default, it means, that it was not stored in the storage
     */
    public static final long DEFAULT_INTERNAL_ID = 0;

    public StorableItem() {
        // DEFAULT PUBLIC CONSTRUCTOR IS REQUIRED for creating class instance in StorageManager
        // for later filling it with Cursor
    }

    /**
     * Internal item id for storage under content provider
     */
    private long _id = DEFAULT_INTERNAL_ID;

    /**
     * Returns internal item id
     * If id is <= 0, StorableItem was never saved in the storage
     * @return internal item id
     */
    public long get_id() {
        return _id;
    }

    /**
     * Sets internal item id
     * @param _id internal item id
     */
    public void set_id(final long _id) {
        this._id = _id;
    }

    /**
     * Converts this object to ContentValues
     * @param res which can be needed for toContentValues process
     * @return ContentValues representation of StorableItem
     */
    @NonNull
    public abstract ContentValues toContentValues(@NonNull Resources res);

    /**
     * Fills StorableItem's fields from cursor
     * @param cursor with item data
     */
    public abstract void _fillFromCursor(@NonNull Cursor cursor);
}
