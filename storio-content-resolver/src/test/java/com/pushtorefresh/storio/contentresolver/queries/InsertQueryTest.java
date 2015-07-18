package com.pushtorefresh.storio.contentresolver.queries;

import android.net.Uri;

import com.pushtorefresh.storio.contentresolver.BuildConfig;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import nl.jqno.equalsverifier.EqualsVerifier;

import static com.pushtorefresh.storio.test.Tests.checkToString;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

@RunWith(RobolectricGradleTestRunner.class) // Required for correct Uri impl
@Config(constants = BuildConfig.class, sdk = 21)
public class InsertQueryTest {

    @Test(expected = NullPointerException.class)
    public void shouldNotAllowNullUriObject() {
        //noinspection ConstantConditions
        InsertQuery.builder()
                .uri((Uri) null)
                .build();
    }

    @Test(expected = NullPointerException.class)
    public void shouldNotAllowNullUriString() {
        //noinspection ConstantConditions
        InsertQuery.builder()
                .uri((String) null)
                .build();
    }

    @Test
    public void buildWithNormalValues() {
        final Uri uri = mock(Uri.class);

        final InsertQuery insertQuery = InsertQuery.builder()
                .uri(uri)
                .build();

        assertEquals(uri, insertQuery.uri());
    }

    @Test
    public void verifyEqualsAndHashCodeImplementation() {
        EqualsVerifier
                .forClass(InsertQuery.class)
                .allFieldsShouldBeUsed()
                .withPrefabValues(Uri.class, Uri.parse("content://1"), Uri.parse("content://2"))
                .verify();
    }

    @Test
    public void checkToStringImplementation() {
        checkToString(InsertQuery.builder()
                        .uri("content://test")
                        .build()
        );
    }
}
