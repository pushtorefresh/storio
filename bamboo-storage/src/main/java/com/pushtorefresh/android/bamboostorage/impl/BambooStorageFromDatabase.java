package com.pushtorefresh.android.bamboostorage.impl;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.pushtorefresh.android.bamboostorage.BambooStorableType;
import com.pushtorefresh.android.bamboostorage.PreparedQuery;
import com.pushtorefresh.android.bamboostorage.annotation.StorableType;
import com.pushtorefresh.android.bamboostorage.BambooStorage;
import com.pushtorefresh.android.bamboostorage.ForType;
import com.pushtorefresh.android.bamboostorage.exception.InsertRuntimeException;
import com.pushtorefresh.android.bamboostorage.wtf.Query;
import com.pushtorefresh.android.bamboostorage.wtf.StorableTypeParser;
import com.pushtorefresh.android.bamboostorage.wtf.StorableTypeSerializer;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class BambooStorageFromDatabase implements BambooStorage {

    private final Map<Class, StorableTypeParser> parsers = new ConcurrentHashMap<>();
    private final Map<Class, StorableTypeSerializer> serializers = new ConcurrentHashMap<>();

    @NonNull private final SQLiteDatabase db;
    @NonNull private final Internal internal = new InternalImpl();
    private final boolean searchForGeneratedClasses;

    protected BambooStorageFromDatabase(@NonNull SQLiteDatabase db, boolean searchForGeneratedClasses) {
        this.db = db;
        this.searchForGeneratedClasses = searchForGeneratedClasses;
    }

    @SuppressWarnings("unchecked")
    @NonNull @Override public <T extends BambooStorableType> StorableTypeParser<T> getParser(@NonNull Class<T> type) {
        return parsers.get(type);
    }

    @Override public <T extends BambooStorableType> void setParserForType(@NonNull Class<T> type, @NonNull StorableTypeParser<T> parser) {
        parsers.put(type, parser);
    }

    @SuppressWarnings("unchecked")
    @NonNull @Override public <T extends BambooStorableType> StorableTypeSerializer<T> getSerializer(@NonNull Class<T> type) {
        return serializers.get(type);
    }

    @Override public <T extends BambooStorableType> void setSerializerForType(
            @NonNull Class<T> type, @NonNull StorableTypeSerializer<T> serializer) {
        serializers.put(type, serializer);
    }

    @NonNull @Override public <T extends BambooStorableType> ForType<T> forType(@NonNull Class<T> type) {
        return new ForType<>(this, type);
    }

    @NonNull @Override public PreparedQuery.Builder prepareQuery(@NonNull Query query) {
        return new PreparedQuery.Builder(query);
    }

    @NonNull @Override public Internal getInternal() {
        return internal;
    }

    private class InternalImpl implements Internal {
        @NonNull @Override public <T extends BambooStorableType> String getStorableIdFieldName(@NonNull Class<T> type) {
            return getAnnotation(type).idFieldName();
        }

        @NonNull @Override public <T extends BambooStorableType> Cursor query(@NonNull Class<T> type, @Nullable String where, @Nullable String[] whereArgs, @Nullable String orderBy) {
            return db.query(
                    getTableName(type),
                    null,
                    where,
                    whereArgs,
                    null,
                    null,
                    orderBy
            );
        }

        @SuppressWarnings("unchecked")
        @Override public <T extends BambooStorableType> long insert(@NonNull T object) {
            Class<T> type = (Class<T>) object.getClass();

            long insertedId = db.insert(
                    getTableName(type),
                    null,
                    getSerializer(type).toContentValues(object)
            );

            if (insertedId == -1) {
                throw new InsertRuntimeException("Can not insert object of type " + object.getClass().getCanonicalName() + ": " + object + ", sqlDataBase returned -1");
            }

            // setting inserted id to object
            object.setStorableId(insertedId);

            return insertedId;
        }

        @SuppressWarnings("unchecked")
        @Override public <T extends BambooStorableType> int update(@NonNull T object, @Nullable String where, @Nullable String[] whereArgs) {
            Class<T> type = (Class<T>) object.getClass();

            return db.update(
                    getTableName(type),
                    getSerializer(type).toContentValues(object),
                    where,
                    whereArgs
            );
        }

        @Override
        public <T extends BambooStorableType> int delete(@NonNull Class<T> type, @Nullable String where, @Nullable String[] whereArgs) {
            return db.delete(
                    getTableName(type),
                    where,
                    whereArgs
            );
        }

        @SuppressWarnings("unchecked")
        @Override
        public <T extends BambooStorableType> int delete(@NonNull T object) {
            Class<T> type = (Class<T>) object.getClass();

            return db.delete(
                    getTableName(type),
                    getStorableIdFieldName(type) + " = ?",
                    new String[] { String.valueOf(object.getStorableId()) }
            );
        }
    }

    @NonNull protected <T extends BambooStorableType> StorableType getAnnotation(Class<T> type) {
        // TODO REFACTOR and FIX, add caching layer for annotations

        if (!type.isAnnotationPresent(StorableType.class)) {
            throw new IllegalStateException("Type " + type.getCanonicalName() + " should be annotated with " + StorableType.class.getCanonicalName() + " annotation");
        }

        return type.getAnnotation(StorableType.class);
    }

    @NonNull protected <T extends BambooStorableType> String getTableName(Class<T> type) {
        return getAnnotation(type).tableName();
    }

    public static class Builder {
        @Nullable private SQLiteDatabase db;
        private boolean searchForGeneratedClasses = true;

        @NonNull public Builder database(SQLiteDatabase db) {
            if (db == null) {
                throw new IllegalArgumentException("db should not be null");
            }

            this.db = db;
            return this;
        }

        @NonNull public Builder sqLiteOpenHelper(SQLiteOpenHelper sqLiteOpenHelper) {
            if (sqLiteOpenHelper == null) {
                throw new IllegalArgumentException("sqLiteOpenHelper should not be null");
            }

            SQLiteDatabase db = sqLiteOpenHelper.getWritableDatabase();

            if (db == null) {
                throw new IllegalArgumentException("sqLiteOpenHelper should return non null writable database");
            }

            this.db = db;
            return this;
        }

        @NonNull public Builder searchForGeneratedClasses(boolean searchForGeneratedClasses) {
            this.searchForGeneratedClasses = searchForGeneratedClasses;
            return this;
        }

        @NonNull public BambooStorage build() {
            if (db == null) {
                throw new IllegalStateException("database should not be null for building " + BambooStorageFromDatabase.class.getSimpleName() + " instance");
            }

            return new BambooStorageFromDatabase(db, searchForGeneratedClasses);
        }
    }
}
