@file:Suppress("MemberVisibilityCanPrivate")

package com.pushtorefresh.storio3.sqlite.interop1to3

import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.verifyNoMoreInteractions
import com.pushtorefresh.private_constructor_checker.PrivateConstructorChecker
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import org.mockito.ArgumentMatchers.eq
import org.mockito.Mockito.any
import com.pushtorefresh.storio.sqlite.SQLiteTypeMapping as SQLiteTypeMapping1
import com.pushtorefresh.storio.sqlite.StorIOSQLite as StorIOSQLite1
import com.pushtorefresh.storio.sqlite.operations.delete.DeleteResolver as DeleteResolver1
import com.pushtorefresh.storio.sqlite.operations.get.GetResolver as GetResolver1
import com.pushtorefresh.storio.sqlite.operations.put.PutResolver as PutResolver1
import com.pushtorefresh.storio.sqlite.queries.Query as Query1
import com.pushtorefresh.storio.sqlite.queries.RawQuery as RawQuery1
import com.pushtorefresh.storio3.sqlite.SQLiteTypeMapping as SQLiteTypeMapping3
import com.pushtorefresh.storio3.sqlite.StorIOSQLite as StorIOSQLite3
import com.pushtorefresh.storio3.sqlite.operations.delete.DeleteResolver as DeleteResolver3
import com.pushtorefresh.storio3.sqlite.operations.get.GetResolver as GetResolver3
import com.pushtorefresh.storio3.sqlite.operations.put.PutResolver as PutResolver3
import com.pushtorefresh.storio3.sqlite.queries.Query as Query3
import com.pushtorefresh.storio3.sqlite.queries.RawQuery as RawQuery3

class SQLiteTypeMapping1To3Test {

    val testEntities = TestEntities()

    val entity = "value"

    val storIOSQLite1 = mock<StorIOSQLite1>()

    val storIOSQLite3 = mock<StorIOSQLite3>()

    val putResolver1 = mock<PutResolver1<String>> {
        on { performPut(eq(storIOSQLite1), any()) } doReturn testEntities.insertResult1
    }

    val getResolver1 = mock<GetResolver1<String>> {
        on { mapFromCursor(testEntities.cursor) } doReturn entity
        on { performGet(eq(storIOSQLite1), any(Query1::class.java)) } doReturn testEntities.cursor
        on { performGet(eq(storIOSQLite1), any(RawQuery1::class.java)) } doReturn testEntities.cursor
    }

    val deleteResolver1 = mock<DeleteResolver1<String>> {
        on { performDelete(eq(storIOSQLite1), any()) } doReturn testEntities.deleteResult1
    }

    val putResolver3 = mock<PutResolver3<String>> {
        on { performPut(eq(storIOSQLite3), any()) } doReturn testEntities.insertResult3
    }

    val getResolver3 = mock<GetResolver3<String>> {
        on { mapFromCursor(storIOSQLite3, testEntities.cursor) } doReturn entity
        on { performGet(eq(storIOSQLite3), any(Query3::class.java)) } doReturn testEntities.cursor
        on { performGet(eq(storIOSQLite3), any(RawQuery3::class.java)) } doReturn testEntities.cursor
    }

    val deleteResolver3 = mock<DeleteResolver3<String>> {
        on { performDelete(eq(storIOSQLite3), any()) } doReturn testEntities.deleteResult3
    }

    @Test
    fun `toV1PutResolver should work`() {
        val resolver = SQLiteTypeMapping1To3.toV1PutResolver(storIOSQLite3, putResolver3)
        verifyPutResolver1Behavior(resolver)
    }

    @Test
    fun `toV1GetResolver should work for mapFromCursor`() {
        val resolver = SQLiteTypeMapping1To3.toV1GetResolver(storIOSQLite3, getResolver3)
        verifyGetResolver1MapBehavior(resolver)
    }

    @Test
    fun `toV1GetResolver should work for performGet with query`() {
        val resolver = SQLiteTypeMapping1To3.toV1GetResolver(storIOSQLite3, getResolver3)
        verifyGetResolver1PerformQueryBehavior(resolver)
    }

    @Test
    fun `toV1GetResolver should work for performGet with rawQuery`() {
        val resolver = SQLiteTypeMapping1To3.toV1GetResolver(storIOSQLite3, getResolver3)
        verifyGetResolver1PerformRawQueryBehavior(resolver)
    }

    @Test
    fun `toV1DeleteResolver should work`() {
        val resolver = SQLiteTypeMapping1To3.toV1DeleteResolver(storIOSQLite3, deleteResolver3)
        verifyDeleteResolver1Behavior(resolver)
    }

    @Test
    fun `toV1SQLiteTypeMapping should work`() {
        val sqliteTypeMapping3 = SQLiteTypeMapping3.builder<String>()
                .putResolver(putResolver3)
                .getResolver(getResolver3)
                .deleteResolver(deleteResolver3)
                .build()

        val sqliteTypeMapping1 =
                SQLiteTypeMapping1To3.toV1SQLiteTypeMapping(storIOSQLite3, sqliteTypeMapping3)

        verifyPutResolver1Behavior(sqliteTypeMapping1.putResolver())

        @Suppress("UsePropertyAccessSyntax")
        val getResolver1 = sqliteTypeMapping1.getResolver()
        verifyGetResolver1MapBehavior(getResolver1)
        verifyGetResolver1PerformQueryBehavior(getResolver1)
        verifyGetResolver1PerformRawQueryBehavior(getResolver1)

        verifyDeleteResolver1Behavior(sqliteTypeMapping1.deleteResolver())
    }

    @Test
    fun `toV3SQLiteTypeMapping should work`() {
        val sqliteTypeMapping1 = SQLiteTypeMapping1.builder<String>()
                .putResolver(putResolver1)
                .getResolver(getResolver1)
                .deleteResolver(deleteResolver1)
                .build()

        val sqliteTypeMapping3 =
                SQLiteTypeMapping1To3.toV3SQLiteTypeMapping(storIOSQLite1, sqliteTypeMapping1)

        verifyPutResolver3Behavior(sqliteTypeMapping3.putResolver())

        @Suppress("UsePropertyAccessSyntax")
        val getResolver3 = sqliteTypeMapping3.getResolver()
        verifyGetResolver3MapBehavior(getResolver3)
        verifyGetResolver3PerformQueryBehavior(getResolver3)
        verifyGetResolver3PerformRawQueryBehavior(getResolver3)

        verifyDeleteResolver3Behavior(sqliteTypeMapping3.deleteResolver())
    }

    @Test
    fun `toV3PutResolver should work`() {
        val resolver = SQLiteTypeMapping1To3.toV3PutResolver(storIOSQLite1, putResolver1)
        verifyPutResolver3Behavior(resolver)
    }

    @Test
    fun `toV3GetResolver should work for mapFromCursor`() {
        val resolver = SQLiteTypeMapping1To3.toV3GetResolver(storIOSQLite1, getResolver1)
        verifyGetResolver3MapBehavior(resolver)
    }

    @Test
    fun `toV3GetResolver should work for performGet with query`() {
        val resolver = SQLiteTypeMapping1To3.toV3GetResolver(storIOSQLite1, getResolver1)
        verifyGetResolver3PerformQueryBehavior(resolver)
    }

    @Test
    fun `toV3GetResolver should work for performGet with rawQuery`() {
        val resolver = SQLiteTypeMapping1To3.toV3GetResolver(storIOSQLite1, getResolver1)
        verifyGetResolver3PerformRawQueryBehavior(resolver)
    }

    @Test
    fun `toV3DeleteResolver should work`() {
        val resolver = SQLiteTypeMapping1To3.toV3DeleteResolver(storIOSQLite1, deleteResolver1)
        verifyDeleteResolver3Behavior(resolver)
    }

    @Test
    fun `constructor must be private and throw exception`() {
        PrivateConstructorChecker
                .forClass(SQLiteTypeMapping1To3::class.java)
                .expectedTypeOfException(IllegalStateException::class.java)
                .expectedExceptionMessage("No instances please.")
                .check()
    }

    private fun verifyPutResolver1Behavior(resolver: PutResolver1<String>) {
        val result = resolver.performPut(storIOSQLite1, entity)

        assertThat(result).isEqualTo(testEntities.insertResult1)

        verify(putResolver3).performPut(storIOSQLite3, entity)
        verifyNoMoreInteractions(putResolver3)
    }

    private fun verifyGetResolver1MapBehavior(resolver: GetResolver1<String>) {
        val result = resolver.mapFromCursor(testEntities.cursor)

        assertThat(result).isEqualTo(entity)

        verify(getResolver3).mapFromCursor(storIOSQLite3, testEntities.cursor)
        verifyNoMoreInteractions(putResolver3)
    }

    private fun verifyGetResolver1PerformQueryBehavior(resolver: GetResolver1<String>) {
        val result = resolver.performGet(storIOSQLite1, testEntities.query1)

        assertThat(result).isEqualTo(testEntities.cursor)

        verify(getResolver3).performGet(storIOSQLite3, testEntities.query3)
        verifyNoMoreInteractions(putResolver3)
    }

    private fun verifyGetResolver1PerformRawQueryBehavior(resolver: GetResolver1<String>) {
        val result = resolver.performGet(storIOSQLite1, testEntities.rawQuery1)

        assertThat(result).isEqualTo(testEntities.cursor)

        verify(getResolver3).performGet(storIOSQLite3, testEntities.rawQuery3)
        verifyNoMoreInteractions(putResolver3)
    }

    private fun verifyDeleteResolver1Behavior(resolver: DeleteResolver1<String>) {
        val result = resolver.performDelete(storIOSQLite1, entity)

        assertThat(result).isEqualTo(testEntities.deleteResult1)

        verify(deleteResolver3).performDelete(storIOSQLite3, entity)
        verifyNoMoreInteractions(putResolver3)
    }

    private fun verifyPutResolver3Behavior(resolver: PutResolver3<String>) {
        val result = resolver.performPut(storIOSQLite3, entity)

        assertThat(result).isEqualTo(testEntities.insertResult3)

        verify(putResolver1).performPut(storIOSQLite1, entity)
        verifyNoMoreInteractions(putResolver1)
    }

    private fun verifyGetResolver3MapBehavior(resolver: GetResolver3<String>) {
        val result = resolver.mapFromCursor(storIOSQLite3, testEntities.cursor)

        assertThat(result).isEqualTo(entity)

        verify(getResolver1).mapFromCursor(testEntities.cursor)
        verifyNoMoreInteractions(putResolver1)
    }

    private fun verifyGetResolver3PerformQueryBehavior(resolver: GetResolver3<String>) {
        val result = resolver.performGet(storIOSQLite3, testEntities.query3)

        assertThat(result).isEqualTo(testEntities.cursor)

        verify(getResolver1).performGet(storIOSQLite1, testEntities.query1)
        verifyNoMoreInteractions(putResolver1)
    }

    private fun verifyGetResolver3PerformRawQueryBehavior(resolver: GetResolver3<String>) {
        val result = resolver.performGet(storIOSQLite3, testEntities.rawQuery3)

        assertThat(result).isEqualTo(testEntities.cursor)

        verify(getResolver1).performGet(storIOSQLite1, testEntities.rawQuery1)
        verifyNoMoreInteractions(putResolver1)
    }

    private fun verifyDeleteResolver3Behavior(resolver: DeleteResolver3<String>) {
        val result = resolver.performDelete(storIOSQLite3, entity)

        assertThat(result).isEqualTo(testEntities.deleteResult3)

        verify(deleteResolver1).performDelete(storIOSQLite1, entity)
        verifyNoMoreInteractions(putResolver1)
    }
}
