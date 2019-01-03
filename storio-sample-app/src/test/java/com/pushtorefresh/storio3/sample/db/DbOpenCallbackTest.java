package com.pushtorefresh.storio3.sample.db;

import com.pushtorefresh.storio3.contentresolver.BuildConfig;
import com.pushtorefresh.storio3.sample.SampleRobolectricTestRunner;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import androidx.sqlite.db.SupportSQLiteDatabase;
import androidx.sqlite.db.SupportSQLiteOpenHelper;
import androidx.sqlite.db.framework.FrameworkSQLiteOpenHelperFactory;

import static org.assertj.core.api.Java6Assertions.assertThat;

@RunWith(SampleRobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)
public final class DbOpenCallbackTest {

    @Test
    public void shouldCreateDb() {
        SupportSQLiteOpenHelper.Configuration configuration = SupportSQLiteOpenHelper.Configuration
                .builder(RuntimeEnvironment.application)
                .name(DbOpenCallback.DB_NAME)
                .callback(new DbOpenCallback())
                .build();

        SupportSQLiteDatabase database = new FrameworkSQLiteOpenHelperFactory().create(configuration)
                .getWritableDatabase();

        assertThat(database).isNotNull();
    }
}
