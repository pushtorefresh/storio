package com.pushtorefresh.storio.contentresolver.design;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.provider.BaseColumns;
import android.support.annotation.NonNull;

import com.pushtorefresh.storio.contentresolver.operations.delete.DefaultDeleteResolver;
import com.pushtorefresh.storio.contentresolver.operations.delete.DeleteResolver;
import com.pushtorefresh.storio.contentresolver.operations.get.DefaultGetResolver;
import com.pushtorefresh.storio.contentresolver.operations.get.GetResolver;
import com.pushtorefresh.storio.contentresolver.operations.put.DefaultPutResolver;
import com.pushtorefresh.storio.contentresolver.operations.put.PutResolver;
import com.pushtorefresh.storio.contentresolver.queries.DeleteQuery;
import com.pushtorefresh.storio.contentresolver.queries.InsertQuery;
import com.pushtorefresh.storio.contentresolver.queries.UpdateQuery;

import static org.mockito.Mockito.mock;

public class ArticleMeta {

    static final Uri CONTENT_URI = mock(Uri.class);

    static final PutResolver<Article> PUT_RESOLVER = new DefaultPutResolver<Article>() {
        @NonNull
        @Override
        protected InsertQuery mapToInsertQuery(@NonNull Article object) {
            return InsertQuery.builder()
                    .uri(CONTENT_URI)
                    .build();
        }

        @NonNull
        @Override
        protected UpdateQuery mapToUpdateQuery(@NonNull Article article) {
            return UpdateQuery.builder()
                    .uri(CONTENT_URI)
                    .where(BaseColumns._ID + " = ?")
                    .whereArgs(article.id())
                    .build();
        }

        @NonNull
        @Override
        protected ContentValues mapToContentValues(@NonNull Article object) {
            return mock(ContentValues.class);
        }
    };

    static final GetResolver<Article> GET_RESOLVER = new DefaultGetResolver<Article>() {
        @NonNull
        @Override
        public Article mapFromCursor(@NonNull Cursor cursor) {
            return Article.newInstance(null, null); // in Design tests it does not matter
        }
    };

    static final DeleteResolver<Article> DELETE_RESOLVER = new DefaultDeleteResolver<Article>() {
        @NonNull
        @Override
        protected DeleteQuery mapToDeleteQuery(@NonNull Article article) {
            return DeleteQuery.builder()
                    .uri(CONTENT_URI)
                    .where(BaseColumns._ID + " = ?")
                    .whereArgs(article.id())
                    .build();
        }
    };

    private ArticleMeta() {
        throw new IllegalStateException("No instances please");
    }
}
