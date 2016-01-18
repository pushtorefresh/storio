package com.pushtorefresh.storio.contentresolver.design;

import android.database.Cursor;
import android.net.Uri;

import com.pushtorefresh.storio.contentresolver.operations.get.GetResolver;
import com.pushtorefresh.storio.contentresolver.queries.Query;

import org.junit.Test;

import java.util.List;

import rx.Observable;
import rx.Single;

import static org.mockito.Mockito.mock;

public class GetOperationDesignTest extends OperationDesignTest {

    @SuppressWarnings("unchecked")
    @Test
    public void getCursorBlocking() {
        Cursor cursor = storIOContentResolver()
                .get()
                .cursor()
                .withQuery(Query.builder()
                        .uri(mock(Uri.class))
                        .build())
                .withGetResolver(mock(GetResolver.class))
                .prepare()
                .executeAsBlocking();
    }

    @Test
    public void getListOfObjectsBlocking() {
        List<Article> articles = storIOContentResolver()
                .get()
                .listOfObjects(Article.class)
                .withQuery(Query.builder()
                        .uri(mock(Uri.class))
                        .build())
                .withGetResolver(ArticleMeta.GET_RESOLVER)
                .prepare()
                .executeAsBlocking();
    }

    @Test
    public void getObjectBlocking() {
        Article article = storIOContentResolver()
                .get()
                .object(Article.class)
                .withQuery(Query.builder()
                        .uri(mock(Uri.class))
                        .build())
                .withGetResolver(ArticleMeta.GET_RESOLVER)
                .prepare()
                .executeAsBlocking();
    }

    @Test
    public void getCursorAsObservable() {
        Observable<Cursor> observable = storIOContentResolver()
                .get()
                .cursor()
                .withQuery(Query.builder()
                        .uri(mock(Uri.class))
                        .build())
                .withGetResolver(mock(GetResolver.class))
                .prepare()
                .asRxObservable();
    }

    @Test
    public void getListOfObjectsAsObservable() {
        Observable<List<Article>> observable = storIOContentResolver()
                .get()
                .listOfObjects(Article.class)
                .withQuery(Query.builder()
                        .uri(mock(Uri.class))
                        .build())
                .withGetResolver(ArticleMeta.GET_RESOLVER)
                .prepare()
                .asRxObservable();
    }

    @Test
    public void getObjectAsObservable() {
        Observable<Article> observable = storIOContentResolver()
                .get()
                .object(Article.class)
                .withQuery(Query.builder()
                        .uri(mock(Uri.class))
                        .build())
                .withGetResolver(ArticleMeta.GET_RESOLVER)
                .prepare()
                .asRxObservable();
    }

    @Test
    public void getCursorAsSingle() {
        Single<Cursor> single = storIOContentResolver()
                .get()
                .cursor()
                .withQuery(Query.builder()
                        .uri(mock(Uri.class))
                        .build())
                .withGetResolver(mock(GetResolver.class))
                .prepare()
                .asRxSingle();
    }

    @Test
    public void getListOfObjectsAsSingle() {
        Single<List<Article>> single = storIOContentResolver()
                .get()
                .listOfObjects(Article.class)
                .withQuery(Query.builder()
                        .uri(mock(Uri.class))
                        .build())
                .withGetResolver(ArticleMeta.GET_RESOLVER)
                .prepare()
                .asRxSingle();
    }

    @Test
    public void getObjectAsSingle() {
        Single<Article> single = storIOContentResolver()
                .get()
                .object(Article.class)
                .withQuery(Query.builder()
                        .uri(mock(Uri.class))
                        .build())
                .withGetResolver(ArticleMeta.GET_RESOLVER)
                .prepare()
                .asRxSingle();
    }
}
