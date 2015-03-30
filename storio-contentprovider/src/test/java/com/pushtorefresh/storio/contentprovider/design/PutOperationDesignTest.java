package com.pushtorefresh.storio.contentprovider.design;

import com.pushtorefresh.storio.contentprovider.operation.put.PutResult;

import org.junit.Test;

import rx.Observable;

public class PutOperationDesignTest extends OperationDesignTest {

    @Test
    public void putObjectBlocking() {
        Article article = new Article();

        PutResult putResult = storIOContentProvider()
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

        Observable<PutResult> putResultObservable = storIOContentProvider()
                .put()
                .object(article)
                .withMapFunc(Article.MAP_TO_CONTENT_VALUES)
                .withPutResolver(Article.PUT_RESOLVER)
                .prepare()
                .createObservable();
    }
}
