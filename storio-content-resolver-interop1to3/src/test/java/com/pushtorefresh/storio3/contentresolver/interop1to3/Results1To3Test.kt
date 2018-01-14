@file:Suppress("MemberVisibilityCanPrivate")

package com.pushtorefresh.storio3.contentresolver.interop1to3

import com.pushtorefresh.private_constructor_checker.PrivateConstructorChecker
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

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

    @Test
    fun `constructor must be private and throw exception`() {
        PrivateConstructorChecker
                .forClass(Results1To3::class.java)
                .expectedTypeOfException(IllegalStateException::class.java)
                .expectedExceptionMessage("No instances please.")
                .check()
    }
}
