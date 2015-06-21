package com.pushtorefresh.storio.contentresolver.operations.delete;

import android.net.Uri;

import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

public class DeleteResultTest {

    @SuppressWarnings("ConstantConditions")
    @Test(expected = NullPointerException.class)
    public void nullAffectedUri() {
        DeleteResult.newInstance(1, (Uri) null);
    }

    @SuppressWarnings("ConstantConditions")
    @Test(expected = NullPointerException.class)
    public void nullAffectedUris() {
        DeleteResult.newInstance(1, (Set<Uri>) null);
    }

    @Test
    public void numberOfRowsDeleted() {
        final DeleteResult deleteResult = DeleteResult.newInstance(3, mock(Uri.class));
        assertEquals(3, deleteResult.numberOfRowsDeleted());
    }

    @Test
    public void affectedUri() {
        final Uri affectedUri = mock(Uri.class);
        final DeleteResult deleteResult = DeleteResult.newInstance(2, affectedUri);
        assertEquals(1, deleteResult.affectedUris().size());
        assertTrue(deleteResult.affectedUris().contains(affectedUri));
    }

    @Test
    public void affectedUris() {
        final Set<Uri> affectedUris = new HashSet<Uri>();

        affectedUris.add(mock(Uri.class));
        affectedUris.add(mock(Uri.class));
        affectedUris.add(mock(Uri.class));

        final DeleteResult deleteResult = DeleteResult.newInstance(3, affectedUris);
        assertEquals(affectedUris, deleteResult.affectedUris());
    }
}
