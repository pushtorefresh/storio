package com.pushtorefresh.storio2.sqlite.impl;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.NonNull;

import com.pushtorefresh.storio2.TypeMappingFinder;
import com.pushtorefresh.storio2.internal.ChangesBus;
import com.pushtorefresh.storio2.internal.TypeMappingFinderImpl;
import com.pushtorefresh.storio2.sqlite.Changes;
import com.pushtorefresh.storio2.sqlite.Interceptor;
import com.pushtorefresh.storio2.sqlite.SQLiteTypeMapping;
import com.pushtorefresh.storio2.sqlite.StorIOSQLite;
import com.pushtorefresh.storio2.sqlite.operations.delete.DeleteResolver;
import com.pushtorefresh.storio2.sqlite.operations.get.GetResolver;
import com.pushtorefresh.storio2.sqlite.operations.put.PutResolver;
import com.pushtorefresh.storio2.sqlite.queries.InsertQuery;
import com.pushtorefresh.storio2.sqlite.queries.RawQuery;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.annotation.Nullable;

import io.reactivex.Scheduler;
import io.reactivex.subscribers.TestSubscriber;

import static io.reactivex.BackpressureStrategy.LATEST;
import static io.reactivex.schedulers.Schedulers.io;
import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.same;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

public class DefaultStorIOSQLiteTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Mock
    @NonNull
    private SQLiteOpenHelper sqLiteOpenHelper;

    @Mock
    @NonNull
    private SQLiteDatabase sqLiteDatabase;

    @NonNull
    private DefaultStorIOSQLite storIOSQLite;

    @Before
    public void beforeEachTest() {
        MockitoAnnotations.initMocks(this);

        when(sqLiteOpenHelper.getWritableDatabase()).thenReturn(sqLiteDatabase);

        storIOSQLite = DefaultStorIOSQLite.builder()
                .sqliteOpenHelper(sqLiteOpenHelper)
                .build();
    }

    @Test
    public void nullSQLiteOpenHelper() {
        DefaultStorIOSQLite.Builder builder = DefaultStorIOSQLite.builder();

        expectedException.expect(NullPointerException.class);
        expectedException.expectMessage("Please specify SQLiteOpenHelper instance");
        expectedException.expectCause(nullValue(Throwable.class));

        //noinspection ConstantConditions
        builder.sqliteOpenHelper(null);
    }

    @Test
    public void lowLevelReturnsSameInstanceOfSQLiteOpenHelper() {
        assertThat(storIOSQLite.lowLevel().sqliteOpenHelper()).isSameAs(sqLiteOpenHelper);
    }

    @Test
    public void addTypeMappingNullType() {
        DefaultStorIOSQLite.CompleteBuilder builder = DefaultStorIOSQLite.builder()
                .sqliteOpenHelper(sqLiteOpenHelper);

        expectedException.expect(NullPointerException.class);
        expectedException.expectMessage("Please specify type");
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
                .sqliteOpenHelper(sqLiteOpenHelper);

        expectedException.expect(NullPointerException.class);
        expectedException.expectMessage("Please specify type mapping");
        expectedException.expectCause(nullValue(Throwable.class));

        //noinspection ConstantConditions
        builder.addTypeMapping(Object.class, null);
    }

    @Test
    public void nullTypeMappingFinder() {
        DefaultStorIOSQLite.CompleteBuilder builder = DefaultStorIOSQLite.builder()
                .sqliteOpenHelper(sqLiteOpenHelper);

        expectedException.expect(NullPointerException.class);
        expectedException.expectMessage("Please specify typeMappingFinder");
        expectedException.expectCause(nullValue(Throwable.class));

        //noinspection ConstantConditions
        builder.typeMappingFinder(null);
    }

    @Test
    public void shouldUseSpecifiedTypeMappingFinder() throws NoSuchFieldException, IllegalAccessException {
        TypeMappingFinder typeMappingFinder = mock(TypeMappingFinder.class);
        DefaultStorIOSQLite storIOSQLite = DefaultStorIOSQLite.builder()
                .sqliteOpenHelper(sqLiteOpenHelper)
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
                .sqliteOpenHelper(sqLiteOpenHelper)
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
                .sqliteOpenHelper(sqLiteOpenHelper)
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
                .sqliteOpenHelper(sqLiteOpenHelper)
                .addTypeMapping(ClassEntity.class, entityMapping)
                .addTypeMapping(AnotherEntity.class, anotherMapping)
                .build();

        assertThat(storIOSQLite.lowLevel().typeMapping(ClassEntity.class)).isEqualTo(entityMapping);
        assertThat(storIOSQLite.lowLevel().typeMapping(AnotherEntity.class)).isEqualTo(anotherMapping);
    }

    @Test
    public void shouldCloseSQLiteOpenHelper() throws IOException {
        // Should not call close before explicit call to close
        verify(sqLiteOpenHelper, never()).close();

        storIOSQLite.close();

        // Should call close on SQLiteOpenHelper
        verify(sqLiteOpenHelper).close();
    }

    @Test
    public void shouldPassSQLWithArgsToExecSQL() {
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
        StorIOSQLite.LowLevel lowLevel = storIOSQLite.lowLevel();
        assertThat(lowLevel).isNotNull();

        expectedException.expect(NullPointerException.class);
        expectedException.expectMessage("Changes can not be null");
        expectedException.expectCause(nullValue(Throwable.class));

        //noinspection ConstantConditions
        lowLevel.notifyAboutChanges(null);
    }

    @Test
    public void observeChangesAndNotifyAboutChangesShouldWorkCorrectly() {
        TestSubscriber<Changes> testSubscriber = new TestSubscriber<Changes>();

        storIOSQLite
                .observeChanges(LATEST)
                .subscribe(testSubscriber);

        testSubscriber.assertNoValues();

        Changes changes = Changes.newInstance("test_table", "tag");

        storIOSQLite
                .lowLevel()
                .notifyAboutChanges(changes);

        testSubscriber.assertValue(changes);
        testSubscriber.assertNoErrors();
        testSubscriber.dispose();
    }

    @Test
    public void observeChangesShouldThrowIfRxJavaNotInClassPath() throws NoSuchFieldException, IllegalAccessException {
        //noinspection unchecked
        ChangesBus<Changes> changesBus = mock(ChangesBus.class);
        setChangesBus(storIOSQLite, changesBus);

        expectedException.expect(IllegalStateException.class);
        expectedException.expectMessage("Observing changes in StorIOSQLite requires RxJava");
        expectedException.expectCause(nullValue(Throwable.class));

        storIOSQLite.observeChanges(LATEST);
    }

    @Test
    public void observeChangesInTablesShouldNotAcceptNullAsTables() {
        expectedException.expect(NullPointerException.class);
        expectedException.expectMessage("Set of tables can not be null");
        expectedException.expectCause(nullValue(Throwable.class));

        //noinspection ConstantConditions
        storIOSQLite.observeChangesInTables(null, LATEST);
    }

    @Test
    public void observeChangesOfTagsShouldNotAcceptNullAsTags() {
        expectedException.expect(NullPointerException.class);
        expectedException.expectMessage("Set of tags can not be null");
        expectedException.expectCause(nullValue(Throwable.class));

        //noinspection ConstantConditions
        storIOSQLite.observeChangesOfTags(null, LATEST);
    }

    @Test
    public void observeChangesInTables_shouldReceiveIfTableWasChanged() {
        TestSubscriber<Changes> testSubscriber = new TestSubscriber<Changes>();

        Set<String> tables = new HashSet<String>(2);
        tables.add("table1");
        tables.add("table2");

        storIOSQLite
                .observeChangesInTables(tables, LATEST)
                .subscribe(testSubscriber);

        testSubscriber.assertNoValues();

        Changes changes = Changes.newInstance("table2");

        storIOSQLite
                .lowLevel()
                .notifyAboutChanges(changes);

        testSubscriber.assertValues(changes);
        testSubscriber.assertNoErrors();
        testSubscriber.dispose();
    }

    @Test
    public void observeChangesInTables_shouldNotReceiveIfTableWasNotChanged() {
        TestSubscriber<Changes> testSubscriber = new TestSubscriber<Changes>();

        Set<String> tables = new HashSet<String>(2);
        tables.add("table1");
        tables.add("table2");

        storIOSQLite
                .observeChangesInTables(tables, LATEST)
                .subscribe(testSubscriber);

        storIOSQLite
                .lowLevel()
                .notifyAboutChanges(Changes.newInstance("table3"));

        testSubscriber.assertNoValues();
        testSubscriber.assertNoErrors();
        testSubscriber.dispose();
    }

    @Test
    public void observeChangesOfTags_shouldReceiveIfObservedTagExistInChanges() {
        TestSubscriber<Changes> testSubscriber = new TestSubscriber<Changes>();

        String tag1 = "tag1";
        String tag2 = "tag2";
        Set<String> tags = new HashSet<String>(2);
        tags.add(tag1);
        tags.add(tag2);

        storIOSQLite
                .observeChangesOfTags(tags, LATEST)
                .subscribe(testSubscriber);

        testSubscriber.assertNoValues();

        Changes changes = Changes.newInstance("table1", tag1);

        storIOSQLite
                .lowLevel()
                .notifyAboutChanges(changes);

        testSubscriber.assertValues(changes);
        testSubscriber.assertNoErrors();
        testSubscriber.dispose();
    }

    @Test
    public void observeChangesOfTags_shouldNotReceiveIfObservedTagDoesNotExistInChanges() {
        TestSubscriber<Changes> testSubscriber = new TestSubscriber<Changes>();

        Set<String> tags = new HashSet<String>(2);
        tags.add("tag1");
        tags.add("tag2");

        storIOSQLite
                .observeChangesOfTags(tags, LATEST)
                .subscribe(testSubscriber);

        storIOSQLite
                .lowLevel()
                .notifyAboutChanges(Changes.newInstance("table3", "tag3"));

        testSubscriber.assertNoValues();
        testSubscriber.assertNoErrors();
        testSubscriber.dispose();
    }

    @Test
    public void observeChangesInTable_shouldNotAcceptNullAsTable() {
        expectedException.expect(NullPointerException.class);
        expectedException.expectMessage("Table can not be null or empty");
        expectedException.expectCause(nullValue(Throwable.class));

        //noinspection ConstantConditions
        storIOSQLite.observeChangesInTable(null, LATEST);
    }

    @Test
    public void observeChangeOfTag_shouldNotAcceptNullAsTag() {
        expectedException.expect(NullPointerException.class);
        expectedException.expectMessage("Tag can not be null or empty");
        expectedException.expectCause(nullValue(Throwable.class));

        //noinspection ConstantConditions
        storIOSQLite.observeChangesOfTag(null, LATEST);
    }

    @Test
    public void observeChangeOfTag_shouldNotAcceptEmptyTag() {
        expectedException.expect(IllegalStateException.class);
        expectedException.expectMessage("Tag can not be null or empty");
        expectedException.expectCause(nullValue(Throwable.class));

        storIOSQLite.observeChangesOfTag("", LATEST);
    }

    @Test
    public void observeChangesInTable() {
        TestSubscriber<Changes> testSubscriber = new TestSubscriber<Changes>();

        storIOSQLite
                .observeChangesInTable("table1", LATEST)
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
        testSubscriber.dispose();
    }

    @Test
    public void deprecatedInternalImplShouldReturnSentToConstructorTypeMapping() throws NoSuchFieldException, IllegalAccessException {
        TypeMappingFinder typeMappingFinder = mock(TypeMappingFinder.class);

        TestDefaultStorIOSQLite storIOSQLite =
                new TestDefaultStorIOSQLite(sqLiteOpenHelper, typeMappingFinder);

        assertThat(storIOSQLite.typeMappingFinder()).isSameAs(typeMappingFinder);
    }

    @Test
    public void defaultSchedulerReturnsIOSchedulerIfNotSpecified() {
        assertThat(storIOSQLite.defaultRxScheduler()).isSameAs(io());
    }

    @Test
    public void defaultSchedulerReturnsSpecifiedScheduler() {
        Scheduler scheduler = mock(Scheduler.class);
        StorIOSQLite storIOSQLite = DefaultStorIOSQLite.builder()
                .sqliteOpenHelper(sqLiteOpenHelper)
                .defaultRxScheduler(scheduler)
                .build();

        assertThat(storIOSQLite.defaultRxScheduler()).isSameAs(scheduler);
    }

    @Test
    public void defaultSchedulerReturnsNullIfSpecifiedSchedulerNull() {
        StorIOSQLite storIOSQLite = DefaultStorIOSQLite.builder()
                .sqliteOpenHelper(sqLiteOpenHelper)
                .defaultRxScheduler(null)
                .build();

        assertThat(storIOSQLite.defaultRxScheduler()).isNull();
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
        private final LowLevel lowLevel;

        TestDefaultStorIOSQLite(@NonNull SQLiteOpenHelper sqLiteOpenHelper, @NonNull TypeMappingFinder typeMappingFinder) {
            super(sqLiteOpenHelper, typeMappingFinder, null, Collections.<Interceptor>emptyList());
            lowLevel = new LowLevelImpl(typeMappingFinder);
        }

        @Nullable
        TypeMappingFinder typeMappingFinder() throws NoSuchFieldException, IllegalAccessException {
            Field field = TestDefaultStorIOSQLite.LowLevelImpl.class.getDeclaredField("typeMappingFinder");
            field.setAccessible(true);
            return (TypeMappingFinder) field.get(lowLevel);
        }
    }
}
