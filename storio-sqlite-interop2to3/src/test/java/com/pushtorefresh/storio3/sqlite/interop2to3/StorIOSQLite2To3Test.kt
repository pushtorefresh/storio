package com.pushtorefresh.storio3.sqlite.interop2to3

import com.nhaarman.mockito_kotlin.mock
import io.reactivex.BackpressureStrategy
import org.junit.Before
import org.junit.Test
import com.pushtorefresh.storio2.sqlite.Changes as Changes2
import com.pushtorefresh.storio2.sqlite.StorIOSQLite as StorIOSQLite2
import com.pushtorefresh.storio2.sqlite.impl.DefaultStorIOSQLite as DefaultStorIOSQLite2
import com.pushtorefresh.storio3.sqlite.Changes as Changes3
import com.pushtorefresh.storio3.sqlite.StorIOSQLite as StorIOSQLite3
import com.pushtorefresh.storio3.sqlite.impl.DefaultStorIOSQLite as DefaultStorIOSQLite3
import io.reactivex.subscribers.TestSubscriber as TestSubscriberRx2
import rx.observers.TestSubscriber as TestSubscriberRx1

class StorIOSQLite2To3Test {

    val observer2 = TestSubscriberRx1.create<Changes2>()

    val observer3 = TestSubscriberRx2.create<Changes3>()

    lateinit var storIOSQLite2: StorIOSQLite2

    lateinit var storIOSQLite3: StorIOSQLite3

    lateinit var changes2WithTags: Changes2

    lateinit var changes2WithoutTags: Changes2

    lateinit var changes3WithTags: Changes3

    lateinit var changes3WithoutTags: Changes3

    @Before
    fun `before each test`() {
        storIOSQLite2 = DefaultStorIOSQLite2.builder()
                .sqliteOpenHelper(mock())
                .defaultScheduler(null)
                .build()
        storIOSQLite3 = DefaultStorIOSQLite3.builder()
                .sqliteOpenHelper(mock())
                .defaultRxScheduler(null)
                .build()
        val interop = StorIOSQLite2To3()
        interop.forwardNotifications(storIOSQLite2, storIOSQLite3)

        storIOSQLite2.observeChanges().subscribe(observer2)
        storIOSQLite3.observeChanges(BackpressureStrategy.BUFFER).subscribe(observer3)

        val tables = HashSet<String>()
        tables.add("table1")
        tables.add("table2")

        val tags = HashSet<String>()
        tags.add("tag1")
        tags.add("tag2")

        changes2WithTags = Changes2.newInstance(tables, tags)
        changes3WithTags = Changes3.newInstance(tables, tags)

        changes2WithoutTags = Changes2.newInstance(tables, null as Collection<String>?)
        changes3WithoutTags = Changes3.newInstance(tables, null as Collection<String>?)
    }

    @Test
    fun `forwardNotifications should forward from 2 to 3 with tags`() {
        storIOSQLite2.lowLevel().notifyAboutChanges(changes2WithTags)
        observer2.assertValue(changes2WithTags)
        observer3.assertValue(changes3WithTags)
    }

    @Test
    fun `forwardNotifications should forward from 2 to 3 without tags`() {
        storIOSQLite2.lowLevel().notifyAboutChanges(changes2WithoutTags)
        observer2.assertValue(changes2WithoutTags)
        observer3.assertValue(changes3WithoutTags)
    }

    @Test
    fun `forwardNotifications should forward from 2 to 3 multiple`() {
        storIOSQLite2.lowLevel().notifyAboutChanges(changes2WithTags)
        observer2.assertValue(changes2WithTags)
        observer3.assertValue(changes3WithTags)

        storIOSQLite2.lowLevel().notifyAboutChanges(changes2WithTags)
        observer2.assertValues(changes2WithTags, changes2WithTags)
        observer3.assertValues(changes3WithTags, changes3WithTags)
    }

    @Test
    fun `forwardNotifications should forward from 3 to 2 with tags`() {
        storIOSQLite3.lowLevel().notifyAboutChanges(changes3WithTags)
        observer2.assertValue(changes2WithTags)
        observer3.assertValue(changes3WithTags)
    }

    @Test
    fun `forwardNotifications should forward from 3 to 2 without tags`() {
        storIOSQLite3.lowLevel().notifyAboutChanges(changes3WithoutTags)
        observer2.assertValue(changes2WithoutTags)
        observer3.assertValue(changes3WithoutTags)
    }

    @Test
    fun `forwardNotifications should forward from 3 to 2 with multiple`() {
        storIOSQLite3.lowLevel().notifyAboutChanges(changes3WithTags)
        observer2.assertValue(changes2WithTags)
        observer3.assertValue(changes3WithTags)

        storIOSQLite3.lowLevel().notifyAboutChanges(changes3WithTags)
        observer2.assertValues(changes2WithTags, changes2WithTags)
        observer3.assertValues(changes3WithTags, changes3WithTags)
    }
}
