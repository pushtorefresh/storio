package com.pushtorefresh.storio.sqlite.operations.put;

import com.pushtorefresh.storio.StorIOException;
import com.pushtorefresh.storio.sqlite.StorIOSQLite;
import com.pushtorefresh.storio.sqlite.operations.SchedulerChecker;

import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;

import rx.Completable;
import rx.Observable;
import rx.Single;
import rx.observers.TestSubscriber;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.assertj.core.api.Assertions.failBecauseExceptionWasNotThrown;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@RunWith(Enclosed.class)
public class PreparedPutObjectTest {

    public static class WithoutTypeMapping {

        @Test
        public void shouldPutObjectWithoutTypeMappingBlocking() {
            final PutObjectsStub putStub = PutObjectsStub.newPutStubForOneObjectWithoutTypeMapping();

            final PutResult putResult = putStub.storIOSQLite
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

            final Observable<PutResult> putResultObservable = putStub.storIOSQLite
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

            final Single<PutResult> putResultSingle = putStub.storIOSQLite
                    .put()
                    .object(putStub.items.get(0))
                    .withPutResolver(putStub.putResolver)
                    .prepare()
                    .asRxSingle();

            putStub.verifyBehaviorForOneObject(putResultSingle);
        }

        @Test
        public void shouldPutObjectWithoutTypeMappingAsCompetable() {
            final PutObjectsStub putStub = PutObjectsStub.newPutStubForOneObjectWithoutTypeMapping();

            final Completable completable = putStub.storIOSQLite
                    .put()
                    .object(putStub.items.get(0))
                    .withPutResolver(putStub.putResolver)
                    .prepare()
                    .asRxCompletable();

            putStub.verifyBehaviorForOneObject(completable);
        }
    }

    public static class WithTypeMapping {

        @Test
        public void shouldPutObjectWithTypeMappingBlocking() {
            final PutObjectsStub putStub = PutObjectsStub.newPutStubForOneObjectWithTypeMapping();

            final PutResult putResult = putStub.storIOSQLite
                    .put()
                    .object(putStub.items.get(0))
                    .prepare()
                    .executeAsBlocking();

            putStub.verifyBehaviorForOneObject(putResult);
        }

        @Test
        public void shouldPutObjectWithTypeMappingAsObservable() {
            final PutObjectsStub putStub = PutObjectsStub.newPutStubForOneObjectWithTypeMapping();

            final Observable<PutResult> putResultObservable = putStub.storIOSQLite
                    .put()
                    .object(putStub.items.get(0))
                    .prepare()
                    .asRxObservable();

            putStub.verifyBehaviorForOneObject(putResultObservable);
        }

        @Test
        public void shouldPutObjectWithTypeMappingAsSingle() {
            final PutObjectsStub putStub = PutObjectsStub.newPutStubForOneObjectWithTypeMapping();

            final Single<PutResult> putResultSingle = putStub.storIOSQLite
                    .put()
                    .object(putStub.items.get(0))
                    .prepare()
                    .asRxSingle();

            putStub.verifyBehaviorForOneObject(putResultSingle);
        }

        @Test
        public void shouldPutObjectWithTypeMappingAsCompletable() {
            final PutObjectsStub putStub = PutObjectsStub.newPutStubForOneObjectWithTypeMapping();

            final Completable completable = putStub.storIOSQLite
                    .put()
                    .object(putStub.items.get(0))
                    .prepare()
                    .asRxCompletable();

            putStub.verifyBehaviorForOneObject(completable);
        }
    }

    public static class TypeMappingError {

        @Test
        public void shouldThrowExceptionIfNoTypeMappingWasFoundWithoutAffectingDbBlocking() {
            final StorIOSQLite storIOSQLite = mock(StorIOSQLite.class);
            final StorIOSQLite.Internal internal = mock(StorIOSQLite.Internal.class);

            when(storIOSQLite.lowLevel()).thenReturn(internal);

            final Object object = new Object();

            try {
                new PreparedPutObject.Builder<Object>(storIOSQLite, object)
                        .prepare()
                        .executeAsBlocking();
                failBecauseExceptionWasNotThrown(StorIOException.class);
            } catch (StorIOException expected) {
                IllegalStateException cause = (IllegalStateException) expected.getCause();

                assertThat(cause).hasMessage("Object does not have type mapping: " +
                                "object = " + object + ", object.class = " + object.getClass() + ", " +
                                "db was not affected by this operation, please add type mapping for this type");

                verify(storIOSQLite).lowLevel();
                verify(internal).typeMapping(Object.class);
                verifyNoMoreInteractions(storIOSQLite, internal);
            }
        }

        @Test
        public void shouldThrowExceptionIfNoTypeMappingWasFoundWithoutAffectingDbObservable() {
            final StorIOSQLite storIOSQLite = mock(StorIOSQLite.class);
            final StorIOSQLite.Internal internal = mock(StorIOSQLite.Internal.class);

            when(storIOSQLite.lowLevel()).thenReturn(internal);

            final Object object = new Object();

            final TestSubscriber<PutResult> testSubscriber = new TestSubscriber<PutResult>();

            new PreparedPutObject.Builder<Object>(storIOSQLite, object)
                    .prepare()
                    .asRxObservable()
                    .subscribe(testSubscriber);

            testSubscriber.awaitTerminalEvent();
            testSubscriber.assertNoValues();
            testSubscriber.assertError(StorIOException.class);

            //noinspection ThrowableResultOfMethodCallIgnored
            StorIOException expected = (StorIOException) testSubscriber.getOnErrorEvents().get(0);

            IllegalStateException cause = (IllegalStateException) expected.getCause();

            assertThat(cause).hasMessage("Object does not have type mapping: " +
                            "object = " + object + ", object.class = " + object.getClass() + ", " +
                            "db was not affected by this operation, please add type mapping for this type");

            verify(storIOSQLite).lowLevel();
            verify(storIOSQLite).defaultScheduler();
            verify(internal).typeMapping(Object.class);
            verifyNoMoreInteractions(storIOSQLite, internal);
        }

        @Test
        public void shouldThrowExceptionIfNoTypeMappingWasFoundWithoutAffectingDbSingle() {
            final StorIOSQLite storIOSQLite = mock(StorIOSQLite.class);
            final StorIOSQLite.Internal internal = mock(StorIOSQLite.Internal.class);

            when(storIOSQLite.lowLevel()).thenReturn(internal);

            final Object object = new Object();

            final TestSubscriber<PutResult> testSubscriber = new TestSubscriber<PutResult>();

            new PreparedPutObject.Builder<Object>(storIOSQLite, object)
                    .prepare()
                    .asRxSingle()
                    .subscribe(testSubscriber);

            testSubscriber.awaitTerminalEvent();
            testSubscriber.assertNoValues();
            testSubscriber.assertError(StorIOException.class);

            //noinspection ThrowableResultOfMethodCallIgnored
            StorIOException expected = (StorIOException) testSubscriber.getOnErrorEvents().get(0);

            IllegalStateException cause = (IllegalStateException) expected.getCause();

            assertThat(cause).hasMessage("Object does not have type mapping: " +
                    "object = " + object + ", object.class = " + object.getClass() + ", " +
                    "db was not affected by this operation, please add type mapping for this type");

            verify(storIOSQLite).lowLevel();
            verify(storIOSQLite).defaultScheduler();
            verify(internal).typeMapping(Object.class);
            verifyNoMoreInteractions(storIOSQLite, internal);
        }

        @Test
        public void shouldThrowExceptionIfNoTypeMappingWasFoundWithoutAffectingDbCompletable() {
            final StorIOSQLite storIOSQLite = mock(StorIOSQLite.class);
            final StorIOSQLite.Internal internal = mock(StorIOSQLite.Internal.class);

            when(storIOSQLite.lowLevel()).thenReturn(internal);

            final Object object = new Object();

            final TestSubscriber<PutResult> testSubscriber = new TestSubscriber<PutResult>();

            new PreparedPutObject.Builder<Object>(storIOSQLite, object)
                    .prepare()
                    .asRxCompletable()
                    .subscribe(testSubscriber);

            testSubscriber.awaitTerminalEvent();
            testSubscriber.assertNoValues();
            testSubscriber.assertError(StorIOException.class);

            //noinspection ThrowableResultOfMethodCallIgnored
            StorIOException expected = (StorIOException) testSubscriber.getOnErrorEvents().get(0);

            IllegalStateException cause = (IllegalStateException) expected.getCause();

            assertThat(cause).hasMessage("Object does not have type mapping: " +
                    "object = " + object + ", object.class = " + object.getClass() + ", " +
                    "db was not affected by this operation, please add type mapping for this type");

            verify(storIOSQLite).lowLevel();
            verify(storIOSQLite).defaultScheduler();
            verify(internal).typeMapping(Object.class);
            verifyNoMoreInteractions(storIOSQLite, internal);
        }
    }

    public static class OtherTests {

        @Test
        public void putObjectObservableExecutesOnSpecifiedScheduler() {
            final PutObjectsStub putStub = PutObjectsStub.newPutStubForOneObjectWithTypeMapping();
            final SchedulerChecker schedulerChecker = SchedulerChecker.create(putStub.storIOSQLite);

            final PreparedPutObject<TestItem> operation = putStub.storIOSQLite
                    .put()
                    .object(putStub.items.get(0))
                    .prepare();

            schedulerChecker.checkAsObservable(operation);
        }

        @Test
        public void putObjectAsSingleExecutesOnSpecifiedScheduler() {
            final PutObjectsStub putStub = PutObjectsStub.newPutStubForOneObjectWithTypeMapping();
            final SchedulerChecker schedulerChecker = SchedulerChecker.create(putStub.storIOSQLite);

            final PreparedPutObject<TestItem> operation = putStub.storIOSQLite
                    .put()
                    .object(putStub.items.get(0))
                    .prepare();

            schedulerChecker.checkAsSingle(operation);
        }

        @Test
        public void putObjectAsCompletableExecutesOnSpecifiedScheduler() {
            final PutObjectsStub putStub = PutObjectsStub.newPutStubForOneObjectWithTypeMapping();
            final SchedulerChecker schedulerChecker = SchedulerChecker.create(putStub.storIOSQLite);

            final PreparedPutObject<TestItem> operation = putStub.storIOSQLite
                    .put()
                    .object(putStub.items.get(0))
                    .prepare();

            schedulerChecker.checkAsCompletable(operation);
        }

        @Test
        public void createObservableReturnsAsRxObservable() {
            final PutObjectsStub putStub = PutObjectsStub.newPutStubForOneObjectWithTypeMapping();

            PreparedPutObject<TestItem> preparedOperation = spy(putStub.storIOSQLite
                    .put()
                    .object(putStub.items.get(0))
                    .prepare());

            Observable<PutResult> observable = Observable.just(PutResult.newInsertResult(1, TestItem.TABLE));

            //noinspection CheckResult
            doReturn(observable).when(preparedOperation).asRxObservable();

            //noinspection deprecation
            assertThat(preparedOperation.createObservable()).isEqualTo(observable);

            //noinspection CheckResult
            verify(preparedOperation).asRxObservable();
        }
    }
}