package com.pushtorefresh.storio.contentresolver.operations.get;

import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;

import com.pushtorefresh.storio.contentresolver.Changes;
import com.pushtorefresh.storio.contentresolver.ContentResolverTypeMapping;
import com.pushtorefresh.storio.contentresolver.StorIOContentResolver;
import com.pushtorefresh.storio.contentresolver.queries.Query;
import com.pushtorefresh.storio.test.ObservableBehaviorChecker;

import rx.Observable;
import rx.functions.Action1;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

class GetObjectStub {

    @NonNull
    final StorIOContentResolver storIOContentResolver;

    @NonNull
    private final StorIOContentResolver.Internal internal;

    @NonNull
    final Query query;

    @NonNull
    final TestItem item;

    @NonNull
    final GetResolver<TestItem> getResolver;

    @NonNull
    final Cursor cursor;

    @NonNull
    private final ContentResolverTypeMapping<TestItem> typeMapping;

    private final boolean withTypeMapping;

    @SuppressWarnings("unchecked")
    private GetObjectStub(boolean withTypeMapping) {
        this.withTypeMapping = withTypeMapping;

        storIOContentResolver = mock(StorIOContentResolver.class);
        internal = mock(StorIOContentResolver.Internal.class);

        query = Query.builder()
                .uri(mock(Uri.class))
                .build();

        getResolver = mock(GetResolver.class);
        cursor = mock(Cursor.class);

        item = new TestItem();

        when(storIOContentResolver.internal())
                .thenReturn(internal);

        when(cursor.getCount())
                .thenReturn(1);

        when(cursor.moveToFirst()).thenReturn(true);

        when(storIOContentResolver.get())
                .thenReturn(new PreparedGet.Builder(storIOContentResolver));

        when(getResolver.performGet(storIOContentResolver, query))
                .thenReturn(cursor);

        when(storIOContentResolver.observeChangesOfUri(query.uri()))
                .thenReturn(Observable.<Changes>empty());

        when(getResolver.mapFromCursor(cursor))
                .thenReturn(item);

        typeMapping = mock(ContentResolverTypeMapping.class);

        if (withTypeMapping) {
            when(internal.typeMapping(TestItem.class)).thenReturn(typeMapping);
            when(typeMapping.getResolver()).thenReturn(getResolver);
        }
    }

    @NonNull
    static GetObjectStub newStubWithoutTypeMapping() {
        return new GetObjectStub(false);
    }

    @NonNull
    static GetObjectStub newStubWithTypeMapping() {
        return new GetObjectStub(true);
    }

    void verifyBehavior(@NonNull TestItem actualItem) {
        // should be called once
        verify(storIOContentResolver).get();

        // should be called once
        verify(getResolver).performGet(storIOContentResolver, query);

        // should be called only once because of Performance!
        verify(cursor).getCount();

        // should be called once
        verify(cursor).moveToFirst();

        // should be called once
        verify(getResolver).mapFromCursor(cursor);

        // cursor should be closed!
        verify(cursor).close();

        // checks that items are okay
        assertThat(actualItem).isEqualTo(item);

        if (withTypeMapping) {
            // should be called only once because of Performance!
            verify(storIOContentResolver).internal();

            // should be called only once because of Performance!
            verify(internal).typeMapping(TestItem.class);

            // should be called only once
            verify(typeMapping).getResolver();
        }

        verifyNoMoreInteractions(storIOContentResolver, internal, getResolver, cursor);
    }

    void verifyBehavior(@NonNull Observable<TestItem> observable) {
        when(storIOContentResolver.observeChangesOfUri(query.uri()))
                .thenReturn(Observable.<Changes>empty());

        new ObservableBehaviorChecker<TestItem>()
                .observable(observable)
                .expectedNumberOfEmissions(1)
                .testAction(new Action1<TestItem>() {
                    @Override
                    public void call(TestItem testItem) {
                        // Get Operation should be subscribed to changes of Uri!
                        verify(storIOContentResolver).observeChangesOfUri(query.uri());

                        verifyBehavior(testItem);
                    }
                })
                .checkBehaviorOfObservable();

    }
}
