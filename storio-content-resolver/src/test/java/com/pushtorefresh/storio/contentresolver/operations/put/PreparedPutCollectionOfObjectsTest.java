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

import java.util.List;

import rx.Completable;
import rx.Observable;
import rx.Single;
import rx.observers.TestSubscriber;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.assertj.core.api.Assertions.failBecauseExceptionWasNotThrown;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@RunWith(Enclosed.class)
public class PreparedPutCollectionOfObjectsTest {

    public static class WithoutTypeMapping {

        @Test
        public void shouldPutObjectsWithoutTypeMappingBlocking() {
            final PutObjectsStub putStub = PutObjectsStub.newPutStubForMultipleObjectsWithoutTypeMapping();

            final PutResults<TestItem> putResults = putStub.storIOContentResolver
                    .put()
                    .objects(putStub.items)
                    .withPutResolver(putStub.putResolver)
                    .prepare()
                    .executeAsBlocking();

            putStub.verifyBehaviorForMultipleObjects(putResults);
        }

        @Test
        public void shouldPutObjectsWithoutTypeMappingAsObservable() {
            final PutObjectsStub putStub = PutObjectsStub.newPutStubForMultipleObjectsWithoutTypeMapping();

            final Observable<PutResults<TestItem>> observable = putStub.storIOContentResolver
                    .put()
                    .objects(putStub.items)
                    .withPutResolver(putStub.putResolver)
                    .prepare()
                    .asRxObservable();

            putStub.verifyBehaviorForMultipleObjects(observable);
        }

        @Test
        public void shouldPutObjectsWithoutTypeMappingAsSingle() {
            final PutObjectsStub putStub = PutObjectsStub.newPutStubForMultipleObjectsWithoutTypeMapping();

            final Single<PutResults<TestItem>> single = putStub.storIOContentResolver
                    .put()
                    .objects(putStub.items)
                    .withPutResolver(putStub.putResolver)
                    .prepare()
                    .asRxSingle();

            putStub.verifyBehaviorForMultipleObjects(single);
        }

        @Test
        public void shouldPutObjectsWithoutTypeMappingAsCompletable() {
            final PutObjectsStub putStub = PutObjectsStub.newPutStubForMultipleObjectsWithoutTypeMapping();

            final Completable completable = putStub.storIOContentResolver
                    .put()
                    .objects(putStub.items)
                    .withPutResolver(putStub.putResolver)
                    .prepare()
                    .asRxCompletable();

            putStub.verifyBehaviorForMultipleObjects(completable);
        }
    }

    public static class WithTypeMapping {

        @Test
        public void shouldPutObjectsWithTypeMappingBlocking() {
            final PutObjectsStub putStub = PutObjectsStub.newPutStubForMultipleObjectsWithTypeMapping();

            final PutResults<TestItem> putResults = putStub.storIOContentResolver
                    .put()
                    .objects(putStub.items)
                    .prepare()
                    .executeAsBlocking();

            putStub.verifyBehaviorForMultipleObjects(putResults);
        }

        @Test
        public void shouldPutObjectsWithTypeMappingAsObservable() {
            final PutObjectsStub putStub = PutObjectsStub.newPutStubForMultipleObjectsWithTypeMapping();

            final Observable<PutResults<TestItem>> observable = putStub.storIOContentResolver
                    .put()
                    .objects(putStub.items)
                    .prepare()
                    .asRxObservable();

            putStub.verifyBehaviorForMultipleObjects(observable);
        }

        @Test
        public void shouldPutObjectsWithTypeMappingAsSingle() {
            final PutObjectsStub putStub = PutObjectsStub.newPutStubForMultipleObjectsWithTypeMapping();

            final Single<PutResults<TestItem>> single = putStub.storIOContentResolver
                    .put()
                    .objects(putStub.items)
                    .prepare()
                    .asRxSingle();

            putStub.verifyBehaviorForMultipleObjects(single);
        }

        @Test
        public void shouldPutObjectsWithTypeMappingAsCompletable() {
            final PutObjectsStub putStub = PutObjectsStub.newPutStubForMultipleObjectsWithTypeMapping();

            final Completable completable = putStub.storIOContentResolver
                    .put()
                    .objects(putStub.items)
                    .prepare()
                    .asRxCompletable();

            putStub.verifyBehaviorForMultipleObjects(completable);
        }
    }

    public static class NoTypeMappingError {

        @Test
        public void shouldThrowExceptionIfNoTypeMappingWasFoundWithoutAffectingContentProviderBlocking() {
            final StorIOContentResolver storIOContentResolver = mock(StorIOContentResolver.class);
            final StorIOContentResolver.LowLevel lowLevel = mock(StorIOContentResolver.LowLevel.class);

            when(storIOContentResolver.lowLevel()).thenReturn(lowLevel);

            when(storIOContentResolver.put()).thenReturn(new PreparedPut.Builder(storIOContentResolver));

            final List<TestItem> items = asList(TestItem.newInstance(), TestItem.newInstance());

            final PreparedPut<PutResults<TestItem>> preparedPut = storIOContentResolver
                    .put()
                    .objects(items)
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

            final List<TestItem> items = asList(TestItem.newInstance(), TestItem.newInstance());

            final TestSubscriber<PutResults<TestItem>> testSubscriber = new TestSubscriber<PutResults<TestItem>>();

            storIOContentResolver
                    .put()
                    .objects(items)
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

            final List<TestItem> items = asList(TestItem.newInstance(), TestItem.newInstance());

            final TestSubscriber<PutResults<TestItem>> testSubscriber = new TestSubscriber<PutResults<TestItem>>();

            storIOContentResolver
                    .put()
                    .objects(items)
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

            final List<TestItem> items = asList(TestItem.newInstance(), TestItem.newInstance());

            final TestSubscriber<PutResults<TestItem>> testSubscriber = new TestSubscriber<PutResults<TestItem>>();

            storIOContentResolver
                    .put()
                    .objects(items)
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
        public void putCollectionOfObjectsObservableExecutesOnSpecifiedScheduler() {
            final PutObjectsStub putStub = PutObjectsStub.newPutStubForMultipleObjectsWithoutTypeMapping();
            final SchedulerChecker schedulerChecker = SchedulerChecker.create(putStub.storIOContentResolver);

            final PreparedPutCollectionOfObjects<TestItem> operation = putStub.storIOContentResolver
                    .put()
                    .objects(putStub.items)
                    .withPutResolver(putStub.putResolver)
                    .prepare();

            schedulerChecker.checkAsObservable(operation);
        }

        @Test
        public void putCollectionOfObjectsSingleExecutesOnSpecifiedScheduler() {
            final PutObjectsStub putStub = PutObjectsStub.newPutStubForMultipleObjectsWithoutTypeMapping();
            final SchedulerChecker schedulerChecker = SchedulerChecker.create(putStub.storIOContentResolver);

            final PreparedPutCollectionOfObjects<TestItem> operation = putStub.storIOContentResolver
                    .put()
                    .objects(putStub.items)
                    .withPutResolver(putStub.putResolver)
                    .prepare();

            schedulerChecker.checkAsSingle(operation);
        }

        @Test
        public void putCollectionOfObjectsCompletableExecutesOnSpecifiedScheduler() {
            final PutObjectsStub putStub = PutObjectsStub.newPutStubForMultipleObjectsWithoutTypeMapping();
            final SchedulerChecker schedulerChecker = SchedulerChecker.create(putStub.storIOContentResolver);

            final PreparedPutCollectionOfObjects<TestItem> operation = putStub.storIOContentResolver
                    .put()
                    .objects(putStub.items)
                    .withPutResolver(putStub.putResolver)
                    .prepare();

            schedulerChecker.checkAsCompletable(operation);
        }
    }
}
