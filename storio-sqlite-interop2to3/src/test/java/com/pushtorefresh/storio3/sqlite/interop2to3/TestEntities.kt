@file:Suppress("MemberVisibilityCanPrivate")

package com.pushtorefresh.storio3.sqlite.interop2to3

import android.database.Cursor
import com.nhaarman.mockito_kotlin.mock
import com.pushtorefresh.storio2.sqlite.StorIOSQLite as StorIOSQLite2
import com.pushtorefresh.storio2.sqlite.operations.delete.DeleteResult as DeleteResult2
import com.pushtorefresh.storio2.sqlite.operations.put.PutResult as PutResult2
import com.pushtorefresh.storio2.sqlite.queries.Query as Query2
import com.pushtorefresh.storio2.sqlite.queries.RawQuery as RawQuery2
import com.pushtorefresh.storio3.sqlite.StorIOSQLite as StorIOSQLite3
import com.pushtorefresh.storio3.sqlite.operations.delete.DeleteResult as DeleteResult3
import com.pushtorefresh.storio3.sqlite.operations.put.PutResult as PutResult3
import com.pushtorefresh.storio3.sqlite.queries.Query as Query3
import com.pushtorefresh.storio3.sqlite.queries.RawQuery as RawQuery3

class TestEntities {

    val cursor = mock<Cursor>()

    val insertedId = 1L

    val numberOfRowsUpdated = 300

    val numberOfRowsDeleted = 3

    val affectedTables = setOf("table1", "table2")

    val affectedTags = setOf("tag1", "tag2")

    val insertResult2 = PutResult2.newInsertResult(
            insertedId,
            affectedTables,
            affectedTags
    )

    val updateResult2 = PutResult2.newUpdateResult(
            numberOfRowsUpdated,
            affectedTables,
            affectedTags
    )

    val insertResult3 = PutResult3.newInsertResult(
            insertedId,
            affectedTables,
            affectedTags
    )

    val updateResult3 = PutResult3.newUpdateResult(
            numberOfRowsUpdated,
            affectedTables,
            affectedTags
    )

    val deleteResult2 = DeleteResult2.newInstance(
            numberOfRowsDeleted,
            affectedTables,
            affectedTags
    )

    val deleteResult3 = DeleteResult3.newInstance(
            numberOfRowsDeleted,
            affectedTables,
            affectedTags
    )

    val query2 = Query2.builder()
            .table("table")
            .distinct(true)
            .columns("column")
            .where("where")
            .whereArgs("whereArgs")
            .groupBy("groupBy")
            .having("having")
            .orderBy("orderBy")
            .limit("limit")
            .observesTags("tag")
            .build()

    val query3 = Query3.builder()
            .table("table")
            .distinct(true)
            .columns("column")
            .where("where")
            .whereArgs("whereArgs")
            .groupBy("groupBy")
            .having("having")
            .orderBy("orderBy")
            .limit("limit")
            .observesTags("tag")
            .build()

    val rawQuery2 = RawQuery2.builder()
            .query("query")
            .args("args")
            .affectsTables("affectsTable")
            .affectsTags("affectsTag")
            .observesTables("observesTable")
            .observesTags("observesTag")
            .build()

    val rawQuery3 = RawQuery3.builder()
            .query("query")
            .args("args")
            .affectsTables("affectsTable")
            .affectsTags("affectsTag")
            .observesTables("observesTable")
            .observesTags("observesTag")
            .build()
}
