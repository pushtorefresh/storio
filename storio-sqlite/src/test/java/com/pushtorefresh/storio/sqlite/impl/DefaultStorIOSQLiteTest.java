package com.pushtorefresh.storio.sqlite.impl;

import android.database.sqlite.SQLiteOpenHelper;

import com.pushtorefresh.storio.sqlite.SQLiteTypeMapping;
import com.pushtorefresh.storio.sqlite.StorIOSQLite;
import com.pushtorefresh.storio.sqlite.operation.delete.DeleteResolver;
import com.pushtorefresh.storio.sqlite.operation.get.GetResolver;
import com.pushtorefresh.storio.sqlite.operation.put.PutResolver;

import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class DefaultStorIOSQLiteTest {

    @SuppressWarnings("ConstantConditions")
    @Test(expected = NullPointerException.class)
    public void nullSQLiteOpenHelper() {
        DefaultStorIOSQLite.builder()
                .sqliteOpenHelper(null);
    }

    @SuppressWarnings({"ConstantConditions", "unchecked"})
    @Test(expected = NullPointerException.class)
    public void addTypeMappingNullType() {
        DefaultStorIOSQLite.builder()
                .sqliteOpenHelper(mock(SQLiteOpenHelper.class))
                .addTypeMapping(null, SQLiteTypeMapping.builder()
                        .putResolver(mock(PutResolver.class))
                        .getResolver(mock(GetResolver.class))
                        .deleteResolver(mock(DeleteResolver.class))
                        .build());
    }

    @SuppressWarnings({"unchecked", "ConstantConditions"})
    @Test(expected = NullPointerException.class)
    public void addTypeMappingNullMapping() {
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

        assertNull(storIOSQLite.internal().typeMapping(TestItem.class));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void shouldReturnNullIfNotTypeMappingRegisteredForType() {
        class TestItem {

        }

        class Entity {

        }

        final SQLiteTypeMapping<Entity> entityContentResolverTypeMapping = SQLiteTypeMapping.<Entity>builder()
                .putResolver(mock(PutResolver.class))
                .getResolver(mock(GetResolver.class))
                .deleteResolver(mock(DeleteResolver.class))
                .build();

        final StorIOSQLite storIOSQLite = DefaultStorIOSQLite.builder()
                .sqliteOpenHelper(mock(SQLiteOpenHelper.class))
                .addTypeMapping(Entity.class, entityContentResolverTypeMapping)
                .build();

        assertSame(entityContentResolverTypeMapping, storIOSQLite.internal().typeMapping(Entity.class));

        assertNull(storIOSQLite.internal().typeMapping(TestItem.class));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void directTypeMappingShouldWork() {
        class TestItem {

        }

        final SQLiteTypeMapping<TestItem> typeMapping = SQLiteTypeMapping.<TestItem>builder()
                .putResolver(mock(PutResolver.class))
                .getResolver(mock(GetResolver.class))
                .deleteResolver(mock(DeleteResolver.class))
                .build();

        final StorIOSQLite storIOSQLite = DefaultStorIOSQLite.builder()
                .sqliteOpenHelper(mock(SQLiteOpenHelper.class))
                .addTypeMapping(TestItem.class, typeMapping)
                .build();

        assertSame(typeMapping, storIOSQLite.internal().typeMapping(TestItem.class));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void indirectTypeMappingShouldWork() {
        class TestItem {

        }

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
        assertSame(typeMapping, storIOSQLite.internal().typeMapping(TestItem.class));

        // Indirect type mapping should give same type mapping as for parent class
        assertSame(typeMapping, storIOSQLite.internal().typeMapping(TestItemSubclass.class));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void typeMappingShouldWorkInCaseOfMoreConcreteTypeMapping() {
        class TestItem {

        }

        final SQLiteTypeMapping<TestItem> typeMapping = SQLiteTypeMapping.<TestItem>builder()
                .putResolver(mock(PutResolver.class))
                .getResolver(mock(GetResolver.class))
                .deleteResolver(mock(DeleteResolver.class))
                .build();


        class TestItemSubclass extends TestItem {

        }

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
        assertSame(typeMapping, storIOSQLite.internal().typeMapping(TestItem.class));

        // Child class should have its own type mapping
        assertSame(subclassTypeMapping, storIOSQLite.internal().typeMapping(TestItemSubclass.class));
    }

    @SuppressWarnings("unchecked")
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

        final SQLiteTypeMapping<Entity> entitySQLiteTypeMapping = SQLiteTypeMapping.<Entity>builder()
                .putResolver(mock(PutResolver.class))
                .getResolver(mock(GetResolver.class))
                .deleteResolver(mock(DeleteResolver.class))
                .build();

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
        assertSame(entitySQLiteTypeMapping, storIOSQLite.internal().typeMapping(Entity.class));

        // Direct type mapping for ConcreteEntity should work
        assertSame(concreteEntitySQLiteTypeMapping, storIOSQLite.internal().typeMapping(ConcreteEntity.class));

        // Indirect type mapping for AutoValue_Entity should get type mapping for Entity
        assertSame(entitySQLiteTypeMapping, storIOSQLite.internal().typeMapping(AutoValue_Entity.class));

        // Indirect type mapping for AutoValue_ConcreteEntity should get type mapping for ConcreteEntity, not for Entity!
        assertSame(concreteEntitySQLiteTypeMapping, storIOSQLite.internal().typeMapping(AutoValue_ConcreteEntity.class));
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
}
