package com.pushtorefresh.storio.contentresolver.impl;

import android.content.ContentResolver;
import android.net.Uri;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.pushtorefresh.storio.TypeMappingFinder;
import com.pushtorefresh.storio.contentresolver.BuildConfig;
import com.pushtorefresh.storio.contentresolver.ContentResolverTypeMapping;
import com.pushtorefresh.storio.contentresolver.StorIOContentResolver;
import com.pushtorefresh.storio.contentresolver.operations.delete.DeleteResolver;
import com.pushtorefresh.storio.contentresolver.operations.get.GetResolver;
import com.pushtorefresh.storio.contentresolver.operations.put.PutResolver;
import com.pushtorefresh.storio.contentresolver.queries.Query;
import com.pushtorefresh.storio.internal.TypeMappingFinderImpl;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.lang.reflect.Field;

import rx.Scheduler;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static rx.schedulers.Schedulers.io;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)
public class DefaultStorIOContentResolverTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void nullContentResolver() {
        DefaultStorIOContentResolver.Builder builder = DefaultStorIOContentResolver.builder();

        expectedException.expect(NullPointerException.class);
        expectedException.expectMessage("Please specify content resolver");
        expectedException.expectCause(nullValue(Throwable.class));

        //noinspection ConstantConditions
        builder.contentResolver(null);
    }

    @Test
    public void addTypeMappingNullType() {
        DefaultStorIOContentResolver.CompleteBuilder builder = DefaultStorIOContentResolver.builder()
                .contentResolver(mock(ContentResolver.class));

        expectedException.expect(NullPointerException.class);
        expectedException.expectMessage("Please specify type");
        expectedException.expectCause(nullValue(Throwable.class));

        //noinspection ConstantConditions,unchecked
        builder.addTypeMapping(null, ContentResolverTypeMapping.builder()
                        .putResolver(mock(PutResolver.class))
                        .getResolver(mock(GetResolver.class))
                        .deleteResolver(mock(DeleteResolver.class))
                        .build());
    }

    @Test
    public void addTypeMappingNullMapping() {
        DefaultStorIOContentResolver.CompleteBuilder builder = DefaultStorIOContentResolver.builder()
                .contentResolver(mock(ContentResolver.class));

        expectedException.expect(NullPointerException.class);
        expectedException.expectMessage("Please specify type mapping");
        expectedException.expectCause(nullValue(Throwable.class));

        //noinspection ConstantConditions
        builder.addTypeMapping(Object.class, null);
    }

    @Test
    public void nullTypeMappingFinder() {
        DefaultStorIOContentResolver.CompleteBuilder builder = DefaultStorIOContentResolver.builder()
                .contentResolver(mock(ContentResolver.class));

        expectedException.expect(NullPointerException.class);
        expectedException.expectMessage("Please specify typeMappingFinder");
        expectedException.expectCause(nullValue(Throwable.class));

        //noinspection ConstantConditions
        builder.typeMappingFinder(null);
    }

    @Test
    public void shouldUseSpecifiedTypeMappingFinder() throws NoSuchFieldException, IllegalAccessException {
        TypeMappingFinder typeMappingFinder = mock(TypeMappingFinder.class);
        DefaultStorIOContentResolver storIOContentResolver = DefaultStorIOContentResolver.builder()
                .contentResolver(mock(ContentResolver.class))
                .typeMappingFinder(typeMappingFinder)
                .build();

        assertThat(getTypeMappingFinder(storIOContentResolver)).isEqualTo(typeMappingFinder);
    }

    @Test
    public void typeMappingShouldWorkWithoutSpecifiedTypeMappingFinder() {
        //noinspection unchecked
        ContentResolverTypeMapping<ClassEntity> typeMapping = ContentResolverTypeMapping.builder()
                .putResolver(mock(PutResolver.class))
                .getResolver(mock(GetResolver.class))
                .deleteResolver(mock(DeleteResolver.class))
                .build();

        StorIOContentResolver storIOContentResolver = DefaultStorIOContentResolver.builder()
                .contentResolver(mock(ContentResolver.class))
                .addTypeMapping(ClassEntity.class, typeMapping)
                .build();

        assertThat(storIOContentResolver.lowLevel().typeMapping(ClassEntity.class)).isEqualTo(typeMapping);
    }


    @Test
    public void typeMappingShouldWorkWithSpecifiedTypeMappingFinder() {
        TypeMappingFinder typeMappingFinder = new TypeMappingFinderImpl();

        //noinspection unchecked
        ContentResolverTypeMapping<ClassEntity> typeMapping = ContentResolverTypeMapping.builder()
                .putResolver(mock(PutResolver.class))
                .getResolver(mock(GetResolver.class))
                .deleteResolver(mock(DeleteResolver.class))
                .build();

        StorIOContentResolver storIOContentResolver = DefaultStorIOContentResolver.builder()
                .contentResolver(mock(ContentResolver.class))
                .typeMappingFinder(typeMappingFinder)
                .addTypeMapping(ClassEntity.class, typeMapping)
                .build();

        assertThat(storIOContentResolver.lowLevel().typeMapping(ClassEntity.class)).isEqualTo(typeMapping);
    }

    @Test
    public void typeMappingShouldWorkForMultipleTypes() {
        class AnotherEntity {
        }

        TypeMappingFinder typeMappingFinder = new TypeMappingFinderImpl();

        //noinspection unchecked
        ContentResolverTypeMapping<ClassEntity> typeMapping = ContentResolverTypeMapping.builder()
                .putResolver(mock(PutResolver.class))
                .getResolver(mock(GetResolver.class))
                .deleteResolver(mock(DeleteResolver.class))
                .build();

        //noinspection unchecked
        ContentResolverTypeMapping<AnotherEntity> anotherMapping = ContentResolverTypeMapping.builder()
                .putResolver(mock(PutResolver.class))
                .getResolver(mock(GetResolver.class))
                .deleteResolver(mock(DeleteResolver.class))
                .build();

        StorIOContentResolver storIOContentResolver = DefaultStorIOContentResolver.builder()
                .contentResolver(mock(ContentResolver.class))
                .typeMappingFinder(typeMappingFinder)
                .addTypeMapping(ClassEntity.class, typeMapping)
                .addTypeMapping(AnotherEntity.class, anotherMapping)
                .build();

        assertThat(storIOContentResolver.lowLevel().typeMapping(ClassEntity.class)).isEqualTo(typeMapping);
        assertThat(storIOContentResolver.lowLevel().typeMapping(AnotherEntity.class)).isEqualTo(anotherMapping);
    }

    @Test
    public void shouldThrowExceptionIfContentResolverReturnsNull() {
        ContentResolver contentResolver = mock(ContentResolver.class);

        StorIOContentResolver storIOContentResolver = DefaultStorIOContentResolver.builder()
                .contentResolver(contentResolver)
                .build();

        Query query = Query.builder()
                .uri(mock(Uri.class))
                .build();

        when(contentResolver
                .query(any(Uri.class), any(String[].class), anyString(), any(String[].class), anyString()))
                .thenReturn(null); // Notice, we return null instead of Cursor

        try {
            storIOContentResolver
                    .lowLevel()
                    .query(query);
        } catch (IllegalStateException expected) {
            assertThat(expected).hasMessage("Cursor returned by content provider is null");
        }
    }

    @Test
    public void shouldReturnSameContentResolver() {
        ContentResolver contentResolver = mock(ContentResolver.class);

        StorIOContentResolver storIOContentResolver = DefaultStorIOContentResolver.builder()
                .contentResolver(contentResolver)
                .build();

        assertThat(storIOContentResolver.lowLevel().contentResolver()).isSameAs(contentResolver);
    }

    @Test
    public void deprecatedInternalImplShouldReturnSentToConstructorTypeMapping() throws NoSuchFieldException, IllegalAccessException {
        ContentResolver contentResolver = mock(ContentResolver.class);
        TypeMappingFinder typeMappingFinder = mock(TypeMappingFinder.class);

        TestDefaultStorIOContentResolver storIOContentResolver = new TestDefaultStorIOContentResolver(
                contentResolver,
                mock(Handler.class),
                typeMappingFinder
        );

        assertThat(storIOContentResolver.typeMappingFinder()).isSameAs(typeMappingFinder);
    }

    static class ClassEntity {
    }

    @Nullable
    private static TypeMappingFinder getTypeMappingFinder(@NonNull DefaultStorIOContentResolver storIOContentResolver)
            throws NoSuchFieldException, IllegalAccessException {

        Field field = DefaultStorIOContentResolver.LowLevelImpl.class.getDeclaredField("typeMappingFinder");
        field.setAccessible(true);
        return (TypeMappingFinder) field.get(storIOContentResolver.lowLevel());
    }

    class TestDefaultStorIOContentResolver extends DefaultStorIOContentResolver {

        private final Internal internal;

        public TestDefaultStorIOContentResolver(
                @NonNull ContentResolver contentResolver,
                @NonNull Handler contentObserverHandler,
                @NonNull TypeMappingFinder typeMappingFinder
        ) {
            super(contentResolver, contentObserverHandler, typeMappingFinder, null);
            internal = new InternalImpl(typeMappingFinder);
        }

        @Nullable
        public TypeMappingFinder typeMappingFinder() throws NoSuchFieldException, IllegalAccessException {
            Field field = DefaultStorIOContentResolver.LowLevelImpl.class.getDeclaredField("typeMappingFinder");
            field.setAccessible(true);
            return (TypeMappingFinder) field.get(internal);
        }
    }

    @Test
    public void defaultSchedulerReturnsIOSchedulerIfNotSpecified() {
        StorIOContentResolver storIOContentResolver = DefaultStorIOContentResolver.builder()
                .contentResolver(mock(ContentResolver.class))
                .build();

        assertThat(storIOContentResolver.defaultScheduler()).isSameAs(io());
    }

    @Test
    public void defaultSchedulerReturnsSpecifiedScheduler() {
        Scheduler scheduler = mock(Scheduler.class);
        StorIOContentResolver storIOContentResolver = DefaultStorIOContentResolver.builder()
                .contentResolver(mock(ContentResolver.class))
                .defaultScheduler(scheduler)
                .build();

        assertThat(storIOContentResolver.defaultScheduler()).isSameAs(scheduler);
    }

    @Test
    public void defaultSchedulerReturnsNullIfSpecifiedSchedulerNull() {
        StorIOContentResolver storIOContentResolver = DefaultStorIOContentResolver.builder()
                .contentResolver(mock(ContentResolver.class))
                .defaultScheduler(null)
                .build();

        assertThat(storIOContentResolver.defaultScheduler()).isNull();
    }
}
