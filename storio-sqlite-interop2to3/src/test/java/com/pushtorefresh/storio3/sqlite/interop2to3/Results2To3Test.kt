@file:Suppress("MemberVisibilityCanPrivate")

package com.pushtorefresh.storio3.sqlite.interop2to3

import com.pushtorefresh.private_constructor_checker.PrivateConstructorChecker
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class Results2To3Test {

    val testEntities = TestEntities()

    @Test
    fun `toV2PutResult should work for insert result`() {
        assertThat(Results2To3.toV2PutResult(testEntities.insertResult3))
                .isEqualTo(testEntities.insertResult2)
    }

    @Test
    fun `toV2PutResult should work for update result`() {
        assertThat(Results2To3.toV2PutResult(testEntities.updateResult3))
                .isEqualTo(testEntities.updateResult2)
    }

    @Test
    fun `toV2DeleteResult should work`() {
        assertThat(Results2To3.toV2DeleteResult(testEntities.deleteResult3))
                .isEqualTo(testEntities.deleteResult2)
    }

    @Test
    fun `toV3PutResult should work for insert result`() {
        assertThat(Results2To3.toV3PutResult(testEntities.insertResult2))
                .isEqualTo(testEntities.insertResult3)
    }

    @Test
    fun `toV3PutResult should work for update result`() {
        assertThat(Results2To3.toV3PutResult(testEntities.updateResult2))
                .isEqualTo(testEntities.updateResult3)
    }

    @Test
    fun `toV3DeleteResult should work`() {
        assertThat(Results2To3.toV3DeleteResult(testEntities.deleteResult2))
                .isEqualTo(testEntities.deleteResult3)
    }

    @Test
    fun `constructor must be private and throw exception`() {
        PrivateConstructorChecker
                .forClass(Results2To3::class.java)
                .expectedTypeOfException(IllegalStateException::class.java)
                .expectedExceptionMessage("No instances please.")
                .check()
    }
}
