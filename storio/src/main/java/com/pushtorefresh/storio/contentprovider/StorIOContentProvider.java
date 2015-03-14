package com.pushtorefresh.storio.contentprovider;

import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.pushtorefresh.storio.contentprovider.operation.delete.PreparedDelete;
import com.pushtorefresh.storio.contentprovider.operation.get.PreparedGet;
import com.pushtorefresh.storio.contentprovider.operation.put.PreparedPut;
import com.pushtorefresh.storio.contentprovider.query.Query;

/**
 * Powerful abstraction over {@link android.content.ContentProvider}
 */
public abstract class StorIOContentProvider {

    @NonNull public PreparedGet.Builder get() {
        return new PreparedGet.Builder(this);
    }

    @NonNull public PreparedPut.Builder put() {
        return new PreparedPut.Builder(this);
    }

    @NonNull public PreparedDelete.Builder delete() {
        return new PreparedDelete.Builder(this);
    }

    /**
     * Hides some internal operations of {@link StorIOContentProvider}
     * to make {@link StorIOContentProvider} API clean and easy to understand
     */
    public static abstract class Internal {

        /**
         * Implement this to handle query requests from clients. This method can be called from multiple threads
         *
         * @param query query
         * @return cursor with result data or null
         */
        @Nullable public abstract Cursor query(@NonNull Query query);


    }
}
