package com.pushtorefresh.bamboostorage;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.res.Resources;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Artem Zinnatullin [artem.zinnatullin@gmail.com]
 */
public class BambooStorage {

    /**
     * Thread-safe cache of (StorableItemClass, BambooStorableTypeMeta) pairs for better performance
     */
    private static final Map<Class<? extends IBambooStorableItem>, BambooStorableTypeMetaWithExtra> CACHE_OF_STORABLE_ITEM_TYPES_AND_THEIR_META = new ConcurrentHashMap<Class<? extends IBambooStorableItem>, BambooStorableTypeMetaWithExtra>();

    /**
     * Content path string for building uris for requests
     */
    private final String mContentPath;

    /**
     * Content resolver for work with content provider
     */
    private final ContentResolver mContentResolver;

    /**
     * Resources for StorableItem.toContentValues() calls
     */
    private final Resources mResources;

    /**
     * Creates BambooStorage object
     * @param context feel free to pass any context, no memory leaks. Required to work with ContentResolver and provide Resources to IBambooStorableItem.toContentValues(res)
     * @param contentProviderAuthority authority of your ContentProvider
     */
    public BambooStorage(@NonNull Context context, @NonNull String contentProviderAuthority) {
        mContentPath     = "content://" + contentProviderAuthority + "/%s";
        mContentResolver = context.getContentResolver();
        mResources       = context.getResources();
    }

    /**
     * Adds storableItem to the Storage
     * @param storableItem to add
     */
    public void add(@NonNull IBambooStorableItem storableItem) {
        storableItem.setInternalId(IBambooStorableItem.DEFAULT_INTERNAL_ITEM_ID);
        Uri uri = mContentResolver.insert(buildUri(storableItem.getClass()), storableItem.toContentValues(mResources));
        storableItem.setInternalId(ContentUris.parseId(uri));
    }

    /**
     * Updates storable item in the storage
     * @param storableItem to update
     * @return count of updated items
     * @throws IllegalArgumentException if storable item internal id less or equals zero — it was not stored in StorageManager
     */
    public int update(@NonNull IBambooStorableItem storableItem) {
        final long itemInternalId = storableItem.getInternalId();

        if (itemInternalId <= 0) {
            throw new IllegalArgumentException("Item: " + storableItem + " can not be updated, because its internal id is <= 0");
        } else {
            final Class<? extends IBambooStorableItem> classOfStorableItem = storableItem.getClass();
            return mContentResolver.update(
                    buildUri(classOfStorableItem),
                    storableItem.toContentValues(mResources),
                    getTypeMetaWithExtra(classOfStorableItem).whereById,
                    buildWhereArgsByInternalId(storableItem)
            );
        }
    }

    /**
     * Adds or updates storable item to/in storage
     * @param storableItem to add or update
     * @return true if item was added, false if item was updated
     */
    public boolean addOrUpdate(@NonNull IBambooStorableItem storableItem) {
        if (storableItem.getInternalId() <= 0) {
            // item was not stored in the storage
            add(storableItem);
            return true;
        } else {
            update(storableItem);
            return false;
        }
    }

    /**
     * Gets stored item by its internal id
     * @param classOfStorableItem class of storable item
     * @param itemInternalId internal id of the item you want to getByInternalId from storage
     * @param <T> generic type of StorableItem
     * @return storable item with required internal id or null if storage does not contain this item
     */
    @Nullable
    public <T extends IBambooStorableItem> T getByInternalId(@NonNull Class<T> classOfStorableItem, long itemInternalId) {
        final Cursor cursor = getAsCursor(
                classOfStorableItem,
                getTypeMetaWithExtra(classOfStorableItem).whereById,
                buildWhereArgsByInternalId(itemInternalId),
                null
        );

        try {
            if (cursor != null && cursor.moveToFirst()) {
                return createStorableItemFromCursor(classOfStorableItem, cursor);
            } else {
                return null;
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    /**
     * Gets all stored items of required type which satisfies where condition as list in memory
     * @param classOfStorableItem class of StorableItem
     * @param where where clause
     * @param whereArgs args for binding to where clause, same format as for ContentResolver
     * @param orderBy order by clause
     * @param <T> generic type of StorableItem
     * @return list of StorableItems, can be empty but not null
     */
    @NonNull
    public <T extends IBambooStorableItem> List<T> getAsList(@NonNull Class<T> classOfStorableItem, @Nullable String where, @Nullable String[] whereArgs, @Nullable String orderBy) {
        final Cursor cursor = getAsCursor(classOfStorableItem, where, whereArgs, orderBy);

        final List<T> list = new ArrayList<T>(cursor == null ? 0 : cursor.getCount());

        if (cursor == null || !cursor.moveToFirst()) {
            return list;
        }

        final String internalIdFieldName = getTypeMetaWithExtra(classOfStorableItem).typeMeta.internalIdFieldName();

        try {
            do {
                list.add(createStorableItemFromCursor(classOfStorableItem, internalIdFieldName, cursor));
            } while (cursor.moveToNext());
        } finally {
            cursor.close();
        }

        return list;
    }

    /**
     * Gets all stored items of required type which satisfies where condition as list in memory
     *
     * NOTICE: orderBy is null, so it's default for your type of storage in ContentProvider
     *
     * @param classOfStorableItem class of StorableItem
     * @param where where clause
     * @param whereArgs args for binding to where clause, same format as for ContentResolver
     * @param <T> generic type of StorableItem
     * @return list of StorableItems, can be empty but not null
     */
    public <T extends IBambooStorableItem> List<T> getAsList(@NonNull Class<T> classOfStorableItem, @Nullable String where, @Nullable String[] whereArgs) {
        return getAsList(classOfStorableItem, where, whereArgs, null);
    }

    /**
     * Gets all stored items of required type with default order as list in memory
     * @param classOfStorableItem class of StorableItem
     * @param <T> generic type of StorableItem
     * @return list of StorableItems, can be empty but not null
     */
    @NonNull
    public <T extends IBambooStorableItem> List<T> getAsList(@NonNull Class<T> classOfStorableItem) {
        return getAsList(classOfStorableItem, null, null);
    }

    /**
     * Gets all stored items of required type which satisfies where condition as cursor
     * @param classOfStorableItem class of StorableItem
     * @param where where clause
     * @param whereArgs args for binding to where clause, same format as for ContentResolver
     * @param orderBy order by clause
     * @return cursor with query result, can be null
     */
    @Nullable
    public Cursor getAsCursor(@NonNull Class<? extends IBambooStorableItem> classOfStorableItem, @Nullable String where, @Nullable String[] whereArgs, @Nullable String orderBy) {
        return mContentResolver.query(
                buildUri(classOfStorableItem),
                null,
                where,
                whereArgs,
                orderBy
        );
    }

    /**
     * Gets first item from the query result
     * @param classOfStorableItem class of StorableItem
     * @param where where clause
     * @param whereArgs args for binding to where clause, same format as for ContentResolver
     * @param orderBy order by clause
     * @param <T> generic type of StorableItem
     * @return first item in the query result or null if there are no query results
     */
    @Nullable
    public <T extends IBambooStorableItem> T getFirst(@NonNull Class<T> classOfStorableItem, @Nullable String where, @Nullable String[] whereArgs, @Nullable String orderBy) {
        final Cursor cursor = getAsCursor(classOfStorableItem, where, whereArgs, orderBy);

        try {
            if (cursor != null && cursor.moveToFirst()) {
                return createStorableItemFromCursor(classOfStorableItem, cursor);
            } else {
                return null;
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    /**
     * Gets first item of required type with default order
     * @param classOfStorableItem class of StorableItem
     * @param <T> generic type of StorableItem
     * @return first item of required type, can be null
     */
    @Nullable
    public <T extends IBambooStorableItem> T getFirst(@NonNull Class<T> classOfStorableItem) {
        return getFirst(classOfStorableItem, null, null, null);
    }

    /**
     * Gets last item from the query result
     *
     * It's pretty fast implementation based on cursor using, it's memory and speed efficiently
     *
     * @param classOfStorableItem class of StorableItem
     * @param where where clause
     * @param whereArgs args for binding to where clause, same format as for ContentResolver
     * @param orderBy order by clause
     * @param <T> generic type of StorableItem
     * @return last item in the query result or null if there are no query results
     */
    @Nullable
    public <T extends IBambooStorableItem> T getLast(@NonNull Class<T> classOfStorableItem, @Nullable String where, @Nullable String[] whereArgs, @Nullable String orderBy) {
        final Cursor cursor = getAsCursor(classOfStorableItem, where, whereArgs, orderBy);

        try {
            if (cursor != null && cursor.moveToFirst()) {

                while (true) {
                    if (!cursor.moveToNext()) {
                        break;
                    }
                }

                cursor.moveToPrevious();

                return createStorableItemFromCursor(classOfStorableItem, cursor);
            } else {
                return null;
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    /**
     * Gets last item of required type with default order
     * @param classOfStorableItem class of StorableItem
     * @param <T> generic type of StorableItem
     * @return last item of required type, can be null
     */
    @Nullable
    public <T extends IBambooStorableItem> T getLast(@NonNull Class<T> classOfStorableItem) {
        return getLast(classOfStorableItem, null, null, null);
    }

    /**
     * Removes storable item from storage
     * @param storableItem item to be removed from the storage
     * @return count of removed items, it could be 0, or 1, or greater than 1 if you have items with same internal id in the storage
     */
    public int remove(@NonNull IBambooStorableItem storableItem) {
        final Class<? extends IBambooStorableItem> classOfStorableItem = storableItem.getClass();
        return remove(
                classOfStorableItem,
                getTypeMetaWithExtra(classOfStorableItem).whereById,
                buildWhereArgsByInternalId(storableItem)
        );
    }

    /**
     * Removes all storable items of required type which matched where condition
     * @param classOfStorableItems type of storable item you want to delete
     * @param where where clause
     * @param whereArgs args for binding to where clause, same format as for ContentResolver
     * @return count of removed items
     */
    public int remove(@NonNull Class<? extends IBambooStorableItem> classOfStorableItems, String where, String[] whereArgs) {
        return mContentResolver.delete(buildUri(classOfStorableItems), where, whereArgs);
    }

    /**
     * Removes all storable items of required type
     * Same as calling remove(class, null, null)
     * @param classOfStorableItems type of storable item you want to delete
     * @return count of removed items
     */
    public int removeAllOfType(@NonNull Class<? extends IBambooStorableItem> classOfStorableItems) {
        return remove(classOfStorableItems, null, null);
    }

    /**
     * Checks "is item currently stored in storage"
     * @param storableItem to check
     * @return true if item stored in storage, false if not
     */
    public boolean contains(@NonNull IBambooStorableItem storableItem) {
        final Class<? extends IBambooStorableItem> classOfStorableItem = storableItem.getClass();
        final Cursor cursor = getAsCursor(
                classOfStorableItem,
                getTypeMetaWithExtra(classOfStorableItem).whereById,
                buildWhereArgsByInternalId(storableItem),
                null
        );

        if (cursor == null || !cursor.moveToFirst()) {
            return false;
        } else {
            cursor.close();
            return true;
        }
    }

    /**
     * Returns count of items in the storage of required type
     * @param classOfStorableItems type of storable item you want to count
     * @return count of items in the storage of required type
     */
    public int countOfItems(@NonNull Class<? extends IBambooStorableItem> classOfStorableItems) {
        Cursor cursor = getAsCursor(classOfStorableItems, null, null, null);

        try {
            return cursor != null ? cursor.getCount() : 0;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    /**
     * Notifying about change in the storage using content resolver notifyChange method
     * @param classOfStorableItem class of StorableItem
     * @param contentObserver the observer that originated the change, may be null
     * The observer that originated the change will only receive the notification if it
     * has requested to receive self-change notifications by implementing
     */
    public void notifyChange(@NonNull Class<? extends IBambooStorableItem> classOfStorableItem, @Nullable ContentObserver contentObserver) {
        mContentResolver.notifyChange(buildUri(classOfStorableItem), contentObserver);
    }

    /**
     * Notifying about change in the storage using content resolver notifyChange method
     * @param classOfStorableItem class of StorableItem
     */
    public void notifyChange(@NonNull Class<? extends IBambooStorableItem> classOfStorableItem) {
        mContentResolver.notifyChange(buildUri(classOfStorableItem), null);
    }

    /**
     * Gets type meta with some extra from cache or directly from classOfStorableItem
     * @param classOfStorableItem type of storable item to get meta info from
     * @return type meta info with some extra data
     */
    @NonNull
    private static BambooStorableTypeMetaWithExtra getTypeMetaWithExtra(@NonNull Class<? extends IBambooStorableItem> classOfStorableItem) {
        BambooStorableTypeMetaWithExtra typeMetaWithExtra = CACHE_OF_STORABLE_ITEM_TYPES_AND_THEIR_META.get(classOfStorableItem);

        // no cached value
        if (typeMetaWithExtra == null) {
            if (!classOfStorableItem.isAnnotationPresent(BambooStorableTypeMeta.class)) {
                throw new IllegalArgumentException("Class " + classOfStorableItem + " should be marked with " + BambooStorableTypeMeta.class + " annotation");
            }

            typeMetaWithExtra = new BambooStorableTypeMetaWithExtra(classOfStorableItem.getAnnotation(BambooStorableTypeMeta.class));
            CACHE_OF_STORABLE_ITEM_TYPES_AND_THEIR_META.put(classOfStorableItem, typeMetaWithExtra);
        }

        return typeMetaWithExtra;
    }

    /**
     * Builds uri for accessing content
     * @param classOfStorableItem of the content to build uri
     * @return Uri for accessing content
     */
    @NonNull
    public Uri buildUri(@NonNull Class<? extends IBambooStorableItem> classOfStorableItem) {
        BambooStorableTypeMetaWithExtra typeMetaWithExtra = getTypeMetaWithExtra(classOfStorableItem);
        return Uri.parse(String.format(mContentPath, typeMetaWithExtra.typeMeta.contentPath()));
    }

    /**
     * Builds where args with storable item id for common requests to the content resolver
     * @param internalStorableItemId internal id of storable item
     * @return where args
     */
    @NonNull
    private static String[] buildWhereArgsByInternalId(long internalStorableItemId) {
        return new String[] { String.valueOf(internalStorableItemId) };
    }

    /**
     * Builds where args with storable item id for common requests to the content resolver
     * @param storableItem to get internal id
     * @return where args
     */
    @NonNull
    private static String[] buildWhereArgsByInternalId(@NonNull IBambooStorableItem storableItem) {
        return buildWhereArgsByInternalId(storableItem.getInternalId());
    }

    /**
     * Creates and fills storable item from cursor
     * @param classOfStorableItem class of storable item to instantiate
     * @param internalIdFieldName class's internal id field name
     * @param cursor cursor to getByInternalId fields of item
     * @param <T> generic type of the storable item
     * @return storable item filled with info from cursor
     * @throws IllegalArgumentException if classOfStorableItem can not be used to create item from Cursor
     */
    @NonNull
    private static <T extends IBambooStorableItem> T createStorableItemFromCursor(@NonNull Class<T> classOfStorableItem, @NonNull String internalIdFieldName, @NonNull Cursor cursor) {
        try {
            T storableItem = classOfStorableItem.newInstance();
            storableItem.setInternalId(cursor.getLong(cursor.getColumnIndex(internalIdFieldName)));
            storableItem.fillFromCursor(cursor);
            return storableItem;
        } catch (InstantiationException e) {
            throw new IllegalArgumentException(classOfStorableItem + " can not be used for createStorableItemFromCursor() because its instance can not be created by class.newInstance()");
        } catch (IllegalAccessException e) {
            throw new IllegalArgumentException(classOfStorableItem + " can not be used for createStorableItemFromCursor() because it had no default constructor or it's not public, instance can not be created by class.newInstance()");
        }
    }

    /**
     * Creates and fills storable item from cursor
     * @param classOfStorableItem class of storable item to instantiate
     * @param cursor cursor to getByInternalId fields of item
     * @param <T> generic type of the storable item
     * @return storable item filled with info from cursor
     * @throws IllegalArgumentException if classOfStorableItem can not be used to create item from Cursor
     */
    @NonNull
    public static <T extends IBambooStorableItem> T createStorableItemFromCursor(@NonNull Class<T> classOfStorableItem, @NonNull Cursor cursor) {
        return createStorableItemFromCursor(classOfStorableItem, getTypeMetaWithExtra(classOfStorableItem).typeMeta.internalIdFieldName(), cursor);
    }
}
