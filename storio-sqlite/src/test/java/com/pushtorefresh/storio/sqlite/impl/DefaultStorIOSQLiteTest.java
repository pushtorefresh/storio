package com.pushtorefresh.storio.sqlite.impl;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.pushtorefresh.storio.sqlite.Changes;
import com.pushtorefresh.storio.sqlite.SQLiteTypeMapping;
import com.pushtorefresh.storio.sqlite.StorIOSQLite;
import com.pushtorefresh.storio.sqlite.operations.delete.DeleteResolver;
import com.pushtorefresh.storio.sqlite.operations.get.GetResolver;
import com.pushtorefresh.storio.sqlite.operations.put.PutResolver;
import com.pushtorefresh.storio.sqlite.queries.RawQuery;

import org.junit.Test;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

public class DefaultStorIOSQLiteTest {

    @Test(expected = NullPointerException.class)
    public void nullSQLiteOpenHelper() {
        //noinspection ConstantConditions
        DefaultStorIOSQLite.builder()
                .sqliteOpenHelper(null);
    }

    @Test(expected = NullPointerException.class)
    public void addTypeMappingNullType() {
        //noinspection unchecked,ConstantConditions
        DefaultStorIOSQLite.builder()
                .sqliteOpenHelper(mock(SQLiteOpenHelper.class))
                .addTypeMapping(null, SQLiteTypeMapping.builder()
                        .putResolver(mock(PutResolver.class))
                        .getResolver(mock(GetResolver.class))
                        .deleteResolver(mock(DeleteResolver.class))
                        .build());
    }

    @Test(expected = NullPointerException.class)
    public void addTypeMappingNullMapping() {
        //noinspection ConstantConditions
        DefaultStorIOSQLite.builder()
                .sqliteOpenHelper(mock(SQLiteOpenHelper.class))
                .addTypeMapping(Object.class, null);
    }

    @Test
    public void shouldReturnNullIfNoTypeMappingsRegistered() {
        class TestItem {

        }

        final StorIOSQLite storIOSQLite = DefaultStorIOSQLite.builder()
                .sqliteOpenHelper(mock(SQLiteOpenHelper.class))
                .build();

        assertThat(storIOSQLite.internal().typeMapping(TestItem.class)).isNull();
    }

    @Test
    public void shouldReturnNullIfNotTypeMappingRegisteredForType() {
        class TestItem {

        }

        class Entity {

        }

        //noinspection unchecked
        final SQLiteTypeMapping<Entity> entityContentResolverTypeMapping = SQLiteTypeMapping.<Entity>builder()
                .putResolver(mock(PutResolver.class))
                .getResolver(mock(GetResolver.class))
                .deleteResolver(mock(DeleteResolver.class))
                .build();

        final StorIOSQLite storIOSQLite = DefaultStorIOSQLite.builder()
                .sqliteOpenHelper(mock(SQLiteOpenHelper.class))
                .addTypeMapping(Entity.class, entityContentResolverTypeMapping)
                .build();

        assertThat(storIOSQLite.internal().typeMapping(Entity.class)).isSameAs(entityContentResolverTypeMapping);

        assertThat(storIOSQLite.internal().typeMapping(TestItem.class)).isNull();
    }

    @Test
    public void directTypeMappingShouldWork() {
        class TestItem {

        }

        //noinspection unchecked
        final SQLiteTypeMapping<TestItem> typeMapping = SQLiteTypeMapping.<TestItem>builder()
                .putResolver(mock(PutResolver.class))
                .getResolver(mock(GetResolver.class))
                .deleteResolver(mock(DeleteResolver.class))
                .build();

        final StorIOSQLite storIOSQLite = DefaultStorIOSQLite.builder()
                .sqliteOpenHelper(mock(SQLiteOpenHelper.class))
                .addTypeMapping(TestItem.class, typeMapping)
                .build();

        assertThat(storIOSQLite.internal().typeMapping(TestItem.class)).isSameAs(typeMapping);
    }

    @Test
    public void indirectTypeMappingShouldWork() {
        class TestItem {

        }

        //noinspection unchecked
        final SQLiteTypeMapping<TestItem> typeMapping = SQLiteTypeMapping.<TestItem>builder()
                .putResolver(mock(PutResolver.class))
                .getResolver(mock(GetResolver.class))
                .deleteResolver(mock(DeleteResolver.class))
                .build();

        final StorIOSQLite storIOSQLite = DefaultStorIOSQLite.builder()
                .sqliteOpenHelper(mock(SQLiteOpenHelper.class))
                .addTypeMapping(TestItem.class, typeMapping)
                .build();

        class TestItemSubclass extends TestItem {

        }

        // Direct type mapping should still work
        assertThat(storIOSQLite.internal().typeMapping(TestItem.class)).isSameAs(typeMapping);

        // Indirect type mapping should give same type mapping as for parent class
        assertThat(storIOSQLite.internal().typeMapping(TestItemSubclass.class)).isSameAs(typeMapping);
    }

    @Test
    public void typeMappingShouldWorkInCaseOfMoreConcreteTypeMapping() {
        class TestItem {

        }

        //noinspection unchecked
        final SQLiteTypeMapping<TestItem> typeMapping = SQLiteTypeMapping.<TestItem>builder()
                .putResolver(mock(PutResolver.class))
                .getResolver(mock(GetResolver.class))
                .deleteResolver(mock(DeleteResolver.class))
                .build();


        class TestItemSubclass extends TestItem {

        }

        //noinspection unchecked
        final SQLiteTypeMapping<TestItemSubclass> subclassTypeMapping = SQLiteTypeMapping.<TestItemSubclass>builder()
                .putResolver(mock(PutResolver.class))
                .getResolver(mock(GetResolver.class))
                .deleteResolver(mock(DeleteResolver.class))
                .build();


        final StorIOSQLite storIOSQLite = DefaultStorIOSQLite.builder()
                .sqliteOpenHelper(mock(SQLiteOpenHelper.class))
                .addTypeMapping(TestItem.class, typeMapping)
                .addTypeMapping(TestItemSubclass.class, subclassTypeMapping)
                .build();

        // Parent class should have its own type mapping
        assertThat(storIOSQLite.internal().typeMapping(TestItem.class)).isSameAs(typeMapping);

        // Child class should have its own type mapping
        assertThat(storIOSQLite.internal().typeMapping(TestItemSubclass.class)).isSameAs(subclassTypeMapping);
    }

    @Test
    public void typeMappingShouldFindIndirectTypeMappingInCaseOfComplexInheritance() {
        // Good test case — inheritance with AutoValue/AutoParcel

        class Entity {

        }

        class AutoValue_Entity extends Entity {

        }

        class ConcreteEntity extends Entity {

        }

        class AutoValue_ConcreteEntity extends ConcreteEntity {

        }

        //noinspection unchecked
        final SQLiteTypeMapping<Entity> entitySQLiteTypeMapping = SQLiteTypeMapping.<Entity>builder()
                .putResolver(mock(PutResolver.class))
                .getResolver(mock(GetResolver.class))
                .deleteResolver(mock(DeleteResolver.class))
                .build();

        //noinspection unchecked
        final SQLiteTypeMapping<ConcreteEntity> concreteEntitySQLiteTypeMapping = SQLiteTypeMapping.<ConcreteEntity>builder()
                .putResolver(mock(PutResolver.class))
                .getResolver(mock(GetResolver.class))
                .deleteResolver(mock(DeleteResolver.class))
                .build();

        final StorIOSQLite storIOSQLite = DefaultStorIOSQLite.builder()
                .sqliteOpenHelper(mock(SQLiteOpenHelper.class))
                .addTypeMapping(Entity.class, entitySQLiteTypeMapping)
                .addTypeMapping(ConcreteEntity.class, concreteEntitySQLiteTypeMapping)
                .build();

        // Direct type mapping for Entity should work
        assertThat(storIOSQLite.internal().typeMapping(Entity.class)).isSameAs(entitySQLiteTypeMapping);

        // Direct type mapping for ConcreteEntity should work
        assertThat(storIOSQLite.internal().typeMapping(ConcreteEntity.class)).isSameAs(concreteEntitySQLiteTypeMapping);

        // Indirect type mapping for AutoValue_Entity should get type mapping for Entity
        assertThat(storIOSQLite.internal().typeMapping(AutoValue_Entity.class)).isSameAs(entitySQLiteTypeMapping);

        // Indirect type mapping for AutoValue_ConcreteEntity should get type mapping for ConcreteEntity, not for Entity!
        assertThat(storIOSQLite.internal().typeMapping(AutoValue_ConcreteEntity.class)).isSameAs(concreteEntitySQLiteTypeMapping);
    }

    @Test
    public void shouldCloseSQLiteOpenHelper() throws IOException {
        SQLiteOpenHelper sqLiteOpenHelper = mock(SQLiteOpenHelper.class);

        StorIOSQLite storIOSQLite = DefaultStorIOSQLite.builder()
                .sqliteOpenHelper(sqLiteOpenHelper)
                .build();

        // Should not call close before explicit call to close
        verify(sqLiteOpenHelper, times(0)).close();

        storIOSQLite.close();

        // Should call close on SQLiteOpenHelper
        verify(sqLiteOpenHelper).close();
    }

    @Test
    public void shouldPassSQLToExecSQL() {
        SQLiteOpenHelper sqLiteOpenHelper = mock(SQLiteOpenHelper.class);

        SQLiteDatabase sqLiteDatabase = mock(SQLiteDatabase.class);

        when(sqLiteOpenHelper.getWritableDatabase())
                .thenReturn(sqLiteDatabase);

        StorIOSQLite storIOSQLite = DefaultStorIOSQLite.builder()
                .sqliteOpenHelper(sqLiteOpenHelper)
                .build();

        RawQuery rawQuery = RawQuery.builder()
                .query("DROP TABLE users")
                .args("arg1", "arg2")
                .build();

        storIOSQLite
                .internal()
                .executeSQL(rawQuery);

        verify(sqLiteOpenHelper).getWritableDatabase();
        verify(sqLiteDatabase).execSQL(eq(rawQuery.query()), eq(new String[]{"arg1", "arg2"}));
        verifyNoMoreInteractions(sqLiteOpenHelper, sqLiteDatabase);
    }

    // See https://github.com/pushtorefresh/storio/issues/478
    @Test
    public void nestedTransactionShouldWorkOkay() {
        SQLiteOpenHelper sqLiteOpenHelper = mock(SQLiteOpenHelper.class);
        SQLiteDatabase sqLiteDatabase = mock(SQLiteDatabase.class);

        when(sqLiteOpenHelper.getWritableDatabase()).thenReturn(sqLiteDatabase);

        StorIOSQLite storIOSQLite = DefaultStorIOSQLite.builder()
                .sqliteOpenHelper(sqLiteOpenHelper)
                .build();

        // External transaction
        storIOSQLite.internal().beginTransaction();

        try {
            try {
                // Nested transaction
                storIOSQLite.internal().beginTransaction();

                storIOSQLite
                        .internal()
                        .notifyAboutChanges(Changes.newInstance("table1"));

                storIOSQLite
                        .internal()
                        .notifyAboutChanges(Changes.newInstance("table2"));

                // Finishing nested transaction
                storIOSQLite.internal().setTransactionSuccessful();
            } finally {
                storIOSQLite.internal().endTransaction();
            }

            // Marking external transaction as successful
            storIOSQLite.internal().setTransactionSuccessful();
        } finally {
            // Finishing external transaction
            storIOSQLite.internal().endTransaction();
        }
    }
}
