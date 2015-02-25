package com.pushtorefresh.android.bamboostorage;

import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.pushtorefresh.android.bamboostorage.wtf.Query;
import com.pushtorefresh.android.bamboostorage.wtf.StorableTypeParser;
import com.pushtorefresh.android.bamboostorage.wtf.StorableTypeSerializer;

public interface BambooStorage {

    @NonNull <T extends BambooStorableType> StorableTypeParser<T> getParser(@NonNull Class<T> type);

    <T extends BambooStorableType> void setParserForType(@NonNull Class<T> type, @NonNull StorableTypeParser<T> parser);

    @NonNull <T extends BambooStorableType> StorableTypeSerializer<T> getSerializer(@NonNull Class<T> type);

    <T extends BambooStorableType> void setSerializerForType(@NonNull Class<T> type, @NonNull StorableTypeSerializer<T> serializer);

    @NonNull <T extends BambooStorableType> ForType<T> forType(@NonNull Class<T> type);

    @NonNull PreparedQuery.Builder prepareQuery(@NonNull Query query);

    @NonNull Internal getInternal();

    interface Internal {
        @NonNull <T extends BambooStorableType> String getStorableIdFieldName(@NonNull Class<T> type);

        @NonNull <T extends BambooStorableType> Cursor query(@NonNull Class<T> type, @Nullable String where, @Nullable String[] whereArgs, @Nullable String orderBy);

        <T extends BambooStorableType> long insert(@NonNull T object);

        <T extends BambooStorableType> int update(@NonNull T object, @Nullable String where, @Nullable String[] whereArgs);

        <T extends BambooStorableType> int delete(@NonNull Class<T> type, @Nullable String where, @Nullable String[] whereArgs);

        <T extends BambooStorableType> int delete(@NonNull T object);
    }
}
