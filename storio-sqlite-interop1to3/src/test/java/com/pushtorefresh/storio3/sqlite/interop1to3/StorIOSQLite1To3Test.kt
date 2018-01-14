@file:Suppress("MemberVisibilityCanPrivate")

package com.pushtorefresh.storio3.sqlite.interop1to3

import com.nhaarman.mockito_kotlin.mock
import io.reactivex.BackpressureStrategy
import org.junit.Before
import org.junit.Test
import com.pushtorefresh.storio.sqlite.Changes as Changes1
import com.pushtorefresh.storio.sqlite.StorIOSQLite as StorIOSQLite1
import com.pushtorefresh.storio.sqlite.impl.DefaultStorIOSQLite as DefaultStorIOSQLite1
import com.pushtorefresh.storio3.sqlite.Changes as Changes3
import com.pushtorefresh.storio3.sqlite.StorIOSQLite as StorIOSQLite3
import com.pushtorefresh.storio3.sqlite.impl.DefaultStorIOSQLite as DefaultStorIOSQLite3
import io.reactivex.subscribers.TestSubscriber as TestSubscriberRx2
import rx.observers.TestSubscriber as TestSubscriberRx1

class StorIOSQLite1To3Test {

    val observer1 = TestSubscriberRx1.create<Changes1>()!!

    val observer3 = TestSubscriberRx2.create<Changes3>()!!

    lateinit var storIOSQLite1: StorIOSQLite1

    lateinit var storIOSQLite3: StorIOSQLite3

    lateinit var changes1WithTags: Changes1

    lateinit var changes1WithoutTags: Changes1

    lateinit var changes3WithTags: Changes3

    lateinit var changes3WithoutTags: Changes3

    @Before
    fun `before each test`() {
        storIOSQLite1 = DefaultStorIOSQLite1.builder()
                .sqliteOpenHelper(mock())
                .defaultScheduler(null)
                .build()
        storIOSQLite3 = DefaultStorIOSQLite3.builder()
                .sqliteOpenHelper(mock())
                .defaultRxScheduler(null)
                .build()
        val interop = StorIOSQLite1To3()
        interop.forwardNotifications(storIOSQLite1, storIOSQLite3)

        storIOSQLite1.observeChanges().subscribe(observer1)
        storIOSQLite3.observeChanges(BackpressureStrategy.BUFFER).subscribe(observer3)

        val tables = HashSet<String>()
        tables.add("table1")
        tables.add("table2")

        val tags = HashSet<String>()
        tags.add("tag1")
        tags.add("tag2")

        changes1WithTags = Changes1.newInstance(tables, tags)
        changes3WithTags = Changes3.newInstance(tables, tags)

        changes1WithoutTags = Changes1.newInstance(tables, null as Collection<String>?)
        changes3WithoutTags = Changes3.newInstance(tables, null as Collection<String>?)
    }

    @Test
    fun `forwardNotifications should forward from 1 to 3 with tags`() {
        storIOSQLite1.lowLevel().notifyAboutChanges(changes1WithTags)
        observer1.assertValue(changes1WithTags)
        observer3.assertValue(changes3WithTags)
    }

    @Test
    fun `forwardNotifications should forward from 1 to 3 without tags`() {
        storIOSQLite1.lowLevel().notifyAboutChanges(changes1WithoutTags)
        observer1.assertValue(changes1WithoutTags)
        observer3.assertValue(changes3WithoutTags)
    }

    @Test
    fun `forwardNotifications should forward from 1 to 3 multiple`() {
        storIOSQLite1.lowLevel().notifyAboutChanges(changes1WithTags)
        observer1.assertValue(changes1WithTags)
        observer3.assertValue(changes3WithTags)

        storIOSQLite1.lowLevel().notifyAboutChanges(changes1WithTags)
        observer1.assertValues(changes1WithTags, changes1WithTags)
        observer3.assertValues(changes3WithTags, changes3WithTags)
    }

    @Test
    fun `forwardNotifications should forward from 3 to 1 with tags`() {
        storIOSQLite3.lowLevel().notifyAboutChanges(changes3WithTags)
        observer1.assertValue(changes1WithTags)
        observer3.assertValue(changes3WithTags)
    }

    @Test
    fun `forwardNotifications should forward from 3 to 1 without tags`() {
        storIOSQLite3.lowLevel().notifyAboutChanges(changes3WithoutTags)
        observer1.assertValue(changes1WithoutTags)
        observer3.assertValue(changes3WithoutTags)
    }

    @Test
    fun `forwardNotifications should forward from 3 to 1 with multiple`() {
        storIOSQLite3.lowLevel().notifyAboutChanges(changes3WithTags)
        observer1.assertValue(changes1WithTags)
        observer3.assertValue(changes3WithTags)

        storIOSQLite3.lowLevel().notifyAboutChanges(changes3WithTags)
        observer1.assertValues(changes1WithTags, changes1WithTags)
        observer3.assertValues(changes3WithTags, changes3WithTags)
    }
}
