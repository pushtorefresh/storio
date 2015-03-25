package com.pushtorefresh.storio.contentprovider.design;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.pushtorefresh.storio.contentprovider.Changes;
import com.pushtorefresh.storio.contentprovider.StorIOContentProvider;
import com.pushtorefresh.storio.contentprovider.query.DeleteQuery;
import com.pushtorefresh.storio.contentprovider.query.InsertQuery;
import com.pushtorefresh.storio.contentprovider.query.Query;
import com.pushtorefresh.storio.contentprovider.query.UpdateQuery;

import java.util.Set;

import rx.Observable;

import static org.mockito.Mockito.mock;

class DesignTestStorIOContentProviderImpl extends StorIOContentProvider {

    @NonNull
    private final Internal internal = new InternalImpl();

    @NonNull
    @Override
    public Observable<Changes> observeChangesOfUris(@NonNull Set<Uri> uris) {
        return Observable.empty();
    }

    @NonNull
    @Override
    public Internal internal() {
        return internal;
    }

    private class InternalImpl extends Internal {

        @Nullable
        @Override
        public Cursor query(@NonNull Query query) {
            return mock(Cursor.class);
        }

        @NonNull
        @Override
        public Uri insert(@NonNull InsertQuery insertQuery, @NonNull ContentValues contentValues) {
            return mock(Uri.class);
        }

        @Override
        public int update(@NonNull UpdateQuery updateQuery, @NonNull ContentValues contentValues) {
            // no impl
            return 0;
        }

        @Override
        public int delete(@NonNull DeleteQuery deleteQuery) {
            // no impl
            return 0;
        }
    }
}
