@file:Suppress("MemberVisibilityCanPrivate")

package com.pushtorefresh.storio3.contentresolver.interop2to3

import com.pushtorefresh.private_constructor_checker.PrivateConstructorChecker
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class Queries2To3Test {

    val testEntities = TestEntities()

    @Test
    fun `toV1Query should work`() {
        assertThat(Queries2To3.toV2Query(testEntities.query3))
                .isEqualTo(testEntities.query2)
    }

    @Test
    fun `toV3Query should work`() {
        assertThat(Queries2To3.toV3Query(testEntities.query2))
                .isEqualTo(testEntities.query3)
    }

    @Test
    fun `constructor must be private and throw exception`() {
        PrivateConstructorChecker
                .forClass(Queries2To3::class.java)
                .expectedTypeOfException(IllegalStateException::class.java)
                .expectedExceptionMessage("No instances please.")
                .check()
    }
}
