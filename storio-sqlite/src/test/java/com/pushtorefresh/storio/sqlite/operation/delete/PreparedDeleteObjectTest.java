package com.pushtorefresh.storio.sqlite.operation.delete;

import com.pushtorefresh.storio.sqlite.StorIOSQLite;
import com.pushtorefresh.storio.sqlite.query.DeleteQuery;

import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;

import rx.Observable;
import rx.observers.TestSubscriber;

import static org.junit.Assert.fail;
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
            final DeleteStub deleteStub = DeleteStub.newStubForOneObjectWithoutTypeMapping();

            final DeleteResult deleteResult = deleteStub.storIOSQLite
                    .delete()
                    .object(deleteStub.items.get(0))
                    .withDeleteResolver(deleteStub.deleteResolver)
                    .prepare()
                    .executeAsBlocking();

            deleteStub.verifyBehaviorForOneObject(deleteResult);
        }

        @Test
        public void shouldDeleteObjectWithoutTypeMappingAsObservable() {
            final DeleteStub deleteStub = DeleteStub.newStubForOneObjectWithoutTypeMapping();

            final Observable<DeleteResult> observable = deleteStub.storIOSQLite
                    .delete()
                    .object(deleteStub.items.get(0))
                    .withDeleteResolver(deleteStub.deleteResolver)
                    .prepare()
                    .createObservable();

            deleteStub.verifyBehaviorForOneObject(observable);
        }
    }

    public static class WithTypeMapping {

        @Test
        public void shouldDeleteObjectWithTypeMappingBlocking() {
            final DeleteStub deleteStub = DeleteStub.newStubForOneObjectWithTypeMapping();

            final DeleteResult deleteResult = deleteStub.storIOSQLite
                    .delete()
                    .object(deleteStub.items.get(0))
                    .prepare()
                    .executeAsBlocking();

            deleteStub.verifyBehaviorForOneObject(deleteResult);
        }

        @Test
        public void shouldDeleteObjectWithTypeMappingAsObservable() {
            final DeleteStub deleteStub = DeleteStub.newStubForOneObjectWithTypeMapping();

            final Observable<DeleteResult> observable = deleteStub.storIOSQLite
                    .delete()
                    .object(deleteStub.items.get(0))
                    .prepare()
                    .createObservable();

            deleteStub.verifyBehaviorForOneObject(observable);
        }
    }

    public static class NoTypeMappingError {

        @Test
        public void shouldThrowExceptionIfNoTypeMappingWasFoundWithoutAffectingDbBlocking() {
            final StorIOSQLite storIOSQLite = mock(StorIOSQLite.class);
            final StorIOSQLite.Internal internal = mock(StorIOSQLite.Internal.class);

            when(storIOSQLite.internal()).thenReturn(internal);

            when(storIOSQLite.delete()).thenReturn(new PreparedDelete.Builder(storIOSQLite));

            final PreparedDelete<DeleteResult> preparedDelete = storIOSQLite
                    .delete()
                    .object(TestItem.newInstance())
                    .prepare();

            try {
                preparedDelete.executeAsBlocking();
                fail();
            } catch (IllegalStateException expected) {
                // it's okay, no type mapping was found
            }

            verify(storIOSQLite).delete();
            verify(storIOSQLite).internal();
            verify(internal).typeMapping(TestItem.class);
            verify(internal, never()).delete(any(DeleteQuery.class));
            verifyNoMoreInteractions(storIOSQLite, internal);
        }

        @Test
        public void shouldThrowExceptionIfNoTypeMappingWasFoundWithoutAffectingDbAsObservable() {
            final StorIOSQLite storIOSQLite = mock(StorIOSQLite.class);
            final StorIOSQLite.Internal internal = mock(StorIOSQLite.Internal.class);

            when(storIOSQLite.internal()).thenReturn(internal);

            when(storIOSQLite.delete()).thenReturn(new PreparedDelete.Builder(storIOSQLite));

            final TestSubscriber<DeleteResult> testSubscriber = new TestSubscriber<DeleteResult>();

            storIOSQLite
                    .delete()
                    .object(TestItem.newInstance())
                    .prepare()
                    .createObservable()
                    .subscribe(testSubscriber);

            testSubscriber.awaitTerminalEvent();
            testSubscriber.assertNoValues();
            testSubscriber.assertError(IllegalStateException.class);

            verify(storIOSQLite).delete();
            verify(storIOSQLite).internal();
            verify(internal).typeMapping(TestItem.class);
            verify(internal, never()).delete(any(DeleteQuery.class));
            verifyNoMoreInteractions(storIOSQLite, internal);
        }
    }
}
