package com.pushtorefresh.storio.contentresolver;

import com.pushtorefresh.storio.contentresolver.operations.delete.DeleteResolver;
import com.pushtorefresh.storio.contentresolver.operations.get.GetResolver;
import com.pushtorefresh.storio.contentresolver.operations.put.PutResolver;

import org.junit.Test;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.mockito.Mockito.mock;

public class ContentResolverTypeMappingTest {

    @SuppressWarnings({"unchecked", "ConstantConditions"})
    @Test(expected = NullPointerException.class)
    public void nullPutResolver() {
        ContentResolverTypeMapping.builder()
                .putResolver(null)
                .getResolver(mock(GetResolver.class))
                .deleteResolver(mock(DeleteResolver.class))
                .build();
    }

    @SuppressWarnings({"unchecked", "ConstantConditions"})
    @Test(expected = NullPointerException.class)
    public void nullGetResolver() {
        ContentResolverTypeMapping.builder()
                .putResolver(mock(PutResolver.class))
                .getResolver(null)
                .deleteResolver(mock(DeleteResolver.class))
                .build();
    }

    @SuppressWarnings({"unchecked", "ConstantConditions"})
    @Test(expected = NullPointerException.class)
    public void nullDeleteResolver() {
        ContentResolverTypeMapping.builder()
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

        final ContentResolverTypeMapping<TestItem> typeMapping = ContentResolverTypeMapping.<TestItem>builder()
                .putResolver(putResolver)
                .getResolver(getResolver)
                .deleteResolver(deleteResolver)
                .build();

        assertThat(typeMapping.putResolver()).isSameAs(putResolver);
        assertThat(typeMapping.getResolver()).isSameAs(getResolver);
        assertThat(typeMapping.deleteResolver()).isSameAs(deleteResolver);
    }
}
