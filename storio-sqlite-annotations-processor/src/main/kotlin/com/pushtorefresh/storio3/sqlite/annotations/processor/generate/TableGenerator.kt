package com.pushtorefresh.storio3.sqlite.annotations.processor.generate

import com.pushtorefresh.storio3.common.annotations.processor.generate.Common.INDENT
import com.pushtorefresh.storio3.common.annotations.processor.generate.Generator
import com.pushtorefresh.storio3.common.annotations.processor.toUpperSnakeCase
import com.pushtorefresh.storio3.sqlite.annotations.processor.introspection.StorIOSQLiteColumnMeta
import com.pushtorefresh.storio3.sqlite.annotations.processor.introspection.StorIOSQLiteTypeMeta
import com.squareup.javapoet.*
import javax.lang.model.element.Modifier.*

private const val ANDROID_NON_NULL_ANNOTATION = "android.support.annotation.NonNull"
private const val DB_PARAM = "db"
private const val OLD_VERSION_PARAM = "oldVersion"

object TableGenerator : Generator<StorIOSQLiteTypeMeta> {

    private val sqliteDatabase = ClassName.get("android.database.sqlite", "SQLiteDatabase")

    override fun generateJavaFile(typeMeta: StorIOSQLiteTypeMeta): JavaFile {
        val tableSpec = TypeSpec.classBuilder("${typeMeta.simpleName}Table")
                .addModifiers(PUBLIC, FINAL)
                .addMethod(generatePrivateConstructor())
                .addFields(generateFields(typeMeta.storIOType.table, typeMeta.columns.values))
                .addMethod(generateCreateMethod(typeMeta.storIOType.table, typeMeta.columns.values))
                .addMethod(generateUpdateMethod(typeMeta.storIOType.table, typeMeta.columns.values))
                .build()

        return JavaFile.builder(typeMeta.packageName, tableSpec)
                .indent(INDENT)
                .build()
    }

    private fun generateFields(table: String, columns: Collection<StorIOSQLiteColumnMeta>): Iterable<FieldSpec> {
        val list = mutableListOf<FieldSpec>()

        list += FieldSpec.builder(String::class.java, "NAME")
                .initializer("\$S", table)
                .addModifiers(PUBLIC, STATIC, FINAL)
                .build()

        columns.forEach { column ->
            list += FieldSpec.builder(String::class.java, "${column.elementName.toUpperSnakeCase()}_COLUMN")
                    .initializer("\$S", column.storIOColumn.name)
                    .addModifiers(PUBLIC, STATIC, FINAL)
                    .build()
        }

        return list
    }

    private fun generateCreateMethod(table: String, columns: Collection<StorIOSQLiteColumnMeta>): MethodSpec {
        val builder = StringBuilder()

        builder.append("CREATE TABLE $table (")

        val primaryKeys = columns.filter { it.storIOColumn.key }.toList()

        columns.forEachIndexed { index, column ->
            builder.append("${column.storIOColumn.name} ${column.javaType.sqliteType}")
            if (column.isNotNull()) builder.append(" NOT NULL")
            if (column.storIOColumn.key && primaryKeys.size == 1) builder.append(" PRIMARY KEY")
            if (index != columns.size - 1) builder.append(",\n")
        }

        if (primaryKeys.size > 1) {
            builder.append(",\nPRIMARY KEY(")
            primaryKeys.forEachIndexed { index, key ->
                builder.append(key.storIOColumn.name)
                if (index != primaryKeys.size - 1) builder.append(", ")
            }
            builder.append(")")
        }

        builder.append(");")

        return MethodSpec.methodBuilder("createTable")
                .addModifiers(PUBLIC, STATIC)
                .addParameter(ParameterSpec.builder(sqliteDatabase, DB_PARAM).build())
                .addStatement("$DB_PARAM.execSQL(\$S)", builder.toString())
                .build()
    }

    private fun generateUpdateMethod(table: String, columns: Collection<StorIOSQLiteColumnMeta>): MethodSpec {
        val builder = MethodSpec.methodBuilder("updateTable")
                        .addModifiers(PUBLIC, STATIC)
                        .addParameter(ParameterSpec.builder(sqliteDatabase, DB_PARAM).build())
                        .addParameter(ParameterSpec.builder(TypeName.INT, OLD_VERSION_PARAM).build())

        val columnsToUpdate = columns
                .filter { it.storIOColumn.version > 1 }
                .sortedBy { it.storIOColumn.version }

        columnsToUpdate.forEach { column ->
            builder.beginControlFlow("if ($OLD_VERSION_PARAM < ${column.storIOColumn.version})")
            builder.addCode("$DB_PARAM.execSQL(\$S);\n", "ALTER TABLE $table ADD COLUMN ${column.storIOColumn.name} ${column.javaType.sqliteType}${if (column.isNotNull()) " NOT NULL" else ""}")
            builder.endControlFlow()
        }

        return builder.build()
    }

    private fun generatePrivateConstructor() = MethodSpec.constructorBuilder().addModifiers(PRIVATE).build()

    private fun StorIOSQLiteColumnMeta.isNotNull(): Boolean {
        this.element.annotationMirrors.forEach {
            if (it.annotationType.toString() == ANDROID_NON_NULL_ANNOTATION) return true
        }
        return false
    }
}