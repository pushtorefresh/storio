package com.pushtorefresh.storio.contentresolver;

import android.net.Uri;

import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

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
        final Set<Uri> affectedUris = new HashSet<>();
        affectedUris.add(mock(Uri.class));
        affectedUris.add(mock(Uri.class));
        affectedUris.add(mock(Uri.class));

        final Changes changes = Changes.newInstance(affectedUris);

        assertEquals(affectedUris, changes.affectedUris());
    }
}
