package com.pushtorefresh.storio.contentresolver.annotations.processor.generate;

import com.pushtorefresh.storio.contentresolver.annotations.processor.introspection.StorIOContentResolverColumnMeta;
import com.pushtorefresh.storio.contentresolver.annotations.processor.introspection.StorIOContentResolverTypeMeta;

import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class QueryGenerator {

    public static final String WHERE_CLAUSE = "where";
    public static final String WHERE_ARGS = "whereArgs";

    @NotNull
    public static Map<String, String> createWhere(
            @NotNull final StorIOContentResolverTypeMeta storIOContentResolverTypeMeta,
            @NotNull final String varName) {
        final StringBuilder whereClause = new StringBuilder();
        final StringBuilder whereArgs = new StringBuilder();

        int i = 0;

        for (final StorIOContentResolverColumnMeta columnMeta : storIOContentResolverTypeMeta.columns.values()) {
            if (columnMeta.storIOColumn.key()) {
                if (i == 0) {
                    whereClause
                            .append(columnMeta.storIOColumn.name())
                            .append(" = ?");

                    whereArgs
                            .append(varName)
                            .append(".")
                            .append(columnMeta.elementName);
                } else {
                    whereClause
                            .append(" AND ")
                            .append(columnMeta.storIOColumn.name())
                            .append(" = ?");

                    whereArgs
                            .append(", ")
                            .append(varName)
                            .append(".")
                            .append(columnMeta.elementName);
                }

                if (columnMeta.isMethod()) {
                    whereArgs.append("()");
                }

                i++;
            }
        }

        if (whereClause.length() == 0 || whereArgs.length() == 0) {
            return Collections.emptyMap();
        } else {
            final Map<String, String> result = new HashMap<String, String>(2);

            result.put(WHERE_CLAUSE, whereClause.toString()); // example: "email = ? AND user_id = ?"
            result.put(WHERE_ARGS, whereArgs.toString()); // example: "object.email, object.userId"

            return result;
        }
    }
}
