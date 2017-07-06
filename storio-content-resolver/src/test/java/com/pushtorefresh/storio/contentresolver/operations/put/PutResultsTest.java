package com.pushtorefresh.storio.contentresolver.operations.put;

import android.net.Uri;

import com.pushtorefresh.storio.contentresolver.BuildConfig;
import com.pushtorefresh.storio.test.ToStringChecker;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.HashMap;
import java.util.Map;

import nl.jqno.equalsverifier.EqualsVerifier;

import static java.util.Collections.singletonMap;
import static org.assertj.core.api.Java6Assertions.assertThat;

@RunWith(RobolectricTestRunner.class) // Required for correct Uri impl
@Config(constants = BuildConfig.class, sdk = 21)
public class PutResultsTest {

    @Test
    public void numberOfInsertsShouldBeZero() {
        final Map<String, PutResult> putResultMap = singletonMap(
                "key",
                PutResult.newUpdateResult(1, Uri.parse("content://affectedUri"))
        );

        final PutResults<String> putResults = PutResults.newInstance(putResultMap);

        assertThat(putResults.numberOfInserts()).isEqualTo(0);

        // We cache this value, so let's test that cache works too
        // (coverage tool will report it as untested branch if we won't check this)
        assertThat(putResults.numberOfInserts()).isEqualTo(0);
    }

    @Test
    public void numberOfUpdatesShouldBeZero() {
        final Map<String, PutResult> putResultMap = new HashMap<String, PutResult>();
        putResultMap.put("key", PutResult.newInsertResult(Uri.parse("content://insertedUri"), Uri.parse("content://affectedUri")));

        final PutResults<String> putResults = PutResults.newInstance(putResultMap);

        assertThat(putResults.numberOfUpdates()).isEqualTo(0);

        // We cache this value, so let's test that cache works too
        // (coverage tool will report it as untested branch if we won't check this)
        assertThat(putResults.numberOfUpdates()).isEqualTo(0);
    }

    @Test
    public void mixOfInsertsAndUpdatesShouldBeHandledCorrectly() {
        final Map<String, PutResult> putResultMap = new HashMap<String, PutResult>();

        // 3 inserts + 2 updates (notice, that one of the updates updated 5 rows!)

        putResultMap.put("insert1", PutResult.newInsertResult(Uri.parse("content://insertedUri"), Uri.parse("content://affectedUri")));
        putResultMap.put("update1", PutResult.newUpdateResult(5, Uri.parse("content://affectedUri")));
        putResultMap.put("insert2", PutResult.newInsertResult(Uri.parse("content://insertedUri"), Uri.parse("content://affectedUri")));
        putResultMap.put("update2", PutResult.newUpdateResult(1, Uri.parse("content://affectedUri")));
        putResultMap.put("insert3", PutResult.newInsertResult(Uri.parse("content://insertedUri"), Uri.parse("content://affectedUri")));

        final PutResults<String> putResults = PutResults.newInstance(putResultMap);

        assertThat(putResults.numberOfInserts()).isEqualTo(3);
        assertThat(putResults.numberOfUpdates()).isEqualTo(6);
    }

    @Test
    public void verifyEqualsAndHashCodeImplementation() {
        EqualsVerifier
                .forClass(PutResults.class)
                .allFieldsShouldBeUsed()
                .verify();
    }

    @Test
    public void checkToStringImplementation() {
        ToStringChecker
                .forClass(PutResults.class)
                .check();
    }
}
