@file:Suppress("MemberVisibilityCanPrivate")

package com.pushtorefresh.storio3.contentresolver.interop2to3

import android.database.Cursor
import android.net.Uri
import com.nhaarman.mockito_kotlin.mock
import com.pushtorefresh.storio2.contentresolver.operations.delete.DeleteResult as DeleteResult2
import com.pushtorefresh.storio2.contentresolver.operations.put.PutResult as PutResult2
import com.pushtorefresh.storio2.contentresolver.queries.Query as Query2
import com.pushtorefresh.storio3.contentresolver.operations.delete.DeleteResult as DeleteResult3
import com.pushtorefresh.storio3.contentresolver.operations.put.PutResult as PutResult3
import com.pushtorefresh.storio3.contentresolver.queries.Query as Query3

class TestEntities {

    val cursor = mock<Cursor>()

    val insertedUri = mock<Uri>()

    val numberOfRowsUpdated = 300

    val numberOfRowsDeleted = 3

    val affectedUri = mock<Uri>()

    val insertResult2 = PutResult2.newInsertResult(insertedUri, affectedUri)

    val updateResult2 = PutResult2.newUpdateResult(numberOfRowsUpdated, affectedUri)

    val insertResult3 = PutResult3.newInsertResult(insertedUri, affectedUri)

    val updateResult3 = PutResult3.newUpdateResult(numberOfRowsUpdated, affectedUri)

    val deleteResult2 = DeleteResult2.newInstance(numberOfRowsDeleted, affectedUri)

    val deleteResult3 = DeleteResult3.newInstance(numberOfRowsDeleted, affectedUri)

    val uri = mock<Uri>()

    val query2 = Query2.builder()
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
