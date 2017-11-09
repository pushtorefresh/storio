package com.pushtorefresh.storio2.contentresolver.operations.get;

import android.database.Cursor;
import android.net.Uri;

import com.pushtorefresh.storio2.StorIOException;
import com.pushtorefresh.storio2.contentresolver.Changes;
import com.pushtorefresh.storio2.contentresolver.StorIOContentResolver;
import com.pushtorefresh.storio2.contentresolver.operations.SchedulerChecker;
import com.pushtorefresh.storio2.contentresolver.queries.Query;

import org.junit.Test;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.Single;
import io.reactivex.observers.TestObserver;
import io.reactivex.subscribers.TestSubscriber;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.assertj.core.api.Fail.failBecauseExceptionWasNotThrown;
import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class PreparedGetNumberOfResultsTest {

    @Test
    public void shouldReturnQueryInGetData() {
        final GetNumberOfResultsStub getStub = GetNumberOfResultsStub.newInstance();

        final PreparedGetNumberOfResults operation = getStub.storIOContentResolver
                .get()
                .numberOfResults()
                .withQuery(getStub.query)
                .withGetResolver(getStub.getResolverForNumberOfResults)
                .prepare();

        assertThat(operation.getData()).isEqualTo(getStub.query);
    }

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
    public void shouldGetNumberOfResultsWithQueryAsFlowable() {
        final GetNumberOfResultsStub getStub = GetNumberOfResultsStub.newInstance();

        final Flowable<Integer> numberOfResultsFlowable = getStub.storIOContentResolver
                .get()
                .numberOfResults()
                .withQuery(getStub.query)
                .withGetResolver(getStub.getResolverForNumberOfResults)
                .prepare()
                .asRxFlowable(BackpressureStrategy.MISSING)
                .take(1);

        getStub.verifyQueryBehaviorForInteger(numberOfResultsFlowable);
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
    public void shouldWrapExceptionIntoStorIOExceptionForFlowable() {
        final StorIOContentResolver storIOContentResolver = mock(StorIOContentResolver.class);

        Uri testUri = mock(Uri.class);
        when(storIOContentResolver.observeChangesOfUri(eq(testUri), eq(BackpressureStrategy.MISSING)))
                .thenReturn(Flowable.<Changes>empty());

        //noinspection unchecked
        final GetResolver<Integer> getResolver = mock(GetResolver.class);

        when(getResolver.performGet(eq(storIOContentResolver), any(Query.class)))
                .thenThrow(new IllegalStateException("test exception"));

        final TestSubscriber<Integer> testSubscriber = new TestSubscriber<Integer>();

        new PreparedGetNumberOfResults.Builder(storIOContentResolver)
                .withQuery(Query.builder().uri(testUri).build())
                .withGetResolver(getResolver)
                .prepare()
                .asRxFlowable(BackpressureStrategy.MISSING)
                .subscribe(testSubscriber);

        testSubscriber.awaitTerminalEvent(60, SECONDS);
        testSubscriber.assertError(StorIOException.class);

        assertThat(testSubscriber.errors()).hasSize(1);
        StorIOException storIOException = (StorIOException) testSubscriber.errors().get(0);
        IllegalStateException cause = (IllegalStateException) storIOException.getCause();
        assertThat(cause).hasMessage("test exception");

        testSubscriber.dispose();
    }

    @Test
    public void shouldWrapExceptionIntoStorIOExceptionForSingle() {
        final StorIOContentResolver storIOContentResolver = mock(StorIOContentResolver.class);

        Uri testUri = mock(Uri.class);

        //noinspection unchecked
        final GetResolver<Integer> getResolver = mock(GetResolver.class);

        when(getResolver.performGet(eq(storIOContentResolver), any(Query.class)))
                .thenThrow(new IllegalStateException("test exception"));

        final TestObserver<Integer> testObserver = new TestObserver<Integer>();

        new PreparedGetNumberOfResults.Builder(storIOContentResolver)
                .withQuery(Query.builder().uri(testUri).build())
                .withGetResolver(getResolver)
                .prepare()
                .asRxSingle()
                .subscribe(testObserver);

        testObserver.awaitTerminalEvent(60, SECONDS);
        testObserver.assertError(StorIOException.class);

        assertThat(testObserver.errors()).hasSize(1);
        StorIOException storIOException = (StorIOException) testObserver.errors().get(0);
        IllegalStateException cause = (IllegalStateException) storIOException.getCause();
        assertThat(cause).hasMessage("test exception");

        testObserver.dispose();
    }

    @Test
    public void verifyThatStandardGetResolverJustReturnsCursorGetCount() {
        final GetNumberOfResultsStub getStub = GetNumberOfResultsStub.newInstance();
        final GetResolver<Integer> standardGetResolver
                = PreparedGetNumberOfResults.CompleteBuilder.STANDARD_GET_RESOLVER;

        final Cursor cursor = mock(Cursor.class);

        when(cursor.getCount()).thenReturn(12314);

        assertThat(standardGetResolver.mapFromCursor(getStub.storIOContentResolver, cursor)).isEqualTo(12314);
    }

    @Test
    public void getNumberOfResultsFlowableExecutesOnSpecifiedScheduler() {
        final GetNumberOfResultsStub getStub = GetNumberOfResultsStub.newInstance();
        final SchedulerChecker schedulerChecker = SchedulerChecker.create(getStub.storIOContentResolver);

        final PreparedGetNumberOfResults operation = getStub.storIOContentResolver
                .get()
                .numberOfResults()
                .withQuery(getStub.query)
                .withGetResolver(getStub.getResolverForNumberOfResults)
                .prepare();

        schedulerChecker.checkAsFlowable(operation);
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

    @Test
    public void shouldPassStorIOContentResolverToGetResolver() {
        final GetNumberOfResultsStub getStub = GetNumberOfResultsStub.newInstance();
        getStub.storIOContentResolver
                .get()
                .numberOfResults()
                .withQuery(getStub.query)
                .withGetResolver(getStub.getResolverForNumberOfResults)
                .prepare()
                .executeAsBlocking();

        verify(getStub.getResolverForNumberOfResults).mapFromCursor(eq(getStub.storIOContentResolver), any(Cursor.class));
    }
}
