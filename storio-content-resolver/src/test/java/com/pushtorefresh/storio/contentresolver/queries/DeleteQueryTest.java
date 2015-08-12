package com.pushtorefresh.storio.contentresolver.queries;

import android.net.Uri;

import com.pushtorefresh.storio.contentresolver.BuildConfig;
import com.pushtorefresh.storio.test.ToStringChecker;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import java.util.Arrays;

import nl.jqno.equalsverifier.EqualsVerifier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

@RunWith(RobolectricGradleTestRunner.class) // Required for correct Uri impl
@Config(constants = BuildConfig.class, sdk = 21)
public class DeleteQueryTest {

    @Test(expected = NullPointerException.class)
    public void shouldNotAllowNullUriObject() {
        //noinspection ConstantConditions
        DeleteQuery.builder()
                .uri((Uri) null) // LOL, via overload we disabled null uri without specifying Type!
                .build();
    }

    @Test(expected = NullPointerException.class) // Uri#parse() not mocked
    public void shouldNotAllowNullUriString() {
        //noinspection ConstantConditions
        DeleteQuery.builder()
                .uri((String) null)
                .build();
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
