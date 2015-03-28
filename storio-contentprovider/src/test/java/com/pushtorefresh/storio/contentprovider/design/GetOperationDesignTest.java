package com.pushtorefresh.storio.contentprovider.design;

import android.database.Cursor;
import android.net.Uri;

import com.pushtorefresh.storio.contentprovider.query.Query;

import org.junit.Test;

import java.util.List;

import static org.mockito.Mockito.mock;

public class GetOperationDesignTest extends OperationDesignTest {

    @Test
    public void getCursorBlocking() {
        Cursor cursor = storIOContentProvider()
                .get()
                .cursor()
                .withQuery(new Query.Builder()
                        .uri(mock(Uri.class))
                        .build())
                .prepare()
                .executeAsBlocking();
    }

    @Test
    public void getListOfObjectsBlocking() {
        List<Article> articles = storIOContentProvider()
                .get()
                .listOfObjects(Article.class)
                .withMapFunc(Article.MAP_FROM_CURSOR)
                .withQuery(mock(Query.class))
                .prepare()
                .executeAsBlocking();
    }
}
