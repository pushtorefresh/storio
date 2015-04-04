package com.pushtorefresh.storio.contentresolver.operation.delete;

import android.net.Uri;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

public class DeleteResultTest {

    @SuppressWarnings("ConstantConditions")
    @Test(expected = NullPointerException.class)
    public void nullAffectedUri() {
        DeleteResult.newInstance(1, null);
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
        assertEquals(affectedUri, deleteResult.affectedUri());
    }
}
