package com.pushtorefresh.storio2.contentresolver.design;

import android.database.Cursor;
import android.net.Uri;

import com.pushtorefresh.storio2.contentresolver.operations.get.GetResolver;
import com.pushtorefresh.storio2.contentresolver.queries.Query;

import org.junit.Test;

import java.util.List;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.Single;

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
    public void getCursorAsFlowable() {
        Flowable<Cursor> flowable = storIOContentResolver()
                .get()
                .cursor()
                .withQuery(Query.builder()
                        .uri(mock(Uri.class))
                        .build())
                .withGetResolver(mock(GetResolver.class))
                .prepare()
                .asRxFlowable(BackpressureStrategy.LATEST);
    }

    @Test
    public void getListOfObjectsAsFlowable() {
        Flowable<List<Article>> flowable = storIOContentResolver()
                .get()
                .listOfObjects(Article.class)
                .withQuery(Query.builder()
                        .uri(mock(Uri.class))
                        .build())
                .withGetResolver(ArticleMeta.GET_RESOLVER)
                .prepare()
                .asRxFlowable(BackpressureStrategy.LATEST);
    }

    @Test
    public void getObjectAsFlowable() {
        Flowable<Article> flowable = storIOContentResolver()
                .get()
                .object(Article.class)
                .withQuery(Query.builder()
                        .uri(mock(Uri.class))
                        .build())
                .withGetResolver(ArticleMeta.GET_RESOLVER)
                .prepare()
                .asRxFlowable(BackpressureStrategy.LATEST);
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
