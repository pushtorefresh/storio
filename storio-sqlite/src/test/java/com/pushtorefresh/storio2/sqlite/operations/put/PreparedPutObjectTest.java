package com.pushtorefresh.storio2.sqlite.operations.put;

import com.pushtorefresh.storio2.StorIOException;
import com.pushtorefresh.storio2.sqlite.StorIOSQLite;
import com.pushtorefresh.storio2.sqlite.operations.SchedulerChecker;

import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;

import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Single;
import io.reactivex.observers.TestObserver;
import io.reactivex.subscribers.TestSubscriber;

import static io.reactivex.BackpressureStrategy.MISSING;
import static org.assertj.core.api.Assertions.failBecauseExceptionWasNotThrown;
import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.mockito.Mockito.mock;
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
        public void shouldPutObjectWithoutTypeMappingAsFlowable() {
            final PutObjectsStub putStub = PutObjectsStub.newPutStubForOneObjectWithoutTypeMapping();

            final Flowable<PutResult> putResultFlowable = putStub.storIOSQLite
                    .put()
                    .object(putStub.items.get(0))
                    .withPutResolver(putStub.putResolver)
                    .prepare()
                    .asRxFlowable(MISSING);

            putStub.verifyBehaviorForOneObject(putResultFlowable);
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
        public void shouldPutObjectWithTypeMappingAsFlowable() {
            final PutObjectsStub putStub = PutObjectsStub.newPutStubForOneObjectWithTypeMapping();

            final Flowable<PutResult> putResultFlowable = putStub.storIOSQLite
                    .put()
                    .object(putStub.items.get(0))
                    .prepare()
                    .asRxFlowable(MISSING);

            putStub.verifyBehaviorForOneObject(putResultFlowable);
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
            final StorIOSQLite.LowLevel lowLevel = mock(StorIOSQLite.LowLevel.class);

            when(storIOSQLite.lowLevel()).thenReturn(lowLevel);

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
                verify(storIOSQLite).interceptors();
                verify(lowLevel).typeMapping(Object.class);
                verifyNoMoreInteractions(storIOSQLite, lowLevel);
            }
        }

        @Test
        public void shouldThrowExceptionIfNoTypeMappingWasFoundWithoutAffectingDbFlowable() {
            final StorIOSQLite storIOSQLite = mock(StorIOSQLite.class);
            final StorIOSQLite.LowLevel lowLevel = mock(StorIOSQLite.LowLevel.class);

            when(storIOSQLite.lowLevel()).thenReturn(lowLevel);

            final Object object = new Object();

            final TestSubscriber<PutResult> testSubscriber = new TestSubscriber<PutResult>();

            new PreparedPutObject.Builder<Object>(storIOSQLite, object)
                    .prepare()
                    .asRxFlowable(MISSING)
                    .subscribe(testSubscriber);

            testSubscriber.awaitTerminalEvent();
            testSubscriber.assertNoValues();
            testSubscriber.assertError(StorIOException.class);

            //noinspection ThrowableResultOfMethodCallIgnored
            StorIOException expected = (StorIOException) testSubscriber.errors().get(0);

            IllegalStateException cause = (IllegalStateException) expected.getCause();

            assertThat(cause).hasMessage("Object does not have type mapping: " +
                            "object = " + object + ", object.class = " + object.getClass() + ", " +
                            "db was not affected by this operation, please add type mapping for this type");

            verify(storIOSQLite).lowLevel();
            verify(storIOSQLite).defaultRxScheduler();
            verify(storIOSQLite).interceptors();
            verify(lowLevel).typeMapping(Object.class);
            verifyNoMoreInteractions(storIOSQLite, lowLevel);
        }

        @Test
        public void shouldThrowExceptionIfNoTypeMappingWasFoundWithoutAffectingDbSingle() {
            final StorIOSQLite storIOSQLite = mock(StorIOSQLite.class);
            final StorIOSQLite.LowLevel lowLevel = mock(StorIOSQLite.LowLevel.class);

            when(storIOSQLite.lowLevel()).thenReturn(lowLevel);

            final Object object = new Object();

            final TestObserver<PutResult> testObserver = new TestObserver<PutResult>();

            new PreparedPutObject.Builder<Object>(storIOSQLite, object)
                    .prepare()
                    .asRxSingle()
                    .subscribe(testObserver);

            testObserver.awaitTerminalEvent();
            testObserver.assertNoValues();
            testObserver.assertError(StorIOException.class);

            //noinspection ThrowableResultOfMethodCallIgnored
            StorIOException expected = (StorIOException) testObserver.errors().get(0);

            IllegalStateException cause = (IllegalStateException) expected.getCause();

            assertThat(cause).hasMessage("Object does not have type mapping: " +
                    "object = " + object + ", object.class = " + object.getClass() + ", " +
                    "db was not affected by this operation, please add type mapping for this type");

            verify(storIOSQLite).lowLevel();
            verify(storIOSQLite).defaultRxScheduler();
            verify(storIOSQLite).interceptors();
            verify(lowLevel).typeMapping(Object.class);
            verifyNoMoreInteractions(storIOSQLite, lowLevel);
        }

        @Test
        public void shouldThrowExceptionIfNoTypeMappingWasFoundWithoutAffectingDbCompletable() {
            final StorIOSQLite storIOSQLite = mock(StorIOSQLite.class);
            final StorIOSQLite.LowLevel lowLevel = mock(StorIOSQLite.LowLevel.class);

            when(storIOSQLite.lowLevel()).thenReturn(lowLevel);

            final Object object = new Object();

            final TestObserver<PutResult> testObserver = new TestObserver<PutResult>();

            new PreparedPutObject.Builder<Object>(storIOSQLite, object)
                    .prepare()
                    .asRxCompletable()
                    .subscribe(testObserver);

            testObserver.awaitTerminalEvent();
            testObserver.assertNoValues();
            testObserver.assertError(StorIOException.class);

            //noinspection ThrowableResultOfMethodCallIgnored
            StorIOException expected = (StorIOException) testObserver.errors().get(0);

            IllegalStateException cause = (IllegalStateException) expected.getCause();

            assertThat(cause).hasMessage("Object does not have type mapping: " +
                    "object = " + object + ", object.class = " + object.getClass() + ", " +
                    "db was not affected by this operation, please add type mapping for this type");

            verify(storIOSQLite).lowLevel();
            verify(storIOSQLite).defaultRxScheduler();
            verify(storIOSQLite).interceptors();
            verify(lowLevel).typeMapping(Object.class);
            verifyNoMoreInteractions(storIOSQLite, lowLevel);
        }
    }

    public static class OtherTests {

        @Test
        public void shouldReturnObjectInGetData() {
            final StorIOSQLite storIOSQLite = mock(StorIOSQLite.class);
            when(storIOSQLite.put()).thenReturn(new PreparedPut.Builder(storIOSQLite));

            final TestItem object = TestItem.newInstance();
            final PreparedPut<PutResult, TestItem> operation = storIOSQLite
                    .put()
                    .object(object)
                    .prepare();

            assertThat(operation.getData()).isEqualTo(object);
        }

        @Test
        public void putObjectFlowableExecutesOnSpecifiedScheduler() {
            final PutObjectsStub putStub = PutObjectsStub.newPutStubForOneObjectWithTypeMapping();
            final SchedulerChecker schedulerChecker = SchedulerChecker.create(putStub.storIOSQLite);

            final PreparedPutObject<TestItem> operation = putStub.storIOSQLite
                    .put()
                    .object(putStub.items.get(0))
                    .prepare();

            schedulerChecker.checkAsFlowable(operation);
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
        public void shouldNotNotifyIfWasNotInsertedAndUpdatedWithoutTypeMapping() {
            final PutObjectsStub putStub = PutObjectsStub.newPutStubForOneObjectWithoutInsertsAndUpdatesWithoutTypeMapping();

            PutResult putResult = putStub.storIOSQLite
                    .put()
                    .object(putStub.items.get(0))
                    .withPutResolver(putStub.putResolver)
                    .prepare()
                    .executeAsBlocking();

            putStub.verifyBehaviorForOneObject(putResult);
        }

        @Test
        public void shouldNotNotifyIfWasNotInsertedAndUpdatedWithTypeMapping() {
            final PutObjectsStub putStub = PutObjectsStub.newPutStubForOneObjectWithoutInsertsAndUpdatesWithTypeMapping();

            PutResult putResult = putStub.storIOSQLite
                    .put()
                    .object(putStub.items.get(0))
                    .prepare()
                    .executeAsBlocking();

            putStub.verifyBehaviorForOneObject(putResult);
        }
    }
}
