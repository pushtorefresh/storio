package com.pushtorefresh.storio.contentresolver.queries;

import android.net.Uri;

import com.pushtorefresh.storio.contentresolver.BuildConfig;
import com.pushtorefresh.storio.test.ToStringChecker;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.Arrays;

import nl.jqno.equalsverifier.EqualsVerifier;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.assertj.core.api.Assertions.failBecauseExceptionWasNotThrown;
import static org.mockito.Mockito.mock;

@RunWith(RobolectricTestRunner.class) // Required for correct Uri impl
@Config(constants = BuildConfig.class, sdk = 21)
public class DeleteQueryTest {

    @Test
    public void shouldNotAllowNullUriObject() {
        try {
            //noinspection ConstantConditions
            DeleteQuery.builder()
                    .uri((Uri) null); // LOL, via overload we disabled null uri without specifying Type!
            failBecauseExceptionWasNotThrown(NullPointerException.class);
        } catch (NullPointerException expected) {
            assertThat(expected)
                    .hasMessage("Please specify uri")
                    .hasNoCause();
        }
    }

    @Test
    public void shouldNotAllowNullUriString() { // Uri#parse() not mocked
        try {
            //noinspection ConstantConditions
            DeleteQuery.builder()
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
            DeleteQuery.builder()
                    .uri("");
            failBecauseExceptionWasNotThrown(IllegalStateException.class);
        } catch (IllegalStateException expected) {
            assertThat(expected)
                    .hasMessage("Uri should not be null")
                    .hasNoCause();
        }
    }

    @Test
    public void whereClauseShouldNotBeNull() {
        DeleteQuery deleteQuery = DeleteQuery.builder()
                .uri(mock(Uri.class))
                .where(null)
                .build();

        assertThat(deleteQuery.where()).isEqualTo("");
    }

    @Test
    public void whereArgsShouldNotBeNull() {
        DeleteQuery deleteQuery = DeleteQuery.builder()
                .uri(mock(Uri.class))
                .where("c1 = s")
                .build();

        assertThat(deleteQuery.whereArgs()).isNotNull();
        assertThat(deleteQuery.whereArgs().isEmpty()).isTrue();
    }

    @Test
    public void completeBuilderShouldNotAllowNullUriObject() {
        try {
            //noinspection ConstantConditions
            DeleteQuery.builder()
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
            DeleteQuery.builder()
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
            DeleteQuery.builder()
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

        DeleteQuery query = DeleteQuery.builder()
                .uri(oldUri)
                .uri(newUri)
                .build();

        assertThat(query.uri()).isSameAs(newUri);
    }

    @Test
    public void completeBuilderShouldUpdateUriString() {
        Uri oldUri = Uri.parse("content://1");
        String newUri = "content://2";

        DeleteQuery query = DeleteQuery.builder()
                .uri(oldUri)
                .uri(newUri)
                .build();

        assertThat(query.uri()).isEqualTo(Uri.parse(newUri));
    }

    @Test
    public void createdThroughToBuilderQueryShouldBeEqual() {
        final Uri uri = mock(Uri.class);
        final String where = "test_where";
        final Object[] whereArgs = {"arg1", "arg2", "arg3"};

        final DeleteQuery firstQuery = DeleteQuery.builder()
                .uri(uri)
                .where(where)
                .whereArgs(whereArgs)
                .build();

        final DeleteQuery secondQuery = firstQuery.toBuilder().build();

        assertThat(secondQuery).isEqualTo(firstQuery);
    }

    @Test
    public void shouldTakeStringArrayAsWhereArgs() {
        final String[] whereArgs = {"arg1", "arg2", "arg3"};

        final DeleteQuery deleteQuery = DeleteQuery.builder()
                .uri(mock(Uri.class))
                .where("test_where")
                .whereArgs(whereArgs)
                .build();

        assertThat(deleteQuery.whereArgs()).isEqualTo(Arrays.asList(whereArgs));
    }

    @Test
    public void buildWithNormalValues() {
        final Uri uri = mock(Uri.class);
        final String where = "test_where";
        final Object[] whereArgs = {"arg1", "arg2", "arg3"};

        final DeleteQuery deleteQuery = DeleteQuery.builder()
                .uri(uri)
                .where(where)
                .whereArgs(whereArgs)
                .build();

        assertThat(deleteQuery.uri()).isEqualTo(uri);
        assertThat(deleteQuery.where()).isEqualTo(where);
        assertThat(deleteQuery.whereArgs()).isEqualTo(Arrays.asList(whereArgs));
    }

    @Test
    public void verifyEqualsAndHashCodeImplementation() {
        EqualsVerifier
                .forClass(DeleteQuery.class)
                .allFieldsShouldBeUsed()
                .withPrefabValues(Uri.class, Uri.parse("content://1"), Uri.parse("content://2"))
                .verify();
    }

    @Test
    public void checkToStringImplementation() {
        ToStringChecker
                .forClass(DeleteQuery.class)
                .check();
    }
}
