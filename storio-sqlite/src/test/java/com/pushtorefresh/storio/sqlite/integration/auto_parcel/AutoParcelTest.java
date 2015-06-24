package com.pushtorefresh.storio.sqlite.integration.auto_parcel;

import android.support.annotation.NonNull;

import com.pushtorefresh.storio.sqlite.BuildConfig;
import com.pushtorefresh.storio.sqlite.SQLiteTypeMapping;
import com.pushtorefresh.storio.sqlite.StorIOSQLite;
import com.pushtorefresh.storio.sqlite.impl.DefaultStorIOSQLite;
import com.pushtorefresh.storio.sqlite.operations.delete.DeleteResult;
import com.pushtorefresh.storio.sqlite.operations.put.PutResult;
import com.pushtorefresh.storio.sqlite.queries.DeleteQuery;
import com.pushtorefresh.storio.sqlite.queries.Query;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)
public class AutoParcelTest {

    @NonNull // Initialized in @Before
    private StorIOSQLite storIOSQLite;

    @Before
    public void setUp() {
        storIOSQLite = DefaultStorIOSQLite.builder()
                .sqliteOpenHelper(new OpenHelper(RuntimeEnvironment.application))
                .addTypeMapping(Book.class, SQLiteTypeMapping.<Book>builder()
                        .putResolver(BookTableMeta.PUT_RESOLVER)
                        .getResolver(BookTableMeta.GET_RESOLVER)
                        .deleteResolver(BookTableMeta.DELETE_RESOLVER)
                        .build())
                .build();

        // Clearing books table before each test case
        storIOSQLite
                .delete()
                .byQuery(DeleteQuery.builder()
                        .table(BookTableMeta.TABLE)
                        .build())
                .prepare()
                .executeAsBlocking();
    }

    @Test
    public void insertObject() {
        final Book book = Book.builder()
                .id(1)
                .title("What a great book")
                .author("Somebody")
                .build();

        final PutResult putResult = storIOSQLite
                .put()
                .object(book)
                .prepare()
                .executeAsBlocking();

        assertTrue(putResult.wasInserted());

        final List<Book> storedBooks = storIOSQLite
                .get()
                .listOfObjects(Book.class)
                .withQuery(Query.builder()
                        .table(BookTableMeta.TABLE)
                        .build())
                .prepare()
                .executeAsBlocking();

        assertEquals(1, storedBooks.size());

        assertEquals(book, storedBooks.get(0));
    }

    @Test
    public void updateObject() {
        final Book book = Book.builder()
                .id(1)
                .title("What a great book")
                .author("Somebody")
                .build();

        final PutResult putResult1 = storIOSQLite
                .put()
                .object(book)
                .prepare()
                .executeAsBlocking();

        assertTrue(putResult1.wasInserted());

        final Book bookWithUpdatedInfo = Book.builder()
                .id(1) // Same id, should be updated
                .title("Corrected title")
                .author("Corrected author")
                .build();

        final PutResult putResult2 = storIOSQLite
                .put()
                .object(bookWithUpdatedInfo)
                .prepare()
                .executeAsBlocking();

        assertTrue(putResult2.wasUpdated());

        final List<Book> storedBooks = storIOSQLite
                .get()
                .listOfObjects(Book.class)
                .withQuery(Query.builder()
                        .table(BookTableMeta.TABLE)
                        .build())
                .prepare()
                .executeAsBlocking();

        assertEquals(1, storedBooks.size());

        assertEquals(bookWithUpdatedInfo, storedBooks.get(0));
    }

    @Test
    public void deleteObject() {
        final Book book = Book.builder()
                .id(1)
                .title("What a great book")
                .author("Somebody")
                .build();

        final PutResult putResult = storIOSQLite
                .put()
                .object(book)
                .prepare()
                .executeAsBlocking();

        assertTrue(putResult.wasInserted());

        final DeleteResult deleteResult = storIOSQLite
                .delete()
                .object(book)
                .prepare()
                .executeAsBlocking();

        assertEquals(1, deleteResult.numberOfRowsDeleted());

        final List<Book> storedBooks = storIOSQLite
                .get()
                .listOfObjects(Book.class)
                .withQuery(Query.builder()
                        .table(BookTableMeta.TABLE)
                        .build())
                .prepare()
                .executeAsBlocking();

        assertEquals(0, storedBooks.size());
    }
}
