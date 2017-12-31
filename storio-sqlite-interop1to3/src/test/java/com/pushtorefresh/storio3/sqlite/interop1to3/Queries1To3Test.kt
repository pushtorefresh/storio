package com.pushtorefresh.storio3.sqlite.interop1to3

import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import com.pushtorefresh.storio.sqlite.StorIOSQLite as StorIOSQLite1
import com.pushtorefresh.storio.sqlite.queries.Query as Query1
import com.pushtorefresh.storio.sqlite.queries.RawQuery as RawQuery1
import com.pushtorefresh.storio3.sqlite.StorIOSQLite as StorIOSQLite3
import com.pushtorefresh.storio3.sqlite.queries.Query as Query3
import com.pushtorefresh.storio3.sqlite.queries.RawQuery as RawQuery3

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
    fun `toV1RawQuery should work`() {
        assertThat(Queries1To3.toV1RawQuery(testEntities.rawQuery3))
                .isEqualTo(testEntities.rawQuery1)
    }

    @Test
    fun `toV3RawQuery should work`() {
        assertThat(Queries1To3.toV3RawQuery(testEntities.rawQuery1))
                .isEqualTo(testEntities.rawQuery3)
    }
}
