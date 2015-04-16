package com.pushtorefresh.storio.contentresolver;

import com.pushtorefresh.storio.contentresolver.operation.delete.DeleteResolver;
import com.pushtorefresh.storio.contentresolver.operation.get.GetResolver;
import com.pushtorefresh.storio.contentresolver.operation.put.PutResolver;

import org.junit.Test;

import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.mock;

public class ContentResolverTypeDefaultsTest {

    @SuppressWarnings({"unchecked", "ConstantConditions"})
    @Test(expected = NullPointerException.class)
    public void nullPutResolver() {
        new ContentResolverTypeDefaults.Builder<Object>()
                .putResolver(null)
                .getResolver(mock(GetResolver.class))
                .deleteResolver(mock(DeleteResolver.class))
                .build();
    }

    @SuppressWarnings({"unchecked", "ConstantConditions"})
    @Test(expected = NullPointerException.class)
    public void nullGetResolver() {
        new ContentResolverTypeDefaults.Builder<Object>()
                .putResolver(mock(PutResolver.class))
                .getResolver(null)
                .deleteResolver(mock(DeleteResolver.class))
                .build();
    }

    @SuppressWarnings({"unchecked", "ConstantConditions"})
    @Test(expected = NullPointerException.class)
    public void nullDeleteResolver() {
        new ContentResolverTypeDefaults.Builder<Object>()
                .putResolver(mock(PutResolver.class))
                .getResolver(mock(GetResolver.class))
                .deleteResolver(null)
                .build();
    }

    @SuppressWarnings("unchecked")
    @Test
    public void build() {
        class TestItem {

        }

        final PutResolver<TestItem> putResolver = mock(PutResolver.class);
        final GetResolver<TestItem> getResolver = mock(GetResolver.class);
        final DeleteResolver<TestItem> deleteResolver = mock(DeleteResolver.class);

        final ContentResolverTypeDefaults<TestItem> contentResolverTypeDefaults = new ContentResolverTypeDefaults.Builder<TestItem>()
                .putResolver(putResolver)
                .getResolver(getResolver)
                .deleteResolver(deleteResolver)
                .build();

        assertSame(putResolver, contentResolverTypeDefaults.putResolver);
        assertSame(getResolver, contentResolverTypeDefaults.getResolver);
        assertSame(deleteResolver, contentResolverTypeDefaults.deleteResolver);
    }
}
