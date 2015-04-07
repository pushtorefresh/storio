package com.pushtorefresh.storio.contentresolver.design;

import android.database.Cursor;
import android.net.Uri;

import com.pushtorefresh.storio.contentresolver.query.Query;

import org.junit.Test;

import java.util.List;

import static org.mockito.Mockito.mock;

public class GetOperationDesignTest extends OperationDesignTest {

    @Test
    public void getCursorBlocking() {
        Cursor cursor = storIOContentResolver()
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
        List<Article> articles = storIOContentResolver()
                .get()
                .listOfObjects(Article.class)
                .withQuery(mock(Query.class))
                .withMapFunc(Article.MAP_FROM_CURSOR)
                .prepare()
                .executeAsBlocking();
    }
}
