package com.pushtorefresh.storio.sqlite.operations.get;

import android.database.Cursor;

import com.pushtorefresh.storio.StorIOException;
import com.pushtorefresh.storio.sqlite.Changes;
import com.pushtorefresh.storio.sqlite.StorIOSQLite;
import com.pushtorefresh.storio.sqlite.operations.SchedulerChecker;
import com.pushtorefresh.storio.sqlite.queries.Query;

import org.junit.Test;

import rx.Observable;
import rx.Single;
import rx.observers.TestSubscriber;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.assertj.core.api.Assertions.failBecauseExceptionWasNotThrown;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class PreparedGetNumberOfResultsTest {

    @Test
    public void shouldGetNumberOfResultsWithQueryBlocking() {
        final GetNumberOfResultsStub getStub = GetNumberOfResultsStub.newInstance();

        final Integer numberOfResults = getStub.storIOSQLite
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

        final Observable<Integer> numberOfResultsObservable = getStub.storIOSQLite
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

        final Single<Integer> numberOfResultsSingle = getStub.storIOSQLite
                .get()
                .numberOfResults()
                .withQuery(getStub.query)
                .withGetResolver(getStub.getResolverForNumberOfResults)
                .prepare()
                .asRxSingle();

        getStub.verifyQueryBehaviorForInteger(numberOfResultsSingle);
    }

    @Test
    public void shouldGetNumberOfResultsWithRawQueryBlocking() {
        final GetNumberOfResultsStub getStub = GetNumberOfResultsStub.newInstance();

        final Integer numberOfResults = getStub.storIOSQLite
                .get()
                .numberOfResults()
                .withQuery(getStub.rawQuery)
                .withGetResolver(getStub.getResolverForNumberOfResults)
                .prepare()
                .executeAsBlocking();

        getStub.verifyRawQueryBehaviorForInteger(numberOfResults);
    }

    @Test
    public void shouldGetNumberOfResultsWithRawQueryAsObservable() {
        final GetNumberOfResultsStub getStub = GetNumberOfResultsStub.newInstance();

        final Observable<Integer> numberOfResultsObservable = getStub.storIOSQLite
                .get()
                .numberOfResults()
                .withQuery(getStub.rawQuery)
                .withGetResolver(getStub.getResolverForNumberOfResults)
                .prepare()
                .asRxObservable()
                .take(1);

        getStub.verifyRawQueryBehaviorForInteger(numberOfResultsObservable);
    }

    @Test
    public void shouldGetNumberOfResultsWithRawQueryAsSingle() {
        final GetNumberOfResultsStub getStub = GetNumberOfResultsStub.newInstance();

        final Single<Integer> numberOfResultsSingle = getStub.storIOSQLite
                .get()
                .numberOfResults()
                .withQuery(getStub.rawQuery)
                .withGetResolver(getStub.getResolverForNumberOfResults)
                .prepare()
                .asRxSingle();

        getStub.verifyRawQueryBehaviorForInteger(numberOfResultsSingle);
    }

    @Test
    public void shouldWrapExceptionIntoStorIOExceptionForBlocking() {
        final StorIOSQLite storIOSQLite = mock(StorIOSQLite.class);

        //noinspection unchecked
        final GetResolver<Integer> getResolver = mock(GetResolver.class);

        when(getResolver.performGet(eq(storIOSQLite), any(Query.class)))
                .thenThrow(new IllegalStateException("test exception"));

        try {
            new PreparedGetNumberOfResults.Builder(storIOSQLite)
                    .withQuery(Query.builder().table("test_table").build())
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
        final StorIOSQLite storIOSQLite = mock(StorIOSQLite.class);

        when(storIOSQLite.observeChanges()).thenReturn(Observable.<Changes>empty());

        //noinspection unchecked
        final GetResolver<Integer> getResolver = mock(GetResolver.class);

        when(getResolver.performGet(eq(storIOSQLite), any(Query.class)))
                .thenThrow(new IllegalStateException("test exception"));

        final TestSubscriber<Integer> testSubscriber = new TestSubscriber<Integer>();

        new PreparedGetNumberOfResults.Builder(storIOSQLite)
                .withQuery(Query.builder().table("test_table").observesTags("test_tag").build())
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
        final StorIOSQLite storIOSQLite = mock(StorIOSQLite.class);

        //noinspection unchecked
        final GetResolver<Integer> getResolver = mock(GetResolver.class);

        when(getResolver.performGet(eq(storIOSQLite), any(Query.class)))
                .thenThrow(new IllegalStateException("test exception"));

        final TestSubscriber<Integer> testSubscriber = new TestSubscriber<Integer>();

        new PreparedGetNumberOfResults.Builder(storIOSQLite)
                .withQuery(Query.builder().table("test_table").build())
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
    }

    @Test
    public void completeBuilderShouldThrowExceptionIfNoQueryWasSet() {
        PreparedGetNumberOfResults.CompleteBuilder completeBuilder = new PreparedGetNumberOfResults.Builder(mock(StorIOSQLite.class))
                .withQuery(Query.builder().table("test_table").build()); // We will null it later

        completeBuilder.query = null;

        try {
            completeBuilder.prepare();
            failBecauseExceptionWasNotThrown(IllegalStateException.class);
        } catch (IllegalStateException expected) {
            assertThat(expected).hasMessage("Please specify query");
        }
    }

    @Test
    public void executeAsBlockingShouldThrowExceptionIfNoQueryWasSet() {
        //noinspection unchecked,ConstantConditions
        PreparedGetNumberOfResults preparedGetNumberOfResults
                = new PreparedGetNumberOfResults(mock(StorIOSQLite.class), (Query) null, (GetResolver<Integer>) mock(GetResolver.class));

        try {
            preparedGetNumberOfResults.executeAsBlocking();
            failBecauseExceptionWasNotThrown(StorIOException.class);
        } catch (StorIOException expected) {
            IllegalStateException cause = (IllegalStateException) expected.getCause();
            assertThat(cause).hasMessage("Please specify query");
        }
    }

    @Test
    public void asRxObservableShouldThrowExceptionIfNoQueryWasSet() {
        //noinspection unchecked,ConstantConditions
        PreparedGetNumberOfResults preparedGetNumberOfResults
                = new PreparedGetNumberOfResults(mock(StorIOSQLite.class), (Query) null, (GetResolver<Integer>) mock(GetResolver.class));

        try {
            //noinspection CheckResult
            preparedGetNumberOfResults.asRxObservable();
            failBecauseExceptionWasNotThrown(StorIOException.class);
        } catch (StorIOException expected) {
            assertThat(expected).hasMessage("Please specify query");
        }
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
        final SchedulerChecker schedulerChecker = SchedulerChecker.create(getStub.storIOSQLite);

        final PreparedGetNumberOfResults operation = getStub.storIOSQLite
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
        final SchedulerChecker schedulerChecker = SchedulerChecker.create(getStub.storIOSQLite);

        final PreparedGetNumberOfResults operation = getStub.storIOSQLite
                .get()
                .numberOfResults()
                .withQuery(getStub.query)
                .withGetResolver(getStub.getResolverForNumberOfResults)
                .prepare();

        schedulerChecker.checkAsSingle(operation);
    }

    @Test
    public void createObservableReturnsAsRxObservable() {
        final GetNumberOfResultsStub getStub = GetNumberOfResultsStub.newInstance();

        PreparedGetNumberOfResults preparedOperation = spy(getStub.storIOSQLite
                .get()
                .numberOfResults()
                .withQuery(getStub.query)
                .withGetResolver(getStub.getResolverForNumberOfResults)
                .prepare());

        Observable<Integer> observable = Observable.just(0);

        //noinspection CheckResult
        doReturn(observable).when(preparedOperation).asRxObservable();

        //noinspection deprecation
        assertThat(preparedOperation.createObservable()).isEqualTo(observable);

        //noinspection CheckResult
        verify(preparedOperation).asRxObservable();
    }
}
