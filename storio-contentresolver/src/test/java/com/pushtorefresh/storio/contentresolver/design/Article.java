package com.pushtorefresh.storio.contentresolver.design;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.provider.BaseColumns;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.pushtorefresh.storio.contentresolver.operation.put.DefaultPutResolver;
import com.pushtorefresh.storio.contentresolver.operation.put.PutResolver;
import com.pushtorefresh.storio.operation.MapFunc;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Test class that represents an object stored in ContentProvider
 */
class Article {

    static final Uri URI = mock(Uri.class);

    static final MapFunc<Cursor, Article> MAP_FROM_CURSOR = new MapFunc<Cursor, Article>() {
        @Override
        public Article map(Cursor cursor) {
            return new Article(); // parse cursor here
        }
    };

    static final MapFunc<Article, ContentValues> MAP_TO_CONTENT_VALUES = new MapFunc<Article, ContentValues>() {
        @Override
        public ContentValues map(Article article) {
            final ContentValues contentValues = mock(ContentValues.class);

            when(contentValues.get(BaseColumns._ID))
                    .thenReturn(article.getId());

            return contentValues;
        }
    };

    static final PutResolver<Article> PUT_RESOLVER = new DefaultPutResolver<Article>() {
        @NonNull
        @Override
        protected Uri getUri(@NonNull ContentValues contentValues) {
            return URI;
        }
    };

    @Nullable
    private Long id;

    @NonNull
    private String title;

    @Nullable
    public Long getId() {
        return id;
    }

    @NonNull
    public String getTitle() {
        return title;
    }
}
