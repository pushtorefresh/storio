package com.pushtorefresh.storio.sqlite.impl;

import com.pushtorefresh.storio.sqlite.Changes;

import org.junit.Test;

import java.util.HashSet;

import rx.Observable;
import rx.observers.TestSubscriber;

import static java.util.Collections.singleton;

public class ChangesFilterTest {

    @Test
    public void shouldFilterRequiredTable() {
        final TestSubscriber<Changes> testSubscriber = new TestSubscriber<Changes>();

        ChangesFilter
                .apply(
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
    public void shouldFilterRequiredTableWhichIsPartOfSomeChanges() {
        final TestSubscriber<Changes> testSubscriber = new TestSubscriber<Changes>();

        ChangesFilter
                .apply(
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
}
