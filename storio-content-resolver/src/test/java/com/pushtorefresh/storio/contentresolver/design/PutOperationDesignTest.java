package com.pushtorefresh.storio.contentresolver.design;

import android.content.ContentValues;
import android.net.Uri;
import android.support.annotation.NonNull;

import com.pushtorefresh.storio.contentresolver.operations.put.DefaultPutResolver;
import com.pushtorefresh.storio.contentresolver.operations.put.PutResolver;
import com.pushtorefresh.storio.contentresolver.operations.put.PutResult;
import com.pushtorefresh.storio.contentresolver.operations.put.PutResults;
import com.pushtorefresh.storio.contentresolver.queries.InsertQuery;
import com.pushtorefresh.storio.contentresolver.queries.UpdateQuery;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import rx.Completable;
import rx.Observable;
import rx.Single;

import static org.mockito.Mockito.mock;

public class PutOperationDesignTest extends OperationDesignTest {

    final PutResolver<ContentValues> putResolverForContentValues = new DefaultPutResolver<ContentValues>() {
        @NonNull
        @Override
        protected InsertQuery mapToInsertQuery(@NonNull ContentValues object) {
            return InsertQuery.builder()
                    .uri(mock(Uri.class))
                    .build();
        }

        @NonNull
        @Override
        protected UpdateQuery mapToUpdateQuery(@NonNull ContentValues object) {
            return UpdateQuery.builder()
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
                .asRxObservable();
    }

    @Test
    public void putObjectSingle() {
        Article article = Article.newInstance(null, "test");

        Single<PutResult> putResultSingle = storIOContentResolver()
                .put()
                .object(article)
                .withPutResolver(ArticleMeta.PUT_RESOLVER)
                .prepare()
                .asRxSingle();
    }

    @Test
    public void putObjectCompletable() {
        Article article = Article.newInstance(null, "test");

        Completable completable = storIOContentResolver()
                .put()
                .object(article)
                .withPutResolver(ArticleMeta.PUT_RESOLVER)
                .prepare()
                .asRxCompletable();
    }

    @Test
    public void putCollectionOfObjectsBlocking() {
        Collection<Article> articles = new ArrayList<Article>();

        PutResults<Article> putResults = storIOContentResolver()
                .put()
                .objects(articles)
                .withPutResolver(ArticleMeta.PUT_RESOLVER)
                .prepare()
                .executeAsBlocking();
    }

    @Test
    public void putCollectionOfObjectsObservable() {
        Collection<Article> articles = new ArrayList<Article>();

        Observable<PutResults<Article>> putResultsObservable = storIOContentResolver()
                .put()
                .objects(articles)
                .withPutResolver(ArticleMeta.PUT_RESOLVER)
                .prepare()
                .asRxObservable();
    }

    @Test
    public void putCollectionOfObjectsSingle() {
        Collection<Article> articles = new ArrayList<Article>();

        Single<PutResults<Article>> putResultsSingle = storIOContentResolver()
                .put()
                .objects(articles)
                .withPutResolver(ArticleMeta.PUT_RESOLVER)
                .prepare()
                .asRxSingle();
    }

    @Test
    public void putCollectionOfObjectsCompletable() {
        Collection<Article> articles = new ArrayList<Article>();

        Completable completable = storIOContentResolver()
                .put()
                .objects(articles)
                .withPutResolver(ArticleMeta.PUT_RESOLVER)
                .prepare()
                .asRxCompletable();
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
                .asRxObservable();
    }

    @Test
    public void putContentValuesSingle() {
        ContentValues contentValues = mock(ContentValues.class);

        Single<PutResult> putResultSingle = storIOContentResolver()
                .put()
                .contentValues(contentValues)
                .withPutResolver(putResolverForContentValues)
                .prepare()
                .asRxSingle();
    }

    @Test
    public void putContentValuesCompletable() {
        ContentValues contentValues = mock(ContentValues.class);

        Completable completable = storIOContentResolver()
                .put()
                .contentValues(contentValues)
                .withPutResolver(putResolverForContentValues)
                .prepare()
                .asRxCompletable();
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
                .asRxObservable();
    }

    @Test
    public void putContentValuesIterableSingle() {
        List<ContentValues> contentValuesList = new ArrayList<ContentValues>();

        Single<PutResults<ContentValues>> putResultsSingle = storIOContentResolver()
                .put()
                .contentValues(contentValuesList)
                .withPutResolver(putResolverForContentValues)
                .prepare()
                .asRxSingle();
    }

    @Test
    public void putContentValuesIterableCompletable() {
        List<ContentValues> contentValuesList = new ArrayList<ContentValues>();

        Completable completable = storIOContentResolver()
                .put()
                .contentValues(contentValuesList)
                .withPutResolver(putResolverForContentValues)
                .prepare()
                .asRxCompletable();
    }
}
