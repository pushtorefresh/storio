package com.pushtorefresh.storio.contentresolver;

import android.net.Uri;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import java.util.HashSet;
import java.util.Set;

import nl.jqno.equalsverifier.EqualsVerifier;

import static com.pushtorefresh.storio.test.Tests.checkToString;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

@RunWith(RobolectricGradleTestRunner.class) // Required for correct Uri impl
@Config(constants = BuildConfig.class, sdk = 21)
public class ChangesTest {

    @SuppressWarnings("ConstantConditions")
    @Test(expected = NullPointerException.class)
    public void nullAffectedUri() {
        Changes.newInstance((Uri) null);
    }

    @SuppressWarnings("ConstantConditions")
    @Test(expected = NullPointerException.class)
    public void nullAffectedUris() {
        Changes.newInstance((Set<Uri>) null);
    }

    @Test
    public void newInstanceOneAffectedUri() {
        final Uri uri = mock(Uri.class);
        final Changes changes = Changes.newInstance(uri);
        assertEquals(1, changes.affectedUris().size());
        assertTrue(changes.affectedUris().contains(uri));
    }

    @Test
    public void newInstanceMultipleAffectedUris() {
        final Set<Uri> affectedUris = new HashSet<Uri>();
        affectedUris.add(mock(Uri.class));
        affectedUris.add(mock(Uri.class));
        affectedUris.add(mock(Uri.class));

        final Changes changes = Changes.newInstance(affectedUris);

        assertEquals(affectedUris, changes.affectedUris());
    }

    @Test
    public void verifyEqualsAndHashCodeImplementation() {
        EqualsVerifier
                .forClass(Changes.class)
                .allFieldsShouldBeUsed()
                .withPrefabValues(Uri.class, Uri.parse("content://1"), Uri.parse("content://2"))
                .verify();
    }

    @Test
    public void checkToStringImplementation() {
        checkToString(Changes.newInstance(Uri.parse("content://testUri")));
    }
}
