package com.pushtorefresh.storio.contentresolver.operations.delete;

import com.pushtorefresh.storio.StorIOException;
import com.pushtorefresh.storio.contentresolver.StorIOContentResolver;
import com.pushtorefresh.storio.contentresolver.queries.DeleteQuery;

import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;

import rx.Observable;
import rx.observers.TestSubscriber;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.failBecauseExceptionWasNotThrown;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@RunWith(Enclosed.class)
public class PreparedDeleteObjectTest {

    public static class WithoutTypeMapping {

        @Test
        public void shouldDeleteObjectWithoutTypeMappingBlocking() {
            final DeleteObjectsStub deleteStub = DeleteObjectsStub.newInstanceForDeleteOneObjectWithoutTypeMapping();

            final DeleteResult deleteResult = deleteStub.storIOContentResolver
                    .delete()
                    .object(deleteStub.items.get(0))
                    .withDeleteResolver(deleteStub.deleteResolver)
                    .prepare()
                    .executeAsBlocking();

            deleteStub.verifyBehaviorForDeleteOneObject(deleteResult);
        }

        @Test
        public void shouldDeleteObjectWithoutTypeMappingAsObservable() {
            final DeleteObjectsStub deleteStub = DeleteObjectsStub.newInstanceForDeleteOneObjectWithoutTypeMapping();

            final Observable<DeleteResult> observable = deleteStub.storIOContentResolver
                    .delete()
                    .object(deleteStub.items.get(0))
                    .withDeleteResolver(deleteStub.deleteResolver)
                    .prepare()
                    .createObservable();

            deleteStub.verifyBehaviorForDeleteOneObject(observable);
        }
    }

    public static class WithTypeMapping {

        @Test
        public void shouldDeleteObjectWithTypeMappingBlocking() {
            final DeleteObjectsStub deleteStub = DeleteObjectsStub.newInstanceForDeleteOneObjectWithTypeMapping();

            final DeleteResult deleteResult = deleteStub.storIOContentResolver
                    .delete()
                    .object(deleteStub.items.get(0))
                    .prepare()
                    .executeAsBlocking();

            deleteStub.verifyBehaviorForDeleteOneObject(deleteResult);
        }

        @Test
        public void shouldDeleteObjectWithTypeMappingAsObservable() {
            final DeleteObjectsStub deleteStub = DeleteObjectsStub.newInstanceForDeleteOneObjectWithTypeMapping();

            final Observable<DeleteResult> observable = deleteStub.storIOContentResolver
                    .delete()
                    .object(deleteStub.items.get(0))
                    .prepare()
                    .createObservable();

            deleteStub.verifyBehaviorForDeleteOneObject(observable);
        }
    }

    public static class NoTypeMappingError {

        @Test
        public void shouldThrowExceptionIfNoTypeMappingWasFoundWithoutAffectingContentProviderBlocking() {
            final StorIOContentResolver storIOContentResolver = mock(StorIOContentResolver.class);
            final StorIOContentResolver.Internal internal = mock(StorIOContentResolver.Internal.class);

            when(storIOContentResolver.internal()).thenReturn(internal);

            when(storIOContentResolver.delete()).thenReturn(new PreparedDelete.Builder(storIOContentResolver));

            final PreparedDelete<DeleteResult> preparedDelete = storIOContentResolver
                    .delete()
                    .object(TestItem.newInstance())
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

            final TestSubscriber<DeleteResult> testSubscriber = new TestSubscriber<DeleteResult>();

            storIOContentResolver
                    .delete()
                    .object(TestItem.newInstance())
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
    }
}
