package com.pushtorefresh.android.bamboostorage.integration_test.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.test.AndroidTestCase;

import com.pushtorefresh.android.bamboostorage.BambooStorage;
import com.pushtorefresh.android.bamboostorage.result.SinglePutResult;
import com.pushtorefresh.android.bamboostorage.wtf.StorableTypeParser;
import com.pushtorefresh.android.bamboostorage.wtf.StorableTypeSerializer;

public class PutToDBTest extends AndroidTestCase {

    @NonNull private BambooStorage bambooStorage;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        bambooStorage = TestDBBambooStorageFactory.getTestBambooStorageFromDatabase(getContext());

        bambooStorage.setSerializerForType(User.class, new StorableTypeSerializer<User>() {
            @NonNull
            @Override
            public ContentValues toContentValues(@NonNull User object) {
                ContentValues contentValues = new ContentValues();
                contentValues.put(User.COLUMN_ID, object.getStorableId());
                contentValues.put(User.COLUMN_NAME, object.getName());
                contentValues.put(User.COLUMN_SURNAME, object.getSurname());
                return contentValues;
            }
        });

        bambooStorage.setParserForType(User.class, new StorableTypeParser<User>() {
            @NonNull
            @Override
            public User parseFromCursor(@NonNull Cursor cursor) {
                User user = new User();
                user.setStorableId(cursor.getLong(cursor.getColumnIndex(User.COLUMN_ID)));
                user.setName(cursor.getString(cursor.getColumnIndex(User.COLUMN_NAME)));
                user.setSurname(cursor.getString(cursor.getColumnIndex(User.COLUMN_SURNAME)));
                return user;
            }
        });

        // clearing db for each test case
        bambooStorage.forType(User.class).deleteAll();
    }

    public void testPutNullObject() {
        try {
            bambooStorage.forType(User.class).put(null);
            fail("NullPointerException should be thrown");
        } catch (NullPointerException ignored) {
            // okay
        }
    }

    public void testPutWithoutReplace() {
        // storable id == null, no update should occur
        User user = new User().setName("Haizenberg").setSurname("White");

        SinglePutResult<User> putResult = bambooStorage.forType(User.class).put(user);

        assertEquals(true,  putResult.wasInserted());
        assertEquals(false, putResult.wasUpdated());

        assertEquals(user, bambooStorage.forType(User.class).getFirst().asObject());
    }
}
