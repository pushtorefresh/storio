package com.pushtorefresh.storio.sqlite.annotations.processor.generate

import com.pushtorefresh.storio.sqlite.annotations.processor.introspection.StorIOSQLiteTypeMeta

object QueryGenerator {

    val WHERE_CLAUSE = "where"
    val WHERE_ARGS = "whereArgs"

    fun createWhere(typeMeta: StorIOSQLiteTypeMeta, varName: String): Map<String, String> {
        val whereClause = StringBuilder()
        val whereArgs = StringBuilder()

        var i = 0

        typeMeta.columns.values.forEach {
            if (it.storIOColumn.key) {
                if (i == 0) {
                    whereClause
                            .append(it.storIOColumn.name)
                            .append(" = ?")

                    whereArgs
                            .append(varName)
                            .append(".")
                            .append(it.elementName)
                } else {
                    whereClause
                            .append(" AND ")
                            .append(it.storIOColumn.name)
                            .append(" = ?")

                    whereArgs
                            .append(", ")
                            .append(varName)
                            .append(".")
                            .append(it.elementName)
                }

                if (it.isMethod) {
                    whereArgs.append("()")
                }

                i++
            }
        }

        if (whereClause.isEmpty() || whereArgs.isEmpty()) {
            return emptyMap()
        } else {
            return mapOf(WHERE_CLAUSE to whereClause.toString(),
                    WHERE_ARGS to whereArgs.toString())
        }
    }
}
