package com.pushtorefresh.storio.contentresolver.design;

import android.content.ContentValues;

import com.pushtorefresh.storio.contentresolver.operation.put.PutResult;
import com.pushtorefresh.storio.contentresolver.operation.put.PutResults;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;

import static org.mockito.Mockito.mock;

public class PutOperationDesignTest extends OperationDesignTest {

    @Test
    public void putObjectBlocking() {
        Article article = new Article();

        PutResult putResult = storIOContentResolver()
                .put()
                .object(article)
                .withPutResolver(Article.PUT_RESOLVER)
                .withMapFunc(Article.MAP_TO_CONTENT_VALUES)
                .prepare()
                .executeAsBlocking();
    }

    @Test
    public void putObjectObservable() {
        Article article = new Article();

        Observable<PutResult> putResultObservable = storIOContentResolver()
                .put()
                .object(article)
                .withPutResolver(Article.PUT_RESOLVER)
                .withMapFunc(Article.MAP_TO_CONTENT_VALUES)
                .prepare()
                .createObservable();
    }

    @Test
    public void putObjectsBlocking() {
        Iterable<Article> articles = new ArrayList<Article>();

        PutResults<Article> putResults = storIOContentResolver()
                .put()
                .objects(articles)
                .withPutResolver(Article.PUT_RESOLVER)
                .withMapFunc(Article.MAP_TO_CONTENT_VALUES)
                .prepare()
                .executeAsBlocking();
    }

    @Test
    public void putObjectsObservable() {
        Iterable<Article> articles = new ArrayList<Article>();

        Observable<PutResults<Article>> putResultsObservable = storIOContentResolver()
                .put()
                .objects(articles)
                .withPutResolver(Article.PUT_RESOLVER)
                .withMapFunc(Article.MAP_TO_CONTENT_VALUES)
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

    @Test
    public void putContentValuesIterableBlocking() {
        List<ContentValues> contentValuesList = new ArrayList<ContentValues>();

        PutResults<ContentValues> putResults = storIOContentResolver()
                .put()
                .contentValues(contentValuesList)
                .withPutResolver(Article.PUT_RESOLVER_FOR_CONTENT_VALUES)
                .prepare()
                .executeAsBlocking();
    }

    @Test
    public void putContentValuesIterableObservable() {
        List<ContentValues> contentValuesList = new ArrayList<ContentValues>();

        Observable<PutResults<ContentValues>> putResultsObservable = storIOContentResolver()
                .put()
                .contentValues(contentValuesList)
                .withPutResolver(Article.PUT_RESOLVER_FOR_CONTENT_VALUES)
                .prepare()
                .createObservable();
    }
}
