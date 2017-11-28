package com.pushtorefresh.storio3.sqlite.annotations;

import android.database.sqlite.SQLiteDatabase;
import java.lang.String;

public final class WithGeneratedTableMultipleKeysTable {
    public static final String NAME = "table";

    public static final String FIELD_1_COLUMN = "field1";

    public static final String FIELD_2_COLUMN = "field2";

    public static final String FIELD_3_COLUMN = "field3";

    public static final String FIELD_4_COLUMN = "field4";

    public static final String FIELD_5_COLUMN = "field5";

    public static final String FIELD_6_COLUMN = "field6";

    public static final String FIELD_7_COLUMN = "field7";

    public static final String FIELD_8_COLUMN = "field8";

    public static final String NEW_FIELD_1_COLUMN = "new_field_1";

    public static final String NEW_FIELD_2_COLUMN = "new_field_2";

    private WithGeneratedTableTable() {
    }

    public static void createTable(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE table (field1 INTEGER,\n"
            + "field2 INTEGER,\n"
            + "field3 INTEGER,\n"
            + "field4 INTEGER,\n"
            + "field5 REAL,\n"
            + "field6 REAL,\n"
            + "field7 TEXT,\n"
            + "field8 BLOB,\n"
            + "new_field_1 INTEGER,\n"
            + "new_field_2 TEXT NOT NULL,\n"
            + "PRIMARY KEY(field4, field7));");
    }

    public static void updateTable(SQLiteDatabase db, int oldVersion) {
        if (oldVersion < 2) {
            db.execSQL("ALTER TABLE table ADD COLUMN new_field_1 INTEGER");
        }
        if (oldVersion < 3) {
            db.execSQL("ALTER TABLE table ADD COLUMN new_field_2 TEXT NOT NULL");
        }
    }
}