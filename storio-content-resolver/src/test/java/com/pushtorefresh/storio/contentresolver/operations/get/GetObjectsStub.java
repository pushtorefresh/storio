package com.pushtorefresh.storio.contentresolver.operations.get;

import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;

import com.pushtorefresh.storio.contentresolver.Changes;
import com.pushtorefresh.storio.contentresolver.ContentResolverTypeMapping;
import com.pushtorefresh.storio.contentresolver.StorIOContentResolver;
import com.pushtorefresh.storio.contentresolver.queries.Query;
import com.pushtorefresh.storio.test.ObservableBehaviorChecker;

import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.functions.Action1;

import static com.pushtorefresh.storio.test.Asserts.assertThatListIsImmutable;
import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

class GetObjectsStub {

    @NonNull
    final StorIOContentResolver storIOContentResolver;

    @NonNull
    private final StorIOContentResolver.Internal internal;

    @NonNull
    final Query query;

    @NonNull
    final List<TestItem> items;

    @NonNull
    final GetResolver<TestItem> getResolver;

    @NonNull
    final Cursor cursor;

    @NonNull
    private final ContentResolverTypeMapping<TestItem> typeMapping;

    private final boolean withTypeMapping;

    @SuppressWarnings("unchecked")
    private GetObjectsStub(boolean withTypeMapping) {
        this.withTypeMapping = withTypeMapping;

        storIOContentResolver = mock(StorIOContentResolver.class);
        internal = mock(StorIOContentResolver.Internal.class);

        query = Query.builder()
                .uri(mock(Uri.class))
                .build();

        getResolver = mock(GetResolver.class);
        cursor = mock(Cursor.class);

        items = new ArrayList<TestItem>();
        items.add(new TestItem());
        items.add(new TestItem());
        items.add(new TestItem());

        when(storIOContentResolver.internal())
                .thenReturn(internal);

        when(cursor.moveToNext())
                .thenAnswer(new Answer<Boolean>() {
                    int invocationsCount = 0;

                    @Override
                    public Boolean answer(InvocationOnMock invocation) throws Throwable {
                        return invocationsCount++ < items.size();
                    }
                });

        when(cursor.getCount())
                .thenReturn(items.size());

        when(storIOContentResolver.get())
                .thenReturn(new PreparedGet.Builder(storIOContentResolver));

        when(getResolver.performGet(storIOContentResolver, query))
                .thenReturn(cursor);

        when(storIOContentResolver.observeChangesOfUri(query.uri()))
                .thenReturn(Observable.<Changes>empty());

        when(getResolver.mapFromCursor(cursor))
                .thenAnswer(new Answer<TestItem>() {
                    int invocationsCount = 0;

                    @Override
                    public TestItem answer(InvocationOnMock invocation) throws Throwable {
                        final TestItem testItem = items.get(invocationsCount);
                        invocationsCount++;
                        return testItem;
                    }
                });

        typeMapping = mock(ContentResolverTypeMapping.class);

        if (withTypeMapping) {
            when(internal.typeMapping(TestItem.class)).thenReturn(typeMapping);
            when(typeMapping.getResolver()).thenReturn(getResolver);
        }
    }

    @NonNull
    static GetObjectsStub newStubWithoutTypeMapping() {
        return new GetObjectsStub(false);
    }

    @NonNull
    static GetObjectsStub newStubWithTypeMapping() {
        return new GetObjectsStub(true);
    }

    void verifyBehavior(@NonNull List<TestItem> actualList) {
        // should be called once
        verify(storIOContentResolver).get();

        // should be called once
        verify(getResolver).performGet(storIOContentResolver, query);

        // should be called only once because of Performance!
        verify(cursor).getCount();

        // should be called same number of times as count of items in cursor + 1 (last -> false)
        verify(cursor, times(items.size() + 1)).moveToNext();

        // should be called same number of times as count of items in cursor
        verify(getResolver, times(items.size())).mapFromCursor(cursor);

        // cursor should be closed!
        verify(cursor).close();

        // checks that items are okay
        assertEquals(items, actualList);

        // returned list should be immutable
        assertThatListIsImmutable(actualList);

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

    void verifyBehavior(@NonNull Observable<List<TestItem>> observable) {
        when(storIOContentResolver.observeChangesOfUri(query.uri()))
                .thenReturn(Observable.<Changes>empty());

        new ObservableBehaviorChecker<List<TestItem>>()
                .observable(observable)
                .expectedNumberOfEmissions(1)
                .testAction(new Action1<List<TestItem>>() {
                    @Override
                    public void call(List<TestItem> testItems) {
                        // Get Operation should be subscribed to changes of Uri!
                        verify(storIOContentResolver).observeChangesOfUri(query.uri());

                        verifyBehavior(testItems);
                    }
                })
                .checkBehaviorOfObservable();

    }
}
