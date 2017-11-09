package com.pushtorefresh.storio2.contentresolver.operations.delete;

import com.pushtorefresh.storio2.StorIOException;
import com.pushtorefresh.storio2.contentresolver.StorIOContentResolver;
import com.pushtorefresh.storio2.contentresolver.operations.SchedulerChecker;
import com.pushtorefresh.storio2.contentresolver.queries.DeleteQuery;

import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Single;
import io.reactivex.observers.TestObserver;
import io.reactivex.subscribers.TestSubscriber;

import static org.assertj.core.api.Assertions.failBecauseExceptionWasNotThrown;
import static org.assertj.core.api.Java6Assertions.assertThat;
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
        public void shouldDeleteObjectWithoutTypeMappingAsFlowable() {
            final DeleteObjectsStub deleteStub = DeleteObjectsStub.newInstanceForDeleteOneObjectWithoutTypeMapping();

            final Flowable<DeleteResult> observable = deleteStub.storIOContentResolver
                    .delete()
                    .object(deleteStub.items.get(0))
                    .withDeleteResolver(deleteStub.deleteResolver)
                    .prepare()
                    .asRxFlowable(BackpressureStrategy.MISSING);

            deleteStub.verifyBehaviorForDeleteOneObject(observable);
        }

        @Test
        public void shouldDeleteObjectWithoutTypeMappingAsSingle() {
            final DeleteObjectsStub deleteStub = DeleteObjectsStub.newInstanceForDeleteOneObjectWithoutTypeMapping();

            final Single<DeleteResult> single = deleteStub.storIOContentResolver
                    .delete()
                    .object(deleteStub.items.get(0))
                    .withDeleteResolver(deleteStub.deleteResolver)
                    .prepare()
                    .asRxSingle();

            deleteStub.verifyBehaviorForDeleteOneObject(single);
        }

        @Test
        public void shouldDeleteObjectWithoutTypeMappingAsCompletable() {
            final DeleteObjectsStub deleteStub = DeleteObjectsStub.newInstanceForDeleteOneObjectWithoutTypeMapping();

            final Completable completable = deleteStub.storIOContentResolver
                    .delete()
                    .object(deleteStub.items.get(0))
                    .withDeleteResolver(deleteStub.deleteResolver)
                    .prepare()
                    .asRxCompletable();

            deleteStub.verifyBehaviorForDeleteOneObject(completable);
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
        public void shouldDeleteObjectWithTypeMappingAsFlowable() {
            final DeleteObjectsStub deleteStub = DeleteObjectsStub.newInstanceForDeleteOneObjectWithTypeMapping();

            final Flowable<DeleteResult> observable = deleteStub.storIOContentResolver
                    .delete()
                    .object(deleteStub.items.get(0))
                    .prepare()
                    .asRxFlowable(BackpressureStrategy.MISSING);

            deleteStub.verifyBehaviorForDeleteOneObject(observable);
        }

        @Test
        public void shouldDeleteObjectWithTypeMappingAsSingle() {
            final DeleteObjectsStub deleteStub = DeleteObjectsStub.newInstanceForDeleteOneObjectWithTypeMapping();

            final Single<DeleteResult> single = deleteStub.storIOContentResolver
                    .delete()
                    .object(deleteStub.items.get(0))
                    .prepare()
                    .asRxSingle();

            deleteStub.verifyBehaviorForDeleteOneObject(single);
        }

        @Test
        public void shouldDeleteObjectWithTypeMappingAsCompletable() {
            final DeleteObjectsStub deleteStub = DeleteObjectsStub.newInstanceForDeleteOneObjectWithTypeMapping();

            final Completable completable = deleteStub.storIOContentResolver
                    .delete()
                    .object(deleteStub.items.get(0))
                    .prepare()
                    .asRxCompletable();

            deleteStub.verifyBehaviorForDeleteOneObject(completable);
        }
    }

    public static class NoTypeMappingError {

        @Test
        public void shouldThrowExceptionIfNoTypeMappingWasFoundWithoutAffectingContentProviderBlocking() {
            final StorIOContentResolver storIOContentResolver = mock(StorIOContentResolver.class);
            final StorIOContentResolver.LowLevel lowLevel = mock(StorIOContentResolver.LowLevel.class);

            when(storIOContentResolver.lowLevel()).thenReturn(lowLevel);

            when(storIOContentResolver.delete()).thenReturn(new PreparedDelete.Builder(storIOContentResolver));

            final PreparedDelete<DeleteResult, TestItem> preparedDelete = storIOContentResolver
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
            verify(storIOContentResolver).lowLevel();
            verify(lowLevel).typeMapping(TestItem.class);
            verify(lowLevel, never()).delete(any(DeleteQuery.class));
            verifyNoMoreInteractions(storIOContentResolver, lowLevel);
        }

        @Test
        public void shouldThrowExceptionIfNoTypeMappingWasFoundWithoutAffectingContentProviderAsFlowable() {
            final StorIOContentResolver storIOContentResolver = mock(StorIOContentResolver.class);
            final StorIOContentResolver.LowLevel lowLevel = mock(StorIOContentResolver.LowLevel.class);

            when(storIOContentResolver.lowLevel()).thenReturn(lowLevel);

            when(storIOContentResolver.delete()).thenReturn(new PreparedDelete.Builder(storIOContentResolver));

            final TestSubscriber<DeleteResult> testSubscriber = new TestSubscriber<DeleteResult>();

            storIOContentResolver
                    .delete()
                    .object(TestItem.newInstance())
                    .prepare()
                    .asRxFlowable(BackpressureStrategy.MISSING)
                    .subscribe(testSubscriber);

            testSubscriber.awaitTerminalEvent();
            testSubscriber.assertNoValues();
            assertThat(testSubscriber.errors().get(0))
                    .isInstanceOf(StorIOException.class)
                    .hasCauseInstanceOf(IllegalStateException.class)
                    .hasMessage("Error has occurred during Delete operation. object = TestItem{data='null'}");

            verify(storIOContentResolver).delete();
            verify(storIOContentResolver).lowLevel();
            verify(storIOContentResolver).defaultRxScheduler();
            verify(lowLevel).typeMapping(TestItem.class);
            verify(lowLevel, never()).delete(any(DeleteQuery.class));
            verifyNoMoreInteractions(storIOContentResolver, lowLevel);
        }

        @Test
        public void shouldThrowExceptionIfNoTypeMappingWasFoundWithoutAffectingContentProviderAsSingle() {
            final StorIOContentResolver storIOContentResolver = mock(StorIOContentResolver.class);
            final StorIOContentResolver.LowLevel lowLevel = mock(StorIOContentResolver.LowLevel.class);

            when(storIOContentResolver.lowLevel()).thenReturn(lowLevel);

            when(storIOContentResolver.delete()).thenReturn(new PreparedDelete.Builder(storIOContentResolver));

            final TestObserver<DeleteResult> testObserver = new TestObserver<DeleteResult>();

            storIOContentResolver
                    .delete()
                    .object(TestItem.newInstance())
                    .prepare()
                    .asRxSingle()
                    .subscribe(testObserver);

            testObserver.awaitTerminalEvent();
            testObserver.assertNoValues();
            assertThat(testObserver.errors().get(0))
                    .isInstanceOf(StorIOException.class)
                    .hasCauseInstanceOf(IllegalStateException.class)
                    .hasMessage("Error has occurred during Delete operation. object = TestItem{data='null'}");

            verify(storIOContentResolver).delete();
            verify(storIOContentResolver).lowLevel();
            verify(storIOContentResolver).defaultRxScheduler();
            verify(lowLevel).typeMapping(TestItem.class);
            verify(lowLevel, never()).delete(any(DeleteQuery.class));
            verifyNoMoreInteractions(storIOContentResolver, lowLevel);
        }

        @Test
        public void shouldThrowExceptionIfNoTypeMappingWasFoundWithoutAffectingContentProviderAsCompletable() {
            final StorIOContentResolver storIOContentResolver = mock(StorIOContentResolver.class);
            final StorIOContentResolver.LowLevel lowLevel = mock(StorIOContentResolver.LowLevel.class);

            when(storIOContentResolver.lowLevel()).thenReturn(lowLevel);

            when(storIOContentResolver.delete()).thenReturn(new PreparedDelete.Builder(storIOContentResolver));

            final TestObserver<DeleteResult> testObserver = new TestObserver<DeleteResult>();

            storIOContentResolver
                    .delete()
                    .object(TestItem.newInstance())
                    .prepare()
                    .asRxCompletable()
                    .subscribe(testObserver);

            testObserver.awaitTerminalEvent();
            testObserver.assertNoValues();
            assertThat(testObserver.errors().get(0))
                    .isInstanceOf(StorIOException.class)
                    .hasCauseInstanceOf(IllegalStateException.class)
                    .hasMessage("Error has occurred during Delete operation. object = TestItem{data='null'}");

            verify(storIOContentResolver).delete();
            verify(storIOContentResolver).lowLevel();
            verify(storIOContentResolver).defaultRxScheduler();
            verify(lowLevel).typeMapping(TestItem.class);
            verify(lowLevel, never()).delete(any(DeleteQuery.class));
            verifyNoMoreInteractions(storIOContentResolver, lowLevel);
        }
    }

    public static class OtherTests {

        @Test
        public void shouldReturnObjectInGetData() {
            final DeleteObjectsStub deleteStub = DeleteObjectsStub.newInstanceForDeleteOneObjectWithoutTypeMapping();

            final PreparedDeleteObject<TestItem> operation = deleteStub.storIOContentResolver
                    .delete()
                    .object(deleteStub.items.get(0))
                    .withDeleteResolver(deleteStub.deleteResolver)
                    .prepare();

            assertThat(operation.getData()).isEqualTo(deleteStub.items.get(0));
        }

        @Test
        public void deleteObjectFlowableExecutesOnSpecifiedScheduler() {
            final DeleteObjectsStub deleteStub = DeleteObjectsStub.newInstanceForDeleteOneObjectWithoutTypeMapping();
            final SchedulerChecker schedulerChecker = SchedulerChecker.create(deleteStub.storIOContentResolver);

            final PreparedDeleteObject<TestItem> operation = deleteStub.storIOContentResolver
                    .delete()
                    .object(deleteStub.items.get(0))
                    .withDeleteResolver(deleteStub.deleteResolver)
                    .prepare();

            schedulerChecker.checkAsFlowable(operation);
        }

        @Test
        public void deleteObjectSingleExecutesOnSpecifiedScheduler() {
            final DeleteObjectsStub deleteStub = DeleteObjectsStub.newInstanceForDeleteOneObjectWithoutTypeMapping();
            final SchedulerChecker schedulerChecker = SchedulerChecker.create(deleteStub.storIOContentResolver);

            final PreparedDeleteObject<TestItem> operation = deleteStub.storIOContentResolver
                    .delete()
                    .object(deleteStub.items.get(0))
                    .withDeleteResolver(deleteStub.deleteResolver)
                    .prepare();

            schedulerChecker.checkAsSingle(operation);
        }

        @Test
        public void deleteObjectCompletableExecutesOnSpecifiedScheduler() {
            final DeleteObjectsStub deleteStub = DeleteObjectsStub.newInstanceForDeleteOneObjectWithoutTypeMapping();
            final SchedulerChecker schedulerChecker = SchedulerChecker.create(deleteStub.storIOContentResolver);

            final PreparedDeleteObject<TestItem> operation = deleteStub.storIOContentResolver
                    .delete()
                    .object(deleteStub.items.get(0))
                    .withDeleteResolver(deleteStub.deleteResolver)
                    .prepare();

            schedulerChecker.checkAsCompletable(operation);
        }
    }
}
