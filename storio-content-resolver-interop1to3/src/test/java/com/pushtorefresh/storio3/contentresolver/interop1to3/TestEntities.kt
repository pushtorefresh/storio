@file:Suppress("MemberVisibilityCanPrivate")

package com.pushtorefresh.storio3.contentresolver.interop1to3

import android.database.Cursor
import android.net.Uri
import com.nhaarman.mockito_kotlin.mock
import com.pushtorefresh.storio.contentresolver.operations.delete.DeleteResult as DeleteResult1
import com.pushtorefresh.storio.contentresolver.operations.put.PutResult as PutResult1
import com.pushtorefresh.storio.contentresolver.queries.Query as Query1
import com.pushtorefresh.storio3.contentresolver.operations.delete.DeleteResult as DeleteResult3
import com.pushtorefresh.storio3.contentresolver.operations.put.PutResult as PutResult3
import com.pushtorefresh.storio3.contentresolver.queries.Query as Query3

class TestEntities {

    val cursor = mock<Cursor>()

    val insertedUri = mock<Uri>()

    val numberOfRowsUpdated = 300

    val numberOfRowsDeleted = 3

    val affectedUri = mock<Uri>()

    val insertResult1 = PutResult1.newInsertResult(insertedUri, affectedUri)

    val updateResult1 = PutResult1.newUpdateResult(numberOfRowsUpdated, affectedUri)

    val insertResult3 = PutResult3.newInsertResult(insertedUri, affectedUri)

    val updateResult3 = PutResult3.newUpdateResult(numberOfRowsUpdated, affectedUri)

    val deleteResult1 = DeleteResult1.newInstance(numberOfRowsDeleted, affectedUri)

    val deleteResult3 = DeleteResult3.newInstance(numberOfRowsDeleted, affectedUri)

    val uri = mock<Uri>()

    val query1 = Query1.builder()
            .uri(uri)
            .columns("column")
            .where("where")
            .whereArgs("whereArgs")
            .sortOrder("order")
            .build()

    val query3 = Query3.builder()
            .uri(uri)
            .columns("column")
            .where("where")
            .whereArgs("whereArgs")
            .sortOrder("order")
            .build()
}
