package com.pushtorefresh.storio.sqlite.impl;

import com.pushtorefresh.storio.sqlite.Changes;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.Collections;
import java.util.HashSet;

import rx.Observable;
import rx.observers.TestSubscriber;

import static java.util.Collections.singleton;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.nullValue;

public class ChangesFilterTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void applyForTables_throwsIfTablesNull() {
        expectedException.expect(NullPointerException.class);
        expectedException.expectMessage(equalTo("Set of tables can not be null"));
        expectedException.expectCause(nullValue(Throwable.class));

        //noinspection ConstantConditions
        ChangesFilter.applyForTables(Observable.<Changes>empty(), null);
    }

    @Test
    public void applyForTables_shouldFilterRequiredTable() {
        final TestSubscriber<Changes> testSubscriber = new TestSubscriber<Changes>();

        ChangesFilter
                .applyForTables(
                        Observable.just(Changes.newInstance("table1"),
                                Changes.newInstance("table2"),
                                Changes.newInstance("table3")),
                        singleton("table2"))
                .subscribe(testSubscriber);

        // All other tables should be filtered
        testSubscriber.assertValue(Changes.newInstance("table2"));

        testSubscriber.unsubscribe();
    }

    @Test
    public void applyForTables_shouldFilterRequiredTableWhichIsPartOfSomeChanges() {
        final TestSubscriber<Changes> testSubscriber = new TestSubscriber<Changes>();

        ChangesFilter
                .applyForTables(
                        Observable.just(Changes.newInstance("table1"),
                                Changes.newInstance(new HashSet<String>() {
                                    {
                                        add("table1");
                                        // Notice, that required table
                                        // Is just a part of one Changes object
                                        add("table2");
                                        add("table3");
                                    }
                                })),
                        singleton("table3"))
                .subscribe(testSubscriber);

        // All other Changes should be filtered
        testSubscriber.assertValue(Changes.newInstance(new HashSet<String>() {
            {
                add("table1");
                add("table2");
                add("table3");
            }
        }));

        testSubscriber.unsubscribe();
    }

    @Test
    public void applyForTables_throwsIfTagsNull() {
        expectedException.expect(NullPointerException.class);
        expectedException.expectMessage(equalTo("Set of tags can not be null"));
        expectedException.expectCause(nullValue(Throwable.class));

        //noinspection ConstantConditions
        ChangesFilter.applyForTags(Observable.<Changes>empty(), null);
    }

    @Test
    public void applyForTags_shouldFilterRequiredTag() {
        final TestSubscriber<Changes> testSubscriber = new TestSubscriber<Changes>();

        ChangesFilter
                .applyForTags(
                        Observable.just(
                                Changes.newInstance("table1", "tag1"),
                                Changes.newInstance("table2", "tag2"),
                                Changes.newInstance("table3")),
                        singleton("tag1"))
                .subscribe(testSubscriber);

        // All other tags should be filtered
        testSubscriber.assertValue(Changes.newInstance("table1", "tag1"));

        testSubscriber.unsubscribe();
    }

    @Test
    public void applyForTags_shouldFilterRequiredTagWhichIsPartOfSomeChanges() {
        final TestSubscriber<Changes> testSubscriber = new TestSubscriber<Changes>();

        Changes changes = Changes.newInstance(
                new HashSet<String>() {{
                    add("table1");
                }},
                new HashSet<String>() {{
                    add("tag1");
                    add("tag2");
                }}
        );

        ChangesFilter
                .applyForTags(
                        Observable.just(
                                changes,
                                Changes.newInstance("table3", "tag3"),
                                Changes.newInstance("table4")),
                        singleton("tag1"))
                .subscribe(testSubscriber);

        // All other tags should be filtered
        testSubscriber.assertValue(changes);

        testSubscriber.unsubscribe();
    }

    @Test
    public void applyForTablesAndTags_throwsIfTablesNull() {
        expectedException.expect(NullPointerException.class);
        expectedException.expectMessage(equalTo("Set of tables can not be null"));
        expectedException.expectCause(nullValue(Throwable.class));

        //noinspection ConstantConditions
        ChangesFilter.applyForTablesAndTags(Observable.<Changes>empty(), null, Collections.<String>emptySet());
    }

    @Test
    public void applyForTablesAndTags_throwsIfTagsNull() {
        expectedException.expect(NullPointerException.class);
        expectedException.expectMessage(equalTo("Set of tags can not be null"));
        expectedException.expectCause(nullValue(Throwable.class));

        //noinspection ConstantConditions
        ChangesFilter.applyForTablesAndTags(Observable.<Changes>empty(), Collections.<String>emptySet(), null);
    }

    @Test
    public void applyForTablesAndTags_shouldNotifyByTable() {
        final TestSubscriber<Changes> testSubscriber = new TestSubscriber<Changes>();

        ChangesFilter
                .applyForTablesAndTags(
                        Observable.just(Changes.newInstance("table1", "another_tag"),
                                Changes.newInstance("table2", "tag2"),
                                Changes.newInstance("table3")),
                        singleton("table1"),
                        singleton("tag1"))
                .subscribe(testSubscriber);

        // All other Changes should be filtered
        testSubscriber.assertValue(Changes.newInstance("table1", "another_tag"));

        testSubscriber.unsubscribe();
    }

    @Test
    public void applyForTablesAndTags_shouldNotifyByTag() {
        final TestSubscriber<Changes> testSubscriber = new TestSubscriber<Changes>();

        ChangesFilter
                .applyForTablesAndTags(
                        Observable.just(Changes.newInstance("another_table", "tag1"),
                                Changes.newInstance("table2", "tag2"),
                                Changes.newInstance("table3")),
                        singleton("table1"),
                        singleton("tag1"))
                .subscribe(testSubscriber);

        // All other Changes should be filtered
        testSubscriber.assertValue(Changes.newInstance("another_table", "tag1"));

        testSubscriber.unsubscribe();
    }

    @Test
    public void applyForTablesAndTags_shouldSendJustOnceNotificationIfBothTableAndTagAreSatisfy() {
        final TestSubscriber<Changes> testSubscriber = new TestSubscriber<Changes>();

        ChangesFilter
                .applyForTablesAndTags(
                        Observable.just(Changes.newInstance("target_table", "target_tag")),
                        singleton("target_table"),
                        singleton("target_tag"))
                .subscribe(testSubscriber);

        testSubscriber.assertValueCount(1);
        testSubscriber.assertValue(Changes.newInstance("target_table", "target_tag"));

        testSubscriber.unsubscribe();
    }
}
