package com.pushtorefresh.storio.contentresolver.operation.get;

import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;

import com.pushtorefresh.storio.contentresolver.Changes;
import com.pushtorefresh.storio.contentresolver.StorIOContentResolver;
import com.pushtorefresh.storio.contentresolver.query.Query;
import com.pushtorefresh.storio.test.ObservableBehaviorChecker;

import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.functions.Action1;

import static com.pushtorefresh.storio.test.Asserts.assertThatListIsImmutable;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class GetStub {
    final StorIOContentResolver storIOContentResolver;
    final Query query;
    final GetResolver<TestItem> getResolverForTestItems;
    final GetResolver<Cursor> getResolverForCursor;
    final Cursor cursor;
    final List<TestItem> testItems;
    private final StorIOContentResolver.Internal internal;

    @SuppressWarnings("unchecked")
    GetStub() {
        storIOContentResolver = mock(StorIOContentResolver.class);
        internal = mock(StorIOContentResolver.Internal.class);

        query = Query.builder()
                .uri(mock(Uri.class))
                .build();

        getResolverForTestItems = mock(GetResolver.class);
        getResolverForCursor = mock(GetResolver.class);
        cursor = mock(Cursor.class);

        testItems = new ArrayList<TestItem>();
        testItems.add(new TestItem());
        testItems.add(new TestItem());
        testItems.add(new TestItem());

        when(storIOContentResolver.internal())
                .thenReturn(internal);

        when(cursor.moveToNext())
                .thenAnswer(new Answer<Boolean>() {
                    int invocationsCount = 0;

                    @Override
                    public Boolean answer(InvocationOnMock invocation) throws Throwable {
                        return invocationsCount++ < testItems.size();
                    }
                });

        when(cursor.getCount())
                .thenReturn(testItems.size());

        when(storIOContentResolver.get())
                .thenReturn(new PreparedGet.Builder(storIOContentResolver));

        when(getResolverForTestItems.performGet(storIOContentResolver, query))
                .thenReturn(cursor);

        when(getResolverForCursor.performGet(storIOContentResolver, query))
                .thenReturn(cursor);

        when(storIOContentResolver.observeChangesOfUri(query.uri()))
                .thenReturn(Observable.<Changes>empty());

        when(getResolverForTestItems.mapFromCursor(cursor))
                .thenAnswer(new Answer<TestItem>() {
                    int invocationsCount = 0;

                    @Override
                    public TestItem answer(InvocationOnMock invocation) throws Throwable {
                        final TestItem testItem = testItems.get(invocationsCount);
                        invocationsCount++;
                        return testItem;
                    }
                });

        when(getResolverForCursor.mapFromCursor(cursor))
                .thenReturn(cursor);
    }

    void verifyQueryBehaviorForCursor(@NonNull Cursor actualCursor) {
        verify(storIOContentResolver, times(1)).get();
        verify(getResolverForCursor, times(1)).performGet(storIOContentResolver, query);
        assertSame(cursor, actualCursor);
        verify(cursor, times(0)).close();
    }

    void verifyQueryBehaviorForCursor(@NonNull Observable<Cursor> observable) {
        new ObservableBehaviorChecker<Cursor>()
                .observable(observable)
                .expectedNumberOfEmissions(1)
                .testAction(new Action1<Cursor>() {
                    @Override
                    public void call(Cursor cursor) {
                        verifyQueryBehaviorForCursor(cursor);
                    }
                })
                .checkBehaviorOfObservable();

        verify(storIOContentResolver, times(1)).observeChangesOfUri(query.uri());
    }

    void verifyQueryBehaviorForList(@NonNull List<TestItem> actualList) {
        verify(storIOContentResolver, times(1)).get();
        verify(getResolverForTestItems, times(1)).performGet(storIOContentResolver, query);
        verify(getResolverForTestItems, times(testItems.size())).mapFromCursor(cursor);
        verify(cursor, times(1)).close();
        assertEquals(testItems, actualList);
        assertThatListIsImmutable(actualList);
    }

    void verifyQueryBehaviorForList(@NonNull Observable<List<TestItem>> observable) {
        new ObservableBehaviorChecker<List<TestItem>>()
                .observable(observable)
                .expectedNumberOfEmissions(1)
                .testAction(new Action1<List<TestItem>>() {
                    @Override
                    public void call(List<TestItem> testItems) {
                        verifyQueryBehaviorForList(testItems);
                    }
                })
                .checkBehaviorOfObservable();

        verify(storIOContentResolver, times(1)).observeChangesOfUri(query.uri());
    }
}
