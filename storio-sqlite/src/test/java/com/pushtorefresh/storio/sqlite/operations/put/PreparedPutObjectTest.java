package com.pushtorefresh.storio.sqlite.operations.put;

import com.pushtorefresh.storio.StorIOException;
import com.pushtorefresh.storio.sqlite.StorIOSQLite;

import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;

import rx.Observable;
import rx.observers.TestSubscriber;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.failBecauseExceptionWasNotThrown;
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
        public void shouldPutObjectWithoutTypeMappingAsObservable() {
            final PutObjectsStub putStub = PutObjectsStub.newPutStubForOneObjectWithoutTypeMapping();

            final Observable<PutResult> putResultObservable = putStub.storIOSQLite
                    .put()
                    .object(putStub.items.get(0))
                    .withPutResolver(putStub.putResolver)
                    .prepare()
                    .createObservable();

            putStub.verifyBehaviorForOneObject(putResultObservable);
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
                    .createObservable();

            putStub.verifyBehaviorForOneObject(putResultObservable);
        }
    }

    public static class TypeMappingError {

        @Test
        public void shouldThrowExceptionIfNoTypeMappingWasFoundWithoutAffectingDbBlocking() {
            final StorIOSQLite storIOSQLite = mock(StorIOSQLite.class);
            final StorIOSQLite.Internal internal = mock(StorIOSQLite.Internal.class);

            when(storIOSQLite.internal()).thenReturn(internal);

            final Object object = new Object();

            try {
                new PreparedPutObject.Builder<Object>(storIOSQLite, object)
                        .prepare()
                        .executeAsBlocking();
                failBecauseExceptionWasNotThrown(StorIOException.class);
            } catch (StorIOException expected) {
                IllegalStateException cause = (IllegalStateException) expected.getCause();

                assertThat(cause).hasMessage("Object does not have type mapping: " +
                                "object = " + object + ", object.class = " + object.getClass() + "," +
                                "db was not affected by this operation, please add type mapping for this type");

                verify(storIOSQLite).internal();
                verify(internal).typeMapping(Object.class);
                verifyNoMoreInteractions(storIOSQLite, internal);
            }
        }

        @Test
        public void shouldThrowExceptionIfNoTypeMappingWasFoundWithoutAffectingDbObservable() {
            final StorIOSQLite storIOSQLite = mock(StorIOSQLite.class);
            final StorIOSQLite.Internal internal = mock(StorIOSQLite.Internal.class);

            when(storIOSQLite.internal()).thenReturn(internal);

            final Object object = new Object();

            final TestSubscriber<PutResult> testSubscriber = new TestSubscriber<PutResult>();

            new PreparedPutObject.Builder<Object>(storIOSQLite, object)
                    .prepare()
                    .createObservable()
                    .subscribe(testSubscriber);

            testSubscriber.awaitTerminalEvent();
            testSubscriber.assertNoValues();
            testSubscriber.assertError(StorIOException.class);

            //noinspection ThrowableResultOfMethodCallIgnored
            StorIOException expected = (StorIOException) testSubscriber.getOnErrorEvents().get(0);

            IllegalStateException cause = (IllegalStateException) expected.getCause();

            assertThat(cause).hasMessage("Object does not have type mapping: " +
                            "object = " + object + ", object.class = " + object.getClass() + "," +
                            "db was not affected by this operation, please add type mapping for this type");

            verify(storIOSQLite).internal();
            verify(internal).typeMapping(Object.class);
            verifyNoMoreInteractions(storIOSQLite, internal);
        }
    }
}
