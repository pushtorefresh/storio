package com.pushtorefresh.storio.contentresolver.operations.put;

import android.content.ContentValues;

import com.pushtorefresh.storio.StorIOException;
import com.pushtorefresh.storio.contentresolver.StorIOContentResolver;
import com.pushtorefresh.storio.contentresolver.operations.SchedulerChecker;
import com.pushtorefresh.storio.contentresolver.queries.InsertQuery;
import com.pushtorefresh.storio.contentresolver.queries.UpdateQuery;

import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;

import rx.Completable;
import rx.Observable;
import rx.Single;
import rx.observers.TestSubscriber;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.assertj.core.api.Assertions.failBecauseExceptionWasNotThrown;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@RunWith(Enclosed.class)
public class PreparedPutObjectTest {

    public static class WithTypeMapping {

        @Test
        public void shouldPutObjectWithoutTypeMappingBlocking() {
            final PutObjectsStub putStub = PutObjectsStub.newPutStubForOneObjectWithoutTypeMapping();

            final PutResult putResult = putStub.storIOContentResolver
                    .put()
                    .object(putStub.items.get(0))
                    .withPutResolver(putStub.putResolver)
                    .prepare()
                    .executeAsBlocking();

            putStub.verifyBehaviorForOneObject(putResult);
        }

        @Test
        public void shouldPutObjectWithoutTypeMappingAsObservable() {
            final PutObjectsStub putStub = PutObjectsStub.newPutStubForOneObjectWithoutTypeMapping();

            final Observable<PutResult> putResultObservable = putStub.storIOContentResolver
                    .put()
                    .object(putStub.items.get(0))
                    .withPutResolver(putStub.putResolver)
                    .prepare()
                    .asRxObservable();

            putStub.verifyBehaviorForOneObject(putResultObservable);
        }

        @Test
        public void shouldPutObjectWithoutTypeMappingAsSingle() {
            final PutObjectsStub putStub = PutObjectsStub.newPutStubForOneObjectWithoutTypeMapping();

            final Single<PutResult> putResultSingle = putStub.storIOContentResolver
                    .put()
                    .object(putStub.items.get(0))
                    .withPutResolver(putStub.putResolver)
                    .prepare()
                    .asRxSingle();

            putStub.verifyBehaviorForOneObject(putResultSingle);
        }

        @Test
        public void shouldPutObjectWithoutTypeMappingAsCompletable() {
            final PutObjectsStub putStub = PutObjectsStub.newPutStubForOneObjectWithoutTypeMapping();

            final Completable completable = putStub.storIOContentResolver
                    .put()
                    .object(putStub.items.get(0))
                    .withPutResolver(putStub.putResolver)
                    .prepare()
                    .asRxCompletable();

            putStub.verifyBehaviorForOneObject(completable);
        }
    }

    public static class WithoutTypeMapping {

        @Test
        public void shouldPutObjectWithTypeMappingBlocking() {
            final PutObjectsStub putStub = PutObjectsStub.newPutStubForOneObjectWithTypeMapping();

            final PutResult putResult = putStub.storIOContentResolver
                    .put()
                    .object(putStub.items.get(0))
                    .prepare()
                    .executeAsBlocking();

            putStub.verifyBehaviorForOneObject(putResult);
        }

        @Test
        public void shouldPutObjectWithTypeMappingAsObservable() {
            final PutObjectsStub putStub = PutObjectsStub.newPutStubForOneObjectWithTypeMapping();

            final Observable<PutResult> putResultObservable = putStub.storIOContentResolver
                    .put()
                    .object(putStub.items.get(0))
                    .prepare()
                    .asRxObservable();

            putStub.verifyBehaviorForOneObject(putResultObservable);
        }

        @Test
        public void shouldPutObjectWithTypeMappingAsSingle() {
            final PutObjectsStub putStub = PutObjectsStub.newPutStubForOneObjectWithTypeMapping();

            final Single<PutResult> putResultSingle = putStub.storIOContentResolver
                    .put()
                    .object(putStub.items.get(0))
                    .prepare()
                    .asRxSingle();

            putStub.verifyBehaviorForOneObject(putResultSingle);
        }

        @Test
        public void shouldPutObjectWithTypeMappingAsCompletable() {
            final PutObjectsStub putStub = PutObjectsStub.newPutStubForOneObjectWithTypeMapping();

            final Completable completable = putStub.storIOContentResolver
                    .put()
                    .object(putStub.items.get(0))
                    .prepare()
                    .asRxCompletable();

            putStub.verifyBehaviorForOneObject(completable);
        }
    }

    public static class NoTypeMappingError {

        @Test
        public void shouldThrowExceptionIfNoTypeMappingWasFoundWithoutAffectingContentProviderBlocking() {
            final StorIOContentResolver storIOContentResolver = mock(StorIOContentResolver.class);
            final StorIOContentResolver.LowLevel lowLevel = mock(StorIOContentResolver.LowLevel.class);

            when(storIOContentResolver.lowLevel()).thenReturn(lowLevel);

            when(storIOContentResolver.put()).thenReturn(new PreparedPut.Builder(storIOContentResolver));

            final PreparedPut<PutResult> preparedPut = storIOContentResolver
                    .put()
                    .object(TestItem.newInstance())
                    .prepare();

            try {
                preparedPut.executeAsBlocking();
                failBecauseExceptionWasNotThrown(StorIOException.class);
            } catch (StorIOException expected) {
                // it's okay, no type mapping was found
                assertThat(expected).hasCauseInstanceOf(IllegalStateException.class);
            }

            verify(storIOContentResolver).put();
            verify(storIOContentResolver).lowLevel();
            verify(lowLevel).typeMapping(TestItem.class);
            verify(lowLevel, never()).insert(any(InsertQuery.class), any(ContentValues.class));
            verify(lowLevel, never()).update(any(UpdateQuery.class), any(ContentValues.class));
            verifyNoMoreInteractions(storIOContentResolver, lowLevel);
        }

        @Test
        public void shouldThrowExceptionIfNoTypeMappingWasFoundWithoutAffectingContentProviderAsObservable() {
            final StorIOContentResolver storIOContentResolver = mock(StorIOContentResolver.class);
            final StorIOContentResolver.LowLevel lowLevel = mock(StorIOContentResolver.LowLevel.class);

            when(storIOContentResolver.lowLevel()).thenReturn(lowLevel);

            when(storIOContentResolver.put()).thenReturn(new PreparedPut.Builder(storIOContentResolver));

            final TestSubscriber<PutResult> testSubscriber = new TestSubscriber<PutResult>();

            storIOContentResolver
                    .put()
                    .object(TestItem.newInstance())
                    .prepare()
                    .asRxObservable()
                    .subscribe(testSubscriber);

            testSubscriber.awaitTerminalEvent();
            testSubscriber.assertNoValues();
            assertThat(testSubscriber.getOnErrorEvents().get(0))
                    .isInstanceOf(StorIOException.class)
                    .hasCauseInstanceOf(IllegalStateException.class);

            verify(storIOContentResolver).put();
            verify(storIOContentResolver).lowLevel();
            verify(storIOContentResolver).defaultScheduler();
            verify(lowLevel).typeMapping(TestItem.class);
            verify(lowLevel, never()).insert(any(InsertQuery.class), any(ContentValues.class));
            verify(lowLevel, never()).update(any(UpdateQuery.class), any(ContentValues.class));
            verifyNoMoreInteractions(storIOContentResolver, lowLevel);
        }

        @Test
        public void shouldThrowExceptionIfNoTypeMappingWasFoundWithoutAffectingContentProviderAsSingle() {
            final StorIOContentResolver storIOContentResolver = mock(StorIOContentResolver.class);
            final StorIOContentResolver.LowLevel lowLevel = mock(StorIOContentResolver.LowLevel.class);

            when(storIOContentResolver.lowLevel()).thenReturn(lowLevel);

            when(storIOContentResolver.put()).thenReturn(new PreparedPut.Builder(storIOContentResolver));

            final TestSubscriber<PutResult> testSubscriber = new TestSubscriber<PutResult>();

            storIOContentResolver
                    .put()
                    .object(TestItem.newInstance())
                    .prepare()
                    .asRxSingle()
                    .subscribe(testSubscriber);

            testSubscriber.awaitTerminalEvent();
            testSubscriber.assertNoValues();
            assertThat(testSubscriber.getOnErrorEvents().get(0))
                    .isInstanceOf(StorIOException.class)
                    .hasCauseInstanceOf(IllegalStateException.class);

            verify(storIOContentResolver).put();
            verify(storIOContentResolver).lowLevel();
            verify(storIOContentResolver).defaultScheduler();
            verify(lowLevel).typeMapping(TestItem.class);
            verify(lowLevel, never()).insert(any(InsertQuery.class), any(ContentValues.class));
            verify(lowLevel, never()).update(any(UpdateQuery.class), any(ContentValues.class));
            verifyNoMoreInteractions(storIOContentResolver, lowLevel);
        }

        @Test
        public void shouldThrowExceptionIfNoTypeMappingWasFoundWithoutAffectingContentProviderAsCompletable() {
            final StorIOContentResolver storIOContentResolver = mock(StorIOContentResolver.class);
            final StorIOContentResolver.LowLevel lowLevel = mock(StorIOContentResolver.LowLevel.class);

            when(storIOContentResolver.lowLevel()).thenReturn(lowLevel);

            when(storIOContentResolver.put()).thenReturn(new PreparedPut.Builder(storIOContentResolver));

            final TestSubscriber<PutResult> testSubscriber = new TestSubscriber<PutResult>();

            storIOContentResolver
                    .put()
                    .object(TestItem.newInstance())
                    .prepare()
                    .asRxCompletable()
                    .subscribe(testSubscriber);

            testSubscriber.awaitTerminalEvent();
            testSubscriber.assertNoValues();
            assertThat(testSubscriber.getOnErrorEvents().get(0))
                    .isInstanceOf(StorIOException.class)
                    .hasCauseInstanceOf(IllegalStateException.class);

            verify(storIOContentResolver).put();
            verify(storIOContentResolver).lowLevel();
            verify(storIOContentResolver).defaultScheduler();
            verify(lowLevel).typeMapping(TestItem.class);
            verify(lowLevel, never()).insert(any(InsertQuery.class), any(ContentValues.class));
            verify(lowLevel, never()).update(any(UpdateQuery.class), any(ContentValues.class));
            verifyNoMoreInteractions(storIOContentResolver, lowLevel);
        }
    }

    public static class OtherTests {

        @Test
        public void putObjectObservableExecutesOnSpecifiedScheduler() {
            final PutObjectsStub putStub = PutObjectsStub.newPutStubForOneObjectWithoutTypeMapping();
            final SchedulerChecker schedulerChecker = SchedulerChecker.create(putStub.storIOContentResolver);

            final PreparedPutObject<TestItem> operation = putStub.storIOContentResolver
                    .put()
                    .object(putStub.items.get(0))
                    .withPutResolver(putStub.putResolver)
                    .prepare();

            schedulerChecker.checkAsObservable(operation);
        }

        @Test
        public void putObjectSingleExecutesOnSpecifiedScheduler() {
            final PutObjectsStub putStub = PutObjectsStub.newPutStubForOneObjectWithoutTypeMapping();
            final SchedulerChecker schedulerChecker = SchedulerChecker.create(putStub.storIOContentResolver);

            final PreparedPutObject<TestItem> operation = putStub.storIOContentResolver
                    .put()
                    .object(putStub.items.get(0))
                    .withPutResolver(putStub.putResolver)
                    .prepare();

            schedulerChecker.checkAsSingle(operation);
        }

        @Test
        public void putObjectCompletableExecutesOnSpecifiedScheduler() {
            final PutObjectsStub putStub = PutObjectsStub.newPutStubForOneObjectWithoutTypeMapping();
            final SchedulerChecker schedulerChecker = SchedulerChecker.create(putStub.storIOContentResolver);

            final PreparedPutObject<TestItem> operation = putStub.storIOContentResolver
                    .put()
                    .object(putStub.items.get(0))
                    .withPutResolver(putStub.putResolver)
                    .prepare();

            schedulerChecker.checkAsCompletable(operation);
        }
    }
}
