@file:Suppress("MemberVisibilityCanPrivate")

package com.pushtorefresh.storio3.contentresolver.interop1to3

import com.pushtorefresh.private_constructor_checker.PrivateConstructorChecker
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class Queries1To3Test {

    val testEntities = TestEntities()

    @Test
    fun `toV1Query should work`() {
        assertThat(Queries1To3.toV1Query(testEntities.query3))
                .isEqualTo(testEntities.query1)
    }

    @Test
    fun `toV3Query should work`() {
        assertThat(Queries1To3.toV3Query(testEntities.query1))
                .isEqualTo(testEntities.query3)
    }

    @Test
    fun `constructor must be private and throw exception`() {
        PrivateConstructorChecker
                .forClass(Queries1To3::class.java)
                .expectedTypeOfException(IllegalStateException::class.java)
                .expectedExceptionMessage("No instances please.")
                .check()
    }
}
