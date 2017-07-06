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
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import java.util.List;

import static org.assertj.core.api.Java6Assertions.assertThat;

@RunWith(RobolectricTestRunner.class)
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

        assertThat(putResult.wasInserted()).isTrue();

        final List<Book> storedBooks = storIOSQLite
                .get()
                .listOfObjects(Book.class)
                .withQuery(Query.builder()
                        .table(BookTableMeta.TABLE)
                        .build())
                .prepare()
                .executeAsBlocking();

        assertThat(storedBooks).hasSize(1);

        assertThat(storedBooks.get(0)).isEqualTo(book);
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

        assertThat(putResult1.wasInserted()).isTrue();

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

        assertThat(putResult2.wasUpdated()).isTrue();

        final List<Book> storedBooks = storIOSQLite
                .get()
                .listOfObjects(Book.class)
                .withQuery(Query.builder()
                        .table(BookTableMeta.TABLE)
                        .build())
                .prepare()
                .executeAsBlocking();

        assertThat(storedBooks).hasSize(1);

        assertThat(storedBooks.get(0)).isEqualTo(bookWithUpdatedInfo);
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

        assertThat(putResult.wasInserted()).isTrue();

        final DeleteResult deleteResult = storIOSQLite
                .delete()
                .object(book)
                .prepare()
                .executeAsBlocking();

        assertThat(deleteResult.numberOfRowsDeleted()).isEqualTo(1);

        final List<Book> storedBooks = storIOSQLite
                .get()
                .listOfObjects(Book.class)
                .withQuery(Query.builder()
                        .table(BookTableMeta.TABLE)
                        .build())
                .prepare()
                .executeAsBlocking();

        assertThat(storedBooks).hasSize(0);
    }
}
