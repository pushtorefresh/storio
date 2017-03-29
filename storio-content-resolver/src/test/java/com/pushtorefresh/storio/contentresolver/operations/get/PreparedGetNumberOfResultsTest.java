package com.pushtorefresh.storio.contentresolver.operations.get;

import android.database.Cursor;
import android.net.Uri;

import com.pushtorefresh.storio.StorIOException;
import com.pushtorefresh.storio.contentresolver.Changes;
import com.pushtorefresh.storio.contentresolver.StorIOContentResolver;
import com.pushtorefresh.storio.contentresolver.operations.SchedulerChecker;
import com.pushtorefresh.storio.contentresolver.queries.Query;

import org.junit.Test;

import rx.Observable;
import rx.Single;
import rx.observers.TestSubscriber;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.assertj.core.api.Fail.failBecauseExceptionWasNotThrown;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class PreparedGetNumberOfResultsTest {
    @Test
    public void shouldGetNumberOfResultsWithQueryBlocking() {
        final GetNumberOfResultsStub getStub = GetNumberOfResultsStub.newInstance();

        final Integer numberOfResults = getStub.storIOContentResolver
                .get()
                .numberOfResults()
                .withQuery(getStub.query)
                .withGetResolver(getStub.getResolverForNumberOfResults)
                .prepare()
                .executeAsBlocking();

        getStub.verifyQueryBehaviorForInteger(numberOfResults);
    }

    @Test
    public void shouldGetNumberOfResultsWithQueryAsObservable() {
        final GetNumberOfResultsStub getStub = GetNumberOfResultsStub.newInstance();

        final Observable<Integer> numberOfResultsObservable = getStub.storIOContentResolver
                .get()
                .numberOfResults()
                .withQuery(getStub.query)
                .withGetResolver(getStub.getResolverForNumberOfResults)
                .prepare()
                .asRxObservable()
                .take(1);

        getStub.verifyQueryBehaviorForInteger(numberOfResultsObservable);
    }

    @Test
    public void shouldGetNumberOfResultsWithQueryAsSingle() {
        final GetNumberOfResultsStub getStub = GetNumberOfResultsStub.newInstance();

        final Single<Integer> numberOfResultsSingle = getStub.storIOContentResolver
                .get()
                .numberOfResults()
                .withQuery(getStub.query)
                .withGetResolver(getStub.getResolverForNumberOfResults)
                .prepare()
                .asRxSingle();

        getStub.verifyQueryBehaviorForInteger(numberOfResultsSingle);
    }

    @Test
    public void shouldWrapExceptionIntoStorIOExceptionForBlocking() {
        final StorIOContentResolver storIOContentResolver = mock(StorIOContentResolver.class);

        //noinspection unchecked
        final GetResolver<Integer> getResolver = mock(GetResolver.class);

        when(getResolver.performGet(eq(storIOContentResolver), any(Query.class)))
                .thenThrow(new IllegalStateException("test exception"));

        try {
            new PreparedGetNumberOfResults.Builder(storIOContentResolver)
                    .withQuery(Query.builder().uri(mock(Uri.class)).build())
                    .withGetResolver(getResolver)
                    .prepare()
                    .executeAsBlocking();

            failBecauseExceptionWasNotThrown(StorIOException.class);
        } catch (StorIOException expected) {
            IllegalStateException cause = (IllegalStateException) expected.getCause();
            assertThat(cause).hasMessage("test exception");
        }
    }

    @Test
    public void shouldWrapExceptionIntoStorIOExceptionForObservable() {
        final StorIOContentResolver storIOContentResolver = mock(StorIOContentResolver.class);

        Uri testUri = mock(Uri.class);
        when(storIOContentResolver.observeChangesOfUri(eq(testUri)))
                .thenReturn(Observable.<Changes>empty());

        //noinspection unchecked
        final GetResolver<Integer> getResolver = mock(GetResolver.class);

        when(getResolver.performGet(eq(storIOContentResolver), any(Query.class)))
                .thenThrow(new IllegalStateException("test exception"));

        final TestSubscriber<Integer> testSubscriber = new TestSubscriber<Integer>();

        new PreparedGetNumberOfResults.Builder(storIOContentResolver)
                .withQuery(Query.builder().uri(testUri).build())
                .withGetResolver(getResolver)
                .prepare()
                .asRxObservable()
                .subscribe(testSubscriber);

        testSubscriber.awaitTerminalEvent(60, SECONDS);
        testSubscriber.assertError(StorIOException.class);

        assertThat(testSubscriber.getOnErrorEvents()).hasSize(1);
        StorIOException storIOException = (StorIOException) testSubscriber.getOnErrorEvents().get(0);
        IllegalStateException cause = (IllegalStateException) storIOException.getCause();
        assertThat(cause).hasMessage("test exception");

        testSubscriber.unsubscribe();
    }

    @Test
    public void shouldWrapExceptionIntoStorIOExceptionForSingle() {
        final StorIOContentResolver storIOContentResolver = mock(StorIOContentResolver.class);

        Uri testUri = mock(Uri.class);

        //noinspection unchecked
        final GetResolver<Integer> getResolver = mock(GetResolver.class);

        when(getResolver.performGet(eq(storIOContentResolver), any(Query.class)))
                .thenThrow(new IllegalStateException("test exception"));

        final TestSubscriber<Integer> testSubscriber = new TestSubscriber<Integer>();

        new PreparedGetNumberOfResults.Builder(storIOContentResolver)
                .withQuery(Query.builder().uri(testUri).build())
                .withGetResolver(getResolver)
                .prepare()
                .asRxSingle()
                .subscribe(testSubscriber);

        testSubscriber.awaitTerminalEvent(60, SECONDS);
        testSubscriber.assertError(StorIOException.class);

        assertThat(testSubscriber.getOnErrorEvents()).hasSize(1);
        StorIOException storIOException = (StorIOException) testSubscriber.getOnErrorEvents().get(0);
        IllegalStateException cause = (IllegalStateException) storIOException.getCause();
        assertThat(cause).hasMessage("test exception");

        testSubscriber.unsubscribe();
    }

    @Test
    public void verifyThatStandardGetResolverJustReturnsCursorGetCount() {
        final GetResolver<Integer> standardGetResolver
                = PreparedGetNumberOfResults.CompleteBuilder.STANDARD_GET_RESOLVER;

        final Cursor cursor = mock(Cursor.class);

        when(cursor.getCount()).thenReturn(12314);

        assertThat(standardGetResolver.mapFromCursor(cursor)).isEqualTo(12314);
    }

    @Test
    public void getNumberOfResultsObservableExecutesOnSpecifiedScheduler() {
        final GetNumberOfResultsStub getStub = GetNumberOfResultsStub.newInstance();
        final SchedulerChecker schedulerChecker = SchedulerChecker.create(getStub.storIOContentResolver);

        final PreparedGetNumberOfResults operation = getStub.storIOContentResolver
                .get()
                .numberOfResults()
                .withQuery(getStub.query)
                .withGetResolver(getStub.getResolverForNumberOfResults)
                .prepare();

        schedulerChecker.checkAsObservable(operation);
    }

    @Test
    public void getNumberOfResultsSingleExecutesOnSpecifiedScheduler() {
        final GetNumberOfResultsStub getStub = GetNumberOfResultsStub.newInstance();
        final SchedulerChecker schedulerChecker = SchedulerChecker.create(getStub.storIOContentResolver);

        final PreparedGetNumberOfResults operation = getStub.storIOContentResolver
                .get()
                .numberOfResults()
                .withQuery(getStub.query)
                .withGetResolver(getStub.getResolverForNumberOfResults)
                .prepare();

        schedulerChecker.checkAsSingle(operation);
    }
}
