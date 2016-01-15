package com.pushtorefresh.storio.contentresolver.operations.delete;

import com.pushtorefresh.storio.StorIOException;
import com.pushtorefresh.storio.contentresolver.StorIOContentResolver;
import com.pushtorefresh.storio.contentresolver.queries.DeleteQuery;

import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;

import java.util.List;

import rx.Observable;
import rx.Single;
import rx.observers.TestSubscriber;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.failBecauseExceptionWasNotThrown;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@RunWith(Enclosed.class)
public class PreparedDeleteCollectionOfObjectsTest {

    public static class WithoutTypeMapping {

        @Test
        public void shouldDeleteObjectsWithoutTypeMappingBlocking() {
            final DeleteObjectsStub deleteStub = DeleteObjectsStub.newInstanceForDeleteMultipleObjectsWithoutTypeMapping();

            final DeleteResults<TestItem> deleteResults = deleteStub.storIOContentResolver
                    .delete()
                    .objects(deleteStub.items)
                    .withDeleteResolver(deleteStub.deleteResolver)
                    .prepare()
                    .executeAsBlocking();

            deleteStub.verifyBehaviorForDeleteMultipleObjects(deleteResults);
        }

        @Test
        public void shouldDeleteObjectsWithoutTypeMappingAsObservable() {
            final DeleteObjectsStub deleteStub = DeleteObjectsStub.newInstanceForDeleteMultipleObjectsWithoutTypeMapping();

            final Observable<DeleteResults<TestItem>> observable = deleteStub.storIOContentResolver
                    .delete()
                    .objects(deleteStub.items)
                    .withDeleteResolver(deleteStub.deleteResolver)
                    .prepare()
                    .createObservable();

            deleteStub.verifyBehaviorForDeleteMultipleObjects(observable);
        }

        @Test
        public void shouldDeleteObjectsWithoutTypeMappingAsSingle() {
            final DeleteObjectsStub deleteStub = DeleteObjectsStub.newInstanceForDeleteMultipleObjectsWithoutTypeMapping();

            final Single<DeleteResults<TestItem>> single = deleteStub.storIOContentResolver
                    .delete()
                    .objects(deleteStub.items)
                    .withDeleteResolver(deleteStub.deleteResolver)
                    .prepare()
                    .asRxSingle();

            deleteStub.verifyBehaviorForDeleteMultipleObjects(single);
        }
    }

    public static class WithTypeMapping {

        @Test
        public void shouldDeleteObjectsWithTypeMappingBlocking() {
            final DeleteObjectsStub deleteStub = DeleteObjectsStub.newInstanceForDeleteMultipleObjectsWithTypeMapping();

            final DeleteResults<TestItem> deleteResults = deleteStub.storIOContentResolver
                    .delete()
                    .objects(deleteStub.items)
                    .prepare()
                    .executeAsBlocking();

            deleteStub.verifyBehaviorForDeleteMultipleObjects(deleteResults);
        }

        @Test
        public void shouldDeleteObjectsWithTypeMappingAsObservable() {
            final DeleteObjectsStub deleteStub = DeleteObjectsStub.newInstanceForDeleteMultipleObjectsWithTypeMapping();

            final Observable<DeleteResults<TestItem>> observable = deleteStub.storIOContentResolver
                    .delete()
                    .objects(deleteStub.items)
                    .prepare()
                    .createObservable();

            deleteStub.verifyBehaviorForDeleteMultipleObjects(observable);
        }

        @Test
        public void shouldDeleteObjectsWithTypeMappingAsSingle() {
            final DeleteObjectsStub deleteStub = DeleteObjectsStub.newInstanceForDeleteMultipleObjectsWithTypeMapping();

            final Single<DeleteResults<TestItem>> single = deleteStub.storIOContentResolver
                    .delete()
                    .objects(deleteStub.items)
                    .prepare()
                    .asRxSingle();

            deleteStub.verifyBehaviorForDeleteMultipleObjects(single);
        }
    }

    public static class NoTypeMappingError {

        @Test
        public void shouldThrowExceptionIfNoTypeMappingWasFoundWithoutAffectingContentProviderBlocking() {
            final StorIOContentResolver storIOContentResolver = mock(StorIOContentResolver.class);
            final StorIOContentResolver.Internal internal = mock(StorIOContentResolver.Internal.class);

            when(storIOContentResolver.internal()).thenReturn(internal);

            when(storIOContentResolver.delete()).thenReturn(new PreparedDelete.Builder(storIOContentResolver));

            final List<TestItem> items = asList(TestItem.newInstance(), TestItem.newInstance());

            final PreparedDelete<DeleteResults<TestItem>> preparedDelete = storIOContentResolver
                    .delete()
                    .objects(items)
                    .prepare();

            try {
                preparedDelete.executeAsBlocking();
                failBecauseExceptionWasNotThrown(StorIOException.class);
            } catch (StorIOException expected) {
                // it's okay, no type mapping was found
                assertThat(expected).hasCauseInstanceOf(IllegalStateException.class);
            }

            verify(storIOContentResolver).delete();
            verify(storIOContentResolver).internal();
            verify(internal).typeMapping(TestItem.class);
            verify(internal, never()).delete(any(DeleteQuery.class));
            verifyNoMoreInteractions(storIOContentResolver, internal);
        }

        @Test
        public void shouldThrowExceptionIfNoTypeMappingWasFoundWithoutAffectingContentProviderAsObservable() {
            final StorIOContentResolver storIOContentResolver = mock(StorIOContentResolver.class);
            final StorIOContentResolver.Internal internal = mock(StorIOContentResolver.Internal.class);

            when(storIOContentResolver.internal()).thenReturn(internal);

            when(storIOContentResolver.delete()).thenReturn(new PreparedDelete.Builder(storIOContentResolver));

            final List<TestItem> items = asList(TestItem.newInstance(), TestItem.newInstance());

            final TestSubscriber<DeleteResults<TestItem>> testSubscriber = new TestSubscriber<DeleteResults<TestItem>>();

            storIOContentResolver
                    .delete()
                    .objects(items)
                    .prepare()
                    .createObservable()
                    .subscribe(testSubscriber);

            testSubscriber.awaitTerminalEvent();
            testSubscriber.assertNoValues();
            assertThat(testSubscriber.getOnErrorEvents().get(0))
                    .isInstanceOf(StorIOException.class)
                    .hasCauseInstanceOf(IllegalStateException.class);

            verify(storIOContentResolver).delete();
            verify(storIOContentResolver).internal();
            verify(internal).typeMapping(TestItem.class);
            verify(internal, never()).delete(any(DeleteQuery.class));
            verifyNoMoreInteractions(storIOContentResolver, internal);
        }

        @Test
        public void shouldThrowExceptionIfNoTypeMappingWasFoundWithoutAffectingContentProviderAsSingle() {
            final StorIOContentResolver storIOContentResolver = mock(StorIOContentResolver.class);
            final StorIOContentResolver.Internal internal = mock(StorIOContentResolver.Internal.class);

            when(storIOContentResolver.internal()).thenReturn(internal);

            when(storIOContentResolver.delete()).thenReturn(new PreparedDelete.Builder(storIOContentResolver));

            final TestItem testItem = TestItem.newInstance();
            final List<TestItem> items = asList(testItem, TestItem.newInstance());

            final TestSubscriber<DeleteResults<TestItem>> testSubscriber = new TestSubscriber<DeleteResults<TestItem>>();

            storIOContentResolver
                    .delete()
                    .objects(items)
                    .prepare()
                    .asRxSingle()
                    .subscribe(testSubscriber);

            testSubscriber.awaitTerminalEvent();
            testSubscriber.assertNoValues();
            assertThat(testSubscriber.getOnErrorEvents().get(0))
                    .isInstanceOf(StorIOException.class)
                    .hasCauseInstanceOf(IllegalStateException.class)
                    .hasMessage(IllegalStateException.class.getName() + ": One of the objects from the collection does not have type mapping: object = " + testItem.toString()
                            + ", object.class = " + TestItem.class + ",ContentProvider was not affected by this operation, please add type mapping for this type");

            verify(storIOContentResolver).delete();
            verify(storIOContentResolver).internal();
            verify(internal).typeMapping(TestItem.class);
            verify(internal, never()).delete(any(DeleteQuery.class));
            verifyNoMoreInteractions(storIOContentResolver, internal);
        }
    }
}
