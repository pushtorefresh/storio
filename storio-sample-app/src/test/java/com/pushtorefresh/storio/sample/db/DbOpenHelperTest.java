package com.pushtorefresh.storio.sample.db;

import android.database.sqlite.SQLiteDatabase;

import com.pushtorefresh.storio.contentresolver.BuildConfig;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import static org.assertj.core.api.Java6Assertions.assertThat;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)
public final class DbOpenHelperTest {

    @Test
    public void shouldCreateDb() {
        SQLiteDatabase database = new DbOpenHelper(RuntimeEnvironment.application)
                .getWritableDatabase();

        assertThat(database).isNotNull();
    }
}
