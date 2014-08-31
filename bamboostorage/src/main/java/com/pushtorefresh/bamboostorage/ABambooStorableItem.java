package com.pushtorefresh.bamboostorage;

/**
 * Base class for Bamboo storable type if you want to use inheritance instead of implementing IBambooStorableItem
 * @author Artem Zinnatullin [artem.zinnatullin@gmail.com]
 */
public abstract class ABambooStorableItem implements IBambooStorableItem {

    private long mInternalItemId = DEFAULT_INTERNAL_ITEM_ID;

    @Override public final long get_id() {
        return mInternalItemId;
    }

    @Override public final void set_id(long _id) {
        mInternalItemId = _id;
    }
}
