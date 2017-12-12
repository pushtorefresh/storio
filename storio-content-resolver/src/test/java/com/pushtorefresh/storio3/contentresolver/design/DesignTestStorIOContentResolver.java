package com.pushtorefresh.storio3.contentresolver.design;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.pushtorefresh.storio3.Interceptor;
import com.pushtorefresh.storio3.contentresolver.Changes;
import com.pushtorefresh.storio3.contentresolver.ContentResolverTypeMapping;
import com.pushtorefresh.storio3.contentresolver.StorIOContentResolver;
import com.pushtorefresh.storio3.contentresolver.queries.DeleteQuery;
import com.pushtorefresh.storio3.contentresolver.queries.InsertQuery;
import com.pushtorefresh.storio3.contentresolver.queries.Query;
import com.pushtorefresh.storio3.contentresolver.queries.UpdateQuery;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.Scheduler;

import static org.mockito.Mockito.mock;

class DesignTestStorIOContentResolver extends StorIOContentResolver {

    @NonNull
    private final LowLevel lowLevel = new LowLevelImpl();

    @NonNull
    @Override
    public Flowable<Changes> observeChangesOfUris(@NonNull Set<Uri> uris, @NonNull BackpressureStrategy backpressureStrategy) {
        return Flowable.empty();
    }

    @Nullable
    @Override
    public Scheduler defaultRxScheduler() {
        return null;
    }

    @NonNull
    @Override
    public LowLevel lowLevel() {
        return lowLevel;
    }

    @NonNull
    @Override
    public List<Interceptor> interceptors() {
        return Collections.emptyList();
    }

    private class LowLevelImpl extends LowLevel {

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
