package com.pushtorefresh.storio.contentprovider;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.pushtorefresh.storio.contentprovider.operation.delete.PreparedDelete;
import com.pushtorefresh.storio.contentprovider.operation.get.PreparedGet;
import com.pushtorefresh.storio.contentprovider.operation.put.PreparedPut;
import com.pushtorefresh.storio.contentprovider.query.DeleteQuery;
import com.pushtorefresh.storio.contentprovider.query.InsertQuery;
import com.pushtorefresh.storio.contentprovider.query.Query;
import com.pushtorefresh.storio.contentprovider.query.UpdateQuery;

/**
 * Powerful abstraction over {@link android.content.ContentProvider}
 */
public abstract class StorIOContentProvider {

    /**
     * Prepares "get" operation for {@link StorIOContentProvider}
     * Allows to get information from {@link StorIOContentProvider}
     *
     * @return builder for PreparedGet
     */
    @NonNull
    public PreparedGet.Builder get() {
        return new PreparedGet.Builder(this);
    }

    @NonNull
    public PreparedPut.Builder put() {
        return new PreparedPut.Builder(this);
    }

    @NonNull
    public PreparedDelete.Builder delete() {
        return new PreparedDelete.Builder(this);
    }

    /**
     * Hides some internal operations of {@link StorIOContentProvider} to make API of {@link StorIOContentProvider} clean and easy to understand
     *
     * @return implementation of Internal operations for {@link StorIOContentProvider}
     */
    @NonNull
    public abstract Internal internal();

    /**
     * Hides some internal operations of {@link StorIOContentProvider}
     * to make {@link StorIOContentProvider} API clean and easy to understand
     */
    public static abstract class Internal {

        /**
         * Gets the data from {@link StorIOContentProvider}
         *
         * @param query query
         * @return cursor with result data or null
         */
        @Nullable
        public abstract Cursor query(@NonNull Query query);

        /**
         * Inserts the data to {@link StorIOContentProvider}
         *
         * @param insertQuery   query
         * @param contentValues data
         * @return Uri for inserted data
         */
        @NonNull
        public abstract Uri insert(@NonNull InsertQuery insertQuery, @NonNull ContentValues contentValues);

        /**
         * Updates data in {@link StorIOContentProvider}
         *
         * @param updateQuery   query
         * @param contentValues data
         * @return number of rows affected
         */
        public abstract int update(@NonNull UpdateQuery updateQuery, @NonNull ContentValues contentValues);

        /**
         * Deletes the data from {@link StorIOContentProvider}
         *
         * @param deleteQuery query
         * @return number of rows deleted
         */
        public abstract int delete(@NonNull DeleteQuery deleteQuery);
    }
}
