package com.pushtorefresh.storio.sqlite.impl;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.NonNull;

import com.pushtorefresh.storio.TypeMappingFinder;
import com.pushtorefresh.storio.internal.ChangesBus;
import com.pushtorefresh.storio.internal.TypeMappingFinderImpl;
import com.pushtorefresh.storio.sqlite.Changes;
import com.pushtorefresh.storio.sqlite.SQLiteTypeMapping;
import com.pushtorefresh.storio.sqlite.StorIOSQLite;
import com.pushtorefresh.storio.sqlite.operations.delete.DeleteResolver;
import com.pushtorefresh.storio.sqlite.operations.get.GetResolver;
import com.pushtorefresh.storio.sqlite.operations.put.PutResolver;
import com.pushtorefresh.storio.sqlite.queries.InsertQuery;
import com.pushtorefresh.storio.sqlite.queries.RawQuery;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashSet;
import java.util.Set;

import javax.annotation.Nullable;

import rx.Scheduler;
import rx.observers.TestSubscriber;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.failBecauseExceptionWasNotThrown;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.same;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static rx.schedulers.Schedulers.io;

public class DefaultStorIOSQLiteTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void nullSQLiteOpenHelper() {
        DefaultStorIOSQLite.Builder builder = DefaultStorIOSQLite.builder();

        expectedException.expect(NullPointerException.class);
        expectedException.expectMessage(equalTo("Please specify SQLiteOpenHelper instance"));
        expectedException.expectCause(nullValue(Throwable.class));

        //noinspection ConstantConditions
        builder.sqliteOpenHelper(null);
    }

    @Test
    public void lowLevelReturnsSameInstanceOfSQLiteOpenHelper() {
        SQLiteOpenHelper sqLiteOpenHelper = mock(SQLiteOpenHelper.class);
        DefaultStorIOSQLite storIOSQLite = DefaultStorIOSQLite.builder()
                .sqliteOpenHelper(sqLiteOpenHelper)
                .build();
        assertThat(storIOSQLite.lowLevel().sqliteOpenHelper()).isSameAs(sqLiteOpenHelper);
    }

    @Test
    public void addTypeMappingNullType() {
        DefaultStorIOSQLite.CompleteBuilder builder = DefaultStorIOSQLite.builder()
                .sqliteOpenHelper(mock(SQLiteOpenHelper.class));

        expectedException.expect(NullPointerException.class);
        expectedException.expectMessage(equalTo("Please specify type"));
        expectedException.expectCause(nullValue(Throwable.class));

        //noinspection unchecked,ConstantConditions
        builder.addTypeMapping(null, SQLiteTypeMapping.builder()
                .putResolver(mock(PutResolver.class))
                .getResolver(mock(GetResolver.class))
                .deleteResolver(mock(DeleteResolver.class))
                .build());
    }

    @Test
    public void addTypeMappingNullMapping() {
        DefaultStorIOSQLite.CompleteBuilder builder = DefaultStorIOSQLite.builder()
                .sqliteOpenHelper(mock(SQLiteOpenHelper.class));

        expectedException.expect(NullPointerException.class);
        expectedException.expectMessage(equalTo("Please specify type mapping"));
        expectedException.expectCause(nullValue(Throwable.class));

        //noinspection ConstantConditions
        builder.addTypeMapping(Object.class, null);
    }

    @Test
    public void nullTypeMappingFinder() {
        DefaultStorIOSQLite.CompleteBuilder builder = DefaultStorIOSQLite.builder()
                .sqliteOpenHelper(mock(SQLiteOpenHelper.class));

        expectedException.expect(NullPointerException.class);
        expectedException.expectMessage(equalTo("Please specify typeMappingFinder"));
        expectedException.expectCause(nullValue(Throwable.class));

        //noinspection ConstantConditions
        builder.typeMappingFinder(null);
    }

    @Test
    public void shouldUseSpecifiedTypeMappingFinder() throws NoSuchFieldException, IllegalAccessException {
        TypeMappingFinder typeMappingFinder = mock(TypeMappingFinder.class);
        DefaultStorIOSQLite storIOSQLite = DefaultStorIOSQLite.builder()
                .sqliteOpenHelper(mock(SQLiteOpenHelper.class))
                .typeMappingFinder(typeMappingFinder)
                .build();

        assertThat(getTypeMappingFinder(storIOSQLite)).isEqualTo(typeMappingFinder);
    }

    @Test
    public void typeMappingShouldWorkWithoutSpecifiedTypeMappingFinder() {
        //noinspection unchecked
        SQLiteTypeMapping<ClassEntity> typeMapping = SQLiteTypeMapping.builder()
                .putResolver(mock(PutResolver.class))
                .getResolver(mock(GetResolver.class))
                .deleteResolver(mock(DeleteResolver.class))
                .build();

        DefaultStorIOSQLite storIOSQLite = DefaultStorIOSQLite.builder()
                .sqliteOpenHelper(mock(SQLiteOpenHelper.class))
                .addTypeMapping(ClassEntity.class, typeMapping)
                .build();

        assertThat(storIOSQLite.lowLevel().typeMapping(ClassEntity.class)).isEqualTo(typeMapping);
    }


    @Test
    public void typeMappingShouldWorkWithSpecifiedTypeMappingFinder() {
        TypeMappingFinder typeMappingFinder = new TypeMappingFinderImpl();

        //noinspection unchecked
        SQLiteTypeMapping<ClassEntity> typeMapping = SQLiteTypeMapping.builder()
                .putResolver(mock(PutResolver.class))
                .getResolver(mock(GetResolver.class))
                .deleteResolver(mock(DeleteResolver.class))
                .build();

        DefaultStorIOSQLite storIOSQLite = DefaultStorIOSQLite.builder()
                .sqliteOpenHelper(mock(SQLiteOpenHelper.class))
                .typeMappingFinder(typeMappingFinder)
                .addTypeMapping(ClassEntity.class, typeMapping)
                .build();

        assertThat(storIOSQLite.lowLevel().typeMapping(ClassEntity.class)).isEqualTo(typeMapping);
    }

    @Test
    public void typeMappingShouldWorkForMultipleTypes() {
        class AnotherEntity {
        }

        //noinspection unchecked
        SQLiteTypeMapping<ClassEntity> entityMapping = SQLiteTypeMapping.builder()
                .putResolver(mock(PutResolver.class))
                .getResolver(mock(GetResolver.class))
                .deleteResolver(mock(DeleteResolver.class))
                .build();

        //noinspection unchecked
        SQLiteTypeMapping<AnotherEntity> anotherMapping = SQLiteTypeMapping.builder()
                .putResolver(mock(PutResolver.class))
                .getResolver(mock(GetResolver.class))
                .deleteResolver(mock(DeleteResolver.class))
                .build();

        DefaultStorIOSQLite storIOSQLite = DefaultStorIOSQLite.builder()
                .sqliteOpenHelper(mock(SQLiteOpenHelper.class))
                .addTypeMapping(ClassEntity.class, entityMapping)
                .addTypeMapping(AnotherEntity.class, anotherMapping)
                .build();

        assertThat(storIOSQLite.lowLevel().typeMapping(ClassEntity.class)).isEqualTo(entityMapping);
        assertThat(storIOSQLite.lowLevel().typeMapping(AnotherEntity.class)).isEqualTo(anotherMapping);
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
                .lowLevel()
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
                .lowLevel()
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
        storIOSQLite.lowLevel().beginTransaction();

        try {
            try {
                // Nested transaction
                storIOSQLite.lowLevel().beginTransaction();

                storIOSQLite
                        .lowLevel()
                        .notifyAboutChanges(Changes.newInstance("table1"));

                storIOSQLite
                        .lowLevel()
                        .notifyAboutChanges(Changes.newInstance("table2"));

                // Finishing nested transaction
                storIOSQLite.lowLevel().setTransactionSuccessful();
            } finally {
                storIOSQLite.lowLevel().endTransaction();
            }

            // Marking external transaction as successful
            storIOSQLite.lowLevel().setTransactionSuccessful();
        } finally {
            // Finishing external transaction
            storIOSQLite.lowLevel().endTransaction();
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

        storIOSQLite.lowLevel().insertWithOnConflict(insertQuery, contentValues, conflictAlgorithm);

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

        StorIOSQLite.LowLevel lowLevel = storIOSQLite.lowLevel();
        assertThat(lowLevel).isNotNull();

        try {
            //noinspection ConstantConditions
            lowLevel.notifyAboutChanges(null);
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
                .lowLevel()
                .notifyAboutChanges(changes);

        testSubscriber.assertValue(changes);
        testSubscriber.assertNoErrors();

        testSubscriber.unsubscribe();
    }

    @Test
    public void observeChangesShouldThrowIfRxJavaNotInClassPath() throws NoSuchFieldException, IllegalAccessException {
        SQLiteOpenHelper sqLiteOpenHelper = mock(SQLiteOpenHelper.class);

        DefaultStorIOSQLite storIOSQLite = DefaultStorIOSQLite.builder()
                .sqliteOpenHelper(sqLiteOpenHelper)
                .build();
        //noinspection unchecked
        ChangesBus<Changes> changesBus = mock(ChangesBus.class);
        setChangesBus(storIOSQLite, changesBus);

        expectedException.expect(IllegalStateException.class);
        expectedException.expectMessage(equalTo("Observing changes in StorIOSQLite requires RxJava"));
        expectedException.expectCause(nullValue(Throwable.class));

        storIOSQLite.observeChanges();
    }

    @Test
    public void observeChangesInTablesShouldNotAcceptNullAsTables() {
        StorIOSQLite storIOSQLite = DefaultStorIOSQLite.builder()
                .sqliteOpenHelper(mock(SQLiteOpenHelper.class))
                .build();

        try {
            //noinspection ConstantConditions
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
                .lowLevel()
                .notifyAboutChanges(changes1);

        testSubscriber.assertValue(changes1);

        Changes changes2 = Changes.newInstance("table2");

        storIOSQLite
                .lowLevel()
                .notifyAboutChanges(changes2);

        testSubscriber.assertValues(changes1, changes2);

        Changes changes3 = Changes.newInstance("table3");

        storIOSQLite
                .lowLevel()
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
            //noinspection ConstantConditions
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
                .lowLevel()
                .notifyAboutChanges(changes1);

        testSubscriber.assertNoValues();

        Changes changes2 = Changes.newInstance("table1");

        storIOSQLite
                .lowLevel()
                .notifyAboutChanges(changes2);

        testSubscriber.assertValue(changes2);

        Changes changes3 = Changes.newInstance("table3");

        storIOSQLite
                .lowLevel()
                .notifyAboutChanges(changes3);

        // Subscriber should not see changes of table2 and table3
        testSubscriber.assertValue(changes2);
        testSubscriber.assertNoErrors();
        testSubscriber.unsubscribe();
    }

    @Test
    public void deprecatedInternalImplShouldReturnSentToConstructorTypeMapping() throws NoSuchFieldException, IllegalAccessException {
        TypeMappingFinder typeMappingFinder = mock(TypeMappingFinder.class);

        TestDefaultStorIOSQLite storIOSQLite =
                new TestDefaultStorIOSQLite(mock(SQLiteOpenHelper.class), typeMappingFinder);

        assertThat(storIOSQLite.typeMappingFinder()).isSameAs(typeMappingFinder);
    }

    @Test
    public void internalShouldReturnLowLevel() {
        DefaultStorIOSQLite storIOSQLite = DefaultStorIOSQLite.builder()
                .sqliteOpenHelper(mock(SQLiteOpenHelper.class))
                .build();

        assertThat(storIOSQLite.internal()).isSameAs(storIOSQLite.lowLevel());
    }

    static class ClassEntity {
    }

    @Nullable
    private static TypeMappingFinder getTypeMappingFinder(@NonNull DefaultStorIOSQLite storIOSQLite)
            throws NoSuchFieldException, IllegalAccessException {

        Field field = DefaultStorIOSQLite.LowLevelImpl.class.getDeclaredField("typeMappingFinder");
        field.setAccessible(true);
        return (TypeMappingFinder) field.get(storIOSQLite.lowLevel());
    }

    private static void setChangesBus(@NonNull DefaultStorIOSQLite storIOSQLite, @NonNull ChangesBus<Changes> changesBus)
            throws NoSuchFieldException, IllegalAccessException {

        Field field = DefaultStorIOSQLite.class.getDeclaredField("changesBus");
        field.setAccessible(true);

        Field modifiersField = Field.class.getDeclaredField("modifiers");
        modifiersField.setAccessible(true);
        modifiersField.setInt(field, field.getModifiers() & ~Modifier.FINAL);

        //noinspection unchecked
        field.set(storIOSQLite, changesBus);
    }

    class TestDefaultStorIOSQLite extends DefaultStorIOSQLite {
        private final Internal internal;

        protected TestDefaultStorIOSQLite(@NonNull SQLiteOpenHelper sqLiteOpenHelper, @NonNull TypeMappingFinder typeMappingFinder) {
            super(sqLiteOpenHelper, typeMappingFinder, null);
            internal = new InternalImpl(typeMappingFinder);
        }

        @Nullable
        public TypeMappingFinder typeMappingFinder() throws NoSuchFieldException, IllegalAccessException {
            Field field = TestDefaultStorIOSQLite.LowLevelImpl.class.getDeclaredField("typeMappingFinder");
            field.setAccessible(true);
            return (TypeMappingFinder) field.get(internal);
        }
    }

    public void defaultSchedulerReturnsIOSchedulerIfNotSpecified() {
        StorIOSQLite storIOSQLite = DefaultStorIOSQLite.builder()
                .sqliteOpenHelper(mock(SQLiteOpenHelper.class))
                .build();

        assertThat(storIOSQLite.defaultScheduler()).isSameAs(io());
    }

    @Test
    public void defaultSchedulerReturnsSpecifiedScheduler() {
        Scheduler scheduler = mock(Scheduler.class);
        StorIOSQLite storIOSQLite = DefaultStorIOSQLite.builder()
                .sqliteOpenHelper(mock(SQLiteOpenHelper.class))
                .defaultScheduler(scheduler)
                .build();

        assertThat(storIOSQLite.defaultScheduler()).isSameAs(scheduler);
    }

    @Test
    public void defaultSchedulerReturnsNullIfSpecifiedSchedulerNull() {
        StorIOSQLite storIOSQLite = DefaultStorIOSQLite.builder()
                .sqliteOpenHelper(mock(SQLiteOpenHelper.class))
                .defaultScheduler(null)
                .build();

        assertThat(storIOSQLite.defaultScheduler()).isNull();
    }
}
