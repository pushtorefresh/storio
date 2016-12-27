package com.pushtorefresh.storio.sqlite.annotations.processor.generate;

import com.pushtorefresh.storio.common.annotations.processor.generate.Generator;
import com.pushtorefresh.storio.sqlite.annotations.processor.introspection.StorIOSQLiteColumnMeta;
import com.pushtorefresh.storio.sqlite.annotations.processor.introspection.StorIOSQLiteTypeMeta;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Modifier;

import static com.pushtorefresh.storio.common.annotations.processor.generate.Common.INDENT;

public class TableGenerator implements Generator<StorIOSQLiteTypeMeta> {

    private static final String SUFFIX = "Table";
    private static final String CREATE_METHOD_NAME = "createTable";
    private static final String UPDATE_METHOD_NAME = "updateTable";
    private static final String UPDATE_METHOD_VERSION_OLD_PARAM = "versionOld";
    private static final String METHOD_DB_PARAM = "db";

    private final ClassName db = ClassName.get("android.database.sqlite", "SQLiteDatabase");
    private static final String tableName = "tableName";

    @Override
    public JavaFile generateJavaFile(@NotNull StorIOSQLiteTypeMeta typeMeta) {
        if (!typeMeta.storIOType.generateTableClass()) {
            return null;
        }

        TypeSpec tableSpec = TypeSpec.classBuilder(typeMeta.simpleName + SUFFIX)
                .addFields(generateFields(typeMeta.storIOType.table(), typeMeta.columns.values()))
                .addMethod(generateCreateMethod(typeMeta.storIOType.table(), typeMeta.columns.values()))
                .addMethod(generateUpdateMethod(typeMeta.storIOType.table(), typeMeta.columns.values()))
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                .addMethod(generateConstructor())
                .build();
        return JavaFile.builder(typeMeta.packageName, tableSpec)
                .indent(INDENT)
                .build();
    }

    @NotNull
    private Iterable<FieldSpec> generateFields(@NotNull String table, @NotNull Collection<StorIOSQLiteColumnMeta> columns) {
        List<FieldSpec> list = new ArrayList<FieldSpec>();
        list.add(FieldSpec.builder(String.class, tableName)
                .initializer("$S", table)
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)
                .build());
        for (StorIOSQLiteColumnMeta column : columns) {
            list.add(FieldSpec.builder(String.class, "COLUMN_" + column.elementName)
                    .initializer("$S", column.storIOColumn.name())
                    .addModifiers(Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)
                    .build());
        }
        return list;
    }

    @NotNull
    private MethodSpec generateConstructor() {
        return MethodSpec.constructorBuilder().addModifiers(Modifier.PRIVATE).build();
    }

    @NotNull
    private MethodSpec generateCreateMethod(@NotNull String table, @NotNull Collection<StorIOSQLiteColumnMeta> columns) {
        String code = "CREATE TABLE " + table + " (";
        int i = 0;
        for (StorIOSQLiteColumnMeta entry : columns) {
            code += entry.storIOColumn.name() + " " + entry.javaType.getSqlType();
            if (isNotNull(entry)) {
                code += " NOT NULL";
            }
            if (entry.storIOColumn.key()) {
                code += " PRIMARY KEY";
            }
            if (i != columns.size() - 1) {
                code += ",\n";
            }
            i++;
        }
        code += ");";
        return MethodSpec.methodBuilder(CREATE_METHOD_NAME)
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .addParameter(ParameterSpec.builder(db, METHOD_DB_PARAM).build())
                .addStatement(METHOD_DB_PARAM + ".execSQL($S)", code)
                .build();
    }

    @NotNull
    private MethodSpec generateUpdateMethod(@NotNull String table, @NotNull Collection<StorIOSQLiteColumnMeta> columns) {
        List<StorIOSQLiteColumnMeta> list = new ArrayList<StorIOSQLiteColumnMeta>(columns);
        //sort on version
        Collections.sort(list, new Comparator<StorIOSQLiteColumnMeta>() {
            @Override
            public int compare(StorIOSQLiteColumnMeta t0, StorIOSQLiteColumnMeta t1) {
                return t0.storIOColumn.version() - t1.storIOColumn.version();
            }
        });
        //remove version == 0, those are create during createTable
        List<StorIOSQLiteColumnMeta> filtered = new ArrayList<StorIOSQLiteColumnMeta>();
        for (StorIOSQLiteColumnMeta o : list) {
            if (o.storIOColumn.version() != 0) {
                filtered.add(o);
            }
        }
        String code = "";
        if (filtered.size() > 0) {
            for (int i = 0; i < filtered.size(); i++) {
                StorIOSQLiteColumnMeta o = filtered.get(i);
                if (o.storIOColumn.version() > 1) {
                    code += "if(" + UPDATE_METHOD_VERSION_OLD_PARAM + " < " + o.storIOColumn.version() + ") {\n" +
                            "    " + METHOD_DB_PARAM + ".execSQL(\"ALTER TABLE " + table + " ADD COLUMN " + o.storIOColumn.name() + " " + o.javaType.getSqlType();
                    if (isNotNull(o)) {
                        code += " NOT NULL";
                    }
                    code += ";\");\n";
                    code += "}\n";
                }
            }

        }
        return MethodSpec.methodBuilder(UPDATE_METHOD_NAME)
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .addParameter(ParameterSpec.builder(db, METHOD_DB_PARAM).build())
                .addParameter(ParameterSpec.builder(TypeName.INT, UPDATE_METHOD_VERSION_OLD_PARAM).build())
                .addCode(code)
                .build();
    }

    private boolean isNotNull(@NotNull StorIOSQLiteColumnMeta entry) {
        for (AnnotationMirror mirror : entry.element.getAnnotationMirrors()) {
            // android.support.annotation class is not in the classpath of annotation processor so I check it by name
            if (mirror.getAnnotationType().toString().equals("android.support.annotation.NonNull")) {
                return true;
            }
        }
        return entry.element.getAnnotation(NotNull.class) != null;
    }
}
