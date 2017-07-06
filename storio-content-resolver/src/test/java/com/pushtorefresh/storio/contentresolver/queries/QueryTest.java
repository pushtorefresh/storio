package com.pushtorefresh.storio.contentresolver.queries;

import android.net.Uri;

import com.pushtorefresh.storio.contentresolver.BuildConfig;
import com.pushtorefresh.storio.test.ToStringChecker;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import nl.jqno.equalsverifier.EqualsVerifier;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.assertj.core.api.Assertions.failBecauseExceptionWasNotThrown;
import static org.mockito.Mockito.mock;

@RunWith(RobolectricTestRunner.class) // Required for correct Uri impl
@Config(constants = BuildConfig.class, sdk = 21)
public class QueryTest {

    @Test
    public void shouldNotAllowNullUriObject() {
        try {
            //noinspection ConstantConditions
            Query.builder()
                    .uri((Uri) null);
            failBecauseExceptionWasNotThrown(NullPointerException.class);
        } catch (NullPointerException expected) {
            assertThat(expected)
                    .hasMessage("Please specify uri")
                    .hasNoCause();
        }
    }

    @Test
    public void shouldNotAllowNullUriString() {
        try {
            //noinspection ConstantConditions
            Query.builder()
                    .uri((String) null);
            failBecauseExceptionWasNotThrown(NullPointerException.class);
        } catch (NullPointerException expected) {
            assertThat(expected)
                    .hasMessage("Uri should not be null")
                    .hasNoCause();
        }
    }

    @Test
    public void shouldNotAllowEmptyUriString() {
        try {
            Query.builder()
                    .uri("");
            failBecauseExceptionWasNotThrown(IllegalStateException.class);
        } catch (IllegalStateException expected) {
            assertThat(expected)
                    .hasMessage("Uri should not be null")
                    .hasNoCause();
        }
    }

    @Test
    public void columnsShouldNotBeNull() {
        Query query = Query.builder()
                .uri(mock(Uri.class))
                .build();

        assertThat(query.columns()).isNotNull();
        assertThat(query.columns()).isEmpty();
    }

    @Test
    public void whereClauseShouldNotBeNull() {
        Query query = Query.builder()
                .uri(mock(Uri.class))
                .build();

        assertThat(query.where()).isEqualTo("");
    }

    @Test
    public void whereArgsShouldNotBeNull() {
        Query query = Query.builder()
                .uri(mock(Uri.class))
                .build();

        assertThat(query.whereArgs()).isNotNull();
        assertThat(query.whereArgs()).isEmpty();
    }

    @Test
    public void sortOrderShouldNotBeNull() {
        Query query = Query.builder()
                .uri(mock(Uri.class))
                .build();

        assertThat(query.sortOrder()).isEqualTo("");
    }

    @Test
    public void completeBuilderShouldNotAllowNullUriObject() {
        try {
            //noinspection ConstantConditions
            Query.builder()
                    .uri(mock(Uri.class))
                    .uri((Uri) null);
            failBecauseExceptionWasNotThrown(NullPointerException.class);
        } catch (NullPointerException expected) {
            assertThat(expected)
                    .hasMessage("Please specify uri")
                    .hasNoCause();
        }
    }

    @Test
    public void completeBuilderShouldNotAllowNullUriString() {
        try {
            //noinspection ConstantConditions
            Query.builder()
                    .uri(mock(Uri.class))
                    .uri((String) null);
            failBecauseExceptionWasNotThrown(NullPointerException.class);
        } catch (NullPointerException expected) {
            assertThat(expected)
                    .hasMessage("Uri should not be null")
                    .hasNoCause();
        }
    }

    @Test
    public void completeBuilderShouldNotAllowEmptyUriString() {
        try {
            Query.builder()
                    .uri(mock(Uri.class))
                    .uri("");
            failBecauseExceptionWasNotThrown(IllegalStateException.class);
        } catch (IllegalStateException expected) {
            assertThat(expected)
                    .hasMessage("Uri should not be null")
                    .hasNoCause();
        }
    }

    @Test
    public void completeBuilderShouldUpdateUriObject() {
        Uri oldUri = mock(Uri.class);
        Uri newUri = mock(Uri.class);

        Query query = Query.builder()
                .uri(oldUri)
                .uri(newUri)
                .build();

        assertThat(query.uri()).isSameAs(newUri);
    }

    @Test
    public void completeBuilderShouldUpdateUriString() {
        Uri oldUri = Uri.parse("content://1");
        String newUri = "content://2";

        Query query = Query.builder()
                .uri(oldUri)
                .uri(newUri)
                .build();

        assertThat(query.uri()).isEqualTo(Uri.parse(newUri));
    }

    @Test
    public void createdThroughToBuilderQueryShouldBeEqual() {
        final Uri uri = mock(Uri.class);
        final String[] columns = {"1", "2", "3"};
        final String where = "test_where";
        final Object[] whereArgs = {"arg1", "arg2", "arg3"};
        final String sortOrder = "test_order";

        final Query firstQuery = Query.builder()
                .uri(uri)
                .columns(columns)
                .where(where)
                .whereArgs(whereArgs)
                .sortOrder(sortOrder)
                .build();

        final Query secondQuery = firstQuery.toBuilder().build();

        assertThat(secondQuery).isEqualTo(firstQuery);
    }

    @Test
    public void shouldTakeStringArrayAsWhereArgs() {
        final String[] whereArgs = {"arg1", "arg2", "arg3"};

        final Query query = Query.builder()
                .uri(mock(Uri.class))
                .where("test_where")
                .whereArgs(whereArgs)
                .build();

        assertThat(query.whereArgs()).isEqualTo(asList(whereArgs));
    }

    @Test
    public void buildWithNormalValues() {
        final Uri uri = mock(Uri.class);
        final String[] columns = {"1", "2", "3"};
        final String where = "test_where";
        final Object[] whereArgs = {"arg1", "arg2", "arg3"};
        final String sortOrder = "test_order";

        final Query query = Query.builder()
                .uri(uri)
                .columns(columns)
                .where(where)
                .whereArgs(whereArgs)
                .sortOrder(sortOrder)
                .build();

        assertThat(query.uri()).isEqualTo(uri);
        assertThat(query.columns()).isEqualTo(asList(columns));
        assertThat(query.where()).isEqualTo(where);
        assertThat(query.whereArgs()).isEqualTo(asList(whereArgs));
        assertThat(query.sortOrder()).isEqualTo(sortOrder);
    }

    @Test
    public void verifyEqualsAndHashCodeImplementation() {
        EqualsVerifier
                .forClass(Query.class)
                .allFieldsShouldBeUsed()
                .withPrefabValues(Uri.class, Uri.parse("content://1"), Uri.parse("content://2"))
                .verify();
    }

    @Test
    public void checkToStringImplementation() {
        ToStringChecker
                .forClass(Query.class)
                .check();
    }
}
