package com.pushtorefresh.storio.sqlite.impl;

import android.database.sqlite.SQLiteOpenHelper;

import com.pushtorefresh.storio.sqlite.SQLiteTypeMapping;
import com.pushtorefresh.storio.sqlite.StorIOSQLite;
import com.pushtorefresh.storio.sqlite.operation.delete.DeleteResolver;
import com.pushtorefresh.storio.sqlite.operation.get.GetResolver;
import com.pushtorefresh.storio.sqlite.operation.put.PutResolver;

import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class DefaultStorIOSQLiteTest {

    @SuppressWarnings("ConstantConditions")
    @Test(expected = NullPointerException.class)
    public void nullSQLiteOpenHelper() {
        new DefaultStorIOSQLite.Builder()
                .sqliteOpenHelper(null)
                .build();
    }

    @SuppressWarnings({"ConstantConditions", "unchecked"})
    @Test(expected = NullPointerException.class)
    public void addTypeMappingNullType() {
        new DefaultStorIOSQLite.Builder()
                .sqliteOpenHelper(mock(SQLiteOpenHelper.class))
                .addTypeMapping(null, new SQLiteTypeMapping.Builder<Object>()
                        .putResolver(mock(PutResolver.class))
                        .getResolver(mock(GetResolver.class))
                        .deleteResolver(mock(DeleteResolver.class))
                        .build())
                .build();
    }

    @SuppressWarnings({"unchecked", "ConstantConditions"})
    @Test(expected = NullPointerException.class)
    public void addTypeMappingNullMapping() {
        new DefaultStorIOSQLite.Builder()
                .sqliteOpenHelper(mock(SQLiteOpenHelper.class))
                .addTypeMapping(Object.class, null)
                .build();
    }

    @SuppressWarnings("unchecked")
    @Test
    public void typeMapping() {
        class TestItem {

        }

        final SQLiteTypeMapping<TestItem> testItemTypeDefinition = new SQLiteTypeMapping.Builder<TestItem>()
                .putResolver(mock(PutResolver.class))
                .getResolver(mock(GetResolver.class))
                .deleteResolver(mock(DeleteResolver.class))
                .build();

        final StorIOSQLite storIOSQLite = new DefaultStorIOSQLite.Builder()
                .sqliteOpenHelper(mock(SQLiteOpenHelper.class))
                .addTypeMapping(TestItem.class, testItemTypeDefinition)
                .build();

        assertEquals(testItemTypeDefinition, storIOSQLite.internal().typeMapping(TestItem.class));
    }

    @Test
    public void shouldCloseSQLiteOpenHelper() throws IOException {
        SQLiteOpenHelper sqLiteOpenHelper = mock(SQLiteOpenHelper.class);

        StorIOSQLite storIOSQLite = new DefaultStorIOSQLite.Builder()
                .sqliteOpenHelper(sqLiteOpenHelper)
                .build();

        // Should not call close before explicit call to close
        verify(sqLiteOpenHelper, times(0)).close();

        storIOSQLite.close();

        // Should call close on SQLiteOpenHelper
        verify(sqLiteOpenHelper).close();
    }
}
