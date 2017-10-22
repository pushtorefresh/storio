package com.pushtorefresh.storio2.sqlite.operations.get;

import android.database.Cursor;

import com.pushtorefresh.storio2.StorIOException;
import com.pushtorefresh.storio2.sqlite.Changes;
import com.pushtorefresh.storio2.sqlite.StorIOSQLite;
import com.pushtorefresh.storio2.sqlite.operations.SchedulerChecker;
import com.pushtorefresh.storio2.sqlite.queries.Query;

import org.junit.Test;
import org.robolectric.util.ReflectionHelpers;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.Single;
import io.reactivex.observers.TestObserver;
import io.reactivex.subscribers.TestSubscriber;

import static io.reactivex.BackpressureStrategy.LATEST;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.assertj.core.api.Assertions.failBecauseExceptionWasNotThrown;
import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class PreparedGetCursorTest {

    @Test
    public void shouldThrowIfNoQueryOrRawQueryIsSet() {
        try {
            final GetCursorStub getStub = GetCursorStub.newInstance();

            final PreparedGetCursor operation = getStub.storIOSQLite
                    .get()
                    .cursor()
                    .withQuery(getStub.query) // will be removed
                    .withGetResolver(getStub.getResolverForCursor)
                    .prepare();

            ReflectionHelpers.setField(operation, "query", null);
            ReflectionHelpers.setField(operation, "rawQuery", null);
            operation.getData();

            failBecauseExceptionWasNotThrown(IllegalStateException.class);
        } catch (IllegalStateException e) {
            assertThat(e).hasMessage("Either rawQuery or query should be set!");
        }
    }

    @Test
    public void shouldReturnQueryInGetData() {
        final GetCursorStub getStub = GetCursorStub.newInstance();

        final PreparedGetCursor operation =  getStub.storIOSQLite
                .get()
                .cursor()
                .withQuery(getStub.query)
                .withGetResolver(getStub.getResolverForCursor)
                .prepare();

        assertThat(operation.getData()).isEqualTo(getStub.query);
    }

    @Test
    public void shouldReturnRawQueryInGetData() {
        final GetCursorStub getStub = GetCursorStub.newInstance();

        final PreparedGetCursor operation =  getStub.storIOSQLite
                .get()
                .cursor()
                .withQuery(getStub.rawQuery)
                .withGetResolver(getStub.getResolverForCursor)
                .prepare();

        assertThat(operation.getData()).isEqualTo(getStub.rawQuery);
    }

    @Test
    public void shouldGetCursorWithQueryBlocking() {
        final GetCursorStub getStub = GetCursorStub.newInstance();

        final Cursor cursor = getStub.storIOSQLite
                .get()
                .cursor()
                .withQuery(getStub.query)
                .withGetResolver(getStub.getResolverForCursor)
                .prepare()
                .executeAsBlocking();

        verify(getStub.storIOSQLite, never()).defaultRxScheduler();
        getStub.verifyQueryBehaviorForCursor(cursor);
    }

    @Test
    public void shouldGetCursorWithQueryAsSingle() {
        final GetCursorStub getStub = GetCursorStub.newInstance();

        final Single<Cursor> cursorSingle = getStub.storIOSQLite
                .get()
                .cursor()
                .withQuery(getStub.query)
                .withGetResolver(getStub.getResolverForCursor)
                .prepare()
                .asRxSingle();

        verify(getStub.storIOSQLite).defaultRxScheduler();
        getStub.verifyQueryBehaviorForCursor(cursorSingle);
    }

    @Test
    public void shouldGetCursorWithRawQueryBlocking() {
        final GetCursorStub getStub = GetCursorStub.newInstance();

        final Cursor cursor = getStub.storIOSQLite
                .get()
                .cursor()
                .withQuery(getStub.rawQuery)
                .withGetResolver(getStub.getResolverForCursor)
                .prepare()
                .executeAsBlocking();

        verify(getStub.storIOSQLite, never()).defaultRxScheduler();
        getStub.verifyRawQueryBehaviorForCursor(cursor);
    }

    @Test
    public void shouldGetCursorWithRawQueryAsFlowable() {
        final GetCursorStub getStub = GetCursorStub.newInstance();

        final Flowable<Cursor> cursorFlowable = getStub.storIOSQLite
                .get()
                .cursor()
                .withQuery(getStub.rawQuery)
                .withGetResolver(getStub.getResolverForCursor)
                .prepare()
                .asRxFlowable(LATEST)
                .take(1);

        verify(getStub.storIOSQLite).defaultRxScheduler();
        getStub.verifyRawQueryBehaviorForCursor(cursorFlowable);
    }

    @Test
    public void shouldGetCursorWithRawQueryAsSingle() {
        final GetCursorStub getStub = GetCursorStub.newInstance();

        final Single<Cursor> cursorSingle = getStub.storIOSQLite
                .get()
                .cursor()
                .withQuery(getStub.rawQuery)
                .withGetResolver(getStub.getResolverForCursor)
                .prepare()
                .asRxSingle();

        verify(getStub.storIOSQLite).defaultRxScheduler();
        getStub.verifyRawQueryBehaviorForCursor(cursorSingle);
    }

    @Test
    public void shouldWrapExceptionIntoStorIOExceptionForBlocking() {
        final StorIOSQLite storIOSQLite = mock(StorIOSQLite.class);

        //noinspection unchecked
        final GetResolver<Cursor> getResolver = mock(GetResolver.class);

        when(getResolver.performGet(eq(storIOSQLite), any(Query.class)))
                .thenThrow(new IllegalStateException("test exception"));

        try {
            new PreparedGetCursor.Builder(storIOSQLite)
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
    public void shouldWrapExceptionIntoStorIOExceptionForFlowable() {
        final StorIOSQLite storIOSQLite = mock(StorIOSQLite.class);

        when(storIOSQLite.observeChanges(any(BackpressureStrategy.class))).thenReturn(Flowable.<Changes>empty());

        //noinspection unchecked
        final GetResolver<Cursor> getResolver = mock(GetResolver.class);

        when(getResolver.performGet(eq(storIOSQLite), any(Query.class)))
                .thenThrow(new IllegalStateException("test exception"));

        final TestSubscriber<Cursor> testSubscriber = new TestSubscriber<Cursor>();

        new PreparedGetCursor.Builder(storIOSQLite)
                .withQuery(Query.builder().table("test_table").observesTags("test_tag").build())
                .withGetResolver(getResolver)
                .prepare()
                .asRxFlowable(LATEST)
                .subscribe(testSubscriber);

        testSubscriber.awaitTerminalEvent(60, SECONDS);
        testSubscriber.assertError(StorIOException.class);

        StorIOException storIOException = (StorIOException) testSubscriber.errors().get(0);
        IllegalStateException cause = (IllegalStateException) storIOException.getCause();
        assertThat(cause).hasMessage("test exception");

        testSubscriber.dispose();
    }

    @Test
    public void shouldWrapExceptionIntoStorIOExceptionForSingle() {
        final StorIOSQLite storIOSQLite = mock(StorIOSQLite.class);

        //noinspection unchecked
        final GetResolver<Cursor> getResolver = mock(GetResolver.class);

        when(getResolver.performGet(eq(storIOSQLite), any(Query.class)))
                .thenThrow(new IllegalStateException("test exception"));

        final TestObserver<Cursor> testObserver = new TestObserver<Cursor>();

        new PreparedGetCursor.Builder(storIOSQLite)
                .withQuery(Query.builder().table("test_table").build())
                .withGetResolver(getResolver)
                .prepare()
                .asRxSingle()
                .subscribe(testObserver);

        testObserver.awaitTerminalEvent(60, SECONDS);
        testObserver.assertError(StorIOException.class);

        StorIOException storIOException = (StorIOException) testObserver.errors().get(0);
        IllegalStateException cause = (IllegalStateException) storIOException.getCause();
        assertThat(cause).hasMessage("test exception");
    }

    @Test
    public void completeBuilderShouldThrowExceptionIfNoQueryWasSet() {
        PreparedGetCursor.CompleteBuilder completeBuilder = new PreparedGetCursor.Builder(mock(StorIOSQLite.class))
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
        PreparedGetCursor preparedGetCursor
                = new PreparedGetCursor(mock(StorIOSQLite.class), (Query) null, (GetResolver<Cursor>) mock(GetResolver.class));

        try {
            preparedGetCursor.executeAsBlocking();
            failBecauseExceptionWasNotThrown(StorIOException.class);
        } catch (StorIOException expected) {
            IllegalStateException cause = (IllegalStateException) expected.getCause();
            assertThat(cause).hasMessage("Please specify query");
        }
    }

    @Test
    public void asRxFlowableShouldThrowExceptionIfNoQueryWasSet() {
        //noinspection unchecked,ConstantConditions
        PreparedGetCursor preparedGetCursor
                = new PreparedGetCursor(mock(StorIOSQLite.class), (Query) null, (GetResolver<Cursor>) mock(GetResolver.class));

        try {
            //noinspection ResourceType
            preparedGetCursor.asRxFlowable(LATEST);
            failBecauseExceptionWasNotThrown(StorIOException.class);
        } catch (StorIOException expected) {
            assertThat(expected).hasMessage("Please specify query");
        }
    }

    @Test
    public void verifyThatStandardGetResolverDoesNotModifyCursor() {
        final GetCursorStub getStub = GetCursorStub.newInstance();
        final GetResolver<Cursor> standardGetResolver
                = PreparedGetCursor.CompleteBuilder.STANDARD_GET_RESOLVER;

        final Cursor cursor = mock(Cursor.class);

        assertThat(standardGetResolver.mapFromCursor(getStub.storIOSQLite, cursor)).isSameAs(cursor);
    }

    @Test
    public void getCursorFlowableExecutesOnSpecifiedScheduler() {
        final GetCursorStub getStub = GetCursorStub.newInstance();
        final SchedulerChecker schedulerChecker = SchedulerChecker.create(getStub.storIOSQLite);

        final PreparedGetCursor operation = getStub.storIOSQLite
                .get()
                .cursor()
                .withQuery(getStub.query)
                .withGetResolver(getStub.getResolverForCursor)
                .prepare();

        schedulerChecker.checkAsFlowable(operation);
    }

    @Test
    public void getCursorSingleExecutesOnSpecifiedScheduler() {
        final GetCursorStub getStub = GetCursorStub.newInstance();
        final SchedulerChecker schedulerChecker = SchedulerChecker.create(getStub.storIOSQLite);

        final PreparedGetCursor operation = getStub.storIOSQLite
                .get()
                .cursor()
                .withQuery(getStub.query)
                .withGetResolver(getStub.getResolverForCursor)
                .prepare();

        schedulerChecker.checkAsSingle(operation);
    }
}
