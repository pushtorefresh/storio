@file:Suppress("MemberVisibilityCanPrivate")

package com.pushtorefresh.storio3.contentresolver.interop2to3

import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.eq
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.verifyNoMoreInteractions
import com.pushtorefresh.private_constructor_checker.PrivateConstructorChecker
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import com.pushtorefresh.storio2.contentresolver.ContentResolverTypeMapping as ContentResolverTypeMapping2
import com.pushtorefresh.storio2.contentresolver.StorIOContentResolver as StorIOContentResolver2
import com.pushtorefresh.storio2.contentresolver.operations.delete.DeleteResolver as DeleteResolver2
import com.pushtorefresh.storio2.contentresolver.operations.get.GetResolver as GetResolver2
import com.pushtorefresh.storio2.contentresolver.operations.put.PutResolver as PutResolver2
import com.pushtorefresh.storio2.contentresolver.queries.Query as Query2
import com.pushtorefresh.storio3.contentresolver.ContentResolverTypeMapping as ContentResolverTypeMapping3
import com.pushtorefresh.storio3.contentresolver.StorIOContentResolver as StorIOContentResolver3
import com.pushtorefresh.storio3.contentresolver.operations.delete.DeleteResolver as DeleteResolver3
import com.pushtorefresh.storio3.contentresolver.operations.get.GetResolver as GetResolver3
import com.pushtorefresh.storio3.contentresolver.operations.put.PutResolver as PutResolver3
import com.pushtorefresh.storio3.contentresolver.queries.Query as Query3

class ContentResolverTypeMapping2To3Test {

    val testEntities = TestEntities()

    val entity = "value"

    val storIOContentResolver2 = mock<StorIOContentResolver2>()

    val storIOContentResolver3 = mock<StorIOContentResolver3>()

    val putResolver2 = mock<PutResolver2<String>> {
        on { performPut(eq(storIOContentResolver2), any()) } doReturn testEntities.insertResult2
    }

    val getResolver2 = mock<GetResolver2<String>> {
        on { mapFromCursor(storIOContentResolver2, testEntities.cursor) } doReturn entity
        on { performGet(eq(storIOContentResolver2), any()) } doReturn testEntities.cursor
    }

    val deleteResolver2 = mock<DeleteResolver2<String>> {
        on { performDelete(eq(storIOContentResolver2), any()) } doReturn testEntities.deleteResult2
    }

    val putResolver3 = mock<PutResolver3<String>> {
        on { performPut(eq(storIOContentResolver3), any()) } doReturn testEntities.insertResult3
    }

    val getResolver3 = mock<GetResolver3<String>> {
        on { mapFromCursor(storIOContentResolver3, testEntities.cursor) } doReturn entity
        on { performGet(eq(storIOContentResolver3), any()) } doReturn testEntities.cursor
    }

    val deleteResolver3 = mock<DeleteResolver3<String>> {
        on { performDelete(eq(storIOContentResolver3), any()) } doReturn testEntities.deleteResult3
    }

    @Test
    fun `toV2PutResolver should work`() {
        val resolver = ContentResolverTypeMapping2To3.toV2PutResolver(storIOContentResolver3, putResolver3)
        verifyPutResolver2Behavior(resolver)
    }

    @Test
    fun `toV2GetResolver should work for mapFromCursor`() {
        val resolver = ContentResolverTypeMapping2To3.toV2GetResolver(storIOContentResolver3, getResolver3)
        verifyGetResolver2MapBehavior(resolver)
    }

    @Test
    fun `toV2GetResolver should work for performGet with query`() {
        val resolver = ContentResolverTypeMapping2To3.toV2GetResolver(storIOContentResolver3, getResolver3)
        verifyGetResolver2PerformQueryBehavior(resolver)
    }

    @Test
    fun `toV2DeleteResolver should work`() {
        val resolver = ContentResolverTypeMapping2To3.toV2DeleteResolver(storIOContentResolver3, deleteResolver3)
        verifyDeleteResolver2Behavior(resolver)
    }

    @Test
    fun `toV2ContentResolverTypeMapping should work`() {
        val contentResolverTypeMapping3 = ContentResolverTypeMapping3.builder<String>()
                .putResolver(putResolver3)
                .getResolver(getResolver3)
                .deleteResolver(deleteResolver3)
                .build()

        val contentResolverTypeMapping2 = ContentResolverTypeMapping2To3.toV2ContentResolverTypeMapping(
                storIOContentResolver3,
                contentResolverTypeMapping3
        )

        verifyPutResolver2Behavior(contentResolverTypeMapping2.putResolver())

        @Suppress("UsePropertyAccessSyntax")
        val getResolver2 = contentResolverTypeMapping2.getResolver()
        verifyGetResolver2MapBehavior(getResolver2)
        verifyGetResolver2PerformQueryBehavior(getResolver2)

        verifyDeleteResolver2Behavior(contentResolverTypeMapping2.deleteResolver())
    }

    @Test
    fun `toV3ContentResolverTypeMapping should work`() {
        val contentResolverTypeMapping2 = ContentResolverTypeMapping2.builder<String>()
                .putResolver(putResolver2)
                .getResolver(getResolver2)
                .deleteResolver(deleteResolver2)
                .build()

        val contentResolverTypeMapping3 =
                ContentResolverTypeMapping2To3.toV3ContentResolverTypeMapping(storIOContentResolver2, contentResolverTypeMapping2)

        verifyPutResolver3Behavior(contentResolverTypeMapping3.putResolver())

        @Suppress("UsePropertyAccessSyntax")
        val getResolver3 = contentResolverTypeMapping3.getResolver()
        verifyGetResolver3MapBehavior(getResolver3)
        verifyGetResolver3PerformQueryBehavior(getResolver3)

        verifyDeleteResolver3Behavior(contentResolverTypeMapping3.deleteResolver())
    }

    @Test
    fun `toV3PutResolver should work`() {
        val resolver = ContentResolverTypeMapping2To3.toV3PutResolver(storIOContentResolver2, putResolver2)
        verifyPutResolver3Behavior(resolver)
    }

    @Test
    fun `toV3GetResolver should work for mapFromCursor`() {
        val resolver = ContentResolverTypeMapping2To3.toV3GetResolver(storIOContentResolver2, getResolver2)
        verifyGetResolver3MapBehavior(resolver)
    }

    @Test
    fun `toV3GetResolver should work for performGet with query`() {
        val resolver = ContentResolverTypeMapping2To3.toV3GetResolver(storIOContentResolver2, getResolver2)
        verifyGetResolver3PerformQueryBehavior(resolver)
    }

    @Test
    fun `toV3DeleteResolver should work`() {
        val resolver = ContentResolverTypeMapping2To3.toV3DeleteResolver(storIOContentResolver2, deleteResolver2)
        verifyDeleteResolver3Behavior(resolver)
    }

    @Test
    fun `constructor must be private and throw exception`() {
        PrivateConstructorChecker
                .forClass(ContentResolverTypeMapping2To3::class.java)
                .expectedTypeOfException(IllegalStateException::class.java)
                .expectedExceptionMessage("No instances please.")
                .check()
    }

    private fun verifyPutResolver2Behavior(resolver: PutResolver2<String>) {
        val result = resolver.performPut(storIOContentResolver2, entity)

        assertThat(result).isEqualTo(testEntities.insertResult2)

        verify(putResolver3).performPut(storIOContentResolver3, entity)
        verifyNoMoreInteractions(putResolver3)
    }

    private fun verifyGetResolver2MapBehavior(resolver: GetResolver2<String>) {
        val result = resolver.mapFromCursor(storIOContentResolver2, testEntities.cursor)

        assertThat(result).isEqualTo(entity)

        verify(getResolver3).mapFromCursor(storIOContentResolver3, testEntities.cursor)
        verifyNoMoreInteractions(putResolver3)
    }

    private fun verifyGetResolver2PerformQueryBehavior(resolver: GetResolver2<String>) {
        val result = resolver.performGet(storIOContentResolver2, testEntities.query2)

        assertThat(result).isEqualTo(testEntities.cursor)

        verify(getResolver3).performGet(storIOContentResolver3, testEntities.query3)
        verifyNoMoreInteractions(putResolver3)
    }

    private fun verifyDeleteResolver2Behavior(resolver: DeleteResolver2<String>) {
        val result = resolver.performDelete(storIOContentResolver2, entity)

        assertThat(result).isEqualTo(testEntities.deleteResult2)

        verify(deleteResolver3).performDelete(storIOContentResolver3, entity)
        verifyNoMoreInteractions(putResolver3)
    }

    private fun verifyPutResolver3Behavior(resolver: PutResolver3<String>) {
        val result = resolver.performPut(storIOContentResolver3, entity)

        assertThat(result).isEqualTo(testEntities.insertResult3)

        verify(putResolver2).performPut(storIOContentResolver2, entity)
        verifyNoMoreInteractions(putResolver2)
    }

    private fun verifyGetResolver3MapBehavior(resolver: GetResolver3<String>) {
        val result = resolver.mapFromCursor(storIOContentResolver3, testEntities.cursor)

        assertThat(result).isEqualTo(entity)

        verify(getResolver2).mapFromCursor(storIOContentResolver2, testEntities.cursor)
        verifyNoMoreInteractions(putResolver2)
    }

    private fun verifyGetResolver3PerformQueryBehavior(resolver: GetResolver3<String>) {
        val result = resolver.performGet(storIOContentResolver3, testEntities.query3)

        assertThat(result).isEqualTo(testEntities.cursor)

        verify(getResolver2).performGet(storIOContentResolver2, testEntities.query2)
        verifyNoMoreInteractions(putResolver2)
    }

    private fun verifyDeleteResolver3Behavior(resolver: DeleteResolver3<String>) {
        val result = resolver.performDelete(storIOContentResolver3, entity)

        assertThat(result).isEqualTo(testEntities.deleteResult3)

        verify(deleteResolver2).performDelete(storIOContentResolver2, entity)
        verifyNoMoreInteractions(putResolver2)
    }
}
