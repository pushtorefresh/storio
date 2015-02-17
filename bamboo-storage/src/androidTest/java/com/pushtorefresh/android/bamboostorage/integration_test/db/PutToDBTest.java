package com.pushtorefresh.android.bamboostorage.integration_test.db;

import android.support.annotation.NonNull;
import android.test.AndroidTestCase;

import com.pushtorefresh.android.bamboostorage.BambooStorage;
import com.pushtorefresh.android.bamboostorage.result.SinglePutResult;

public class PutToDBTest extends AndroidTestCase {

    @NonNull private BambooStorage bambooStorage;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        bambooStorage = TestDBBambooStorageFactory.getTestBambooStorageFromDB(getContext());
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
    }
}
