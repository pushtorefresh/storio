package com.pushtorefresh.storio3.sqlite.annotations.processor.generate

import com.pushtorefresh.storio3.sqlite.annotations.processor.introspection.StorIOSQLiteTypeMeta

object QueryGenerator {

    val WHERE_CLAUSE = "where"
    val WHERE_ARGS = "whereArgs"

    fun createWhere(typeMeta: StorIOSQLiteTypeMeta, varName: String): Map<String, String> {
        val whereClause = StringBuilder()
        val whereArgs = StringBuilder()

        var i = 0

        typeMeta.columns.values.forEach { columnMeta ->
            if (columnMeta.storIOColumn.key) {
                if (i == 0) {
                    whereClause
                            .append(columnMeta.storIOColumn.name)
                            .append(" = ?")

                    whereArgs
                            .append(varName)
                            .append(".")
                            .append(columnMeta.contextAwareName)
                } else {
                    whereClause
                            .append(" AND ")
                            .append(columnMeta.storIOColumn.name)
                            .append(" = ?")

                    whereArgs
                            .append(", ")
                            .append(varName)
                            .append(".")
                            .append(columnMeta.contextAwareName)
                }
                i++
            }
        }

        return when {
            whereClause.isEmpty() || whereArgs.isEmpty() -> emptyMap()
            else -> mapOf(
                    WHERE_CLAUSE to whereClause.toString(),
                    WHERE_ARGS to whereArgs.toString())
        }
    }
}
