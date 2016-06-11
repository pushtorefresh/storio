package com.pushtorefresh.storio.contentresolver.design;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.pushtorefresh.storio.contentresolver.Changes;
import com.pushtorefresh.storio.contentresolver.ContentResolverTypeMapping;
import com.pushtorefresh.storio.contentresolver.StorIOContentResolver;
import com.pushtorefresh.storio.contentresolver.queries.DeleteQuery;
import com.pushtorefresh.storio.contentresolver.queries.InsertQuery;
import com.pushtorefresh.storio.contentresolver.queries.Query;
import com.pushtorefresh.storio.contentresolver.queries.UpdateQuery;

import java.util.Set;

import rx.Observable;
import rx.Scheduler;

import static org.mockito.Mockito.mock;

class DesignTestStorIOContentResolver extends StorIOContentResolver {

    @NonNull
    private final Internal lowLevel = new InternalImpl();

    @NonNull
    @Override
    public Observable<Changes> observeChangesOfUris(@NonNull Set<Uri> uris) {
        return Observable.empty();
    }

    @Nullable
    @Override
    public Scheduler defaultScheduler() {
        return null;
    }

    @NonNull
    @Override
    public Internal internal() {
        return lowLevel;
    }

    @NonNull
    @Override
    public LowLevel lowLevel() {
        return lowLevel;
    }

    private class InternalImpl extends Internal {

        @Nullable
        @Override
        public <T> ContentResolverTypeMapping<T> typeMapping(@NonNull Class<T> type) {
            // no impl
            return null;
        }

        @NonNull
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

        @NonNull
        @Override
        public ContentResolver contentResolver() {
            // no impl
            return null;
        }
    }
}
