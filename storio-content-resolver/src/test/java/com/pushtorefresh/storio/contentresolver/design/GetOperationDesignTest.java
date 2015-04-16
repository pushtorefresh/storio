package com.pushtorefresh.storio.contentresolver.design;

import android.database.Cursor;
import android.net.Uri;

import com.pushtorefresh.storio.contentresolver.operation.get.GetResolver;
import com.pushtorefresh.storio.contentresolver.query.Query;

import org.junit.Test;

import java.util.List;

import static org.mockito.Mockito.mock;

public class GetOperationDesignTest extends OperationDesignTest {

    @SuppressWarnings("unchecked")
    @Test
    public void getCursorBlocking() {
        Cursor cursor = storIOContentResolver()
                .get()
                .cursor()
                .withQuery(new Query.Builder()
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
                .withQuery(mock(Query.class))
                .withGetResolver(ArticleMeta.GET_RESOLVER)
                .prepare()
                .executeAsBlocking();
    }
}
