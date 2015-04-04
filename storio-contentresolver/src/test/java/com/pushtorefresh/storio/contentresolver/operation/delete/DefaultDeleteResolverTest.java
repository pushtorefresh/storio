package com.pushtorefresh.storio.contentresolver.operation.delete;

import android.net.Uri;

import com.pushtorefresh.storio.contentresolver.StorIOContentResolver;
import com.pushtorefresh.storio.contentresolver.query.DeleteQuery;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class DefaultDeleteResolverTest {

    @Test
    public void performDelete() {
        final StorIOContentResolver storIOContentResolver = mock(StorIOContentResolver.class);
        final StorIOContentResolver.Internal internal = mock(StorIOContentResolver.Internal.class);

        when(storIOContentResolver.internal())
                .thenReturn(internal);

        final int expectedNumberOfRowsDeleted = 1;

        when(internal.delete(any(DeleteQuery.class)))
                .thenReturn(expectedNumberOfRowsDeleted);

        final DefaultDeleteResolver defaultDeleteResolver = new DefaultDeleteResolver();

        final Uri expectedUri = mock(Uri.class);

        final DeleteQuery expectedDeleteQuery = new DeleteQuery.Builder()
                .uri(expectedUri)
                .where("test where clause")
                .whereArgs("test")
                .build();

        // Performing Delete Operation
        final DeleteResult deleteResult = defaultDeleteResolver.performDelete(storIOContentResolver, expectedDeleteQuery);

        // checks that required delete was performed
        verify(internal, times(1)).delete(expectedDeleteQuery);

        // only one delete should be performed
        verify(internal, times(1)).delete(any(DeleteQuery.class));

        // delete result checks
        assertEquals(expectedNumberOfRowsDeleted, deleteResult.numberOfRowsDeleted());
        assertEquals(expectedUri, deleteResult.affectedUri());
    }
}
