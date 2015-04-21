package com.pushtorefresh.storio.contentresolver.design;

import android.content.ContentValues;
import android.net.Uri;
import android.support.annotation.NonNull;

import com.pushtorefresh.storio.contentresolver.operation.put.DefaultPutResolver;
import com.pushtorefresh.storio.contentresolver.operation.put.PutResolver;
import com.pushtorefresh.storio.contentresolver.operation.put.PutResult;
import com.pushtorefresh.storio.contentresolver.operation.put.PutResults;
import com.pushtorefresh.storio.contentresolver.query.InsertQuery;
import com.pushtorefresh.storio.contentresolver.query.UpdateQuery;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;

import static org.mockito.Mockito.mock;

public class PutOperationDesignTest extends OperationDesignTest {

    final PutResolver<ContentValues> putResolverForContentValues = new DefaultPutResolver<ContentValues>() {
        @NonNull
        @Override
        protected InsertQuery mapToInsertQuery(@NonNull ContentValues object) {
            return new InsertQuery.Builder()
                    .uri(mock(Uri.class))
                    .build();
        }

        @NonNull
        @Override
        protected UpdateQuery mapToUpdateQuery(@NonNull ContentValues object) {
            return new UpdateQuery.Builder()
                    .uri(mock(Uri.class))
                    .build();
        }

        @NonNull
        @Override
        protected ContentValues mapToContentValues(@NonNull ContentValues contentValues) {
            return contentValues; // easy
        }
    };

    @Test
    public void putObjectBlocking() {
        Article article = Article.newInstance(null, "test");

        PutResult putResult = storIOContentResolver()
                .put()
                .object(article)
                .withPutResolver(ArticleMeta.PUT_RESOLVER)
                .prepare()
                .executeAsBlocking();
    }

    @Test
    public void putObjectObservable() {
        Article article = Article.newInstance(null, "test");

        Observable<PutResult> putResultObservable = storIOContentResolver()
                .put()
                .object(article)
                .withPutResolver(ArticleMeta.PUT_RESOLVER)
                .prepare()
                .createObservable();
    }

    @Test
    public void putObjectsBlocking() {
        Iterable<Article> articles = new ArrayList<Article>();

        PutResults<Article> putResults = storIOContentResolver()
                .put()
                .objects(Article.class, articles)
                .withPutResolver(ArticleMeta.PUT_RESOLVER)
                .prepare()
                .executeAsBlocking();
    }

    @Test
    public void putObjectsObservable() {
        Iterable<Article> articles = new ArrayList<Article>();

        Observable<PutResults<Article>> putResultsObservable = storIOContentResolver()
                .put()
                .objects(Article.class, articles)
                .withPutResolver(ArticleMeta.PUT_RESOLVER)
                .prepare()
                .createObservable();
    }

    @Test
    public void putContentValuesBlocking() {
        ContentValues contentValues = mock(ContentValues.class);

        PutResult putResult = storIOContentResolver()
                .put()
                .contentValues(contentValues)
                .withPutResolver(putResolverForContentValues)
                .prepare()
                .executeAsBlocking();
    }

    @Test
    public void putContentValuesObservable() {
        ContentValues contentValues = mock(ContentValues.class);

        Observable<PutResult> putResultObservable = storIOContentResolver()
                .put()
                .contentValues(contentValues)
                .withPutResolver(putResolverForContentValues)
                .prepare()
                .createObservable();
    }

    @Test
    public void putContentValuesIterableBlocking() {
        List<ContentValues> contentValuesList = new ArrayList<ContentValues>();

        PutResults<ContentValues> putResults = storIOContentResolver()
                .put()
                .contentValues(contentValuesList)
                .withPutResolver(putResolverForContentValues)
                .prepare()
                .executeAsBlocking();
    }

    @Test
    public void putContentValuesIterableObservable() {
        List<ContentValues> contentValuesList = new ArrayList<ContentValues>();

        Observable<PutResults<ContentValues>> putResultsObservable = storIOContentResolver()
                .put()
                .contentValues(contentValuesList)
                .withPutResolver(putResolverForContentValues)
                .prepare()
                .createObservable();
    }
}
