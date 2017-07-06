package com.pushtorefresh.storio.contentresolver.operations.put;

import android.net.Uri;

import com.pushtorefresh.storio.contentresolver.BuildConfig;
import com.pushtorefresh.storio.test.ToStringChecker;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import nl.jqno.equalsverifier.EqualsVerifier;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.assertj.core.api.Assertions.failBecauseExceptionWasNotThrown;
import static org.mockito.Mockito.mock;

@RunWith(RobolectricTestRunner.class) // Required for correct Uri impl
@Config(constants = BuildConfig.class, sdk = 21)
public class PutResultTest {

    @Test
    public void createInsertResult() {
        final Uri insertedUri = mock(Uri.class);
        final Uri affectedUri = mock(Uri.class);

        final PutResult insertResult = PutResult.newInsertResult(insertedUri, affectedUri);

        assertThat(insertResult.wasInserted()).isTrue();
        assertThat(insertResult.wasUpdated()).isFalse();

        assertThat(insertResult.wasNotInserted()).isFalse();
        assertThat(insertResult.wasNotUpdated()).isTrue();

        assertThat(insertResult.insertedUri()).isSameAs(insertedUri);
        assertThat(insertResult.affectedUri()).isSameAs(affectedUri);

        assertThat(insertResult.numberOfRowsUpdated()).isNull();
    }

    @Test
    public void shouldNotCreateInsertResultWithNullInsertedUri() {
        try {
            //noinspection ConstantConditions
            PutResult.newInsertResult(null, mock(Uri.class));
            failBecauseExceptionWasNotThrown(NullPointerException.class);
        } catch (NullPointerException expected) {
            assertThat(expected).hasMessage("insertedUri must not be null");
        }
    }

    @Test
    public void shouldNotCreateInsertResultWithNullAffectedUri() {
        try {
            //noinspection ConstantConditions
            PutResult.newInsertResult(mock(Uri.class), null);
            failBecauseExceptionWasNotThrown(NullPointerException.class);
        } catch (NullPointerException expected) {
            assertThat(expected).hasMessage("affectedUri must not be null");
        }
    }

    @Test
    public void createUpdateResult() {
        final int numberOfRowsUpdated = 10;
        final Uri affectedUri = mock(Uri.class);

        final PutResult updateResult = PutResult.newUpdateResult(numberOfRowsUpdated, affectedUri);

        assertThat(updateResult.wasUpdated()).isTrue();
        assertThat(updateResult.wasInserted()).isFalse();

        assertThat(updateResult.wasNotUpdated()).isFalse();
        assertThat(updateResult.wasNotInserted()).isTrue();

        //noinspection ConstantConditions
        assertThat((int) updateResult.numberOfRowsUpdated()).isEqualTo(numberOfRowsUpdated);
        assertThat(updateResult.affectedUri()).isSameAs(affectedUri);

        assertThat(updateResult.insertedUri()).isNull();
    }

    @Test
    public void shouldAllowCreatingUpdateResultWith0RowsUpdated() {
        PutResult putResult = PutResult.newUpdateResult(0, mock(Uri.class));
        assertThat(putResult.wasUpdated()).isFalse();
        assertThat(putResult.wasInserted()).isFalse();
        assertThat(putResult.numberOfRowsUpdated()).isEqualTo(Integer.valueOf(0));
    }

    @Test
    public void shouldNotCreateUpdateResultWithNegativeNumberOfRowsUpdated() {
        try {
            PutResult.newUpdateResult(-1, mock(Uri.class));
            failBecauseExceptionWasNotThrown(IllegalStateException.class);
        } catch (IllegalStateException expected) {
            assertThat(expected).hasMessage("Number of rows updated must be >= 0");
        }
    }

    @Test
    public void shouldCreateUpdateResultWithOneRowUpdated() {
        PutResult.newUpdateResult(1, mock(Uri.class)); // no exceptions should occur
    }

    @Test
    public void shouldNotCreateUpdateResultWithNullAffectedUri() {
        try {
            //noinspection ConstantConditions
            PutResult.newUpdateResult(1, null);
            failBecauseExceptionWasNotThrown(NullPointerException.class);
        } catch (NullPointerException expected) {
            assertThat(expected).hasMessage("affectedUri must not be null");
        }
    }

    @Test
    public void verifyEqualsAndHashCodeImplementation() {
        EqualsVerifier
                .forClass(PutResult.class)
                .allFieldsShouldBeUsed()
                .withPrefabValues(Uri.class, Uri.parse("content://1"), Uri.parse("content://2"))
                .verify();
    }

    @Test
    public void checkToStringImplementation() {
        ToStringChecker
                .forClass(PutResult.class)
                .check();
    }
}
