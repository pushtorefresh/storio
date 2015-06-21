package com.pushtorefresh.storio.contentresolver.operations.delete;

import android.net.Uri;
import android.support.annotation.NonNull;

import com.pushtorefresh.storio.contentresolver.StorIOContentResolver;
import com.pushtorefresh.storio.contentresolver.queries.DeleteQuery;
import com.pushtorefresh.storio.test.ObservableBehaviorChecker;

import rx.Observable;
import rx.functions.Action1;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

// stub class to avoid violation of DRY in tests
class DeleteByQueryStub {

    @NonNull
    final StorIOContentResolver storIOContentResolver;

    @NonNull
    private final StorIOContentResolver.Internal internal;

    @NonNull
    final DeleteQuery deleteQuery;

    @NonNull
    final DeleteResolver<DeleteQuery> deleteResolver;

    @NonNull
    private final DeleteResult deleteResult;

    @SuppressWarnings("unchecked")
    private DeleteByQueryStub() {
        storIOContentResolver = mock(StorIOContentResolver.class);
        internal = mock(StorIOContentResolver.Internal.class);

        when(storIOContentResolver.internal())
                .thenReturn(internal);

        when(storIOContentResolver.delete())
                .thenReturn(new PreparedDelete.Builder(storIOContentResolver));

        deleteQuery = DeleteQuery.builder()
                .uri(mock(Uri.class))
                .build();

        deleteResolver = mock(DeleteResolver.class);

        deleteResult = DeleteResult.newInstance(1, deleteQuery.uri());

        when(deleteResolver.performDelete(storIOContentResolver, deleteQuery))
                .thenReturn(deleteResult);
    }

    @NonNull
    static DeleteByQueryStub newInstance() {
        return new DeleteByQueryStub();
    }

    void verifyBehavior(@NonNull DeleteResult actualDeleteResult) {
        // checks that required delete was performed
        verify(deleteResolver, times(1)).performDelete(storIOContentResolver, deleteQuery);

        // only one call to DeleteResolver.performDelete() should occur
        verify(deleteResolver, times(1)).performDelete(any(StorIOContentResolver.class), any(DeleteQuery.class));

        // checks that actual delete result equals to expected
        assertEquals(deleteResult, actualDeleteResult);
    }

    void verifyBehavior(@NonNull Observable<DeleteResult> deleteResultObservable) {
        new ObservableBehaviorChecker<DeleteResult>()
                .observable(deleteResultObservable)
                .expectedNumberOfEmissions(1)
                .testAction(new Action1<DeleteResult>() {
                    @Override
                    public void call(DeleteResult deleteResult) {
                        verifyBehavior(deleteResult);
                    }
                })
                .checkBehaviorOfObservable();
    }
}
