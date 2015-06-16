package com.pushtorefresh.storio.sqlite.operation.delete;

import android.support.annotation.NonNull;

import com.pushtorefresh.storio.sqlite.Changes;
import com.pushtorefresh.storio.sqlite.SQLiteTypeMapping;
import com.pushtorefresh.storio.sqlite.StorIOSQLite;
import com.pushtorefresh.storio.test.ObservableBehaviorChecker;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.functions.Action1;

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
    final List<TestItem> items;

    @NonNull
    final DeleteResolver<TestItem> deleteResolver;

    @NonNull
    private final SQLiteTypeMapping<TestItem> typeMapping;

    private final boolean withTypeMapping, useTransaction;

    private DeleteStub(boolean withTypeMapping, boolean useTransaction, int numberOfItems) {
        this.withTypeMapping = withTypeMapping;
        this.useTransaction = useTransaction;

        storIOSQLite = mock(StorIOSQLite.class);
        internal = mock(StorIOSQLite.Internal.class);

        items = new ArrayList<TestItem>(numberOfItems);

        for (int i = 0; i < numberOfItems; i++) {
            items.add(TestItem.newInstance());
        }

        deleteResolver = mock(DeleteResolver.class);

        typeMapping = mock(SQLiteTypeMapping.class);

        when(storIOSQLite.internal()).thenReturn(internal);
        when(storIOSQLite.delete()).thenReturn(new PreparedDelete.Builder(storIOSQLite));
        when(deleteResolver.performDelete(eq(storIOSQLite), any(TestItem.class)))
                .thenReturn(DeleteResult.newInstance(1, TestItem.TABLE));

        if (withTypeMapping) {
            when(internal.typeMapping(TestItem.class)).thenReturn(typeMapping);
            when(typeMapping.deleteResolver()).thenReturn(deleteResolver);
        }
    }

    @NonNull
    static DeleteStub newStubForMultipleObjectsWithoutTypeMappingWithoutTransaction() {
        return new DeleteStub(false, false, 3);
    }

    @NonNull
    static DeleteStub newStubForMultipleObjectsWithoutTypeMappingWithTransaction() {
        return new DeleteStub(false, true, 3);
    }

    @NonNull
    static DeleteStub newStubForMultipleObjectsWithTypeMappingWithoutTransaction() {
        return new DeleteStub(true, false, 3);
    }

    @NonNull
    static DeleteStub newStubForMultipleObjectsWithTypeMappingWithTransaction() {
        return new DeleteStub(true, true, 3);
    }

    void verifyBehavior(@NonNull DeleteResults<TestItem> deleteResults) {
        verify(storIOSQLite).delete(); // Only one call to delete should occur

        verify(storIOSQLite).internal(); // Only one call to internal should occur

        // Number of calls to perform deletion of objects should be equals to number of items
        verify(deleteResolver, times(items.size()))
                .performDelete(eq(storIOSQLite), any(TestItem.class));

        // Perform delete should be called for each item
        for (TestItem item : items) {
            verify(deleteResolver).performDelete(storIOSQLite, item);
        }

        if (withTypeMapping) {
            // Number of calls to receive delete resolver should be equal to number of items
            verify(internal, times(items.size())).typeMapping(TestItem.class);
            verify(typeMapping, times(items.size())).deleteResolver();
        }

        verifyTransactionBehavior();
        verifyNoMoreInteractions(storIOSQLite, internal, deleteResolver, typeMapping);
    }

    void verifyBehavior(@NonNull Observable<DeleteResults<TestItem>> observable) {
        new ObservableBehaviorChecker<DeleteResults<TestItem>>()
                .observable(observable)
                .expectedNumberOfEmissions(1)
                .testAction(new Action1<DeleteResults<TestItem>>() {
                    @Override
                    public void call(DeleteResults<TestItem> deleteResults) {
                        verifyBehavior(deleteResults);
                    }
                })
                .checkBehaviorOfObservable();
    }

    private void verifyTransactionBehavior() {
        if (useTransaction) {
            verify(internal).beginTransaction();
            verify(internal).setTransactionSuccessful();
            verify(internal).endTransaction();

            // Only one notification should be thrown
            verify(internal).notifyAboutChanges(eq(Changes.newInstance(TestItem.TABLE)));
        } else {
            verify(internal, never()).beginTransaction();
            verify(internal, never()).setTransactionSuccessful();
            verify(internal, never()).endTransaction();

            // Each delete should trigger notification
            verify(internal, times(items.size()))
                    .notifyAboutChanges(eq(Changes.newInstance(TestItem.TABLE)));
        }
    }
}
