package com.pushtorefresh.storio.contentresolver.annotations.processor.generate

import com.pushtorefresh.storio.contentresolver.annotations.processor.introspection.StorIOContentResolverTypeMeta

object QueryGenerator {

    val WHERE_CLAUSE = "where"
    val WHERE_ARGS = "whereArgs"

    fun createWhere(typeMeta: StorIOContentResolverTypeMeta, varName: String): Map<String, String> {
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

                if (it.isMethod) whereArgs.append("()")

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
