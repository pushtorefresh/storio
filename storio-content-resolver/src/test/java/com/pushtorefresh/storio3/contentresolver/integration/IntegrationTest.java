package com.pushtorefresh.storio3.contentresolver.integration;

import android.content.ContentResolver;
import android.content.pm.ProviderInfo;
import android.support.annotation.NonNull;

import com.pushtorefresh.storio3.contentresolver.ContentResolverTypeMapping;
import com.pushtorefresh.storio3.contentresolver.StorIOContentResolver;
import com.pushtorefresh.storio3.contentresolver.impl.DefaultStorIOContentResolver;

import org.junit.Before;
import org.robolectric.Robolectric;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.android.controller.ContentProviderController;

public abstract class IntegrationTest {

    @NonNull // Initialized in @Before
    protected ContentResolver contentResolver;

    @NonNull // Initialized in @Before
    protected StorIOContentResolver storIOContentResolver;

    @Before
    public void setUp() {
        contentResolver = RuntimeEnvironment.application.getContentResolver();

        storIOContentResolver = createStoreIOContentResolver().build();

        ContentProviderController<IntegrationContentProvider> controller =
                Robolectric.buildContentProvider(IntegrationContentProvider.class);

        ProviderInfo providerInfo = new ProviderInfo();
        providerInfo.authority = IntegrationContentProvider.AUTHORITY;
        controller.create(providerInfo);
    }

    @NonNull
    protected DefaultStorIOContentResolver.CompleteBuilder createStoreIOContentResolver() {
        return DefaultStorIOContentResolver.builder()
                .contentResolver(contentResolver)
                .addTypeMapping(TestItem.class, ContentResolverTypeMapping.<TestItem>builder()
                        .putResolver(TestItem.PUT_RESOLVER)
                        .getResolver(TestItem.GET_RESOLVER)
                        .deleteResolver(TestItem.DELETE_RESOLVER)
                        .build());
    }
}
