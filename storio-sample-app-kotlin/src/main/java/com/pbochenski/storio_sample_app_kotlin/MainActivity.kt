package com.pbochenski.storio_sample_app_kotlin

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.pushtorefresh.storio.sqlite.annotations.StorIOSQLiteColumn
import com.pushtorefresh.storio.sqlite.annotations.StorIOSQLiteType
import com.pushtorefresh.storio.sqlite.impl.DefaultStorIOSQLite
import com.pushtorefresh.storio.sqlite.queries.Query

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val base = DefaultStorIOSQLite.builder()
                .sqliteOpenHelper(DBHelper(this))
                .addTypeMapping(User::class.java, UserSQLiteTypeMapping())
                .addTypeMapping(JavaModel::class.java, JavaModelSQLiteTypeMapping())
                .addTypeMapping(Open::class.java, OpenSQLiteTypeMapping())
                .build()

        base.put()
                .objects(listOf(User(1, "pbochenski"), User(2, "otherUser")))
                .prepare()
                .executeAsBlocking()

        base.get()
                .listOfObjects(User::class.java)
                .withQuery(Query.builder().table(UsersTable.TABLE).build())
                .prepare()
                .executeAsBlocking().forEach {
            Log.d("STOREIO", "${it.id} = ${it.name}")
        }

        //is it still compatible with java objects:
        base.put()
                .objects(listOf(JavaModel().apply {
                    id = 0
                    author = "pbochenski"
                    content = "sometweet"
                }))
                .prepare()
                .executeAsBlocking()

        base.get()
                .listOfObjects(JavaModel::class.java)
                .withQuery(Query.builder().table("javaModel").build())
                .prepare()
                .executeAsBlocking().forEach {
            Log.d("STOREIO", "${it.id} = ${it.author} , ${it.content}")
        }

        base.put()
                .objects(listOf(Open(1, "pawel")))
                .prepare()
                .executeAsBlocking()
        base.get()
                .listOfObjects(Open::class.java)
                .withQuery(Query.builder().table("second").build())
                .prepare()
                .executeAsBlocking().forEach {
            Log.d("STOREIO", "${it.id} = ${it.name}")
        }
    }
}

class DBHelper(context: Context) : SQLiteOpenHelper(context, "db", null, 1) {
    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL(UsersTable.CREATE_TABLE_QUERY)
        db?.execSQL(JavaModelTable.createJavaModelTable)
        db?.execSQL(OpenTable.createOpenTable)
    }

    override fun onUpgrade(p0: SQLiteDatabase?, p1: Int, p2: Int) {
        //no upgrade in this example
    }

}

object UsersTable {
    const val TABLE = "users"
    const val COLUMN_ID = "_id"
    const val COLUMN_NAME = "name"

    val CREATE_TABLE_QUERY = "CREATE TABLE $TABLE (" +
            "$COLUMN_ID INTEGER NOT NULL PRIMARY KEY," +
            "$COLUMN_NAME TEXT NOT NULL" +
            ");"
}

object JavaModelTable {
    val createJavaModelTable = "CREATE TABLE javaModel (" +
            "id INTEGER NOT NULL PRIMARY KEY," +
            "author TEXT NOT NULL, " +
            "content TEXT NOT NULL" +
            ");"
}

object OpenTable {
    val createOpenTable = "CREATE TABLE second (" +
            "id INTEGER NOT NULL PRIMARY KEY," +
            "name TEXT NOT NULL " +
            ");"
}

/*
    This class is Kotlins way of working. val fields and creating using constructor.
    Immutable (if fields also are immutable)
    Also parameters can be NonNull types.
    As a downside constructorSeq field needs to be introduced, because it is not possible
    to figure out order of parameters in constructor.
 */
@StorIOSQLiteType(table = UsersTable.TABLE, hasConstructor = true)
data class User(@StorIOSQLiteColumn(name = UsersTable.COLUMN_ID, key = true, constructorSeq = 0)
                val id: Int,
                @StorIOSQLiteColumn(name = UsersTable.COLUMN_NAME, constructorSeq = 1)
                val name: String)

/*
    this class is java way but in kotlin. fields can be null.
    but it works without annotation processor modification.
    But this class is mutable :(
 */
@StorIOSQLiteType(table = "second")
data class Open(@JvmField
                @StorIOSQLiteColumn(name = "id", key = true)
                var id: Int?,
                @JvmField
                @StorIOSQLiteColumn(name = "name")
                var name: String?) {
    constructor() : this(null, null) {
    }
}

