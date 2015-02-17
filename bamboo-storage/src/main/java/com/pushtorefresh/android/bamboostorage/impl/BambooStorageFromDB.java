package com.pushtorefresh.android.bamboostorage.impl;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.pushtorefresh.android.bamboostorage.BambooStorableType;
import com.pushtorefresh.android.bamboostorage.StorableType;
import com.pushtorefresh.android.bamboostorage.BambooStorage;
import com.pushtorefresh.android.bamboostorage.ForType;
import com.pushtorefresh.android.bamboostorage.wtf.StorableTypeParser;
import com.pushtorefresh.android.bamboostorage.wtf.StorableTypeSerializer;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class BambooStorageFromDB implements BambooStorage {

    private static final Map<Class, StorableTypeParser> PARSERS  = new ConcurrentHashMap<>();
    private static final Map<Class, StorableTypeSerializer> SERIALIZERS = new ConcurrentHashMap<>();

    @NonNull private final SQLiteDatabase db;
    @NonNull private final Internal internal = new InternalImpl();

    public BambooStorageFromDB(@NonNull SQLiteDatabase db) {
        this.db = db;
    }

    public BambooStorageFromDB(@NonNull SQLiteOpenHelper sqLiteOpenHelper) {
        this.db = sqLiteOpenHelper.getWritableDatabase();
    }

    @SuppressWarnings("unchecked")
    @NonNull @Override public <T extends BambooStorableType> StorableTypeParser<T> getParser(@NonNull Class<T> type) {
        return PARSERS.get(type);
    }

    @Override public <T extends BambooStorableType> void setParserForType(@NonNull Class<T> type, @NonNull StorableTypeParser<T> parser) {
        PARSERS.put(type, parser);
    }

    @SuppressWarnings("unchecked")
    @NonNull @Override public <T extends BambooStorableType> StorableTypeSerializer<T> getSerializer(@NonNull Class<T> type) {
        return SERIALIZERS.get(type);
    }

    @Override public <T extends BambooStorableType> void setSerializerForType(
            @NonNull Class<T> type, @NonNull StorableTypeSerializer<T> serializer) {
        SERIALIZERS.put(type, serializer);
    }

    @NonNull @Override public <T extends BambooStorableType> ForType<T> forType(@NonNull Class<T> type) {
        return new ForType<>(this, type);
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

            return db.insert(
                    getTableName(type),
                    null,
                    getSerializer(type).toContentValues(object)
            );
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
}
