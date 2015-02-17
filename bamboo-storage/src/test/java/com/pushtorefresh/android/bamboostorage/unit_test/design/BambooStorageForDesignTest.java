package com.pushtorefresh.android.bamboostorage.unit_test.design;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.pushtorefresh.android.bamboostorage.BambooStorableType;
import com.pushtorefresh.android.bamboostorage.BambooStorage;
import com.pushtorefresh.android.bamboostorage.ForType;
import com.pushtorefresh.android.bamboostorage.wtf.StorableTypeParser;
import com.pushtorefresh.android.bamboostorage.wtf.StorableTypeSerializer;

import static org.mockito.Mockito.mock;

public class BambooStorageForDesignTest implements BambooStorage {
    @NonNull @Override public <T extends BambooStorableType> StorableTypeParser<T> getParser(@NonNull final Class<T> type) {
        return new StorableTypeParser<T>() {
            @Override public T parseFromCursor(@NonNull Cursor cursor) {
                try {
                    return type.newInstance();
                } catch (IllegalAccessException | InstantiationException e) {
                    e.printStackTrace();
                    throw new IllegalArgumentException("Can not create instance of type " + type.getCanonicalName() + " make sure it has constructor without args");
                }
            }
        };
    }

    @Override public <T extends BambooStorableType> void setParserForType(
            @NonNull Class<T> type, @NonNull StorableTypeParser<T> parser) {

    }

    @NonNull @Override public <T extends BambooStorableType> StorableTypeSerializer<T> getSerializer(@NonNull Class<T> type) {
        return new StorableTypeSerializer<T>() {
            @NonNull @Override public ContentValues toContentValues(T object) {
                return new ContentValues();
            }
        };
    }

    @Override public <T extends BambooStorableType> void setSerializerForType(
            @NonNull Class<T> type, @NonNull StorableTypeSerializer<T> serializer) {

    }

    @NonNull @Override public <T extends BambooStorableType> ForType<T> forType(@NonNull Class<T> type) {
        return new ForType<>(this, type);
    }

    @NonNull @Override public Internal getInternal() {
        return new InternalImpl();
    }

    private class InternalImpl implements Internal {
        @NonNull @Override public <T extends BambooStorableType> String getStorableIdFieldName(
                @NonNull Class<T> type) {
            return "_id";
        }

        @NonNull @Override public <T extends BambooStorableType> Cursor query(@NonNull Class<T> type,
                                                   @Nullable String where,
                                                   @Nullable String[] whereArgs,
                                                   @Nullable String orderBy) {
            return mock(MatrixCursor.class);
        }

        @Override public <T extends BambooStorableType> long insert(@NonNull T object) {
            return 0;
        }

        @Override public <T extends BambooStorableType> int update(
                @NonNull T object, @Nullable String where, @Nullable String[] whereArgs) {
            return 0;
        }

        @Override
        public <T extends BambooStorableType> int delete(@NonNull Class<T> type, @Nullable String where, @Nullable String[] whereArgs) {
            return 0;
        }

        @Override
        public <T extends BambooStorableType> int delete(@NonNull T object) {
            return 0;
        }
    }
}
