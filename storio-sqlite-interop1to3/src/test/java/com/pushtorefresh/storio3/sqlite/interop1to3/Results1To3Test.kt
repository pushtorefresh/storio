@file:Suppress("MemberVisibilityCanPrivate")

package com.pushtorefresh.storio3.sqlite.interop1to3

import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import com.pushtorefresh.storio.sqlite.operations.put.PutResult as PutResult1
import com.pushtorefresh.storio.sqlite.operations.delete.DeleteResult as DeleteResult1
import com.pushtorefresh.storio3.sqlite.operations.put.PutResult as PutResult3
import com.pushtorefresh.storio3.sqlite.operations.delete.DeleteResult as DeleteResult3

class Results1To3Test {

    val testEntities = TestEntities()

    @Test
    fun `toV1PutResult should work for insert result`() {
        assertThat(Results1To3.toV1PutResult(testEntities.insertResult3))
                .isEqualTo(testEntities.insertResult1)
    }

    @Test
    fun `toV1PutResult should work for update result`() {
        assertThat(Results1To3.toV1PutResult(testEntities.updateResult3))
                .isEqualTo(testEntities.updateResult1)
    }

    @Test
    fun `toV1DeleteResult should work`() {
        assertThat(Results1To3.toV1DeleteResult(testEntities.deleteResult3))
                .isEqualTo(testEntities.deleteResult1)
    }

    @Test
    fun `toV3PutResult should work for insert result`() {
        assertThat(Results1To3.toV3PutResult(testEntities.insertResult1))
                .isEqualTo(testEntities.insertResult3)
    }

    @Test
    fun `toV3PutResult should work for update result`() {
        assertThat(Results1To3.toV3PutResult(testEntities.updateResult1))
                .isEqualTo(testEntities.updateResult3)
    }

    @Test
    fun `toV3DeleteResult should work`() {
        assertThat(Results1To3.toV3DeleteResult(testEntities.deleteResult1))
                .isEqualTo(testEntities.deleteResult3)
    }
}
