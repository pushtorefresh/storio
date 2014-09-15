## `Android BambooStorage` 
######Modern, fast and memory efficient Storage API based on [`ContentProviders`](http://developer.android.com/reference/android/content/ContentProvider.html)

*Author of the original idea — [@ivanGusef](https://github.com/ivanGusef)*

**BambooStorage** provides you an easy way to store your data in `ContentProvider` without boilerplate [`CRUD`](http://en.wikipedia.org/wiki/Create,_read,_update_and_delete) (Create-Read-Update-Delete) code for each storable type

If you currently use [`SQLiteOpenHelper`](http://developer.android.com/reference/android/database/sqlite/SQLiteOpenHelper.html) ([`SQLiteDatabase`](http://developer.android.com/reference/android/database/sqlite/SQLiteDatabase.html) under it) to store your data, you can easily switch to `BambooStorage` because it provides base class for `ContentProvider` with `SQLiteOpenHelper`

-----------------------------------
**Released on the mavenCentral**

`compile 'com.pushtorefresh:bamboostorage:1.4.0' // current release is v1.4.0`


**`master`** branch build status [on Travis CI](https://travis-ci.org/pushtorefresh/bamboo-storage): [![Build Status](https://travis-ci.org/pushtorefresh/bamboo-storage.svg?branch=master)](https://travis-ci.org/pushtorefresh/bamboo-storage)

***Listed in:***

[![Android Arsenal](https://img.shields.io/badge/Android%20Arsenal-Android%20BambooStorage-brightgreen.svg?style=flat)](https://android-arsenal.com/details/1/928)

-----------------------------------
##Release notes:

**v1.4.0 (September 16, 2014):**
- Now you can add listener to the `BambooStorage`, check `ABambooStorageListener`, very useful with some `EventBus` like [`Otto`](https://github.com/square/otto)
- New `addAll(collectionOfItems)` and `addOrUpdateAll(collectionOfItems)` methods 

**Breaking changes in 2.0 (under development):**
- Generating `fromCursor()` and `fillFromCursor()` at compile time
- No more `add()`, `update()`, `addOrUpdate()`, just one `save()`, `saveAll()`

-----------------------------------

**What API provides `BambooStorage`? It's CRUD with collection like methods names** 
Implementation is as efficient as possible

- `addListener(ABambooStorageListener)` — adds listener to the `BambooStorage`
- `removeListener(ABambooStorageListener)` — removes listener from the `BambooStorage`
- `add(yourStorableItem)` — adds an item to the storage
- `update(yourStorableItem)` — updates an item in the storage
- `addOrUpdate(yourStorableItem)` — adds or updates item in/to the storage
- `addAll(yourStorableItems)` — adds a collection of items to the storage
- `addOrUpdateAll(yourStorableItems)` — adds/updates a collection of items to the storage
- `getByInternalId(classOfStorableItem, internalItemId)` — low-level get method to receive item from storage
- `getAsList(classOfStorableItems, where, whereArgs, order)` — gets stored items as list
- `getAsList(classOfStorableItems, where, whereArgs)` — gets stored items as list with default storage's order
- `getAsList(classOfStorableItems)` — gets all stored items of required type as list
- `getAsCursor(classOfStorableItems, where, whereArgs, order)` — gets stored items as cursor!
- `getFirst(classOfStorableItems, where, whereArgs, order)` — gets first item from query result
- `getFirst(classOfStorableItems)` — gets first item of required type with default order for storage
- `getLast(classOfStorableItems, where, whereArgs, order)` — gets last item from query result
- `getLast(classOfStorableItems)` — gets last item of required type with default order for storage
- `remove(yourStorableItem)` — removes item from the storage
- `remove(classOfStorableItems, where, whereArgs)` — removes item(s) from the storage using where condition
- `removeAllOfType(classOfStorableItems)` — removes all items of required type
- `contains(yourStorableItem)` — returns true if storage contains item, false if not
- `countOfItems(classOfStorableItems)` — returns count of items of required type 
- `notifyChange(classOfStorableItems, contentObserver)` — notifying content obserevers about change in the storage
- `notifyChange(classOfStorableItems)` — notifying content observers about change in the storage

-----------------------------------
**HOW to use `BambooStorage` in your project, 3 easy steps**

**1) Your storable class should implement `IBambooStorableItem` or extend `ABambooStorableItem`** and implement `toContentValues(Resources res)` and `fillFromCursor(Cursor cursor)` methods

    @BambooStorableTypeMeta(
        contentPath = YourStorableType.TableInfo.TABLE_NAME, // Required
        internalIdFieldName = YourStorableType.TableInfo.INTERNAL_ID // Optional, default value = "_id"
    )
    public class YourStorableType extends ABambooStorableItem {
        
        private String mStringField;
        private int mIntField;
        
        public YourStorableType() {
            // PLEASE left the default constructor 
            // to left BambooStorage a chance to create instance of YourStorableType ;)
        }
        
        @Override @NonNull public toContentValues(@NonNull Resources res) {
            ContentValues contentValues = new ContentValues();
            
            contentValues.put(TableInfo.STRING_FIELD, mStringField);
            contentValues.put(TableInfo.INT_FIELD, mIntField);
            
            return contentValues;
        }
        
        @Override public fillFromCursor(@NonNull Cursor cursor) {
            mStringField = cursor.getString(cursor.getColumnIndex(TableInfo.STRING_FIELD));
            mIntField    = cursor.getInt(cursor.getColumnIndex(TableInfo.INT_FIELD));
        }
        
        // If under your ContentProvider not an SQLiteDatabase
        // -> just rename this interface/class for fields mapping in cursor as you need
        public interface TableInfo {
            String TABLE_NAME         = "your_storable_items";
            
            String INTERNAL_ID_FIELD  = "_internal_id";
            String STRING_FIELD       = "string_field;
            String INT_FIELD          = "int_field;
            
            String CREATE_TABLE_QUERY = "CREATE TABLE " + TABLE_NAME + " (" +
                INTERNAL_ID_FIELD + " INTEGER PRIMARY KEY, " + 
                STRING_FIELD + " TEXT, " + 
                INT_FIELD + " INTEGER);";
        }
    }

**2) Mark your storable class with `@BambooStorableTypeMeta` annotation**

-> Check the previous step sources

**3) Create or use your `ContentProvider` and pass its authority to the `BambooStorage`**

**DO NOT FORGET TO DECLARE YOUR `ContentProvider` in `AndroidManifest.xml`** 

You can extend `ABambooSQLiteOpenHelperContentProvider` and provide your `SQLiteOpenHelper`:

    public class YourContentProvider extends ABambooSQLiteOpenHelperContentProvider {
    
        @Override @NonNull protected SQLiteOpenHelper provideSQLiteOpenHelper() {
            return new SQLiteOpenHelper(getContext());
        }
    }

**Now you can use `BambooStorage`!**

    BambooStorage bambooStorage = new BambooStorage(
                                  getContext(), 
                                  "content://authority_of_your_content_provider"
    );


-----------------------------------

##Good to know

- `BambooStorage` is thread-safe, but your `ContentProvider` should be thread-safe too
- By default, `SQLiteDatabase` is thead-safe, so if you are using `ContentProvider` with `SQLiteDatabase` it is thead-safe too :)
- `BambooStorage` is written in very efficient way, even work with `BambooStorableTypeMeta` annotation is fast because of internal cache (`YourStorableType`, `BambooStorableTypeMeta`)
- It is better to have [`Singleton`](http://en.wikipedia.org/wiki/Singleton_pattern) instance of `BambooStorage` for each `ContentProvider`, even better, if you would use some [`DI`](http://en.wikipedia.org/wiki/Dependency_injection) tool for that, for example — [`Dagger`](http://square.github.io/dagger/)
- `BambooStorage` can work with non `SQLiteDatabase-driven` `ContentProvider`, it is an abstraction over any type of `ContentProvider`
