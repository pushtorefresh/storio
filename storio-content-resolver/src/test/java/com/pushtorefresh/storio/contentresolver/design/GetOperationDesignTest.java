package com.pushtorefresh.storio.contentresolver.design;

import android.database.Cursor;
import android.net.Uri;

import com.pushtorefresh.storio.contentresolver.operations.get.GetResolver;
import com.pushtorefresh.storio.contentresolver.queries.Query;

import org.junit.Test;

import java.util.List;

import rx.Observable;

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
    public void getObjectAsObservable() {
        Observable<Article> observable = storIOContentResolver()
                .get()
                .object(Article.class)
                .withQuery(Query.builder()
                        .uri(mock(Uri.class))
                        .build())
                .withGetResolver(ArticleMeta.GET_RESOLVER)
                .prepare()
                .createObservable();
    }
}
