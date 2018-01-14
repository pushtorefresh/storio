@file:Suppress("MemberVisibilityCanPrivate")

package com.pushtorefresh.storio3.contentresolver.interop1to3

import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.eq
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.verifyNoMoreInteractions
import com.pushtorefresh.private_constructor_checker.PrivateConstructorChecker
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import com.pushtorefresh.storio.contentresolver.ContentResolverTypeMapping as ContentResolverTypeMapping1
import com.pushtorefresh.storio.contentresolver.StorIOContentResolver as StorIOContentResolver1
import com.pushtorefresh.storio.contentresolver.operations.delete.DeleteResolver as DeleteResolver1
import com.pushtorefresh.storio.contentresolver.operations.get.GetResolver as GetResolver1
import com.pushtorefresh.storio.contentresolver.operations.put.PutResolver as PutResolver1
import com.pushtorefresh.storio.contentresolver.queries.Query as Query1
import com.pushtorefresh.storio3.contentresolver.ContentResolverTypeMapping as ContentResolverTypeMapping3
import com.pushtorefresh.storio3.contentresolver.StorIOContentResolver as StorIOContentResolver3
import com.pushtorefresh.storio3.contentresolver.operations.delete.DeleteResolver as DeleteResolver3
import com.pushtorefresh.storio3.contentresolver.operations.get.GetResolver as GetResolver3
import com.pushtorefresh.storio3.contentresolver.operations.put.PutResolver as PutResolver3
import com.pushtorefresh.storio3.contentresolver.queries.Query as Query3

class ContentResolverTypeMapping1To3Test {

    val testEntities = TestEntities()

    val entity = "value"

    val storIOContentResolver1 = mock<StorIOContentResolver1>()

    val storIOContentResolver3 = mock<StorIOContentResolver3>()

    val putResolver1 = mock<PutResolver1<String>> {
        on { performPut(eq(storIOContentResolver1), any()) } doReturn testEntities.insertResult1
    }

    val getResolver1 = mock<GetResolver1<String>> {
        on { mapFromCursor(testEntities.cursor) } doReturn entity
        on { performGet(eq(storIOContentResolver1), any()) } doReturn testEntities.cursor
    }

    val deleteResolver1 = mock<DeleteResolver1<String>> {
        on { performDelete(eq(storIOContentResolver1), any()) } doReturn testEntities.deleteResult1
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
    fun `toV1PutResolver should work`() {
        val resolver = ContentResolverTypeMapping1To3.toV1PutResolver(storIOContentResolver3, putResolver3)
        verifyPutResolver1Behavior(resolver)
    }

    @Test
    fun `toV1GetResolver should work for mapFromCursor`() {
        val resolver = ContentResolverTypeMapping1To3.toV1GetResolver(storIOContentResolver3, getResolver3)
        verifyGetResolver1MapBehavior(resolver)
    }

    @Test
    fun `toV1GetResolver should work for performGet with query`() {
        val resolver = ContentResolverTypeMapping1To3.toV1GetResolver(storIOContentResolver3, getResolver3)
        verifyGetResolver1PerformQueryBehavior(resolver)
    }

    @Test
    fun `toV1DeleteResolver should work`() {
        val resolver = ContentResolverTypeMapping1To3.toV1DeleteResolver(storIOContentResolver3, deleteResolver3)
        verifyDeleteResolver1Behavior(resolver)
    }

    @Test
    fun `toV1ContentResolverTypeMapping should work`() {
        val contentResolverTypeMapping3 = ContentResolverTypeMapping3.builder<String>()
                .putResolver(putResolver3)
                .getResolver(getResolver3)
                .deleteResolver(deleteResolver3)
                .build()

        val contentResolverTypeMapping1 = ContentResolverTypeMapping1To3.toV1ContentResolverTypeMapping(
                storIOContentResolver3,
                contentResolverTypeMapping3
        )

        verifyPutResolver1Behavior(contentResolverTypeMapping1.putResolver())

        @Suppress("UsePropertyAccessSyntax")
        val getResolver1 = contentResolverTypeMapping1.getResolver()
        verifyGetResolver1MapBehavior(getResolver1)
        verifyGetResolver1PerformQueryBehavior(getResolver1)

        verifyDeleteResolver1Behavior(contentResolverTypeMapping1.deleteResolver())
    }

    @Test
    fun `toV3ContentResolverTypeMapping should work`() {
        val contentResolverTypeMapping1 = ContentResolverTypeMapping1.builder<String>()
                .putResolver(putResolver1)
                .getResolver(getResolver1)
                .deleteResolver(deleteResolver1)
                .build()

        val contentResolverTypeMapping3 =
                ContentResolverTypeMapping1To3.toV3ContentResolverTypeMapping(storIOContentResolver1, contentResolverTypeMapping1)

        verifyPutResolver3Behavior(contentResolverTypeMapping3.putResolver())

        @Suppress("UsePropertyAccessSyntax")
        val getResolver3 = contentResolverTypeMapping3.getResolver()
        verifyGetResolver3MapBehavior(getResolver3)
        verifyGetResolver3PerformQueryBehavior(getResolver3)

        verifyDeleteResolver3Behavior(contentResolverTypeMapping3.deleteResolver())
    }

    @Test
    fun `toV3PutResolver should work`() {
        val resolver = ContentResolverTypeMapping1To3.toV3PutResolver(storIOContentResolver1, putResolver1)
        verifyPutResolver3Behavior(resolver)
    }

    @Test
    fun `toV3GetResolver should work for mapFromCursor`() {
        val resolver = ContentResolverTypeMapping1To3.toV3GetResolver(storIOContentResolver1, getResolver1)
        verifyGetResolver3MapBehavior(resolver)
    }

    @Test
    fun `toV3GetResolver should work for performGet with query`() {
        val resolver = ContentResolverTypeMapping1To3.toV3GetResolver(storIOContentResolver1, getResolver1)
        verifyGetResolver3PerformQueryBehavior(resolver)
    }

    @Test
    fun `toV3DeleteResolver should work`() {
        val resolver = ContentResolverTypeMapping1To3.toV3DeleteResolver(storIOContentResolver1, deleteResolver1)
        verifyDeleteResolver3Behavior(resolver)
    }

    @Test
    fun `constructor must be private and throw exception`() {
        PrivateConstructorChecker
                .forClass(ContentResolverTypeMapping1To3::class.java)
                .expectedTypeOfException(IllegalStateException::class.java)
                .expectedExceptionMessage("No instances please.")
                .check()
    }

    private fun verifyPutResolver1Behavior(resolver: PutResolver1<String>) {
        val result = resolver.performPut(storIOContentResolver1, entity)

        assertThat(result).isEqualTo(testEntities.insertResult1)

        verify(putResolver3).performPut(storIOContentResolver3, entity)
        verifyNoMoreInteractions(putResolver3)
    }

    private fun verifyGetResolver1MapBehavior(resolver: GetResolver1<String>) {
        val result = resolver.mapFromCursor(testEntities.cursor)

        assertThat(result).isEqualTo(entity)

        verify(getResolver3).mapFromCursor(storIOContentResolver3, testEntities.cursor)
        verifyNoMoreInteractions(putResolver3)
    }

    private fun verifyGetResolver1PerformQueryBehavior(resolver: GetResolver1<String>) {
        val result = resolver.performGet(storIOContentResolver1, testEntities.query1)

        assertThat(result).isEqualTo(testEntities.cursor)

        verify(getResolver3).performGet(storIOContentResolver3, testEntities.query3)
        verifyNoMoreInteractions(putResolver3)
    }

    private fun verifyDeleteResolver1Behavior(resolver: DeleteResolver1<String>) {
        val result = resolver.performDelete(storIOContentResolver1, entity)

        assertThat(result).isEqualTo(testEntities.deleteResult1)

        verify(deleteResolver3).performDelete(storIOContentResolver3, entity)
        verifyNoMoreInteractions(putResolver3)
    }

    private fun verifyPutResolver3Behavior(resolver: PutResolver3<String>) {
        val result = resolver.performPut(storIOContentResolver3, entity)

        assertThat(result).isEqualTo(testEntities.insertResult3)

        verify(putResolver1).performPut(storIOContentResolver1, entity)
        verifyNoMoreInteractions(putResolver1)
    }

    private fun verifyGetResolver3MapBehavior(resolver: GetResolver3<String>) {
        val result = resolver.mapFromCursor(storIOContentResolver3, testEntities.cursor)

        assertThat(result).isEqualTo(entity)

        verify(getResolver1).mapFromCursor(testEntities.cursor)
        verifyNoMoreInteractions(putResolver1)
    }

    private fun verifyGetResolver3PerformQueryBehavior(resolver: GetResolver3<String>) {
        val result = resolver.performGet(storIOContentResolver3, testEntities.query3)

        assertThat(result).isEqualTo(testEntities.cursor)

        verify(getResolver1).performGet(storIOContentResolver1, testEntities.query1)
        verifyNoMoreInteractions(putResolver1)
    }

    private fun verifyDeleteResolver3Behavior(resolver: DeleteResolver3<String>) {
        val result = resolver.performDelete(storIOContentResolver3, entity)

        assertThat(result).isEqualTo(testEntities.deleteResult3)

        verify(deleteResolver1).performDelete(storIOContentResolver1, entity)
        verifyNoMoreInteractions(putResolver1)
    }
}
