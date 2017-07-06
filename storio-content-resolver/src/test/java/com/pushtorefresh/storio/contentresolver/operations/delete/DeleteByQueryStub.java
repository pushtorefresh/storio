package com.pushtorefresh.storio.contentresolver.operations.delete;

import android.net.Uri;
import android.support.annotation.NonNull;

import com.pushtorefresh.storio.contentresolver.StorIOContentResolver;
import com.pushtorefresh.storio.contentresolver.queries.DeleteQuery;
import com.pushtorefresh.storio.test.ObservableBehaviorChecker;

import rx.Completable;
import rx.Observable;
import rx.Single;
import rx.functions.Action1;

import static org.assertj.core.api.Java6Assertions.assertThat;
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
    private final StorIOContentResolver.LowLevel lowLevel;

    @NonNull
    final DeleteQuery deleteQuery;

    @NonNull
    final DeleteResolver<DeleteQuery> deleteResolver;

    @NonNull
    private final DeleteResult deleteResult;

    @SuppressWarnings("unchecked")
    private DeleteByQueryStub() {
        storIOContentResolver = mock(StorIOContentResolver.class);
        lowLevel = mock(StorIOContentResolver.LowLevel.class);

        when(storIOContentResolver.lowLevel())
                .thenReturn(lowLevel);

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
        assertThat(actualDeleteResult).isEqualTo(deleteResult);
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

    void verifyBehavior(@NonNull Single<DeleteResult> deleteResultSingle) {
        new ObservableBehaviorChecker<DeleteResult>()
                .observable(deleteResultSingle.toObservable())
                .expectedNumberOfEmissions(1)
                .testAction(new Action1<DeleteResult>() {
                    @Override
                    public void call(DeleteResult deleteResult) {
                        verifyBehavior(deleteResult);
                    }
                })
                .checkBehaviorOfObservable();
    }

    void verifyBehavior(@NonNull Completable completable) {
        verifyBehavior(completable.<DeleteResult>toObservable());
    }
}
