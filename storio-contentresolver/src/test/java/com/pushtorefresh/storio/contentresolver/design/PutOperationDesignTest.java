package com.pushtorefresh.storio.contentresolver.design;

import android.content.ContentValues;

import com.pushtorefresh.storio.contentresolver.operation.put.PutCollectionResult;
import com.pushtorefresh.storio.contentresolver.operation.put.PutResult;

import org.junit.Test;

import java.util.ArrayList;

import rx.Observable;

import static org.mockito.Mockito.mock;

public class PutOperationDesignTest extends OperationDesignTest {

    @Test
    public void putObjectBlocking() {
        Article article = new Article();

        PutResult putResult = storIOContentResolver()
                .put()
                .object(article)
                .withMapFunc(Article.MAP_TO_CONTENT_VALUES)
                .withPutResolver(Article.PUT_RESOLVER)
                .prepare()
                .executeAsBlocking();
    }

    @Test
    public void putObjectObservable() {
        Article article = new Article();

        Observable<PutResult> putResultObservable = storIOContentResolver()
                .put()
                .object(article)
                .withMapFunc(Article.MAP_TO_CONTENT_VALUES)
                .withPutResolver(Article.PUT_RESOLVER)
                .prepare()
                .createObservable();
    }

    @Test
    public void putObjectsBlocking() {
        Iterable<Article> articles = new ArrayList<>();

        PutCollectionResult<Article> putCollectionResult = storIOContentResolver()
                .put()
                .objects(articles)
                .withMapFunc(Article.MAP_TO_CONTENT_VALUES)
                .withPutResolver(Article.PUT_RESOLVER)
                .prepare()
                .executeAsBlocking();
    }

    @Test
    public void putObjectsObservable() {
        Iterable<Article> articles = new ArrayList<>();

        Observable<PutCollectionResult<Article>> putCollectionResultObservable = storIOContentResolver()
                .put()
                .objects(articles)
                .withMapFunc(Article.MAP_TO_CONTENT_VALUES)
                .withPutResolver(Article.PUT_RESOLVER)
                .prepare()
                .createObservable();
    }

    @Test
    public void putContentValuesBlocking() {
        ContentValues contentValues = mock(ContentValues.class);

        PutResult putResult = storIOContentResolver()
                .put()
                .contentValues(contentValues)
                .withPutResolver(Article.PUT_RESOLVER_FOR_CONTENT_VALUES)
                .prepare()
                .executeAsBlocking();
    }

    @Test
    public void putContentValuesObservable() {
        ContentValues contentValues = mock(ContentValues.class);

        Observable<PutResult> putResultObservable = storIOContentResolver()
                .put()
                .contentValues(contentValues)
                .withPutResolver(Article.PUT_RESOLVER_FOR_CONTENT_VALUES)
                .prepare()
                .createObservable();
    }
}
