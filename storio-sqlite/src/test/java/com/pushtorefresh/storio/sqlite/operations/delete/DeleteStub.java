package com.pushtorefresh.storio.sqlite.operations.delete;

import android.support.annotation.NonNull;

import com.pushtorefresh.storio.sqlite.Changes;
import com.pushtorefresh.storio.sqlite.SQLiteTypeMapping;
import com.pushtorefresh.storio.sqlite.StorIOSQLite;
import com.pushtorefresh.storio.test.ObservableBehaviorChecker;

import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.util.ArrayList;
import java.util.List;

import rx.Completable;
import rx.Observable;
import rx.Single;
import rx.functions.Action1;

import static java.util.Collections.singletonMap;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@SuppressWarnings("unchecked")
class DeleteStub {

    @NonNull
    final StorIOSQLite storIOSQLite;

    @NonNull
    private final StorIOSQLite.Internal internal;

    @NonNull
    final List<TestItem> itemsRequestedForDelete;

    @NonNull
    final List<TestItem> itemsWillBeDeleted;

    @NonNull
    final DeleteResolver<TestItem> deleteResolver;

    @NonNull
    private final SQLiteTypeMapping<TestItem> typeMapping;

    private final boolean withTypeMapping, useTransaction;

    private DeleteStub(boolean withTypeMapping,
                       boolean useTransaction,
                       int numberRequestedForDelete,
                       int numberWillBeDeleted) {

        this.withTypeMapping = withTypeMapping;
        this.useTransaction = useTransaction;

        storIOSQLite = mock(StorIOSQLite.class);
        internal = mock(StorIOSQLite.Internal.class);

        itemsRequestedForDelete = new ArrayList<TestItem>(numberRequestedForDelete);
        itemsWillBeDeleted = new ArrayList<TestItem>(numberWillBeDeleted);

        for (int i = 0; i < numberRequestedForDelete; i++) {
            TestItem item = TestItem.newInstance();
            itemsRequestedForDelete.add(item);
            if (i < numberWillBeDeleted) {
                itemsWillBeDeleted.add(item);
            }
        }

        deleteResolver = mock(DeleteResolver.class);

        typeMapping = mock(SQLiteTypeMapping.class);

        when(storIOSQLite.lowLevel()).thenReturn(internal);
        when(storIOSQLite.delete()).thenReturn(new PreparedDelete.Builder(storIOSQLite));
        when(deleteResolver.performDelete(eq(storIOSQLite), any(TestItem.class)))
                .thenAnswer(new Answer<DeleteResult>() {
                    @Override
                    public DeleteResult answer(InvocationOnMock invocation) throws Throwable {
                        Object[] args = invocation.getArguments();
                        TestItem item = (TestItem) args[1];
                        int numberOfDeletedRows = itemsWillBeDeleted.contains(item) ? 1 : 0;
                        return DeleteResult.newInstance(numberOfDeletedRows, TestItem.TABLE, TestItem.NOTIFICATION_TAG);
                    }
                });

        if (withTypeMapping) {
            when(internal.typeMapping(TestItem.class)).thenReturn(typeMapping);
            when(typeMapping.deleteResolver()).thenReturn(deleteResolver);
        }
    }

    @NonNull
    static DeleteStub newStubForMultipleObjectsWithoutTypeMappingWithoutTransaction() {
        return new DeleteStub(false, false, 3, 3);
    }

    @NonNull
    static DeleteStub newStubForMultipleObjectsWithoutTypeMappingWithoutTransactionNothingDeleted() {
        return new DeleteStub(false, false, 3, 0);
    }

    @NonNull
    static DeleteStub newStubForMultipleObjectsWithoutTypeMappingWithTransaction() {
        return new DeleteStub(false, true, 3, 3);
    }

    @NonNull
    static DeleteStub newStubForMultipleObjectsWithoutTypeMappingWithTransactionNothingDeleted() {
        return new DeleteStub(false, true, 3, 0);
    }

    @NonNull
    static DeleteStub newStubForMultipleObjectsWithTypeMappingWithoutTransaction() {
        return new DeleteStub(true, false, 3, 3);
    }

    @NonNull
    static DeleteStub newStubForMultipleObjectsWithTypeMappingWithoutTransactionNothingDeleted() {
        return new DeleteStub(true, false, 3, 0);
    }

    @NonNull
    static DeleteStub newStubForMultipleObjectsWithTypeMappingWithTransaction() {
        return new DeleteStub(true, true, 3, 3);
    }

    @NonNull
    static DeleteStub newStubForMultipleObjectsWithTypeMappingWithTransactionNothingDeleted() {
        return new DeleteStub(true, true, 3, 0);
    }

    @NonNull
    static DeleteStub newStubForOneObjectWithoutTypeMapping() {
        return new DeleteStub(false, false, 1, 1);
    }

    @NonNull
    static DeleteStub newStubForOneObjectWithTypeMapping() {
        return new DeleteStub(true, false, 1, 1);
    }

    @NonNull
    static DeleteStub newStubForOneObjectWithoutTypeMappingNothingDeleted() {
        return new DeleteStub(false, false, 1, 0);
    }

    void verifyBehaviorForMultipleObjects(@NonNull DeleteResults<TestItem> deleteResults) {
        verify(storIOSQLite).delete(); // Only one call to delete should occur

        verify(storIOSQLite).lowLevel(); // Only one call to internal should occur

        // Number of calls to perform deletion of objects should be equals to number of items
        verify(deleteResolver, times(itemsRequestedForDelete.size()))
                .performDelete(eq(storIOSQLite), any(TestItem.class));

        // Perform delete should be called for each item
        for (TestItem item : itemsRequestedForDelete) {
            verify(deleteResolver).performDelete(storIOSQLite, item);
        }

        if (withTypeMapping) {
            // Number of calls to receive delete resolver should be equal to number of items
            verify(internal, times(itemsRequestedForDelete.size())).typeMapping(TestItem.class);
            verify(typeMapping, times(itemsRequestedForDelete.size())).deleteResolver();
        }

        verifyTransactionBehavior();
        verifyNoMoreInteractions(storIOSQLite, internal, deleteResolver, typeMapping);
    }

    void verifyBehaviorForMultipleObjects(@NonNull Observable<DeleteResults<TestItem>> observable) {
        new ObservableBehaviorChecker<DeleteResults<TestItem>>()
                .observable(observable)
                .expectedNumberOfEmissions(1)
                .testAction(new Action1<DeleteResults<TestItem>>() {
                    @Override
                    public void call(DeleteResults<TestItem> deleteResults) {
                        verify(storIOSQLite).defaultScheduler();
                        verifyBehaviorForMultipleObjects(deleteResults);
                    }
                })
                .checkBehaviorOfObservable();
    }

    void verifyBehaviorForMultipleObjects(@NonNull Single<DeleteResults<TestItem>> single) {
        new ObservableBehaviorChecker<DeleteResults<TestItem>>()
                .observable(single.toObservable())
                .expectedNumberOfEmissions(1)
                .testAction(new Action1<DeleteResults<TestItem>>() {
                    @Override
                    public void call(DeleteResults<TestItem> deleteResults) {
                        verify(storIOSQLite).defaultScheduler();
                        verifyBehaviorForMultipleObjects(deleteResults);
                    }
                })
                .checkBehaviorOfObservable();
    }

    void verifyBehaviorForMultipleObjects(@NonNull Completable completable) {
        verifyBehaviorForMultipleObjects(completable.<DeleteResults<TestItem>>toObservable());
    }

    void verifyBehaviorForOneObject(@NonNull DeleteResult deleteResult) {
        verifyBehaviorForMultipleObjects(DeleteResults.newInstance(singletonMap(itemsRequestedForDelete.get(0), deleteResult)));
    }

    void verifyBehaviorForOneObject(@NonNull Observable<DeleteResult> observable) {
        new ObservableBehaviorChecker<DeleteResult>()
                .observable(observable)
                .expectedNumberOfEmissions(1)
                .testAction(new Action1<DeleteResult>() {
                    @Override
                    public void call(DeleteResult deleteResult) {
                        verify(storIOSQLite).defaultScheduler();
                        verifyBehaviorForOneObject(deleteResult);
                    }
                })
                .checkBehaviorOfObservable();
    }

    void verifyBehaviorForOneObject(@NonNull Completable completable) {
        verifyBehaviorForOneObject(completable.<DeleteResult>toObservable());
    }

    void verifyBehaviorForOneObject(@NonNull Single<DeleteResult> single) {
        new ObservableBehaviorChecker<DeleteResult>()
                .observable(single.toObservable())
                .expectedNumberOfEmissions(1)
                .testAction(new Action1<DeleteResult>() {
                    @Override
                    public void call(DeleteResult deleteResult) {
                        verify(storIOSQLite).defaultScheduler();
                        verifyBehaviorForOneObject(deleteResult);
                    }
                })
                .checkBehaviorOfObservable();
    }

    private void verifyTransactionBehavior() {
        if (useTransaction) {
            verify(internal).beginTransaction();
            verify(internal).setTransactionSuccessful();
            verify(internal).endTransaction();

            // No more than one notification should be thrown
            verify(internal, times(Math.min(1, itemsWillBeDeleted.size())))
                    .notifyAboutChanges(eq(Changes.newInstance(TestItem.TABLE, TestItem.NOTIFICATION_TAG)));
        } else {
            verify(internal, never()).beginTransaction();
            verify(internal, never()).setTransactionSuccessful();
            verify(internal, never()).endTransaction();

            // Each delete should trigger notification
            verify(internal, times(itemsWillBeDeleted.size()))
                    .notifyAboutChanges(eq(Changes.newInstance(TestItem.TABLE, TestItem.NOTIFICATION_TAG)));
        }
    }
}
