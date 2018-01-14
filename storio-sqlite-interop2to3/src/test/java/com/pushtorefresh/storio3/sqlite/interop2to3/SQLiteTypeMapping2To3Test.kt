@file:Suppress("MemberVisibilityCanPrivate")

package com.pushtorefresh.storio3.sqlite.interop2to3

import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.verifyNoMoreInteractions
import com.pushtorefresh.private_constructor_checker.PrivateConstructorChecker
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import org.mockito.ArgumentMatchers.eq
import org.mockito.Mockito.any
import com.pushtorefresh.storio2.sqlite.SQLiteTypeMapping as SQLiteTypeMapping2
import com.pushtorefresh.storio2.sqlite.StorIOSQLite as StorIOSQLite2
import com.pushtorefresh.storio2.sqlite.operations.delete.DeleteResolver as DeleteResolver2
import com.pushtorefresh.storio2.sqlite.operations.get.GetResolver as GetResolver2
import com.pushtorefresh.storio2.sqlite.operations.put.PutResolver as PutResolver2
import com.pushtorefresh.storio2.sqlite.queries.Query as Query2
import com.pushtorefresh.storio2.sqlite.queries.RawQuery as RawQuery2
import com.pushtorefresh.storio3.sqlite.SQLiteTypeMapping as SQLiteTypeMapping3
import com.pushtorefresh.storio3.sqlite.StorIOSQLite as StorIOSQLite3
import com.pushtorefresh.storio3.sqlite.operations.delete.DeleteResolver as DeleteResolver3
import com.pushtorefresh.storio3.sqlite.operations.get.GetResolver as GetResolver3
import com.pushtorefresh.storio3.sqlite.operations.put.PutResolver as PutResolver3
import com.pushtorefresh.storio3.sqlite.queries.Query as Query3
import com.pushtorefresh.storio3.sqlite.queries.RawQuery as RawQuery3

class SQLiteTypeMapping2To3Test {

    val testEntities = TestEntities()

    val entity = "value"

    val storIOSQLite2 = mock<StorIOSQLite2>()

    val storIOSQLite3 = mock<StorIOSQLite3>()

    val putResolver2 = mock<PutResolver2<String>> {
        on { performPut(eq(storIOSQLite2), any()) } doReturn testEntities.insertResult2
    }

    val getResolver2 = mock<GetResolver2<String>> {
        on { mapFromCursor(storIOSQLite2, testEntities.cursor) } doReturn entity
        on { performGet(eq(storIOSQLite2), any(Query2::class.java)) } doReturn testEntities.cursor
        on { performGet(eq(storIOSQLite2), any(RawQuery2::class.java)) } doReturn testEntities.cursor
    }

    val deleteResolver2 = mock<DeleteResolver2<String>> {
        on { performDelete(eq(storIOSQLite2), any()) } doReturn testEntities.deleteResult2
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
    fun `toV2PutResolver should work`() {
        val resolver = SQLiteTypeMapping2To3.toV2PutResolver(storIOSQLite3, putResolver3)
        verifyPutResolver2Behavior(resolver)
    }

    @Test
    fun `toV2GetResolver should work for mapFromCursor`() {
        val resolver = SQLiteTypeMapping2To3.toV2GetResolver(storIOSQLite3, getResolver3)
        verifyGetResolver2MapBehavior(resolver)
    }

    @Test
    fun `toV2GetResolver should work for performGet with query`() {
        val resolver = SQLiteTypeMapping2To3.toV2GetResolver(storIOSQLite3, getResolver3)
        verifyGetResolver2PerformQueryBehavior(resolver)
    }

    @Test
    fun `toV2GetResolver should work for performGet with rawQuery`() {
        val resolver = SQLiteTypeMapping2To3.toV2GetResolver(storIOSQLite3, getResolver3)
        verifyGetResolver2PerformRawQueryBehavior(resolver)
    }

    @Test
    fun `toV2DeleteResolver should work`() {
        val resolver = SQLiteTypeMapping2To3.toV2DeleteResolver(storIOSQLite3, deleteResolver3)
        verifyDeleteResolver2Behavior(resolver)
    }

    @Test
    fun `toV2SQLiteTypeMapping should work`() {
        val sqliteTypeMapping3 = SQLiteTypeMapping3.builder<String>()
                .putResolver(putResolver3)
                .getResolver(getResolver3)
                .deleteResolver(deleteResolver3)
                .build()

        val sqliteTypeMapping2 =
                SQLiteTypeMapping2To3.toV2SQLiteTypeMapping(storIOSQLite3, sqliteTypeMapping3)

        verifyPutResolver2Behavior(sqliteTypeMapping2.putResolver())

        @Suppress("UsePropertyAccessSyntax")
        val getResolver2 = sqliteTypeMapping2.getResolver()
        verifyGetResolver2MapBehavior(getResolver2)
        verifyGetResolver2PerformQueryBehavior(getResolver2)
        verifyGetResolver2PerformRawQueryBehavior(getResolver2)

        verifyDeleteResolver2Behavior(sqliteTypeMapping2.deleteResolver())
    }

    @Test
    fun `toV3SQLiteTypeMapping should work`() {
        val sqliteTypeMapping2 = SQLiteTypeMapping2.builder<String>()
                .putResolver(putResolver2)
                .getResolver(getResolver2)
                .deleteResolver(deleteResolver2)
                .build()

        val sqliteTypeMapping3 =
                SQLiteTypeMapping2To3.toV3SQLiteTypeMapping(storIOSQLite2, sqliteTypeMapping2)

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
        val resolver = SQLiteTypeMapping2To3.toV3PutResolver(storIOSQLite2, putResolver2)
        verifyPutResolver3Behavior(resolver)
    }

    @Test
    fun `toV3GetResolver should work for mapFromCursor`() {
        val resolver = SQLiteTypeMapping2To3.toV3GetResolver(storIOSQLite2, getResolver2)
        verifyGetResolver3MapBehavior(resolver)
    }

    @Test
    fun `toV3GetResolver should work for performGet with query`() {
        val resolver = SQLiteTypeMapping2To3.toV3GetResolver(storIOSQLite2, getResolver2)
        verifyGetResolver3PerformQueryBehavior(resolver)
    }

    @Test
    fun `toV3GetResolver should work for performGet with rawQuery`() {
        val resolver = SQLiteTypeMapping2To3.toV3GetResolver(storIOSQLite2, getResolver2)
        verifyGetResolver3PerformRawQueryBehavior(resolver)
    }

    @Test
    fun `toV3DeleteResolver should work`() {
        val resolver = SQLiteTypeMapping2To3.toV3DeleteResolver(storIOSQLite2, deleteResolver2)
        verifyDeleteResolver3Behavior(resolver)
    }

    @Test
    fun `constructor must be private and throw exception`() {
        PrivateConstructorChecker
                .forClass(SQLiteTypeMapping2To3::class.java)
                .expectedTypeOfException(IllegalStateException::class.java)
                .expectedExceptionMessage("No instances please.")
                .check()
    }

    private fun verifyPutResolver2Behavior(resolver: PutResolver2<String>) {
        val result = resolver.performPut(storIOSQLite2, entity)

        assertThat(result).isEqualTo(testEntities.insertResult2)

        verify(putResolver3).performPut(storIOSQLite3, entity)
        verifyNoMoreInteractions(putResolver3)
    }

    private fun verifyGetResolver2MapBehavior(resolver: GetResolver2<String>) {
        val result = resolver.mapFromCursor(storIOSQLite2, testEntities.cursor)

        assertThat(result).isEqualTo(entity)

        verify(getResolver3).mapFromCursor(storIOSQLite3, testEntities.cursor)
        verifyNoMoreInteractions(putResolver3)
    }

    private fun verifyGetResolver2PerformQueryBehavior(resolver: GetResolver2<String>) {
        val result = resolver.performGet(storIOSQLite2, testEntities.query2)

        assertThat(result).isEqualTo(testEntities.cursor)

        verify(getResolver3).performGet(storIOSQLite3, testEntities.query3)
        verifyNoMoreInteractions(putResolver3)
    }

    private fun verifyGetResolver2PerformRawQueryBehavior(resolver: GetResolver2<String>) {
        val result = resolver.performGet(storIOSQLite2, testEntities.rawQuery2)

        assertThat(result).isEqualTo(testEntities.cursor)

        verify(getResolver3).performGet(storIOSQLite3, testEntities.rawQuery3)
        verifyNoMoreInteractions(putResolver3)
    }

    private fun verifyDeleteResolver2Behavior(resolver: DeleteResolver2<String>) {
        val result = resolver.performDelete(storIOSQLite2, entity)

        assertThat(result).isEqualTo(testEntities.deleteResult2)

        verify(deleteResolver3).performDelete(storIOSQLite3, entity)
        verifyNoMoreInteractions(putResolver3)
    }

    private fun verifyPutResolver3Behavior(resolver: PutResolver3<String>) {
        val result = resolver.performPut(storIOSQLite3, entity)

        assertThat(result).isEqualTo(testEntities.insertResult3)

        verify(putResolver2).performPut(storIOSQLite2, entity)
        verifyNoMoreInteractions(putResolver2)
    }

    private fun verifyGetResolver3MapBehavior(resolver: GetResolver3<String>) {
        val result = resolver.mapFromCursor(storIOSQLite3, testEntities.cursor)

        assertThat(result).isEqualTo(entity)

        verify(getResolver2).mapFromCursor(storIOSQLite2, testEntities.cursor)
        verifyNoMoreInteractions(putResolver2)
    }

    private fun verifyGetResolver3PerformQueryBehavior(resolver: GetResolver3<String>) {
        val result = resolver.performGet(storIOSQLite3, testEntities.query3)

        assertThat(result).isEqualTo(testEntities.cursor)

        verify(getResolver2).performGet(storIOSQLite2, testEntities.query2)
        verifyNoMoreInteractions(putResolver2)
    }

    private fun verifyGetResolver3PerformRawQueryBehavior(resolver: GetResolver3<String>) {
        val result = resolver.performGet(storIOSQLite3, testEntities.rawQuery3)

        assertThat(result).isEqualTo(testEntities.cursor)

        verify(getResolver2).performGet(storIOSQLite2, testEntities.rawQuery2)
        verifyNoMoreInteractions(putResolver2)
    }

    private fun verifyDeleteResolver3Behavior(resolver: DeleteResolver3<String>) {
        val result = resolver.performDelete(storIOSQLite3, entity)

        assertThat(result).isEqualTo(testEntities.deleteResult3)

        verify(deleteResolver2).performDelete(storIOSQLite2, entity)
        verifyNoMoreInteractions(putResolver2)
    }
}
