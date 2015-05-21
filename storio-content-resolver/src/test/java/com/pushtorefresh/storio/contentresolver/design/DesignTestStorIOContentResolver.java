package com.pushtorefresh.storio.contentresolver.design;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.pushtorefresh.storio.contentresolver.Changes;
import com.pushtorefresh.storio.contentresolver.ContentResolverTypeMapping;
import com.pushtorefresh.storio.contentresolver.StorIOContentResolver;
import com.pushtorefresh.storio.contentresolver.query.DeleteQuery;
import com.pushtorefresh.storio.contentresolver.query.InsertQuery;
import com.pushtorefresh.storio.contentresolver.query.Query;
import com.pushtorefresh.storio.contentresolver.query.UpdateQuery;

import java.util.Set;

import rx.Observable;

import static org.mockito.Mockito.mock;

class DesignTestStorIOContentResolver extends StorIOContentResolver {

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
        public <T> ContentResolverTypeMapping<T> typeMapping(@NonNull Class<T> type) {
            // no impl
            return null;
        }

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
