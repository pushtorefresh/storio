package com.pushtorefresh.storio.sqlite.impl;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.pushtorefresh.storio.sqlite.Changes;
import com.pushtorefresh.storio.sqlite.SQLiteTypeMapping;
import com.pushtorefresh.storio.sqlite.StorIOSQLite;
import com.pushtorefresh.storio.sqlite.operations.delete.DeleteResolver;
import com.pushtorefresh.storio.sqlite.operations.get.GetResolver;
import com.pushtorefresh.storio.sqlite.operations.put.PutResolver;
import com.pushtorefresh.storio.sqlite.queries.InsertQuery;
import com.pushtorefresh.storio.sqlite.queries.RawQuery;

import org.junit.Test;

import java.io.IOException;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import rx.observers.TestSubscriber;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.failBecauseExceptionWasNotThrown;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.same;
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

    interface InterfaceEntity {
    }

    @Test
    public void typeMappingShouldFindInterface() {
        //noinspection unchecked
        SQLiteTypeMapping<InterfaceEntity> typeMapping = mock(SQLiteTypeMapping.class);

        final StorIOSQLite storIOSQLite = DefaultStorIOSQLite.builder()
                .sqliteOpenHelper(mock(SQLiteOpenHelper.class))
                .addTypeMapping(InterfaceEntity.class, typeMapping)
                .build();

        assertThat(storIOSQLite.internal().typeMapping(InterfaceEntity.class)).isSameAs(typeMapping);
    }

    @Test
    public void typeMappingShouldFindIndirectTypeMappingForClassThatImplementsKnownInterface() {
        //noinspection unchecked
        SQLiteTypeMapping<InterfaceEntity> typeMapping = mock(SQLiteTypeMapping.class);

        final StorIOSQLite storIOSQLite = DefaultStorIOSQLite.builder()
                .sqliteOpenHelper(mock(SQLiteOpenHelper.class))
                .addTypeMapping(InterfaceEntity.class, typeMapping)
                .build();

        class ConcreteEntity implements InterfaceEntity {
        }

        assertThat(storIOSQLite.internal().typeMapping(ConcreteEntity.class)).isSameAs(typeMapping);

        // Just to make sure that we don't return this type mapping for all classes.
        assertThat(storIOSQLite.internal().typeMapping(Random.class)).isNull();
    }

    @Test
    public void typeMappingShouldFindIndirectTypeMappingForClassThatHasParentThatImplementsKnownInterface() {
        //noinspection unchecked
        SQLiteTypeMapping<InterfaceEntity> typeMapping = mock(SQLiteTypeMapping.class);

        final StorIOSQLite storIOSQLite = DefaultStorIOSQLite.builder()
                .sqliteOpenHelper(mock(SQLiteOpenHelper.class))
                .addTypeMapping(InterfaceEntity.class, typeMapping)
                .build();

        class ConcreteEntity implements InterfaceEntity {
        }

        class Parent_ConcreteEntity extends ConcreteEntity {
        }


        assertThat(storIOSQLite.internal().typeMapping(Parent_ConcreteEntity.class)).isSameAs(typeMapping);

        // Just to make sure that we don't return this type mapping for all classes.
        assertThat(storIOSQLite.internal().typeMapping(Random.class)).isNull();

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
    public void shouldPassSQLWithArgsToExecSQL() {
        SQLiteOpenHelper sqLiteOpenHelper = mock(SQLiteOpenHelper.class);
        SQLiteDatabase sqLiteDatabase = mock(SQLiteDatabase.class);

        when(sqLiteOpenHelper.getWritableDatabase()).thenReturn(sqLiteDatabase);

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

    @Test
    public void shouldPassSQLWithoutArgsToExecSQL() {
        SQLiteOpenHelper sqLiteOpenHelper = mock(SQLiteOpenHelper.class);
        SQLiteDatabase sqLiteDatabase = mock(SQLiteDatabase.class);

        when(sqLiteOpenHelper.getWritableDatabase()).thenReturn(sqLiteDatabase);

        StorIOSQLite storIOSQLite = DefaultStorIOSQLite.builder()
                .sqliteOpenHelper(sqLiteOpenHelper)
                .build();

        RawQuery rawQuery = RawQuery.builder()
                .query("DROP TABLE IF EXISTS someTable")
                .build(); // No args!

        storIOSQLite
                .internal()
                .executeSQL(rawQuery);

        verify(sqLiteOpenHelper).getWritableDatabase();
        verify(sqLiteDatabase).execSQL(eq(rawQuery.query()));
        verifyNoMoreInteractions(sqLiteOpenHelper, sqLiteDatabase);
    }

    // See https://github.com/pushtorefresh/storio/issues/478
    @Test
    public void nestedTransactionShouldWorkNormally() {
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

    @Test
    public void shouldPassArgsToInsertWithOnConflict() {
        SQLiteOpenHelper sqLiteOpenHelper = mock(SQLiteOpenHelper.class);
        SQLiteDatabase sqLiteDatabase = mock(SQLiteDatabase.class);

        when(sqLiteOpenHelper.getWritableDatabase()).thenReturn(sqLiteDatabase);

        StorIOSQLite storIOSQLite = DefaultStorIOSQLite.builder()
                .sqliteOpenHelper(sqLiteOpenHelper)
                .build();

        InsertQuery insertQuery = InsertQuery.builder()
                .table("test_table")
                .nullColumnHack("custom_null_hack")
                .build();

        ContentValues contentValues = mock(ContentValues.class);

        int conflictAlgorithm = SQLiteDatabase.CONFLICT_ROLLBACK;

        storIOSQLite.internal().insertWithOnConflict(insertQuery, contentValues, conflictAlgorithm);

        verify(sqLiteDatabase).insertWithOnConflict(
                eq("test_table"),
                eq("custom_null_hack"),
                same(contentValues),
                eq(SQLiteDatabase.CONFLICT_ROLLBACK)
        );
    }

    @Test
    public void notifyAboutChangesShouldNotAcceptNullAsChanges() {
        SQLiteOpenHelper sqLiteOpenHelper = mock(SQLiteOpenHelper.class);

        StorIOSQLite storIOSQLite = DefaultStorIOSQLite.builder()
                .sqliteOpenHelper(sqLiteOpenHelper)
                .build();

        StorIOSQLite.Internal internal = storIOSQLite.internal();
        assertThat(internal).isNotNull();

        try {
            internal.notifyAboutChanges(null);
            failBecauseExceptionWasNotThrown(NullPointerException.class);
        } catch (NullPointerException expected) {
            assertThat(expected).hasMessage("Changes can not be null");
        }
    }

    @Test
    public void observeChangesAndNotifyAboutChangesShouldWorkCorrectly() {
        SQLiteOpenHelper sqLiteOpenHelper = mock(SQLiteOpenHelper.class);

        StorIOSQLite storIOSQLite = DefaultStorIOSQLite.builder()
                .sqliteOpenHelper(sqLiteOpenHelper)
                .build();

        TestSubscriber<Changes> testSubscriber = new TestSubscriber<Changes>();

        storIOSQLite
                .observeChanges()
                .subscribe(testSubscriber);

        testSubscriber.assertNoValues();

        Changes changes = Changes.newInstance("test_table");

        storIOSQLite
                .internal()
                .notifyAboutChanges(changes);

        testSubscriber.assertValue(changes);
        testSubscriber.assertNoErrors();

        testSubscriber.unsubscribe();
    }

    @Test
    public void observeChangesInTablesShouldNotAcceptNullAsTables() {
        StorIOSQLite storIOSQLite = DefaultStorIOSQLite.builder()
                .sqliteOpenHelper(mock(SQLiteOpenHelper.class))
                .build();

        try {
            storIOSQLite.observeChangesInTables(null);
            failBecauseExceptionWasNotThrown(NullPointerException.class);
        } catch (NullPointerException expected) {
            assertThat(expected).hasMessage("Set of tables can not be null");
        }
    }

    @Test
    public void observeChangesInTables() {
        StorIOSQLite storIOSQLite = DefaultStorIOSQLite.builder()
                .sqliteOpenHelper(mock(SQLiteOpenHelper.class))
                .build();

        TestSubscriber<Changes> testSubscriber = new TestSubscriber<Changes>();

        Set<String> tables = new HashSet<String>(2);
        tables.add("table1");
        tables.add("table2");

        storIOSQLite
                .observeChangesInTables(tables)
                .subscribe(testSubscriber);

        testSubscriber.assertNoValues();

        Changes changes1 = Changes.newInstance("table1");

        storIOSQLite
                .internal()
                .notifyAboutChanges(changes1);

        testSubscriber.assertValue(changes1);

        Changes changes2 = Changes.newInstance("table2");

        storIOSQLite
                .internal()
                .notifyAboutChanges(changes2);

        testSubscriber.assertValues(changes1, changes2);

        Changes changes3 = Changes.newInstance("table3");

        storIOSQLite
                .internal()
                .notifyAboutChanges(changes3);

        // changes3 or any other changes are not expected here
        testSubscriber.assertValues(changes1, changes2);
        testSubscriber.assertNoErrors();
        testSubscriber.unsubscribe();
    }

    @Test
    public void observeChangesInTableShouldNotAcceptNullAsTables() {
        StorIOSQLite storIOSQLite = DefaultStorIOSQLite.builder()
                .sqliteOpenHelper(mock(SQLiteOpenHelper.class))
                .build();

        try {
            storIOSQLite.observeChangesInTable(null);
            failBecauseExceptionWasNotThrown(NullPointerException.class);
        } catch (NullPointerException expected) {
            assertThat(expected).hasMessage("Table can not be null or empty");
        }
    }

    @Test
    public void observeChangesInTable() {
        StorIOSQLite storIOSQLite = DefaultStorIOSQLite.builder()
                .sqliteOpenHelper(mock(SQLiteOpenHelper.class))
                .build();

        TestSubscriber<Changes> testSubscriber = new TestSubscriber<Changes>();

        storIOSQLite
                .observeChangesInTable("table1")
                .subscribe(testSubscriber);

        testSubscriber.assertNoValues();

        Changes changes1 = Changes.newInstance("table2");

        storIOSQLite
                .internal()
                .notifyAboutChanges(changes1);

        testSubscriber.assertNoValues();

        Changes changes2 = Changes.newInstance("table1");

        storIOSQLite
                .internal()
                .notifyAboutChanges(changes2);

        testSubscriber.assertValue(changes2);

        Changes changes3 = Changes.newInstance("table3");

        storIOSQLite
                .internal()
                .notifyAboutChanges(changes3);

        // Subscriber should not see changes of table2 and table3
        testSubscriber.assertValue(changes2);
        testSubscriber.assertNoErrors();
        testSubscriber.unsubscribe();
    }
}
