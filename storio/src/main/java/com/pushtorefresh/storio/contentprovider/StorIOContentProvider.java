package com.pushtorefresh.storio.contentprovider;

import android.support.annotation.NonNull;

import com.pushtorefresh.storio.contentprovider.operation.delete.PreparedDelete;
import com.pushtorefresh.storio.contentprovider.operation.get.PreparedGet;
import com.pushtorefresh.storio.contentprovider.operation.put.PreparedPut;

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


}
